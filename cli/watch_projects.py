#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
watch_projects.py

Este script "escuta" continuamente a pasta `projects/`. Sempre que um novo
diretório de projeto for criado em `projects/`, ele:
  1) Lê o pom.xml (para extrair <groupId>, <artifactId>, <version>, <dependencies>, <developers>, etc.)
  2) Insere uma nova entrada em build/configs/deploy_config.json com todos os campos extraídos ou padrões
  3) Executa o generate_pom_from_template() (Jinja2) para criar/sobrescrever o pom.xml
     naquele novo projeto, usando o template em build/templates.
"""

import json
import os
import time
import threading
import xml.etree.ElementTree as ET
from pathlib import Path
from watchdog.observers import Observer
from watchdog.events import FileSystemEventHandler
from jinja2 import Environment, FileSystemLoader
from xml.dom import minidom

# ───── CONFIGURAÇÕES FIXAS ─────
ROOT_DIR = Path(__file__).parent.parent.resolve()
PROJECTS_DIR = ROOT_DIR / "projects"
DEPLOY_CONFIG_PATH = ROOT_DIR / "build" / "configs" / "deploy_config.json"
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
    Se não existir ou JSON for inválido, aborta com erro.
    """
    if not DEPLOY_CONFIG_PATH.exists():
        print(f"[ERR] deploy_config.json não encontrado em {DEPLOY_CONFIG_PATH}")
        os._exit(1)
    try:
        text = DEPLOY_CONFIG_PATH.read_text(encoding="utf-8")
        return json.loads(text)
    except Exception as e:
        print(f"[ERR] não foi possível ler/parsear {DEPLOY_CONFIG_PATH}: {e}")
        os._exit(1)


def write_deploy_config(cfg: dict) -> None:
    """
    Escreve de volta o JSON no deploy_config.json (indentado).
    """
    DEPLOY_CONFIG_PATH.write_text(
        json.dumps(cfg, indent=2, ensure_ascii=False),
        encoding="utf-8"
    )


