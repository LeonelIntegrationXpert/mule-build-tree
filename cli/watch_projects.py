#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
watch_projects.py

Este script "escuta" continuamente a pasta `projects/`. Sempre que um novo
diretório de projeto for criado em `projects/`, ele:
  1) Lê (ou inicializa) o deploy_config.json.
  2) Lê o pom.xml (para extrair <groupId>, <artifactId>, <version>, <dependencies>, <developers>, etc.)
  3) Insere uma nova entrada em build/configs/deploy_config.json com todos os campos extraídos ou padrões
  4) Executa o generate_pom_from_template() para criar/sobrescrever o pom.xml naquele novo projeto,
     usando o template em build/templates e incorporando lógica de parent e repositórios conforme regras.
"""
from build.imports import *  # noqa: F403, F401
import json
import os
import time
import threading
import re
import xml.etree.ElementTree as ET
from pathlib import Path
from watchdog.observers import Observer
from watchdog.events import FileSystemEventHandler
from jinja2 import Environment, FileSystemLoader
from xml.dom import minidom

# ───── CONFIGURAÇÕES FIXAS ─────
ROOT_DIR = Path(__file__).parent.parent.resolve()
PROJECTS_DIR = ROOT_DIR / "projects"
# Caminhos absolutos para configs
DEPLOY_CONFIG_PATH = ROOT_DIR / "build" / "configs" / "deploy_config.json"
PARENT_CONFIG_PATH = ROOT_DIR / "build" / "configs" / "parent_config.json"
BOM_CONFIG_PATH = ROOT_DIR / "build" / "configs" / "bom_config.json"
TEMPLATE_DIR = ROOT_DIR / "build" / "templates" / "pom_templates"
TEMPLATE_NAME = "pom.xml.template"

# Default values para novos projetos
DEFAULT_JDK = "8"
DEFAULT_MAVEN = "3.9.5"
DEFAULT_GOAL = "clean deploy -DskipTests"
DEFAULT_DEPLOY = True

# ───── FUNÇÕES AUXILIARES ─────

def read_deploy_config() -> dict:
    """
    Abre o deploy_config.json e retorna o dicionário Python.
    Se não existir, cria com estrutura padrão. Se JSON for inválido, redefine o arquivo.
    """
    DEPLOY_CONFIG_PATH.parent.mkdir(parents=True, exist_ok=True)
    if not DEPLOY_CONFIG_PATH.exists():
        initial = {"projects": []}
        DEPLOY_CONFIG_PATH.write_text(json.dumps(initial, indent=2, ensure_ascii=False), encoding="utf-8")
        return initial
    try:
        text = DEPLOY_CONFIG_PATH.read_text(encoding="utf-8")
        return json.loads(text)
    except json.JSONDecodeError as e:
        print(f"[WARN] JSON inválido em '{DEPLOY_CONFIG_PATH}', redefinindo: {e}")
        initial = {"projects": []}
        DEPLOY_CONFIG_PATH.write_text(json.dumps(initial, indent=2, ensure_ascii=False), encoding="utf-8")
        return initial
    except Exception as e:
        print(f"[ERR] erro ao ler '{DEPLOY_CONFIG_PATH}': {e}")
        os._exit(1)

def write_deploy_config(cfg: dict) -> None:
    """
    Escreve de volta o JSON no deploy_config.json (indentado).
    """
    DEPLOY_CONFIG_PATH.write_text(
        json.dumps(cfg, indent=2, ensure_ascii=False),
        encoding="utf-8"
    )

def load_parent_config() -> dict:
    """
    Carrega o parent_config.json com informações dinâmicas de parent (groupId, artifactId, version, repositories, etc.).
    """
    if not PARENT_CONFIG_PATH.exists():
        return {}
    try:
        return json.loads(PARENT_CONFIG_PATH.read_text(encoding="utf-8"))
    except Exception as e:
        print(f"[WARN] Erro ao ler '{PARENT_CONFIG_PATH}': {e}")
        return {}

def load_bom_settings(bom_json_path: Path) -> tuple[list, list, dict]:
    """
    Carrega o bom_config.json com 'repositories', 'pluginRepositories' e 'distributionManagement'.
    Se não existir ou for inválido, retorna listas vazias e None.
    """
    if not bom_json_path.exists():
        return [], [], None
    try:
        data = json.loads(bom_json_path.read_text(encoding="utf-8"))
        repos = data.get("repositories", [])
        plugin_repos = data.get("pluginRepositories", [])
        dist = data.get("distributionManagement")
        return repos, plugin_repos, dist
    except Exception:
        return [], [], None

def load_bom_version(bom_json_path: Path) -> str:
    """
    Retorna o campo 'version' do bom_config.json.
    """
    try:
        data = json.loads(bom_json_path.read_text(encoding="utf-8"))
        return data.get("version", "")
    except Exception:
        return ""

def decrement_version(version: str) -> str:
    """
    Decrementa a última parte numérica da versão.
    Exemplo: '1.0.3' -> '1.0.2'
    Se não conseguir parsear, retorna a mesma versão.
    """
    m = re.match(r"^(\d+(?:\.\d+)*)(.*)$", version)
    if not m:
        return version
    nums, suffix = m.groups()
    parts = nums.split('.')
    try:
        parts[-1] = str(max(0, int(parts[-1]) - 1))
    except ValueError:
        return version
    return '.'.join(parts) + suffix

def sync_deploy_parent_with_parent_config() -> None:
    """
    Atualiza todos os projetos em deploy_config.json cujo parent.groupId == ORG_ID,
    definindo parent.artifactId e parent.version de acordo com parent_config.json.
    """
    print(f"[INFO] Carregando deploy_config.json de {DEPLOY_CONFIG_PATH}")
    deploy_cfg = read_deploy_config()
    print(f"[INFO] Carregando parent_config.json de {PARENT_CONFIG_PATH}")
    parent_cfg = load_parent_config()

    if not deploy_cfg or 'projects' not in deploy_cfg:
        print(f"[WARN] deploy_config.json está vazio ou não encontrado!")
        return
    if not parent_cfg:
        print(f"[WARN] parent_config.json está vazio ou não encontrado!")
        return

    parent_artifact = parent_cfg.get('artifactId') or ""
    raw_version = parent_cfg.get('version') or ""
    print(f"[INFO] Sincronizando parent.artifactId={parent_artifact}, parent.version={raw_version}")

    changed = False
    updated_projects = []

    for proj in deploy_cfg.get('projects', []):
        pg = proj.get('parent') or {}
        if pg.get('groupId') == ORG_ID:
            if pg.get('artifactId') != parent_artifact or pg.get('version') != raw_version:
                old_artifact = pg.get('artifactId')
                old_version = pg.get('version')
                proj['parent']['artifactId'] = parent_artifact
                proj['parent']['version'] = raw_version
                updated_projects.append((proj.get('name'), old_artifact, old_version))
                changed = True

    if changed:
        write_deploy_config(deploy_cfg)
        print(f"[OK] deploy_config.json atualizado com novo parent:")
        for name, old_artifact, old_version in updated_projects:
            print(f"  - {name}: {old_artifact}@{old_version} -> {parent_artifact}@{raw_version}")
    else:
        print(f"[INFO] Nenhuma alteração necessária: todos os parents já estão atualizados.")

def extract_parent_from_default(parent_pom_path: Path) -> dict:
    """
    Extrai groupId, artifactId e version de um pom.xml default, caso seja necessário.
    """
    if not parent_pom_path.exists():
        return {"groupId": "", "artifactId": "", "version": ""}
    try:
        tree = ET.parse(parent_pom_path)
        root = tree.getroot()
        ns = {}
        if '}' in root.tag:
            uri = root.tag.split('}')[0].strip('{')
            ns = {'m': uri}
        def ftext(tag):
            return root.findtext(f"m:{tag}", namespaces=ns) if ns else root.findtext(tag)
        return {"groupId": ftext("groupId") or "", "artifactId": ftext("artifactId") or "", "version": ftext("version") or ""}
    except Exception:
        return {"groupId": "", "artifactId": "", "version": ""}

import xml.etree.ElementTree as ET
from pathlib import Path

def extract_pom_information(project_path: Path) -> dict:
    """
    Extrai todas as informações possíveis do pom.xml, seguindo
    fielmente as tags definidas no template Jinja2:
    - modelVersion, packaging, name, description, url, inceptionYear
    - parent (incluindo relativePath)
    - organization, licenses, scm, issueManagement, ciManagement, mailingLists
    - developers, contributors
    - properties, modules
    - dependencyManagement, dependencies
    - repositories, pluginRepositories
    - build (pluginManagement, plugins, resources, testResources)
    - reporting (plugins, excludeDefaults)
    - distributionManagement (repository, snapshotRepository, site, directory)
    - profiles (activation, properties, dependencyManagement, dependencies, build/plugins)
    """
    def find(elem, tag):
        return elem.find(f"m:{tag}", namespaces=ns) if ns else elem.find(tag)

    def findall(elem, tag):
        return elem.findall(f"m:{tag}", namespaces=ns) if ns else elem.findall(tag)

    def findtext(elem, tag):
        return elem.findtext(f"m:{tag}", namespaces=ns) if ns else (elem.findtext(tag) or "")

    pom_file = project_path / "pom.xml"
    info = {
        # (0) Modelo / Packaging
        "modelVersion": "",      # <modelVersion>
        "packaging": "",         # <packaging>

        # (1) Coordenadas básicas
        "groupId": "",
        "artifactId": "",
        "version": "",
        "name": project_path.name,
        "description": "",
        "url": "",
        "inceptionYear": "",

        # (2) Parent
        "parent": None,          # { groupId, artifactId, version, relativePath }

        # (3) Metadados opcionais
        "organization": None,    # { name, url }
        "licenses": [],          # [ { name, url, distribution, comments }, … ]
        "scm": None,             # { connection, developerConnection, url, tag }
        "issueManagement": None, # { system, url }
        "ciManagement": None,    # { system, url }
        "mailingLists": [],      # [ { name, subscribe, unsubscribe, post, archive }, … ]

        # (4) Desenvolvedores / Contribuidores
        "developers": [],        # [ { id, name, email, organization, organizationUrl, roles, timezone }, … ]
        "contributors": [],      # [ { name, email, url, organization, organizationUrl, roles }, … ]

        # (5) Properties / Modules
        "properties": {},        # { key: value, … }
        "modules": [],           # [ "modulo1", "modulo2", … ]

        # (6) Dependency Management / Dependencies
        "dependencyManagement": [],  # [ { groupId, artifactId, version, scope, type, classifier }, … ]
        "dependencies": [],          # [ { groupId, artifactId, version, scope, type, classifier, optional }, … ]

        # (7) Repositórios / Plugin Repositories
        "repositories": [],         # [ { id, name, url, releases:{enabled,updatePolicy}, snapshots:{enabled,updatePolicy} }, … ]
        "pluginRepositories": [],   # [ { id, name, url, releases:{enabled}, snapshots:{enabled} }, … ]

        # (8) Build
        "build": None,             # { sourceDirectory, testSourceDirectory, finalName, defaultGoal,
                                   #   pluginManagement:{ plugins:[…] }, plugins:[…], resources:[…], testResources:[…] }

        # (9) Reporting
        "reporting": None,         # { excludeDefaults, plugins:[{ groupId, artifactId, version, configuration:{…}, dependencies:[…] }, …] }

        # (10) Distribution Management
        "distributionManagement": None,  # { repository:{…}, snapshotRepository:{…}, site:{…}, directory }

        # (11) Profiles
        "profiles": []             # [ { id, activation:{…}, properties:{…}, dependencyManagement:[…], dependencies:[…], build:{ plugins:[…] } }, … ]
    }

    if not pom_file.exists():
        return info

    try:
        xml = ET.parse(str(pom_file))
        root = xml.getroot()
        ns_uri = root.tag.split('}')[0].strip('{') if '}' in root.tag else ""
        ns = {"m": ns_uri} if ns_uri else {}

        # ── (0) ModelVersion / Packaging ───────────────────────────────
        info["modelVersion"] = findtext(root, "modelVersion")
        info["packaging"] = findtext(root, "packaging")

        # ── (1) Coordenadas básicas ────────────────────────────────────
        parent_elem = find(root, "parent")  # variável intermediária para evitar warning

        info["groupId"] = findtext(root, "groupId") or (
            findtext(parent_elem, "groupId") if parent_elem is not None else ""
        )
        info["artifactId"] = findtext(root, "artifactId")
        info["version"] = findtext(root, "version") or (
            findtext(parent_elem, "version") if parent_elem is not None else ""
        )
        info["name"] = findtext(root, "name") or info["artifactId"]
        info["description"] = findtext(root, "description")
        info["url"] = findtext(root, "url")
        info["inceptionYear"] = findtext(root, "inceptionYear")

        # ── (2) Parent ───────────────────────────────────────────────────
        parent_elem = find(root, "parent")
        if parent_elem is not None:
            info["parent"] = {
                "groupId": findtext(parent_elem, "groupId"),
                "artifactId": findtext(parent_elem, "artifactId"),
                "version": findtext(parent_elem, "version"),
                "relativePath": findtext(parent_elem, "relativePath")
            }

        # ── (3) Metadados opcionais ────────────────────────────────────
        org_elem = find(root, "organization")
        if org_elem is not None:
            info["organization"] = {
                "name": findtext(org_elem, "name"),
                "url": findtext(org_elem, "url")
            }

        # Licenses
        licenses_elem = find(root, "licenses")
        if licenses_elem is not None:
            for lic in findall(licenses_elem, "license"):
                info["licenses"].append({
                    "name": findtext(lic, "name"),
                    "url": findtext(lic, "url"),
                    "distribution": findtext(lic, "distribution"),
                    "comments": findtext(lic, "comments"),
                })

        # SCM
        scm_elem = find(root, "scm")
        if scm_elem is not None:
            info["scm"] = {
                "connection": findtext(scm_elem, "connection"),
                "developerConnection": findtext(scm_elem, "developerConnection"),
                "url": findtext(scm_elem, "url"),
                "tag": findtext(scm_elem, "tag")
            }

        # IssueManagement
        issue_elem = find(root, "issueManagement")
        if issue_elem is not None:
            info["issueManagement"] = {
                "system": findtext(issue_elem, "system"),
                "url": findtext(issue_elem, "url")
            }

        # CI Management
        ci_elem = find(root, "ciManagement")
        if ci_elem is not None:
            info["ciManagement"] = {
                "system": findtext(ci_elem, "system"),
                "url": findtext(ci_elem, "url")
            }

        # MailingLists
        ml_elem = find(root, "mailingLists")
        if ml_elem is not None:
            for ml in findall(ml_elem, "mailingList"):
                info["mailingLists"].append({
                    "name": findtext(ml, "name"),
                    "subscribe": findtext(ml, "subscribe"),
                    "unsubscribe": findtext(ml, "unsubscribe"),
                    "post": findtext(ml, "post"),
                    "archive": findtext(ml, "archive"),
                })

        # ── (4) Developers / Contributors ───────────────────────────────
        devs_elem = find(root, "developers")
        if devs_elem is not None:
            for dev in findall(devs_elem, "developer"):
                info["developers"].append({
                    "id": findtext(dev, "id"),
                    "name": findtext(dev, "name"),
                    "email": findtext(dev, "email"),
                    "organization": findtext(dev, "organization"),
                    "organizationUrl": findtext(dev, "organizationUrl"),
                    "roles": [r.text for r in findall(dev, "roles/role")] if find(dev, "roles") else [],
                    "timezone": findtext(dev, "timezone"),
                })

        contrib_elem = find(root, "contributors")
        if contrib_elem is not None:
            for contrib in findall(contrib_elem, "contributor"):
                info["contributors"].append({
                    "name": findtext(contrib, "name"),
                    "email": findtext(contrib, "email"),
                    "url": findtext(contrib, "url"),
                    "organization": findtext(contrib, "organization"),
                    "organizationUrl": findtext(contrib, "organizationUrl"),
                    "roles": [r.text for r in findall(contrib, "roles/role")] if find(contrib, "roles") else [],
                })

        # ── (5) Properties / Modules ────────────────────────────────────
        props_elem = find(root, "properties")
        if props_elem is not None:
            for prop in list(props_elem):
                tag = prop.tag.split("}")[-1] if "}" in prop.tag else prop.tag
                info["properties"][tag] = prop.text or ""

        modules_elem = find(root, "modules")
        if modules_elem is not None:
            info["modules"] = [mod.text for mod in findall(modules_elem, "module")]

        # ── (6) Dependency Management / Dependencies ────────────────────
        dm_elem = find(root, "dependencyManagement")
        if dm_elem is not None:
            dm_deps_elem = find(dm_elem, "dependencies")
            if dm_deps_elem is not None:
                for d in findall(dm_deps_elem, "dependency"):
                    info["dependencyManagement"].append({
                        "groupId": findtext(d, "groupId"),
                        "artifactId": findtext(d, "artifactId"),
                        "version": findtext(d, "version"),
                        "scope": findtext(d, "scope"),
                        "type": findtext(d, "type"),
                        "classifier": findtext(d, "classifier"),
                    })

        deps_elem = find(root, "dependencies")
        if deps_elem is not None:
            for d in findall(deps_elem, "dependency"):
                info["dependencies"].append({
                    "groupId": findtext(d, "groupId"),
                    "artifactId": findtext(d, "artifactId"),
                    "version": findtext(d, "version"),
                    "scope": findtext(d, "scope"),
                    "type": findtext(d, "type"),
                    "classifier": findtext(d, "classifier"),
                    "optional": findtext(d, "optional"),
                })

        # ── (7) Repositories / Plugin Repositories ───────────────────────
        repos_elem = find(root, "repositories")
        if repos_elem is not None:
            for r in findall(repos_elem, "repository"):
                info["repositories"].append({
                    "id": findtext(r, "id"),
                    "name": findtext(r, "name"),
                    "url": findtext(r, "url"),
                    "releases": {
                        "enabled": findtext(find(r, "releases") or ET.Element("dummy"), "enabled"),
                        "updatePolicy": findtext(find(r, "releases") or ET.Element("dummy"), "updatePolicy")
                    },
                    "snapshots": {
                        "enabled": findtext(find(r, "snapshots") or ET.Element("dummy"), "enabled"),
                        "updatePolicy": findtext(find(r, "snapshots") or ET.Element("dummy"), "updatePolicy")
                    },
                })

        plrepos_elem = find(root, "pluginRepositories")
        if plrepos_elem is not None:
            for pr in findall(plrepos_elem, "pluginRepository"):
                info["pluginRepositories"].append({
                    "id": findtext(pr, "id"),
                    "name": findtext(pr, "name"),
                    "url": findtext(pr, "url"),
                    "releases": {
                        "enabled": findtext(find(pr, "releases") or ET.Element("dummy"), "enabled"),
                    },
                    "snapshots": {
                        "enabled": findtext(find(pr, "snapshots") or ET.Element("dummy"), "enabled"),
                    },
                })

        # ── (8) Distribution Management ──────────────────────────────────
        dist_elem = find(root, "distributionManagement")
        if dist_elem is not None:
            dist = {}
            repo = find(dist_elem, "repository")
            if repo is not None:
                dist["repository"] = {
                    "id": findtext(repo, "id"),
                    "name": findtext(repo, "name"),
                    "url": findtext(repo, "url"),
                    "layout": findtext(repo, "layout")
                }
            snap_repo = find(dist_elem, "snapshotRepository")
            if snap_repo is not None:
                dist["snapshotRepository"] = {
                    "id": findtext(snap_repo, "id"),
                    "name": findtext(snap_repo, "name"),
                    "url": findtext(snap_repo, "url"),
                    "layout": findtext(snap_repo, "layout")
                }
            site = find(dist_elem, "site")
            if site is not None:
                dist["site"] = {
                    "id": findtext(site, "id"),
                    "name": findtext(site, "name"),
                    "url": findtext(site, "url")
                }
            dist["directory"] = findtext(dist_elem, "directory")
            info["distributionManagement"] = dist

        # ── (9) Build ────────────────────────────────────────────────────
        build_elem = find(root, "build")
        if build_elem is not None:
            build = {
                "sourceDirectory": findtext(build_elem, "sourceDirectory"),
                "testSourceDirectory": findtext(build_elem, "testSourceDirectory"),
                "finalName": findtext(build_elem, "finalName"),
                "defaultGoal": findtext(build_elem, "defaultGoal"),
                "pluginManagement": None,
                "plugins": [],
                "resources": [],
                "testResources": []
            }
            # PluginManagement
            pm_elem = find(build_elem, "pluginManagement")
            if pm_elem is not None:
                pm_plugins_elem = find(pm_elem, "plugins")
                pm_plugins = []
                if pm_plugins_elem is not None:
                    for p in findall(pm_plugins_elem, "plugin"):
                        pm_plugins.append({
                            "groupId": findtext(p, "groupId"),
                            "artifactId": findtext(p, "artifactId"),
                            "version": findtext(p, "version"),
                            "extensions": findtext(p, "extensions"),
                            "configuration": None,
                            "executions": []
                        })
                build["pluginManagement"] = {"plugins": pm_plugins}

            # Plugins
            plugins_elem = find(build_elem, "plugins")
            if plugins_elem is not None:
                for p in findall(plugins_elem, "plugin"):
                    build["plugins"].append({
                        "groupId": findtext(p, "groupId"),
                        "artifactId": findtext(p, "artifactId"),
                        "version": findtext(p, "version"),
                        "extensions": findtext(p, "extensions"),
                        "configuration": None,
                        "executions": []
                    })

            # Resources
            resources_elem = find(build_elem, "resources")
            if resources_elem is not None:
                for res in findall(resources_elem, "resource"):
                    build["resources"].append({
                        "directory": findtext(res, "directory"),
                        "includes": [inc.text for inc in findall(res, "includes/include")] if find(res, "includes") else [],
                        "excludes": [exc.text for exc in findall(res, "excludes/exclude")] if find(res, "excludes") else []
                    })

            # TestResources
            test_resources_elem = find(build_elem, "testResources")
            if test_resources_elem is not None:
                for tres in findall(test_resources_elem, "testResource"):
                    build["testResources"].append({
                        "directory": findtext(tres, "directory"),
                        "includes": [inc.text for inc in findall(tres, "includes/include")] if find(tres, "includes") else [],
                        "excludes": [exc.text for exc in findall(tres, "excludes/exclude")] if find(tres, "excludes") else []
                    })

            info["build"] = build

        # ── (10) Reporting ────────────────────────────────────────────────
        reporting_elem = find(root, "reporting")
        if reporting_elem is not None:
            reporting = {
                "excludeDefaults": findtext(reporting_elem, "excludeDefaults"),
                "plugins": []
            }
            plugins_elem = find(reporting_elem, "plugins")
            if plugins_elem is not None:
                for p in findall(plugins_elem, "plugin"):
                    plugin = {
                        "groupId": findtext(p, "groupId"),
                        "artifactId": findtext(p, "artifactId"),
                        "version": findtext(p, "version"),
                        "configuration": {},
                        "dependencies": []
                    }
                    config_elem = find(p, "configuration")
                    if config_elem is not None:
                        for c in list(config_elem):
                            key = c.tag.split("}")[-1] if "}" in c.tag else c.tag
                            plugin["configuration"][key] = c.text
                    deps_elem = find(p, "dependencies")
                    if deps_elem is not None:
                        for d in findall(deps_elem, "dependency"):
                            plugin["dependencies"].append({
                                "groupId": findtext(d, "groupId"),
                                "artifactId": findtext(d, "artifactId"),
                                "version": findtext(d, "version"),
                                "scope": findtext(d, "scope"),
                            })
                    reporting["plugins"].append(plugin)
            info["reporting"] = reporting

        # ── (11) Profiles ─────────────────────────────────────────────────
        profiles_elem = find(root, "profiles")
        if profiles_elem is not None:
            profiles = []
            for profile_elem in findall(profiles_elem, "profile"):
                profile = {
                    "id": findtext(profile_elem, "id"),
                    "activation": {},
                    "properties": {},
                    "dependencyManagement": [],
                    "dependencies": [],
                    "build": None
                }
                activation_elem = find(profile_elem, "activation")
                if activation_elem is not None:
                    for act_child in list(activation_elem):
                        key = act_child.tag.split("}")[-1] if "}" in act_child.tag else act_child.tag
                        profile["activation"][key] = act_child.text

                properties_elem = find(profile_elem, "properties")
                if properties_elem is not None:
                    for prop in list(properties_elem):
                        key = prop.tag.split("}")[-1] if "}" in prop.tag else prop.tag
                        profile["properties"][key] = prop.text

                dm_elem = find(profile_elem, "dependencyManagement")
                if dm_elem is not None:
                    dm_deps_elem = find(dm_elem, "dependencies")
                    if dm_deps_elem is not None:
                        for d in findall(dm_deps_elem, "dependency"):
                            profile["dependencyManagement"].append({
                                "groupId": findtext(d, "groupId"),
                                "artifactId": findtext(d, "artifactId"),
                                "version": findtext(d, "version"),
                                "scope": findtext(d, "scope"),
                            })

                deps_elem = find(profile_elem, "dependencies")
                if deps_elem is not None:
                    for d in findall(deps_elem, "dependency"):
                        profile["dependencies"].append({
                            "groupId": findtext(d, "groupId"),
                            "artifactId": findtext(d, "artifactId"),
                            "version": findtext(d, "version"),
                            "scope": findtext(d, "scope"),
                        })

                build_elem = find(profile_elem, "build")
                if build_elem is not None:
                    build = {"plugins": []}
                    plugins_elem = find(build_elem, "plugins")
                    if plugins_elem is not None:
                        for p in findall(plugins_elem, "plugin"):
                            build["plugins"].append({
                                "groupId": findtext(p, "groupId"),
                                "artifactId": findtext(p, "artifactId"),
                                "version": findtext(p, "version"),
                                "configuration": None,
                            })
                    profile["build"] = build
                profiles.append(profile)
            info["profiles"] = profiles

    except Exception as e:
        print(f"[WARN] Erro ao extrair info do pom.xml em '{project_path.name}': {e}")

    return info


def prettify_xml(raw: str, indent: int = 2) -> str:
    """
    Gera uma saída XML "bonita" com indentação, removendo linhas em branco.
    """
    try:
        parsed = minidom.parseString(raw.encode("utf-8"))
        pretty = parsed.toprettyxml(indent=" " * indent, newl="\n")
        lines = [line.rstrip() for line in pretty.splitlines() if line.strip()]
        return "\n".join(lines)
    except Exception:
        return raw

def generate_pom_from_template(project_path: Path) -> None:
    """
    Renderiza pom.xml dentro de project_path, usando Jinja2 e campos de deploy_config.json.
    Aplica as seguintes regras atualizadas para <parent> e blocos de repositórios:

      🛑 Regra 1 (Parent do próprio ORG_ID):
      Se o projeto já tiver um <parent> cujo parent.groupId == ORG_ID,
      então ler o parent_config.json para obter (artifactId, version),
      decrementar essa versão em 1 (version-1) e usar esse <parent> atualizado.
      NÃO inserir blocos de <repositories>, <pluginRepositories> ou <distributionManagement>.

      ✅ Regra 2 (Parent de outro groupId):
      Se o projeto tiver um <parent> cujo groupId != ORG_ID,
      manter esse parent exatamente como está (sem alterar groupId, artifactId, version),
      porém inserir blocos de <repositories>, <pluginRepositories> e <distributionManagement>
      vindos de bom_config.json.

      ✅ Regra 3 (Sem parent):
      Se o projeto NÃO tiver <parent>, ler o parent_config.json para obter (artifactId, version),
      decrementar essa versão em 1 e criar um <parent> com:
        <groupId>ORG_ID</groupId>
        <artifactId>[artifactId de parent_config]</artifactId>
        <version>[version-1]</version>
      Em seguida, inserir também blocos de <repositories>, <pluginRepositories> e
      <distributionManagement> vindos de bom_config.json.

    As dependências existentes (<dependencies>) devem ser mantidas sem alterações.
    """

    # 1) Sincronizar parents antes de gerar POM (para refletir possíveis mudanças em parent_config.json)
    sync_deploy_parent_with_parent_config()

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
        print(f"[WARN] Não encontrei entrada no JSON para projeto '{project_path.name}'. Pulando generate_pom().")
        return

    # 3) Pegar coordenadas básicas do projeto
    GROUP_ID = ORG_ID
    ARTIFACT_ID = entry.get("artifactId", entry["name"])
    VERSION = entry.get("version", "")
    NAME = entry.get("displayName", entry["name"])
    DEVS = entry.get("developers", [])

    # 4) Montar lista de dependencies incluindo depends_on
    dependencies = []
    for dep_name in entry.get("depends_on", []):
        dep_entry = next((pp for pp in cfg.get("projects", []) if pp["name"] == dep_name), None)
        if dep_entry:
            dependencies.append({
                "groupId": ORG_ID,
                "artifactId": dep_entry["name"],
                "version": dep_entry["version"],
                "classifier": "mule-plugin"
            })
    # Inclui dependencies definidas no JSON, respeitando classifier, scope e optional
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

    # 5) Carregar configurações do bom_config.json (repositórios, pluginRepositories, distributionManagement)
    bom_repos, bom_plugin_repos, bom_dist = load_bom_settings(BOM_CONFIG_PATH)

    # 6) Carregar parent_config.json para obter artifactId e version
    parent_cfg = load_parent_config()
    parent_artifact_cfg = parent_cfg.get("artifactId", "")    # ex.: "extensions-parent"
    parent_version_cfg = parent_cfg.get("version", "")        # ex.: "1.0.2"
    # Decrementar a versão de parent_config.json (se existir)
    if parent_version_cfg:
        parent_dec_version = decrement_version(parent_version_cfg)
    else:
        parent_dec_version = ""

    # 7) Decidir qual <parent> e quais blocos de repositório usar, de acordo com entry["parent"]
    orig_parent = entry.get("parent") or None

    if orig_parent and orig_parent.get("groupId") == ORG_ID:
        # Regra 1: Já existe parent com meu ORG_ID.
        # Usar parent_config.json (artifactId + version-1) para sobrescrever o parent, e NÃO inserir blocos BOM.
        parent = {
            "groupId": ORG_ID,
            "artifactId": parent_artifact_cfg,
            "version": parent_dec_version
        }
        # Usar repositórios originais que estavam no POM (não inserimos BOM)
        repositories = entry.get("repositories", [])
        pluginRepositories = entry.get("pluginRepositories", [])
        distributionManagement = entry.get("distributionManagement", None)

    elif orig_parent:
        # Regra 2: Já existe parent, mas com groupId diferente de ORG_ID.
        # Manter exatamente o parent original
        parent = {
            "groupId": orig_parent.get("groupId", ""),
            "artifactId": orig_parent.get("artifactId", ""),
            "version": orig_parent.get("version", "")
        }
        # Inserir blocos de BOM (repositórios, pluginRepos, distMgmt)
        repositories = bom_repos
        pluginRepositories = bom_plugin_repos
        distributionManagement = bom_dist

    else:
        # Regra 3: Não existe parent. Criar um novo parent com base em parent_config.json (artifactId + version-1)
        parent = {
            "groupId": ORG_ID,
            "artifactId": parent_artifact_cfg,
            "version": parent_dec_version
        }
        # Inserir blocos de BOM (repositórios, pluginRepos, distMgmt)
        repositories = bom_repos
        pluginRepositories = bom_plugin_repos
        distributionManagement = bom_dist

    # 8) Montar o contexto para o template Jinja2
    context = {
        "groupId": GROUP_ID,
        "artifactId": ARTIFACT_ID,
        "version": VERSION,
        "name": NAME,
        "developers": DEVS,
        "dependencies": dependencies,
        "parent": parent,
        "properties": entry.get("properties", {}),
        "dependencyManagement": entry.get("dependencyManagement", []),
        "repositories": repositories,
        "pluginRepositories": pluginRepositories,
        "distributionManagement": distributionManagement,
        "build": entry.get("build", {}),             # <―― adiciona aqui
        "reporting": entry.get("reporting", {}),     # opcional, para seção <reporting>
        "profiles": entry.get("profiles", [])        # opcional, para seção <profiles>
    }

    # 9) Renderizar o template e escrever o pom.xml
    if not TEMPLATE_DIR.exists():
        print(f"[ERR] Template dir não existe: {TEMPLATE_DIR}")
        return

    env = Environment(
        loader=FileSystemLoader(searchpath=str(TEMPLATE_DIR)),
        autoescape=False,
        trim_blocks=True,
        lstrip_blocks=True
    )

    try:
        template = env.get_template(TEMPLATE_NAME)
    except Exception as e:
        print(f"[ERR] não encontrei template '{TEMPLATE_NAME}' em {TEMPLATE_DIR}: {e}")
        return

    raw = template.render(**context)
    nice = prettify_xml(raw, indent=2)
    target_pom = project_path / "pom.xml"
    target_pom.write_text(nice, encoding="utf-8")
    print(f"[OK] Gerado/atualizado pom.xml em '{project_path.name}'")

def add_project_to_deploy_config(project_path: Path) -> None:
    """
    Lê o pom.xml da pasta 'project_path' e insere/atualiza uma entrada no deploy_config.json
    com todos os campos possíveis.
    """
    cfg = read_deploy_config()
    for p in cfg.get("projects", []):
        if p.get("name") == project_path.name:
            print(f"[INFO] Entrada '{project_path.name}' já existe em deploy_config.json. Não alterando.")
            return

    info = extract_pom_information(project_path)
    relative_path = os.path.relpath(project_path, DEPLOY_CONFIG_PATH.parent).replace("\\", "/")

    new_entry = {
        "name": project_path.name,
        "path": relative_path,
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
            }
            for d in info.get("dependencies", [])
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
    print(f"[OK] Inserido '{project_path.name}' em deploy_config.json")

class NewProjectHandler(FileSystemEventHandler):
    """
    Sempre que um diretório for criado diretamente dentro de PROJECTS_DIR,
    disparamos a rotina de inserção no JSON e geração de POM.
    """
    def on_created(self, event):
        p = Path(event.src_path)
        if p.is_dir() and p.parent.resolve() == PROJECTS_DIR.resolve():
            project_name = p.name
            print(f"\n[EVENT] Nova pasta detectada: '{project_name}'")
            thread = threading.Thread(target=self.process_new_project, args=(p,), daemon=True)
            thread.start()

    def process_new_project(self, project_path: Path):
        time.sleep(2.0)
        print(f"[INFO] Processando projeto novo em: {project_path.name}")
        add_project_to_deploy_config(project_path)
        generate_pom_from_template(project_path)
        print(
            f"[DONE] Projeto '{project_path.name}' totalmente configurado.\n"
            f"       - deploy_config.json atualizado\n"
            f"       - pom.xml gerado em '{project_path.name}'"
        )

def main():
    # Sincronizar parents antes de iniciar o watcher
    sync_deploy_parent_with_parent_config()
    print("Iniciando watcher na pasta:", PROJECTS_DIR)
    PROJECTS_DIR.mkdir(parents=True, exist_ok=True)
    event_handler = NewProjectHandler()
    observer = Observer()
    observer.schedule(event_handler, str(PROJECTS_DIR), recursive=False)
    observer.start()
    try:
        while True:
            time.sleep(1)
    except KeyboardInterrupt:
        print("\n[INFO] Interrompido pelo usuário (Ctrl+C). Saindo...")
        observer.stop()
    observer.join()

if __name__ == "__main__":
    main()
