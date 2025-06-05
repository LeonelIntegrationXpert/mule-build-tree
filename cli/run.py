#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
╔══════════════════════════════════════════════════════════════════════╗
║      🔧  UNIVERSAL JAVA & MAVEN MULTI-BUILD TOOL – v2.0 (2025)       ║
║                                                                      ║
║  • Baixa/usa JDK (8/11/17/21) e Maven (3.6.3–4.x) com cache          ║
║  • Credenciais Exchange lidas **somente** de variáveis de ambiente   ║
║  • NÃO altera o settings.xml – apenas aponta (-s)                    ║
║  • Menus coloridos & logs em build/logs                              ║
║  • Loop de builds com bump de versão se erro 403/409                 ║
║  • Se deploy_config.json não existir, cria e popula lendo todos      ║
║    os pom.xml dos subprojetos em projects/                            ║
║  • **Watcher**: detecta novas pastas em projects/ e gera deploy_config.json e pom automaticamente
╚══════════════════════════════════════════════════════════════════════╝
"""

import os
import sys
import time
import json
import re
import itertools
import subprocess
import requests
import zipfile
import threading

from pathlib import Path
from typing import Optional, Tuple, List, Dict, Any

import xml.etree.ElementTree as ET
from xml.dom import minidom
from jinja2 import Environment, FileSystemLoader
from watchdog.observers import Observer
from watchdog.events import FileSystemEventHandler

from build.imports import *  # noqa: F403, F401

# ───── CONFIGURAÇÕES FIXAS ────────────────────────────────────────────────

WORKSPACE_ROOT       = Path(__file__).resolve().parents[1]
PROJECTS_DIR         = WORKSPACE_ROOT / "projects"
BUILD_DIR            = WORKSPACE_ROOT / "build"
CONFIG_DIR           = BUILD_DIR / "configs"
DEPLOY_CONFIG_PATH   = CONFIG_DIR / "deploy_config.json"
PARENT_CONFIG_PATH   = CONFIG_DIR / "parent_config.json"
BOM_CONFIG_PATH      = CONFIG_DIR / "bom_config.json"
TEMPLATE_DIR         = BUILD_DIR / "templates" / "pom_templates"
TEMPLATE_NAME        = "pom.xml.template"

JDK_DIR              = WORKSPACE_ROOT / "tools" / "jdks"
MAVEN_DIR            = WORKSPACE_ROOT / "tools" / "mavens"

DEFAULT_JDK          = "8"
DEFAULT_MAVEN        = "3.9.5"
DEFAULT_GOAL         = "clean deploy -DskipTests"
DEFAULT_DEPLOY       = True

LOG_DIR              = BUILD_DIR / "logs"
LOG_DIR.mkdir(parents=True, exist_ok=True)

# ───── UTILITÁRIOS ─────────────────────────────────────────────────────────

def prettify_xml(raw: str, indent: int = 2) -> str:
    """
    Retorna XML indentado e sem linhas em branco extras.
    """
    try:
        parsed = minidom.parseString(raw.encode("utf-8"))
        pretty = parsed.toprettyxml(indent=" " * indent, newl="\n")
        lines = [line.rstrip() for line in pretty.splitlines() if line.strip()]
        return "\n".join(lines)
    except Exception:
        return raw

def ensure_dir(path: Path) -> None:
    path.mkdir(parents=True, exist_ok=True)

# ───── CONFIGURAÇÃO JSON ───────────────────────────────────────────────────

def read_json(path: Path, default: Any) -> Any:
    """
    Lê JSON de 'path', retorna 'default' se não existir ou inválido.
    """
    if not path.exists():
        return default
    try:
        text = path.read_text(encoding="utf-8").strip()
        if not text:
            return default
        return json.loads(text)
    except json.JSONDecodeError:
        print(f"[WARN] JSON inválido em '{path}', redefinindo.")
        path.write_text(json.dumps(default, indent=2, ensure_ascii=False), encoding="utf-8")
        return default
    except Exception as e:
        print(f"[ERR] Erro ao ler '{path}': {e}")
        sys.exit(1)

def write_json(path: Path, data: Any) -> None:
    """
    Escreve 'data' em JSON indentado em 'path'.
    """
    ensure_dir(path.parent)
    path.write_text(json.dumps(data, indent=2, ensure_ascii=False), encoding="utf-8")

def read_deploy_config() -> Dict[str, Any]:
    """
    Lê ou inicializa 'deploy_config.json'.
    """
    default = {"projects": []}
    ensure_dir(DEPLOY_CONFIG_PATH.parent)
    if not DEPLOY_CONFIG_PATH.exists():
        write_json(DEPLOY_CONFIG_PATH, default)
        return default

    cfg = read_json(DEPLOY_CONFIG_PATH, default)
    if not isinstance(cfg.get("projects"), list):
        cfg["projects"] = []
    return cfg

def write_deploy_config(cfg: Dict[str, Any]) -> None:
    write_json(DEPLOY_CONFIG_PATH, cfg)

def load_parent_config() -> Dict[str, Any]:
    """
    Carrega 'parent_config.json', retorna {} se não existir ou inválido.
    """
    return read_json(PARENT_CONFIG_PATH, {})

def load_bom_settings(bom_json_path: Path) -> Tuple[List[Any], List[Any], Optional[Any]]:
    """
    Carrega 'bom_config.json' e devolve (repositories, pluginRepositories, distributionManagement).
    """
    data = read_json(bom_json_path, {})
    return (
        data.get("repositories", []),
        data.get("pluginRepositories", []),
        data.get("distributionManagement")
    )

# ───── VERSÕES ─────────────────────────────────────────────────────────────

def decrement_version(version: str) -> str:
    """
    Decrementa a última parte numérica de 'version'. Ex: "1.2.3" → "1.2.2".
    """
    m = re.match(r"^(\d+(?:\.\d+)*)(.*)$", version)
    if not m:
        return version
    nums, suffix = m.groups()
    parts = nums.split(".")
    try:
        parts[-1] = str(max(0, int(parts[-1]) - 1))
    except ValueError:
        return version
    return ".".join(parts) + suffix

def increment_version(version: str) -> str:
    """
    Incrementa a última parte numérica de 'version'. Ex: "1.2.3" → "1.2.4".
    """
    m = re.match(r"^(\d+(?:\.\d+)*)(.*)$", version)
    if not m:
        return version
    nums, suffix = m.groups()
    parts = nums.split(".")
    try:
        parts[-1] = str(int(parts[-1]) + 1)
    except ValueError:
        return version
    return ".".join(parts) + suffix

# ───── EXTRAÇÃO DO POM ──────────────────────────────────────────────────────

def _get_namespace(root: ET.Element) -> Dict[str, str]:
    """
    Retorna namespace {'m': uri} se existir, ou {}.
    """
    if "}" in root.tag:
        uri = root.tag.split("}")[0].strip("{")
        return {"m": uri}
    return {}

def _find(elem: ET.Element, tag: str, ns: Dict[str, str]) -> Optional[ET.Element]:
    return elem.find(f"m:{tag}", namespaces=ns) if ns else elem.find(tag)

def _findall(elem: ET.Element, tag: str, ns: Dict[str, str]) -> List[ET.Element]:
    return elem.findall(f"m:{tag}", namespaces=ns) if ns else elem.findall(tag)

def _findtext(elem: Optional[ET.Element], tag: str, ns: Dict[str, str]) -> str:
    if elem is None:
        return ""
    return elem.findtext(f"m:{tag}", namespaces=ns) or ""

def extract_pom_information(project_path: Path) -> Dict[str, Any]:
    """
    Extrai diversos campos do 'pom.xml' em 'project_path'.
    """
    info: Dict[str, Any] = {
        "modelVersion": "",
        "packaging": "",
        "groupId": "",
        "artifactId": project_path.name,
        "version": "",
        "name": project_path.name,
        "description": "",
        "url": "",
        "inceptionYear": "",
        "parent": None,
        "organization": None,
        "licenses": [],
        "scm": None,
        "issueManagement": None,
        "ciManagement": None,
        "mailingLists": [],
        "developers": [],
        "contributors": [],
        "properties": {},
        "modules": [],
        "dependencyManagement": [],
        "dependencies": [],
        "repositories": [],
        "pluginRepositories": [],
        "distributionManagement": None,
        "build": None,
        "reporting": None,
        "profiles": []
    }

    pom_file = project_path / "pom.xml"
    if not pom_file.exists():
        return info

    try:
        tree = ET.parse(str(pom_file))
        root = tree.getroot()
        ns = _get_namespace(root)

        # (0) modelVersion e packaging
        info["modelVersion"] = _findtext(root, "modelVersion", ns)
        info["packaging"]    = _findtext(root, "packaging", ns)

        # (1) Coordenadas básicas (considera parent se faltar)
        parent_elem = _find(root, "parent", ns)
        info["groupId"]    = _findtext(root, "groupId", ns) or (_findtext(parent_elem, "groupId", ns) if parent_elem else "")
        info["artifactId"] = _findtext(root, "artifactId", ns)
        info["version"]    = _findtext(root, "version", ns) or (_findtext(parent_elem, "version", ns) if parent_elem else "")
        info["name"]       = _findtext(root, "name", ns) or info["artifactId"]
        info["description"]= _findtext(root, "description", ns)
        info["url"]        = _findtext(root, "url", ns)
        info["inceptionYear"]= _findtext(root, "inceptionYear", ns)

        # (2) Parent
        if parent_elem is not None:
            info["parent"] = {
                "groupId": _findtext(parent_elem, "groupId", ns),
                "artifactId": _findtext(parent_elem, "artifactId", ns),
                "version": _findtext(parent_elem, "version", ns),
                "relativePath": _findtext(parent_elem, "relativePath", ns)
            }

        # (3) Organization
        org_elem = _find(root, "organization", ns)
        if org_elem is not None:
            info["organization"] = {
                "name": _findtext(org_elem, "name", ns),
                "url": _findtext(org_elem, "url", ns)
            }

        # (3) Licenses
        for lic in _findall(_find(root, "licenses", ns) or ET.Element("licenses"), "license", ns):
            info["licenses"].append({
                "name": _findtext(lic, "name", ns),
                "url": _findtext(lic, "url", ns),
                "distribution": _findtext(lic, "distribution", ns),
                "comments": _findtext(lic, "comments", ns)
            })

        # (3) SCM
        scm_elem = _find(root, "scm", ns)
        if scm_elem is not None:
            info["scm"] = {
                "connection": _findtext(scm_elem, "connection", ns),
                "developerConnection": _findtext(scm_elem, "developerConnection", ns),
                "url": _findtext(scm_elem, "url", ns),
                "tag": _findtext(scm_elem, "tag", ns)
            }

        # (3) Issue Management
        im_elem = _find(root, "issueManagement", ns)
        if im_elem is not None:
            info["issueManagement"] = {
                "system": _findtext(im_elem, "system", ns),
                "url": _findtext(im_elem, "url", ns)
            }

        # (3) CI Management
        ci_elem = _find(root, "ciManagement", ns)
        if ci_elem is not None:
            info["ciManagement"] = {
                "system": _findtext(ci_elem, "system", ns),
                "url": _findtext(ci_elem, "url", ns)
            }

        # (3) Mailing Lists
        ml_elem = _find(root, "mailingLists", ns)
        for ml in _findall(ml_elem or ET.Element("mailingLists"), "mailingList", ns):
            info["mailingLists"].append({
                "name": _findtext(ml, "name", ns),
                "subscribe": _findtext(ml, "subscribe", ns),
                "unsubscribe": _findtext(ml, "unsubscribe", ns),
                "post": _findtext(ml, "post", ns),
                "archive": _findtext(ml, "archive", ns)
            })

        # (4) Developers
        devs_elem = _find(root, "developers", ns)
        for dev in _findall(devs_elem or ET.Element("developers"), "developer", ns):
            info["developers"].append({
                "id": _findtext(dev, "id", ns),
                "name": _findtext(dev, "name", ns),
                "email": _findtext(dev, "email", ns),
                "organization": _findtext(dev, "organization", ns),
                "organizationUrl": _findtext(dev, "organizationUrl", ns),
                "roles": [r.text for r in _findall(dev, "roles/role", ns)],
                "timezone": _findtext(dev, "timezone", ns)
            })

        # (4) Contributors
        cont_elem = _find(root, "contributors", ns)
        for cont in _findall(cont_elem or ET.Element("contributors"), "contributor", ns):
            info["contributors"].append({
                "name": _findtext(cont, "name", ns),
                "email": _findtext(cont, "email", ns),
                "url": _findtext(cont, "url", ns),
                "organization": _findtext(cont, "organization", ns),
                "organizationUrl": _findtext(cont, "organizationUrl", ns),
                "roles": [r.text for r in _findall(cont, "roles/role", ns)]
            })

        # (5) Properties
        props_elem = _find(root, "properties", ns)
        if props_elem is not None:
            for prop in list(props_elem):
                tag = prop.tag.split("}")[-1]
                info["properties"][tag] = prop.text or ""

        # (5) Modules
        mod_elem = _find(root, "modules", ns)
        if mod_elem is not None:
            info["modules"] = [m.text for m in _findall(mod_elem, "module", ns)]

        # (6) Dependency Management
        dm_elem = _find(root, "dependencyManagement", ns)
        if dm_elem is not None:
            for d in _findall(_find(dm_elem, "dependencies", ns) or ET.Element("dependencies"), "dependency", ns):
                info["dependencyManagement"].append({
                    "groupId": _findtext(d, "groupId", ns),
                    "artifactId": _findtext(d, "artifactId", ns),
                    "version": _findtext(d, "version", ns),
                    "scope": _findtext(d, "scope", ns),
                    "type": _findtext(d, "type", ns),
                    "classifier": _findtext(d, "classifier", ns)
                })

        # (6) Dependencies
        deps_elem = _find(root, "dependencies", ns)
        if deps_elem is not None:
            for d in _findall(deps_elem, "dependency", ns):
                info["dependencies"].append({
                    "groupId": _findtext(d, "groupId", ns),
                    "artifactId": _findtext(d, "artifactId", ns),
                    "version": _findtext(d, "version", ns),
                    "scope": _findtext(d, "scope", ns),
                    "type": _findtext(d, "type", ns),
                    "classifier": _findtext(d, "classifier", ns),
                    "optional": _findtext(d, "optional", ns)
                })

        # (7) Repositories
        repos_elem = _find(root, "repositories", ns)
        repos_container = repos_elem if repos_elem is not None else ET.Element("repositories")
        for r in _findall(repos_container, "repository", ns):
            info["repositories"].append({
                "id": _findtext(r, "id", ns),
                "name": _findtext(r, "name", ns),
                "url": _findtext(r, "url", ns),
                "releases": {
                    "enabled": _findtext(_find(r, "releases", ns) or ET.Element("releases"), "enabled", ns),
                    "updatePolicy": _findtext(_find(r, "releases", ns) or ET.Element("releases"), "updatePolicy", ns)
                },
                "snapshots": {
                    "enabled": _findtext(_find(r, "snapshots", ns) or ET.Element("snapshots"), "enabled", ns),
                    "updatePolicy": _findtext(_find(r, "snapshots", ns) or ET.Element("snapshots"), "updatePolicy", ns)
                }
            })

        # (7) Plugin Repositories
        plrepos_elem = _find(root, "pluginRepositories", ns)
        plrepos_container = plrepos_elem if plrepos_elem is not None else ET.Element("pluginRepositories")
        for pr in _findall(plrepos_container, "pluginRepository", ns):
            info["pluginRepositories"].append({
                "id": _findtext(pr, "id", ns),
                "name": _findtext(pr, "name", ns),
                "url": _findtext(pr, "url", ns),
                "releases": {
                    "enabled": _findtext(_find(pr, "releases", ns) or ET.Element("releases"), "enabled", ns)
                },
                "snapshots": {
                    "enabled": _findtext(_find(pr, "snapshots", ns) or ET.Element("snapshots"), "enabled", ns)
                }
            })

        # (8) Distribution Management
        dist_elem = _find(root, "distributionManagement", ns)
        if dist_elem is not None:
            dist: Dict[str, Any] = {}
            repo_elem = _find(dist_elem, "repository", ns)
            if repo_elem is not None:
                dist["repository"] = {
                    "id": _findtext(repo_elem, "id", ns),
                    "name": _findtext(repo_elem, "name", ns),
                    "url": _findtext(repo_elem, "url", ns),
                    "layout": _findtext(repo_elem, "layout", ns)
                }
            snap_elem = _find(dist_elem, "snapshotRepository", ns)
            if snap_elem is not None:
                dist["snapshotRepository"] = {
                    "id": _findtext(snap_elem, "id", ns),
                    "name": _findtext(snap_elem, "name", ns),
                    "url": _findtext(snap_elem, "url", ns),
                    "layout": _findtext(snap_elem, "layout", ns)
                }
            site_elem = _find(dist_elem, "site", ns)
            if site_elem is not None:
                dist["site"] = {
                    "id": _findtext(site_elem, "id", ns),
                    "name": _findtext(site_elem, "name", ns),
                    "url": _findtext(site_elem, "url", ns)
                }
            dist["directory"] = _findtext(dist_elem, "directory", ns)
            info["distributionManagement"] = dist

        # (9) Build
        build_elem = _find(root, "build", ns)
        if build_elem is not None:
            build: Dict[str, Any] = {
                "sourceDirectory": _findtext(build_elem, "sourceDirectory", ns),
                "testSourceDirectory": _findtext(build_elem, "testSourceDirectory", ns),
                "finalName": _findtext(build_elem, "finalName", ns),
                "defaultGoal": _findtext(build_elem, "defaultGoal", ns),
                "pluginManagement": None,
                "plugins": [],
                "resources": [],
                "testResources": []
            }
            pm_elem = _find(build_elem, "pluginManagement", ns)
            if pm_elem is not None:
                pm_list = []
                for p in _findall(_find(pm_elem, "plugins", ns) or ET.Element("plugins"), "plugin", ns):
                    pm_list.append({
                        "groupId": _findtext(p, "groupId", ns),
                        "artifactId": _findtext(p, "artifactId", ns),
                        "version": _findtext(p, "version", ns),
                        "extensions": _findtext(p, "extensions", ns),
                        "configuration": None,
                        "executions": []
                    })
                build["pluginManagement"] = {"plugins": pm_list}

            plugins_elem = _find(build_elem, "plugins", ns)
            if plugins_elem is not None:
                for p in _findall(plugins_elem, "plugin", ns):
                    build["plugins"].append({
                        "groupId": _findtext(p, "groupId", ns),
                        "artifactId": _findtext(p, "artifactId", ns),
                        "version": _findtext(p, "version", ns),
                        "extensions": _findtext(p, "extensions", ns),
                        "configuration": None,
                        "executions": []
                    })

            res_elem = _find(build_elem, "resources", ns)
            for r in _findall(res_elem or ET.Element("resources"), "resource", ns):
                build["resources"].append({
                    "directory": _findtext(r, "directory", ns),
                    "includes": [inc.text for inc in _findall(r, "includes/include", ns)],
                    "excludes": [exc.text for exc in _findall(r, "excludes/exclude", ns)]
                })

            tres_elem = _find(build_elem, "testResources", ns)
            for tr in _findall(tres_elem or ET.Element("testResources"), "testResource", ns):
                build["testResources"].append({
                    "directory": _findtext(tr, "directory", ns),
                    "includes": [inc.text for inc in _findall(tr, "includes/include", ns)],
                    "excludes": [exc.text for exc in _findall(tr, "excludes/exclude", ns)]
                })

            info["build"] = build

        # (10) Reporting
        rep_elem = _find(root, "reporting", ns)
        if rep_elem is not None:
            reporting: Dict[str, Any] = {
                "excludeDefaults": _findtext(rep_elem, "excludeDefaults", ns),
                "plugins": []
            }
            for p in _findall(_find(rep_elem, "plugins", ns) or ET.Element("plugins"), "plugin", ns):
                plugin: Dict[str, Any] = {
                    "groupId": _findtext(p, "groupId", ns),
                    "artifactId": _findtext(p, "artifactId", ns),
                    "version": _findtext(p, "version", ns),
                    "configuration": {},
                    "dependencies": []
                }
                config_elem = _find(p, "configuration", ns)
                if config_elem is not None:
                    for c in list(config_elem):
                        key = c.tag.split("}")[-1]
                        plugin["configuration"][key] = c.text or ""
                deps_elem2 = _find(p, "dependencies", ns)
                for d in _findall(deps_elem2 or ET.Element("dependencies"), "dependency", ns):
                    plugin["dependencies"].append({
                        "groupId": _findtext(d, "groupId", ns),
                        "artifactId": _findtext(d, "artifactId", ns),
                        "version": _findtext(d, "version", ns),
                        "scope": _findtext(d, "scope", ns)
                    })
                reporting["plugins"].append(plugin)
            info["reporting"] = reporting

        # (11) Profiles
        profiles_elem = _find(root, "profiles", ns)
        for prof in _findall(profiles_elem or ET.Element("profiles"), "profile", ns):
            profile: Dict[str, Any] = {
                "id": _findtext(prof, "id", ns),
                "activation": {},
                "properties": {},
                "dependencyManagement": [],
                "dependencies": [],
                "build": None
            }
            act_elem = _find(prof, "activation", ns)
            if act_elem is not None:
                for act_child in list(act_elem):
                    key = act_child.tag.split("}")[-1]
                    profile["activation"][key] = act_child.text or ""
            props_elem2 = _find(prof, "properties", ns)
            if props_elem2 is not None:
                for prop in list(props_elem2):
                    key = prop.tag.split("}")[-1]
                    profile["properties"][key] = prop.text or ""
            dm_elem2 = _find(prof, "dependencyManagement", ns)
            if dm_elem2 is not None:
                for d in _findall(_find(dm_elem2, "dependencies", ns) or ET.Element("dependencies"), "dependency", ns):
                    profile["dependencyManagement"].append({
                        "groupId": _findtext(d, "groupId", ns),
                        "artifactId": _findtext(d, "artifactId", ns),
                        "version": _findtext(d, "version", ns),
                        "scope": _findtext(d, "scope", ns)
                    })
            deps_elem3 = _find(prof, "dependencies", ns)
            if deps_elem3 is not None:
                for d in _findall(deps_elem3, "dependency", ns):
                    profile["dependencies"].append({
                        "groupId": _findtext(d, "groupId", ns),
                        "artifactId": _findtext(d, "artifactId", ns),
                        "version": _findtext(d, "version", ns),
                        "scope": _findtext(d, "scope", ns)
                    })
            build_elem2 = _find(prof, "build", ns)
            if build_elem2 is not None:
                build_prof = {"plugins": []}
                for p in _findall(_find(build_elem2, "plugins", ns) or ET.Element("plugins"), "plugin", ns):
                    build_prof["plugins"].append({
                        "groupId": _findtext(p, "groupId", ns),
                        "artifactId": _findtext(p, "artifactId", ns),
                        "version": _findtext(p, "version", ns),
                        "configuration": {}
                    })
                profile["build"] = build_prof
            info["profiles"].append(profile)

    except Exception as e:
        print(f"[WARN] Erro ao extrair info do pom.xml em '{project_path.name}': {e}")

    return info


def extract_pom_coords(project_path: Path) -> Tuple[str, str]:
    """
    Extrai apenas <artifactId> e <version> de 'pom.xml'.
    """
    pom_file = project_path / "pom.xml"
    if not pom_file.exists():
        return "", ""
    try:
        tree = ET.parse(pom_file)
        root = tree.getroot()
        ns = _get_namespace(root)
        art = root.findtext("m:artifactId", namespaces=ns) or ""
        ver = root.findtext("m:version", namespaces=ns) or ""
        return art, ver
    except Exception:
        return "", ""

# ───── POPULANDO deploy_config.json ────────────────────────────────────────

def add_project_to_deploy_config(project_path: Path) -> None:
    """
    Insere/atualiza 'project_path' em 'deploy_config.json'.
    """
    cfg = read_deploy_config()
    path_rel = os.path.relpath(project_path, DEPLOY_CONFIG_PATH.parent).replace("\\", "/")

    for p in cfg["projects"]:
        if p.get("name") == project_path.name:
            print(f"[INFO] '{project_path.name}' já existe em deploy_config.json. Pulando.")
            return
        rawpath = p.get("path", "")
        cand = Path(rawpath)
        cand = cand if cand.is_absolute() else (DEPLOY_CONFIG_PATH.parent / rawpath).resolve()
        if cand == project_path.resolve():
            print(f"[INFO] Path correspondente a '{project_path.name}' já existe. Pulando.")
            return

    info = extract_pom_information(project_path)
    new_entry: Dict[str, Any] = {
        "name": project_path.name,
        "path": path_rel,
        "groupId": info.get("groupId", ""),
        "artifactId": info.get("artifactId", project_path.name),
        "version": info.get("version", ""),
        "displayName": info.get("name", project_path.name),
        "description": info.get("description", ""),
        "url": info.get("url", ""),
        "inceptionYear": info.get("inceptionYear", ""),
        "jdk": DEFAULT_JDK,
        "maven": DEFAULT_MAVEN,
        "goal": DEFAULT_GOAL,
        "deploy": DEFAULT_DEPLOY,
        "depends_on": [],
        "dependencies": [
            {
                "groupId": d.get("groupId", ""),
                "artifactId": d.get("artifactId", ""),
                "version": d.get("version", ""),
                "classifier": d.get("classifier", ""),
                "scope": d.get("scope", ""),
                "optional": d.get("optional", "")
            } for d in info.get("dependencies", [])
        ],
        "developers": info.get("developers", []),
        "contributors": info.get("contributors", []),
        "parent": info.get("parent", None),
        "organization": info.get("organization", None),
        "licenses": info.get("licenses", []),
        "scm": info.get("scm", None),
        "issueManagement": info.get("issueManagement", None),
        "ciManagement": info.get("ciManagement", None),
        "mailingLists": info.get("mailingLists", []),
        "modules": info.get("modules", []),
        "properties": info.get("properties", {}),
        "dependencyManagement": info.get("dependencyManagement", []),
        "repositories": info.get("repositories", []),
        "pluginRepositories": info.get("pluginRepositories", []),
        "distributionManagement": info.get("distributionManagement", None),
        "build": info.get("build", None),
        "reporting": info.get("reporting", None),
        "profiles": info.get("profiles", [])
    }

    cfg["projects"].append(new_entry)
    write_deploy_config(cfg)
    print(f"[OK] Inserido '{project_path.name}' em deploy_config.json.")

def populate_deploy_config_if_missing() -> None:
    """
    Se 'deploy_config.json' estiver vazio, popula a partir de todos subdiretórios em PROJECTS_DIR.
    """
    cfg = read_deploy_config()
    if not cfg.get("projects"):
        print(f"[INFO] deploy_config.json vazio. Populando de '{PROJECTS_DIR.name}'.")
        ensure_dir(PROJECTS_DIR)
        for sub in sorted(PROJECTS_DIR.iterdir()):
            if sub.is_dir() and (sub / "pom.xml").exists():
                add_project_to_deploy_config(sub)

# ───── SINCRONIZAÇÃO DE PARENT ──────────────────────────────────────────────

def sync_deploy_parent_with_parent_config() -> None:
    """
    Sincroniza todos projetos cujo parent.groupId == ORG_ID, atualizando artifactId e versão.
    """
    cfg = read_deploy_config()
    parent_cfg = load_parent_config()
    if not cfg.get("projects"):
        print("[WARN] deploy_config.json vazio ou não encontrado.")
        return
    if not parent_cfg.get("artifactId") or not parent_cfg.get("version"):
        print("[WARN] parent_config.json vazio ou não encontrado.")
        return

    parent_artifact = parent_cfg["artifactId"]
    parent_version = parent_cfg["version"]

    print(f"[INFO] Sincronizando parent.artifactId={parent_artifact}, parent.version={parent_version}")

    changed = False
    updated = []
    for proj in cfg["projects"]:
        pg = proj.get("parent") or {}
        if pg.get("groupId") == ORG_ID:
            if pg.get("artifactId") != parent_artifact or pg.get("version") != parent_version:
                old_art, old_ver = pg.get("artifactId"), pg.get("version")
                proj["parent"]["artifactId"] = parent_artifact
                proj["parent"]["version"] = parent_version
                updated.append((proj["name"], old_art, old_ver))
                changed = True

    if changed:
        write_deploy_config(cfg)
        print("[OK] deploy_config.json atualizado:")
        for name, old_art, old_ver in updated:
            print(f"  - {name}: {old_art}@{old_ver} → {parent_artifact}@{parent_version}")
    else:
        print("[INFO] Nenhuma alteração: todos os parents já atualizados.")

# ───── LÓGICA DE GERAÇÃO DE POM ─────────────────────────────────────────────

def generate_pom(
    project_path: Path,
    template_name: str = TEMPLATE_NAME,
    output_name: str   = "pom.xml"
) -> Path:
    """
    Renderiza pom.xml dentro de project_path, usando Jinja2 e campos de deploy_config.json.
    Aplica as seguintes regras:

      🛑 Regra 1 (Parent do próprio ORG_ID):
      Se o projeto já tiver um <parent> cujo parent.groupId == ORG_ID,
      então o POM deve referenciar UMA VERSÃO ANTERIOR ao último parent_config.json.
      O parent_config.json permanece com a versão “última”.

      ✅ Regra 2 (Parent de outro groupId):
      Se o projeto tiver um <parent> cujo groupId != ORG_ID,
      manter esse parent exatamente como está,
      porém inserir blocos de <repositories>, <pluginRepositories> e <distributionManagement>
      vindos de bom_config.json.

      ✅ Regra 3 (Sem parent):
      Se o projeto NÃO tiver <parent>, ler o parent_config.json para obter (artifactId, version),
      criar um <parent> com uma versão anterior (i.e., decrementada) para o POM,
      sem inserir blocos de <repositories>, <pluginRepositories> ou <distributionManagement>.

      ⚠️ Regra 4 (Sem parent, mas POM original já tinha repositórios/plugins/distribution):
      Se o projeto NÃO tiver <parent> e o POM original contiver alguma tag:
        <repositories>, <pluginRepositories> ou <distributionManagement>,
      então ao gerar o novo POM:
        • Inserir o nosso <parent> com ORG_ID e versão anterior (de parent_config.json).
        • REMOVER quaisquer blocos de <repositories>, <pluginRepositories> e <distributionManagement>
          vindos do POM original.
      Essa regra garante que, se já existiam repositórios ou distribuição no POM original,
      não sejam propagados ao novo POM e usemos apenas o nosso <parent>.
    """

    # 1) Sincronizar parents e garantir deploy_config preenchido
    sync_deploy_parent_with_parent_config()
    populate_deploy_config_if_missing()

    # 2) Carregar deploy_config.json e encontrar a entrada deste projeto
    cfg = read_deploy_config()
    entry = None
    base_cfg_dir = DEPLOY_CONFIG_PATH.parent

    for p in cfg.get("projects", []):
        if p.get("name") == project_path.name:
            entry = p
            break
        rawpath = p.get("path", "")
        cand = Path(rawpath)
        cand = cand if cand.is_absolute() else (base_cfg_dir / rawpath).resolve()
        if cand == project_path.resolve():
            entry = p
            break

    if entry is None:
        # Caso não exista, extrair coords mínimas e adicionar
        art, ver = extract_pom_coords(project_path)
        rel = os.path.relpath(project_path, DEPLOY_CONFIG_PATH.parent).replace("\\", "/")
        minimal = {
            "name": project_path.name,
            "path": rel,
            "jdk": DEFAULT_JDK,
            "maven": DEFAULT_MAVEN,
            "goal": DEFAULT_GOAL,
            "deploy": True,
            "version": ver or ""
        }
        cfg["projects"].append(minimal)
        write_deploy_config(cfg)
        cfg = read_deploy_config()
        entry = next((p for p in cfg["projects"] if p["name"] == project_path.name), None)
        if entry is None:
            print(f"[ERR] Não foi possível criar entrada para '{project_path.name}'")
            sys.exit(1)

    # 3) Extrair informações do POM original
    info_do_pom     = extract_pom_information(project_path)
    orig_parent     = info_do_pom.get("parent")
    orig_repos      = info_do_pom.get("repositories", [])
    orig_plugin_repos = info_do_pom.get("pluginRepositories", [])
    orig_dist       = info_do_pom.get("distributionManagement")

    # 4) Coordenadas básicas
    GROUP_ID    = ORG_ID or info_do_pom.get("groupId", "")
    ARTIFACT_ID = entry.get("artifactId", entry["name"])
    VERSION     = entry.get("version", "")
    NAME        = entry.get("displayName", entry["name"])
    DEVS        = entry.get("developers", [])

    # 5) Montar lista de dependencies incluindo depends_on
    dependencies: List[Dict[str, Any]] = []
    for dep_name in entry.get("depends_on", []):
        dep_entry = next((pp for pp in cfg["projects"] if pp["name"] == dep_name), None)
        if dep_entry:
            dependencies.append({
                "groupId": ORG_ID,
                "artifactId": dep_entry["name"],
                "version": dep_entry["version"],
                "classifier": "mule-plugin"
            })
    for d in entry.get("dependencies", []):
        dep = {
            "groupId": d.get("groupId", ""),
            "artifactId": d.get("artifactId", ""),
            "version": d.get("version", "")
        }
        if d.get("classifier"):
            dep["classifier"] = d.get("classifier")
        if d.get("scope"):
            dep["scope"] = d.get("scope")
        if d.get("optional"):
            dep["optional"] = d.get("optional")
        dependencies.append(dep)

    # 6) Carregar configurações do bom_config.json
    bom_repos, bom_plugin_repos, bom_dist = load_bom_settings(BOM_CONFIG_PATH)

    # 7) Carregar parent_config.json
    parent_cfg          = load_parent_config()
    parent_artifact_cfg = parent_cfg.get("artifactId", "")
    parent_version_cfg  = parent_cfg.get("version", "")

    # 8) Decidir parent e blocos de repositório
    if orig_parent and orig_parent.get("groupId") == ORG_ID:
        # ─── Regra 1: Parent já é nosso, MAS o POM deve referenciar versão anterior.
        parent_version_anterior = decrement_version(parent_version_cfg)
        parent = {
            "groupId":    ORG_ID,
            "artifactId": parent_artifact_cfg,
            "version":    parent_version_anterior
        }
        # Não inserimos blocos BOM nem preservamos quaisquer repositórios originais
        repositories            = []
        pluginRepositories     = []
        distributionManagement = None

    elif orig_parent:
        # ─── Regra 2: Parent externo. Mantém original e insere blocos BOM
        parent = {
            "groupId":    orig_parent.get("groupId", ""),
            "artifactId": orig_parent.get("artifactId", ""),
            "version":    orig_parent.get("version", "")
        }
        repositories            = bom_repos
        pluginRepositories     = bom_plugin_repos
        distributionManagement = bom_dist

    else:
        # ─── Sem parent no POM original
        # Checar Regra 4: se existiam repositórios, plugins ou distribuição no POM original
        havia_repos_tags = bool(orig_repos or orig_plugin_repos or orig_dist)
        if havia_repos_tags:
            # ─── Regra 4:
            #   • Criar parent com versão anterior
            parent_version_anterior = decrement_version(parent_version_cfg)
            parent = {
                "groupId":    ORG_ID,
                "artifactId": parent_artifact_cfg,
                "version":    parent_version_anterior
            }
            #   • REMOVER quaisquer blocos de repositórios/plugins/distribution do POM
            repositories            = []
            pluginRepositories     = []
            distributionManagement = None
            #   • Registrar parent no JSON, mas mantendo parent_config.json com última versão
            entry["parent"] = {
                "groupId":    ORG_ID,
                "artifactId": parent_artifact_cfg,
                "version":    parent_version_cfg
            }
            write_deploy_config(cfg)
        else:
            # ─── Regra 3 (sem parent e sem tags originais):
            #   • Criar parent com versão anterior
            parent_version_anterior = decrement_version(parent_version_cfg)
            parent = {
                "groupId":    ORG_ID,
                "artifactId": parent_artifact_cfg,
                "version":    parent_version_anterior
            }
            #   • Não inserir blocos BOM e não preservar tags originais
            repositories            = []
            pluginRepositories     = []
            distributionManagement = None
            #   • Registrar parent no JSON, mas mantendo parent_config.json com última versão
            entry["parent"] = {
                "groupId":    ORG_ID,
                "artifactId": parent_artifact_cfg,
                "version":    parent_version_cfg
            }
            write_deploy_config(cfg)

    # 9) Montar contexto Jinja2
    context: Dict[str, Any] = {
        "groupId":              GROUP_ID,
        "artifactId":           ARTIFACT_ID,
        "version":              VERSION,
        "name":                 NAME,
        "developers":           DEVS,
        "dependencies":         dependencies,
        "parent":               parent,
        "properties":           entry.get("properties", {}),
        "dependencyManagement": entry.get("dependencyManagement", []),
        "repositories":         repositories,
        "pluginRepositories":   pluginRepositories,
        "distributionManagement": distributionManagement,
        "build":                entry.get("build", {}),
        "reporting":            entry.get("reporting", {}),
        "profiles":             entry.get("profiles", [])
    }

    # 10) Renderizar template e escrever pom.xml
    if not TEMPLATE_DIR.exists():
        print(f"[ERR] Templates não encontrados: {TEMPLATE_DIR}")
        sys.exit(1)

    env = Environment(
        loader=FileSystemLoader(searchpath=str(TEMPLATE_DIR)),
        autoescape=False,
        trim_blocks=True,
        lstrip_blocks=True
    )
    try:
        template = env.get_template(template_name)
    except Exception as e:
        print(f"[ERR] Template não encontrado: {e}")
        sys.exit(1)

    raw = template.render(**context).strip()
    nice = prettify_xml(raw, indent=2)
    target_pom = project_path / output_name
    target_pom.write_text(nice, encoding="utf-8")
    print(f"[OK] '{output_name}' gerado em '{project_path.name}'")

    return DEPLOY_CONFIG_PATH


# ───── WATCHER: DETEÇÃO DE NOVOS PROJETOS ────────────────────────────────────

class NewProjectHandler(FileSystemEventHandler):
    """
    Sempre que um diretório for criado diretamente dentro de PROJECTS_DIR,
    dispara a rotina de inserção no JSON e geração de POM.
    """
    def on_created(self, event):
        p = Path(event.src_path)
        if p.is_dir() and p.parent.resolve() == PROJECTS_DIR.resolve():
            project_name = p.name
            print(f"\n[EVENT] Nova pasta detectada: '{project_name}'")
            thread = threading.Thread(target=self.process_new_project, args=(p,), daemon=True)
            thread.start()

    def process_new_project(self, project_path: Path):
        time.sleep(2.0)  # aguarda possível escrita inicial
        print(f"[INFO] Processando projeto novo em: {project_path.name}")
        add_project_to_deploy_config(project_path)
        generate_pom(project_path)
        print(
            f"[DONE] Projeto '{project_path.name}' totalmente configurado.\n"
            f"       - deploy_config.json atualizado\n"
            f"       - pom.xml gerado em '{project_path.name}'"
        )

def start_watcher() -> None:
    """
    Inicia o Observer para monitorar a pasta PROJECTS_DIR.
    """
    ensure_dir(PROJECTS_DIR)
    handler = NewProjectHandler()
    observer = Observer()
    observer.schedule(handler, str(PROJECTS_DIR), recursive=False)
    observer.daemon = True
    observer.start()
    print(f"[INFO] Watcher iniciado em: {PROJECTS_DIR.resolve()}")

# ───── INTERFACE DE USUÁRIO ─────────────────────────────────────────────────

def banner() -> None:
    os.system("cls" if os.name == "nt" else "clear")
    print("\n" + "═" * 80)
    print(f"{'UNIVERSAL JAVA & MAVEN MULTI-BUILD TOOL v2.0'.center(80)}")
    print("═" * 80 + "\n")
    print(f"🔑 EXCHANGE_USER = {os.environ.get('EXCHANGE_USER','[não definido]')}")
    print(f"🔑 EXCHANGE_PASS = {os.environ.get('EXCHANGE_PASS','[não definido]')}")
    print(f"🏷️  ORG_ID       = {ORG_ID or '[não definido]'}")
    print("─" * 80)
    print(f"📂 Diretório projetos: {PROJECTS_DIR.resolve()}")
    print("─" * 80)

def menu_generic(title: str, options: Dict[str, Dict[str, str]]) -> Optional[str]:
    """
    Gera um menu genérico com 'options': {key: {"desc": descrição}}.
    Retorna a key selecionada.
    """
    keys = list(options.keys())
    descs = [options[k]["desc"] for k in keys]
    lines = [f"[{i+1:>2}] {descs[i]}" for i in range(len(keys))]
    max_line = max(len(line) for line in lines)
    width = max(len(title), max_line) + 4
    top    = f"╔{'═'*width}╗"
    head   = f"║{title.center(width)}║"
    sep    = f"╠{'═'*width}╣"
    bottom = f"╚{'═'*width}╝"

    while True:
        os.system("cls" if os.name == "nt" else "clear")
        print(f"{top}")
        print(f"{head}")
        print(f"{sep}")
        for line in lines:
            print(f"║ {line.ljust(width-2)} ║")
        print(f"{bottom}")
        choice = input(f"\n👉 Escolha: ").strip().lower()
        if choice.isdigit():
            idx = int(choice)
            if 1 <= idx <= len(keys):
                return keys[idx-1]
        if choice in ["s", "exit"] and "exit" in keys:
            return "exit"
        print(f"\n❌ Opção inválida. Tente novamente.")
        time.sleep(1)

def menu_principal() -> str:
    options = {
        "build_all": {"desc": "🚀 Build TODOS os projetos"},
        "list_projects": {"desc": "📂 Listar projetos (filtrar/paginar)"},
        "build_bom_parent": {"desc": "🛠️ Build BOM & Parent POM"},
        "auto_commit": {"desc": "🔄 Commit automático no Git"},
        "exit": {"desc": "Sair"}
    }
    return menu_generic("MENU PRINCIPAL", options) or "exit"

def ask_list_projects() -> Optional[Path]:
    """
    Pergunta um filtro; retorna projeto selecionado ou None.
    """
    print(f"\nℹ️ Digite parte do nome do projeto (ou Enter para listar todos):")
    termo = input(f"👉 Filtro: ").strip().lower()
    candidates = sorted([p.name for p in PROJECTS_DIR.iterdir() if p.is_dir() and p.name != "mule-parent-bom-main"])
    if termo:
        filtrados = [name for name in candidates if termo in name.lower()]
        if not filtrados:
            print(f"❌ Nenhum projeto encontrado para '{termo}'.")
            time.sleep(1)
            return None
        return submenu_selecao_dinamica(f"RESULTADOS ({len(filtrados)})", filtrados, filtrados)
    else:
        return submenu_paginacao_lista(candidates)

def submenu_selecao_dinamica(title: str, keys: List[str], descs: List[str]) -> Optional[Path]:
    """
    Mostra uma lista simples de escolhas sem paginação.
    """
    max_line = max(len(f"[{i+1:>3}] {descs[i]}") for i in range(len(keys)))
    width = max(len(title), max_line) + 4
    top    = f"╔{'═'*width}╗"
    head   = f"║{title.center(width)}║"
    sep    = f"╠{'═'*width}╣"
    bottom = f"╚{'═'*width}╝"

    while True:
        os.system("cls" if os.name == "nt" else "clear")
        print(f"{top}")
        print(f"{head}")
        print(f"{sep}")
        for i, desc in enumerate(descs, start=1):
            line = f"[{i:>3}] {desc}"
            print(f"║ {line.ljust(width-2)} ║")
        print(f"{bottom}")
        choice = input(f"\n👉 Número (0 p/ voltar): ").strip()
        if choice == "0":
            return None
        if choice.isdigit():
            idx = int(choice)
            if 1 <= idx <= len(keys):
                return (PROJECTS_DIR / keys[idx-1]).resolve()
        print(f"❌ Opção inválida. Tente novamente.")
        time.sleep(1)

def submenu_paginacao_lista(lista: List[str], page_size: int = 20) -> Optional[Path]:
    """
    Pagina uma lista de strings, retornando Path do projeto ou None.
    """
    total = len(lista)
    pages = (total + page_size - 1) // page_size
    page = 1
    while True:
        start = (page - 1) * page_size
        end   = min(start + page_size, total)
        subset = lista[start:end]
        os.system("cls" if os.name == "nt" else "clear")
        header = f"LISTA DE PROJETOS (Página {page}/{pages}) — {start+1}-{end} de {total}"
        print("═" * 80)
        print(header.center(80))
        print("═" * 80)
        for idx, name in enumerate(subset, start=start+1):
            print(f"[{idx:>3}] {name}")
        print("═" * 80)
        print(f"[N] Próxima página   [P] Página anterior   [0] Voltar")
        choice = input(f"👉 Digite número (N/P/0): ").strip().lower()
        if choice == "n" and page < pages:
            page += 1
            continue
        if choice == "p" and page > 1:
            page -= 1
            continue
        if choice == "0":
            return None
        if choice.isdigit():
            idx = int(choice)
            if 1 <= idx <= total:
                return (PROJECTS_DIR / lista[idx - 1]).resolve()
        print(f"❌ Opção inválida. Tente novamente.")
        time.sleep(1)

def submenu_build_bom_parent() -> Optional[List[str]]:
    """
    Menu para seleção de fases de build: retorna lista de goals.
    """
    ops = {
        "1":  "clean",
        "2":  "clean validate",
        "3":  "validate",
        "4":  "clean compile",
        "5":  "compile",
        "6":  "clean test",
        "7":  "test",
        "8":  "clean package",
        "9":  "package",
        "10": "clean verify",
        "11": "verify",
        "12": "clean install",
        "13": "install",
        "14": "clean deploy",
        "15": "deploy",
    }
    title = "BUILD BOM & PARENT POM"
    lines = [f"[{k.rjust(2,'0')}] {ops[k]}" for k in sorted(ops, key=lambda x: int(x))]
    max_line = max(len(line) for line in lines)
    width    = max(len(title), max_line) + 4
    top      = f"╔{'═'*width}╗"
    head     = f"║{title.center(width)}║"
    sep      = f"╠{'═'*width}╣"
    bottom   = f"╚{'═'*width}╝"

    while True:
        os.system("cls" if os.name == "nt" else "clear")
        print(f"{top}")
        print(f"{head}")
        print(f"{sep}")
        for line in lines:
            print(f"║ {line.ljust(width-2)} ║")
        print(f"{bottom}")
        choice = input(f"\n👉 Número (I=Início, S=Sair): ").strip().lower()
        if choice == "i":
            return None
        if choice == "s":
            print(f"👋 Até mais!")
            sys.exit(0)
        if choice.isdigit() and choice in ops:
            phase = ops[choice].split() + ["-DskipTests"]
            print(f"✔ Selecionado: {' '.join(phase)}")
            time.sleep(0.4)
            return phase
        print(f"\n❌ Opção inválida. Tente novamente.")
        time.sleep(1)

def submenu_auto_commit() -> None:
    """
    Realiza commit automático de todos arquivos no repositório.
    """
    os.system("cls" if os.name == "nt" else "clear")
    print("═" * 80)
    print(f"{'COMMIT AUTOMÁTICO NO GIT'.center(80)}")
    print("═" * 80)
    repo_dir = WORKSPACE_ROOT
    print(f"🗂️  Diretório do Git: {repo_dir}")
    msg = input(f"✏️  Mensagem de commit (Enter para '[auto] build'): ").strip()
    if not msg:
        msg = "[auto] build"
    try:
        subprocess.run(["git", "-C", str(repo_dir), "add", "."], check=True)
        subprocess.run(["git", "-C", str(repo_dir), "commit", "-m", msg], check=True)
        subprocess.run(["git", "-C", str(repo_dir), "push"], check=True)
        print(f"✅ Commit e push bem-sucedidos!")
    except subprocess.CalledProcessError as e:
        print(f"❌ Erro no Git: {e}")
    input("\nPressione Enter para voltar ao Menu Principal...")

# ───── ORDENAÇÃO DE BUILD ─────────────────────────────────────────────────

def resolve_build_order(cfg: Dict[str, Any]) -> List[Dict[str, Any]]:
    """
    Ordena projetos por dependências internas (topological sort).
    """
    projects = {p["name"]: p for p in cfg.get("projects", [])}
    resolved: List[Dict[str, Any]] = []
    visited = set()

    def visit(name: str):
        if name in visited:
            return
        project = projects.get(name)
        if not project:
            print(f"❌ Projeto '{name}' não encontrado no config!")
            sys.exit(1)
        for dep in project.get("depends_on", []):
            visit(dep)
        visited.add(name)
        resolved.append(project)

    for name in projects:
        visit(name)
    return resolved

# ───── EXIBE CREDENCIAIS (effective-settings) ───────────────────────────

def show_effective(settings: Path, mvn_exec: Path, env: Dict[str, str]) -> None:
    """
    Executa 'mvn help:effective-settings' e exibe credenciais (servers) resolvidas.
    """
    ensure_dir(settings.parent)
    effective = settings.parent / "effective-settings.xml"
    cmd = [
        str(mvn_exec),
        "-s", str(settings),
        "help:effective-settings",
        "-q",
        f"-Doutput={effective}"
    ]
    subprocess.run(cmd, env=env, cwd=Path.cwd(), stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
    time.sleep(0.2)
    if effective.exists():
        root = ET.parse(effective).getroot()
        srv  = root.find("servers")
        if srv is not None:
            print(f"\n──── CREDENCIAIS RESOLVIDAS ────")
            for s in srv.findall("server"):
                user = s.findtext("username")
                pwd  = s.findtext("password")[:6] + "…"
                print(f"  {s.findtext('id')}: {user}/{pwd}")
            print("─" * 40 + "\n")
        try:
            effective.unlink()
        except Exception:
            pass

# ───── PREPARAÇÃO DE FERRAMENTAS (JDK/MAVEN) ───────────────────────────────

def prepare_tool(url: str, base: Path) -> Path:
    """
    Baixa e descompacta ZIP de 'url' em 'base'. Retorna pasta raiz do ZIP.
    """
    ensure_dir(base)
    zip_path = base / Path(url).name
    if not zip_path.exists():
        print(f"⬇️  Baixando {zip_path.name}")
        with requests.get(url, stream=True) as r, open(zip_path, "wb") as f:
            for chunk in r.iter_content(8192):
                f.write(chunk)
    with zipfile.ZipFile(zip_path) as z:
        z.extractall(base)
        members = {Path(n).parts[0] for n in z.namelist() if "/" in n}
    return (base / next(iter(members))).resolve()

# ───── LOOP DE BUILD ──────────────────────────────────────────────────────

def build_loop(
    project: Path,
    jdk: Optional[Path],
    mvn: Path,
    settings: Path,
    config_path: Path,
    build_phase: List[str]
) -> bool:
    """
    Executa 'mvn [build_phase]' dentro de 'project' usando 'jdk', 'mvn', 'settings'.
    Ao término, faz bump de versão se for deploy.
    """
    env = os.environ.copy()
    # configura JAVA_HOME e PATH
    if jdk and jdk.exists():
        env["JAVA_HOME"] = str(jdk)
        env["PATH"] = f"{mvn/'bin'};{jdk/'bin'};{env.get('PATH','')}"
        env["M2_HOME"] = str(mvn)
        # ajusta opções para Java 17+
        ver_num = re.search(r"\d+", jdk.name)
        if ver_num and int(ver_num.group()) >= 17:
            env["MAVEN_OPTS"] = ""
    else:
        env["M2_HOME"] = str(mvn)
        env["PATH"] = f"{mvn/'bin'};{env.get('PATH','')}"

    mvn_exec = mvn / "bin" / ("mvn.cmd" if os.name == "nt" else "mvn")
    show_effective(settings, mvn_exec, env)

    cmd = [str(mvn_exec), "-s", str(settings)] + build_phase
    print(f"\n🚀 Executando: {' '.join(cmd)}")
    proc = subprocess.Popen(
        cmd,
        cwd=project,
        env=env,
        shell=False,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        text=True
    )

    spinner = itertools.cycle(["|", "/", "-", "\\"])
    log_buf: List[str] = []

    while True:
        line = proc.stdout.readline()
        if line:
            sys.stdout.write(line)
            sys.stdout.flush()
            log_buf.append(line)
        else:
            if proc.poll() is not None:
                break
            sys.stdout.write(f"\r{next(spinner)} Aguarde...")
            sys.stdout.flush()
            time.sleep(0.1)

    remaining = proc.stdout.read()
    if remaining:
        sys.stdout.write(remaining)
        log_buf.append(remaining)

    timestamp = time.strftime("%Y%m%d_%H%M%S")
    log_name = f"{project.name}_{timestamp}.log"
    (LOG_DIR / log_name).write_text("".join(log_buf), encoding="utf-8")

    if proc.returncode == 0:
        print(f"✅ Build OK!")
        new_ver = bump_version(project / "pom.xml", config_path, build_phase)
        if new_ver:
            print(f"✔ Versão atualizada em {config_path.name}: {new_ver}")
        return True
    else:
        print(f"❌ Build falhou (code {proc.returncode})")
        return False

# ───── BUMP DE VERSÃO ──────────────────────────────────────────────────────

def bump_version(pom: Path, config_path: Path, build_phase: List[str]) -> Optional[str]:
    """
    Se "deploy" em build_phase:
      • Se config_path for bom_config.json ou parent_config.json: incrementa apenas version.
      • Caso contrário: incrementa última parte numérica da versão no projeto em deploy_config.json.
    """
    if "deploy" not in build_phase:
        return None

    cfg_data = read_json(config_path, {})
    cfg_name = config_path.name

    # Caso BOM ou Parent
    if cfg_name in ("bom_config.json", "parent_config.json"):
        old_ver = cfg_data.get("version", "")
        if not old_ver:
            return None
        new_ver = increment_version(old_ver)
        cfg_data["version"] = new_ver
        write_json(config_path, cfg_data)
        print(f"✔ bump '{cfg_name}': {old_ver} → {new_ver}")
        return new_ver

    # Caso deploy_config.json
    dc = read_deploy_config()
    proj_dir = pom.parent.resolve()
    entry = None
    for p in dc["projects"]:
        raw = p.get("path", "")
        cand = Path(raw) if Path(raw).is_absolute() else (DEPLOY_CONFIG_PATH.parent / raw).resolve()
        if cand == proj_dir:
            entry = p
            break
    if not entry:
        entry = next((p for p in dc["projects"] if p.get("name") == proj_dir.name), None)
    if not entry or not entry.get("version"):
        valid = [p.get("name") for p in dc["projects"]]
        print(f"❗️ Projeto '{proj_dir.name}' não encontrado! Válidos: {valid}")
        return None

    old_ver = entry["version"]
    new_ver = increment_version(old_ver)
    entry["version"] = new_ver
    write_deploy_config(dc)
    print(f"✔ bump project '{proj_dir.name}': {old_ver} → {new_ver}")
    return new_ver

# ───── BUILD TODOS PROJETOS ───────────────────────────────────────────────

def build_all_projects(config_path: Path) -> None:
    """
    Lê 'deploy_config.json', pergunta fase de build, e executa builds na ordem correta.
    """
    cfg = read_json(config_path, {})
    if not cfg.get("projects"):
        print(f"❗️ Nenhum projeto encontrado em '{config_path}'")
        return

    build_phase = submenu_build_bom_parent()
    if not build_phase:
        print(f"⚠️ Nenhuma fase selecionada. Abortando...")
        return

    ordered = resolve_build_order(cfg)
    for proj in ordered:
        if not proj.get("deploy", True):
            print(f"🚫 Projeto '{proj['name']}' deploy: false. Pulando...")
            continue

        print(f"\n🔨 Build do projeto: {proj['name']}")
        project_dir = (PROJECTS_DIR / proj["name"]).resolve()
        if not (project_dir / "pom.xml").exists():
            generate_pom(project_dir)

        jdk_key = proj.get("jdk", DEFAULT_JDK)
        maven_key = proj.get("maven", DEFAULT_MAVEN)

        jdk_dir = prepare_tool(JDKS[jdk_key]["url"], JDK_DIR) if jdk_key in JDKS else None
        mvn_dir = prepare_tool(MAVENS[maven_key]["url"], MAVEN_DIR) if maven_key in MAVENS else None

        settings_xml = Path.cwd() / ".maven" / "settings.xml"
        ensure_dir(settings_xml.parent)
        settings_xml.touch(exist_ok=True)

        success = build_loop(project_dir, jdk_dir, mvn_dir, settings_xml, config_path, build_phase)
        if not success:
            print(f"⚠️ Build falhou em '{proj['name']}'. Sequência interrompida.")
            break

    print(f"\n🎉 Todos os projetos foram processados!")

# ───── MAIN ────────────────────────────────────────────────────────────────

if __name__ == "__main__":
    # 1) Sincroniza parents e popula deploy_config.json se vazio
    sync_deploy_parent_with_parent_config()
    populate_deploy_config_if_missing()

    # 2) Inicia watcher em background
    start_watcher()

    # 3) Inicia loop de menu interativo
    while True:
        banner()
        escolha = menu_principal()
        if escolha == "exit":
            print(f"👋 Até mais!")
            sys.exit(0)

        if escolha == "build_all":
            populate_deploy_config_if_missing()
            build_all_projects(DEPLOY_CONFIG_PATH)
            input("\nPressione Enter para voltar ao Menu Principal...")

        elif escolha == "list_projects":
            selecionado = ask_list_projects()
            if selecionado:
                config_path = generate_pom(selecionado)
                jdk_key = menu_generic("SELECIONE JDK", {k: {"desc": k} for k in JDKS})
                if not jdk_key:
                    continue
                jdk_dir = prepare_tool(JDKS[jdk_key]["url"], JDK_DIR)
                mvn_key = menu_generic("SELECIONE MAVEN", {k: {"desc": k} for k in MAVENS})
                if not mvn_key:
                    continue
                mvn_dir = prepare_tool(MAVENS[mvn_key]["url"], MAVEN_DIR)
                settings_xml = Path.cwd() / ".maven" / "settings.xml"
                ensure_dir(settings_xml.parent)
                settings_xml.touch(exist_ok=True)
                phase = submenu_build_bom_parent()
                if phase:
                    build_loop(selecionado, jdk_dir, mvn_dir, settings_xml, config_path, phase)
                input("\nPressione Enter para voltar ao Menu Principal...")

        elif escolha == "build_bom_parent":
            selecionado = PROJECTS_DIR / "mule-parent-bom-main"
            if not selecionado.exists():
                print(f"❌ 'mule-parent-bom-main' não encontrado em 'projects'.")
                time.sleep(1)
                continue

            # Executa scripts externos para gerar BOM e Parent
            bom_script = BUILD_DIR / "bom_config.py"
            if bom_script.exists():
                print(f"🔄 Executando bom_config.py...")
                subprocess.run([
                    sys.executable,
                    str(bom_script),
                    "--json", str(BOM_CONFIG_PATH),
                    "--tpl",  str(BUILD_DIR / "templates" / "bom_template.xml"),
                    "--out",  str(BUILD_DIR / "bom" / "pom.xml")
                ], check=True)
            else:
                print(f"⚠️ 'bom_config.py' não encontrado: {bom_script}")
                time.sleep(1)
                continue

            parent_script = BUILD_DIR / "parent_config.py"
            if parent_script.exists():
                print(f"🔄 Executando parent_config.py...")
                subprocess.run([
                    sys.executable,
                    str(parent_script),
                    "--json", str(PARENT_CONFIG_PATH),
                    "--tpl",  str(BUILD_DIR / "templates" / "parent_template.xml"),
                    "--out",  str(BUILD_DIR / "parent-pom" / "pom.xml")
                ], check=True)
            else:
                print(f"⚠️ 'parent_config.py' não encontrado: {parent_script}")
                time.sleep(1)
                continue

            mvn_key = menu_generic("SELECIONE MAVEN", {k: {"desc": k} for k in MAVENS})
            if not mvn_key:
                continue
            mvn_dir = prepare_tool(MAVENS[mvn_key]["url"], MAVEN_DIR)

            phase = submenu_build_bom_parent()
            if not phase:
                continue

            settings_xml = Path.cwd() / ".maven" / "settings.xml"
            ensure_dir(settings_xml.parent)
            settings_xml.touch(exist_ok=True)

            parent_dir = selecionado / "parent-pom"
            bom_dir    = selecionado / "bom"
            build_loop(parent_dir, None, mvn_dir, settings_xml, PARENT_CONFIG_PATH, phase)
            build_loop(bom_dir,    None, mvn_dir, settings_xml, BOM_CONFIG_PATH, phase)

            input("\nPressione Enter para voltar ao Menu Principal...")

        elif escolha == "auto_commit":
            submenu_auto_commit()

        else:
            continue