def extract_pom_information(project_path: Path) -> dict:
    """
    Lê o pom.xml dentro de project_path e retorna um dicionário com:
      - groupId, artifactId, version, name
      - parent (groupId, artifactId, version) se existir
      - dependencies: lista de dicts {groupId, artifactId, version, scope?, optional?}
      - developers: lista de dicts {id, name, email, organization, organizationUrl}
      - properties, repositories, pluginRepositories, distributionManagement, dependencyManagement
    Se não houver pom.xml, retorna valores vazios para cada campo.
    """
    pom_file = project_path / "pom.xml"
    info = {
        "groupId": "",
        "artifactId": "",
        "version": "",
        "name": project_path.name,
        "parent": None,
        "dependencies": [],
        "developers": [],
        "properties": {},
        "repositories": [],
        "pluginRepositories": [],
        "distributionManagement": None,
        "dependencyManagement": []
    }
    if not pom_file.exists():
        return info
    try:
        xml = ET.parse(str(pom_file))
        root = xml.getroot()
        # namespace handling
        ns_uri = root.tag.split('}')[0].strip('{') if '}' in root.tag else ''
        ns = {"m": ns_uri} if ns_uri else {}
        # Função auxiliar para findtext com ou sem namespace
        def ftext(tag):
            if ns:
                return root.findtext(f"m:{tag}", namespaces=ns) or ""
            return root.findtext(tag) or ""
        # Extrai groupId, artifactId, version, name
        info["groupId"] = ftext("groupId") or (root.find("m:parent", ns).findtext("m:groupId", namespaces=ns) if root.find("m:parent", ns) is not None else "")
        info["artifactId"] = ftext("artifactId")
        info["version"] = ftext("version") or (root.find("m:parent", ns).findtext("m:version", namespaces=ns) if root.find("m:parent", ns) is not None else "")
        info["name"] = ftext("name") or info["artifactId"]
        # Extrai parent se existir
        parent_elem = root.find("m:parent", ns) if ns else root.find("parent")
        if parent_elem is not None:
            pg = parent_elem.findtext("m:groupId", namespaces=ns) if ns else parent_elem.findtext("groupId")
            pa = parent_elem.findtext("m:artifactId", namespaces=ns) if ns else parent_elem.findtext("artifactId")
            pv = parent_elem.findtext("m:version", namespaces=ns) if ns else parent_elem.findtext("version")
            info["parent"] = {"groupId": pg, "artifactId": pa, "version": pv}
        # Extrai dependencies
        deps_parent = root.find("m:dependencies", ns) if ns else root.find("dependencies")
        if deps_parent is not None:
            for d in deps_parent.findall("m:dependency", ns) if ns else deps_parent.findall("dependency"):
                dg = d.findtext("m:groupId", namespaces=ns) if ns else d.findtext("groupId")
                da = d.findtext("m:artifactId", namespaces=ns) if ns else d.findtext("artifactId")
                dv = d.findtext("m:version", namespaces=ns) if ns else d.findtext("version") or ""
                scope = d.findtext("m:scope", namespaces=ns) if ns else d.findtext("scope") or ""
                optional = d.findtext("m:optional", namespaces=ns) if ns else d.findtext("optional") or ""
                info["dependencies"].append({
                    "groupId": dg or "", "artifactId": da or "", "version": dv,
                    "scope": scope, "optional": optional
                })
        # Extrai developers
        devs_parent = root.find("m:developers", ns) if ns else root.find("developers")
        if devs_parent is not None:
            for dv_elem in devs_parent.findall("m:developer", ns) if ns else devs_parent.findall("developer"):
                did = dv_elem.findtext("m:id", namespaces=ns) if ns else dv_elem.findtext("id") or ""
                dname = dv_elem.findtext("m:name", namespaces=ns) if ns else dv_elem.findtext("name") or ""
                demail = dv_elem.findtext("m:email", namespaces=ns) if ns else dv_elem.findtext("email") or ""
                dorg = dv_elem.findtext("m:organization", namespaces=ns) if ns else dv_elem.findtext("organization") or ""
                dorgurl = dv_elem.findtext("m:organizationUrl", namespaces=ns) if ns else dv_elem.findtext("organizationUrl") or ""
                info["developers"].append({"id": did, "name": dname, "email": demail, "organization": dorg, "organizationUrl": dorgurl})
        # Extrai properties
        props_parent = root.find("m:properties", ns) if ns else root.find("properties")
        if props_parent is not None:
            for prop in list(props_parent):
                tag = prop.tag.split('}')[-1] if '}' in prop.tag else prop.tag
                info["properties"][tag] = prop.text or ""
        # Extrai repositories
        repos_parent = root.find("m:repositories", ns) if ns else root.find("repositories")
        if repos_parent is not None:
            for r in repos_parent.findall("m:repository", ns) if ns else repos_parent.findall("repository"):
                rid = r.findtext("m:id", namespaces=ns) if ns else r.findtext("id") or ""
                rurl = r.findtext("m:url", namespaces=ns) if ns else r.findtext("url") or ""
                info["repositories"].append({"id": rid, "url": rurl})
        # Extrai pluginRepositories
        plrepos_parent = root.find("m:pluginRepositories", ns) if ns else root.find("pluginRepositories")
        if plrepos_parent is not None:
            for pr in plrepos_parent.findall("m:pluginRepository", ns) if ns else plrepos_parent.findall("pluginRepository"):
                prid = pr.findtext("m:id", namespaces=ns) if ns else pr.findtext("id") or ""
                prurl = pr.findtext("m:url", namespaces=ns) if ns else pr.findtext("url") or ""
                info["pluginRepositories"].append({"id": prid, "url": prurl})
        # Extrai distributionManagement
        dist_parent = root.find("m:distributionManagement", ns) if ns else root.find("distributionManagement")
        if dist_parent is not None:
            repo = dist_parent.find("m:repository", ns) if ns else dist_parent.find("repository")
            if repo is not None:
                rid = repo.findtext("m:id", namespaces=ns) if ns else repo.findtext("id") or ""
                rurl = repo.findtext("m:url", namespaces=ns) if ns else repo.findtext("url") or ""
                info["distributionManagement"] = {"repository": {"id": rid, "url": rurl}}
        # Extrai dependencyManagement
        dm_parent = root.find("m:dependencyManagement", ns) if ns else root.find("dependencyManagement")
        if dm_parent is not None:
            dm_deps = dm_parent.find("m:dependencies", ns) if ns else dm_parent.find("dependencies")
            if dm_deps is not None:
                for d in dm_deps.findall("m:dependency", ns) if ns else dm_deps.findall("dependency"):
                    dg = d.findtext("m:groupId", namespaces=ns) if ns else d.findtext("groupId")
                    da = d.findtext("m:artifactId", namespaces=ns) if ns else d.findtext("artifactId")
                    dv = d.findtext("m:version", namespaces=ns) if ns else d.findtext("version") or ""
                    info["dependencyManagement"].append({"groupId": dg, "artifactId": da, "version": dv})
    except Exception as e:
        print(f"[WARN] Erro ao extrair info do pom.xml em '{project_path.name}': {e}")
    return info


def prettify_xml(raw: str, indent: int = 2) -> str:
    """
    Usa minidom para indentar XML e remover linhas em branco.
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
    """
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
    GROUP_ID = entry.get("groupId", entry.get("groupId", "com.meuorg"))
    ARTIFACT_ID = entry.get("artifactId", entry["name"])
    VERSION = entry.get("version", "")
    NAME = entry.get("displayName", entry["name"])
    DEPS = entry.get("dependencies", [])
    DEVS = entry.get("developers", [])
    PARENT = entry.get("parent", {"groupId": "com.meuorg", "artifactId": "parent-pom", "version": "1.0.0"})
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
    context = {
        "groupId": GROUP_ID,
        "artifactId": ARTIFACT_ID,
        "version": VERSION,
        "name": NAME,
        "developers": DEVS,
        "dependencies": DEPS,
        "parent": PARENT,
        "properties": entry.get("properties", {}),
        "dependencyManagement": entry.get("dependencyManagement", []),
        "repositories": entry.get("repositories", []),
        "pluginRepositories": entry.get("pluginRepositories", []),
        "distributionManagement": entry.get("distributionManagement", None),
    }
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
    # Extrai dados do pom.xml
    info = extract_pom_information(project_path)
    # Monta novo bloco com as chaves necessárias
    relative_path = os.path.relpath(project_path, DEPLOY_CONFIG_PATH.parent).replace("\\", "/")
    new_entry = {
        "name": project_path.name,
        "path": relative_path,
        "groupId": info.get("groupId", ""),
        "artifactId": info.get("artifactId", project_path.name),
        "version": info.get("version", ""),
        "displayName": info.get("name", project_path.name),
        "jdk": DEFAULT_JDK,
        "maven": DEFAULT_MAVEN,
        "goal": DEFAULT_GOAL,
        "deploy": DEFAULT_DEPLOY,
        # Dependências transformadas para campos de JSON compatível
        "dependencies": [{
            "groupId": d.get("groupId", ""),
            "artifactId": d.get("artifactId", ""),
            "version": d.get("version", ""),
            "scope": d.get("scope", ""),
            "optional": d.get("optional", "")
        } for d in info.get("dependencies", [])],
        "developers": info.get("developers", []),
        "parent": info.get("parent", None),
        "properties": info.get("properties", {}),
        "dependencyManagement": info.get("dependencyManagement", []),
        "repositories": info.get("repositories", []),
        "pluginRepositories": info.get("pluginRepositories", []),
        "distributionManagement": info.get("distributionManagement", None)
    }
    cfg["projects"].append(new_entry)
    write_deploy_config(cfg)
    print(f"[OK] Inserido '{project_path.name}' em deploy_config.json")

# ───── EVENT HANDLER PARA O WATCHER ─────

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
            thread = threading.Thread(
                target=self.process_new_project,
                args=(p,),
                daemon=True
            )
            thread.start()

    def process_new_project(self, project_path: Path):
        time.sleep(2.0)
        print(f"[INFO] Processando projeto novo em: {project_path.name}")
        add_project_to_deploy_config(project_path)
        generate_pom_from_template(project_path)
        print(f"[DONE] Projeto '{project_path.name}' totalmente configurado.\n" 
              f"       - deploy_config.json atualizado\n" 
              f"       - pom.xml (template) gerado em '{project_path.name}'")

# ───── FUNÇÃO PRINCIPAL QUE INICIA O WATCHER ─────

def main():
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