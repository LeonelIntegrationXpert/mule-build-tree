#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Batch “deploy all” – UNIVERSAL JAVA & MAVEN MULTI-BUILD TOOL v1.8
"""

import os
import sys
import json
import re
import time
import subprocess
import zipfile
import shutil
import requests
from pathlib import Path
from typing import List, Dict

# ──────────────────────────── Cores ANSI ─────────────────────────────
C_RST = "\033[0m"
C_CYA = "\033[96m"
C_GRN = "\033[92m"
C_RED = "\033[91m"
C_YLW = "\033[93m"

# ───────────────────────── Catalogos e Defaults ───────────────────────
JDKS: Dict[str, Dict[str, str]] = {
    "8":  {"desc": "Temurin 8u412",  "url": "https://github.com/adoptium/temurin8-binaries/releases/download/jdk8u412-b08/OpenJDK8U-jdk_x64_windows_hotspot_8u412b08.zip"},
    "11": {"desc": "Temurin 11.0.21", "url": "https://github.com/adoptium/temurin11-binaries/releases/download/jdk-11.0.21%2B9/OpenJDK11U-jdk_x64_windows_hotspot_11.0.21_9.zip"},
    "17": {"desc": "Temurin 17.0.11", "url": "https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.11%2B9/OpenJDK17U-jdk_x64_windows_hotspot_17.0.11_9.zip"},
    "21": {"desc": "Temurin 21.0.3",  "url": "https://github.com/adoptium/temurin21-binaries/releases/download/jdk-21.0.3%2B9/OpenJDK21U-jdk_x64_windows_hotspot_21.0.3_9.zip"},
}
MAVENS: Dict[str, Dict[str, str]] = {
    "3.6.3": {"url": "https://archive.apache.org/dist/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.zip"},
    "3.8.8": {"url": "https://archive.apache.org/dist/maven/maven-3/3.8.8/binaries/apache-maven-3.8.8-bin.zip"},
    "3.9.5": {"url": "https://archive.apache.org/dist/maven/maven-3/3.9.5/binaries/apache-maven-3.9.5-bin.zip"},
    "3.9.6": {"url": "https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip"},
    "4.0.0": {"url": "https://archive.apache.org/dist/maven/maven-4/4.0.0/binaries/apache-maven-4.0.0-bin.zip"},
    "4.0.1": {"url": "https://archive.apache.org/dist/maven/maven-4/4.0.1/binaries/apache-maven-4.0.1-bin.zip"},
}
ADD_OPENS_EXTRA = [
    "--add-opens", "java.base/java.lang=ALL-UNNAMED",
    "--add-opens", "java.base/java.util=ALL-UNNAMED",
    "--add-opens", "java.base/java.io=ALL-UNNAMED",
    "--add-opens", "java.base/java.lang.reflect=ALL-UNNAMED",
    "--add-opens", "java.base/java.util.concurrent=ALL-UNNAMED",
    "--add-opens", "java.base/java.net=ALL-UNNAMED",
    "--add-opens", "java.base/java.nio=ALL-UNNAMED",
    "--add-opens", "java.base/sun.nio.ch=ALL-UNNAMED",
    "--add-opens", "java.base/sun.net.util=ALL-UNNAMED",
    "--add-opens", "java.base/sun.security.util=ALL-UNNAMED",
    "--add-opens", "java.base/sun.security.x509=ALL-UNNAMED",
    "--add-opens", "java.xml/com.sun.org.apache.xerces.internal.parsers=ALL-UNNAMED",
    "--add-opens", "java.xml/com.sun.org.apache.xerces.internal.dom=ALL-UNNAMED",
    "--add-opens", "java.xml/javax.xml.namespace=ALL-UNNAMED",
    "--add-opens", "java.xml/com.sun.org.apache.xpath.internal=ALL-UNNAMED",
    "--add-opens", "jdk.internal.loader/jdk.internal.loader=ALL-UNNAMED",
]

DEFAULT_CONFIG_PATH = Path("deploy-config.json")
ROOT_DIR = Path("tooling")
JDK_DIR = ROOT_DIR / "jdks"
MAVEN_DIR = ROOT_DIR / "mavens"

# ─────────────────── Carrega groupId + credenciais ───────────────────
def load_org_id(config_path: Path = DEFAULT_CONFIG_PATH) -> str:
    if not config_path.exists():
        print(f"{C_RED}❌ Arquivo {config_path} não encontrado!{C_RST}")
        sys.exit(1)
    cfg = json.loads(config_path.read_text(encoding="utf-8"))
    org = cfg.get("groupId")
    if not org:
        print(f"{C_RED}❌ ‘groupId’ não encontrado em {config_path}!{C_RST}")
        sys.exit(1)
    return org


ORG_ID = load_org_id()
os.environ.setdefault("EXCHANGE_USER", "~~~Client~~~")
os.environ.setdefault("EXCHANGE_PASS", "9fd0d4f3ba7749e98beb8177116fb492~?~bCD2fa42dE1048Bc8b7876e5bAE8B89a")

# ───────────────────── Satck topo by depends_on ─────────────────────
def topo_sort(projects: List[Dict]) -> List[Dict]:
    lookup = {p["name"]: p for p in projects}
    visited = {}
    order: List[Dict] = []

    def dfs(name: str):
        if visited.get(name) == "perm":
            return
        if visited.get(name) == "temp":
            raise RuntimeError(f"Ciclo em depends_on: {name}")
        visited[name] = "temp"
        for dep in lookup[name].get("depends_on", []):
            if dep in lookup:
                dfs(dep)
        visited[name] = "perm"
        order.append(lookup[name])

    for p in projects:
        if visited.get(p["name"]) is None:
            dfs(p["name"])
    return order


# ───────────────────── Download + unzip com cache ───────────────────
def prepare_tool(url: str, base: Path) -> Path:
    base.mkdir(parents=True, exist_ok=True)
    zip_path = base / Path(url).name
    if not zip_path.exists():
        print(f"{C_CYA}⬇️  Baixando {zip_path.name}{C_RST}")
        with requests.get(url, stream=True) as r, open(zip_path, "wb") as f:
            for chunk in r.iter_content(8192):
                f.write(chunk)
    with zipfile.ZipFile(zip_path) as z:
        z.extractall(base)
        dirs = {Path(n).parts[0] for n in z.namelist() if '/' in n}
    return (base / next(iter(dirs))).resolve()


# ───────────────────── Gera pom.xml Dinâmico ────────────────────────
def generate_pom(project_path: Path, original_versions: Dict[str, str]) -> None:
    template = project_path / "pom.xml.template"
    if not template.exists():
        print(f"{C_RED}❌ Template pom.xml.template não encontrado em {project_path}{C_RST}")
        sys.exit(1)

    cfg = json.loads(DEFAULT_CONFIG_PATH.read_text(encoding="utf-8"))
    group_id = cfg.get("groupId", ORG_ID)
    entry = next(
        (p for p in cfg["projects"] if project_path.resolve() == Path(p["path"]).resolve()), None
    )
    if not entry:
        print(f"{C_RED}❌ Projeto {project_path} não encontrado no config!{C_RST}")
        sys.exit(1)

    aid = entry["name"]
    ver = entry["version"]

    content = template.read_text(encoding="utf-8")
    content = (
        content.replace("{{GROUP_ID}}", group_id)
        .replace("{{ARTIFACT_ID}}", aid)
        .replace("{{VERSION}}", ver)
        .replace("${orgId}", group_id)
    )
    content = re.sub(r"<groupId>\s*\$\{groupId\}\s*</groupId>", f"<groupId>{group_id}</groupId>", content)

    # AUTO_DEPENDENCIES
    deps = entry.get("depends_on", [])
    blocks: List[str] = []
    for dep_name in deps:
        dep_version = original_versions.get(dep_name)
        if dep_version:
            blocks.append(
                f"        <dependency>\n"
                f"            <groupId>{group_id}</groupId>\n"
                f"            <artifactId>{dep_name}</artifactId>\n"
                f"            <version>{dep_version}</version>\n"
                f"            <classifier>mule-plugin</classifier>\n"
                f"        </dependency>"
            )

    dep_xml = "\n".join(blocks)
    if "<!-- AUTO_DEPENDENCIES -->" in content:
        content = content.replace("<!-- AUTO_DEPENDENCIES -->", dep_xml)
    else:
        content = re.sub(r"(</dependencies>)", dep_xml + r"\n\1", content, flags=re.IGNORECASE)

    (project_path / "pom.xml").write_text(content, encoding="utf-8")
    print(f"{C_GRN}✅ pom.xml gerado em {project_path}{C_RST}")


# ───────────────────── Bump de Versão no JSON ───────────────────────
def bump_version(pom: Path, pom_template: Path) -> str:
    cfg_path = DEFAULT_CONFIG_PATH
    cfg = json.loads(cfg_path.read_text(encoding="utf-8"))
    entry = next((p for p in cfg["projects"] if pom.parent.resolve() == Path(p["path"]).resolve()), None)
    if not entry:
        print(f"{C_RED}❌ Projeto não encontrado para bump!{C_RST}")
        return ""

    nums, suf = re.match(r"^(\d+(?:\.\d+)*)(.*)$", entry["version"]).groups()
    parts = nums.split(".")
    parts[-1] = str(int(parts[-1]) + 1)
    newv = ".".join(parts) + suf
    entry["version"] = newv
    cfg_path.write_text(json.dumps(cfg, indent=2, ensure_ascii=False), encoding="utf-8")
    return newv


# ─────────────────── Run Single Build + Retry ───────────────────────
def run_single_build(proj: Dict, settings: Path, original_versions: Dict[str, str]):
    name, goal = proj["name"], proj["goal"]
    print(f"{C_CYA}→ Deploy {name} [{goal}]{C_RST}")

    generate_pom(Path(proj["path"]), original_versions)

    jdk = prepare_tool(JDKS[proj["jdk"]]["url"], JDK_DIR)
    mvn = prepare_tool(MAVENS[proj["maven"]]["url"], MAVEN_DIR)
    mvn_exec = mvn / "bin" / ("mvn.cmd" if os.name == "nt" else "mvn")

    env = os.environ.copy()
    env["JAVA_HOME"], env["M2_HOME"] = str(jdk), str(mvn)
    env["PATH"] = f"{mvn/'bin'};{jdk/'bin'};{env['PATH']}"

    # Versões do Java / Maven
    try:
        java_ver = subprocess.run(
            [str(jdk / "bin" / "java"), "-version"], stderr=subprocess.PIPE, stdout=subprocess.PIPE, env=env, text=True
        ).stderr.splitlines()[0]
        print(f"{C_CYA}• Usando {java_ver}{C_RST}")
    except Exception:
        print(f"{C_YLW}• Não foi possível detectar versão do Java{C_RST}")

    try:
        mvn_ver = subprocess.run(
            [str(mvn_exec), "-version"], stdout=subprocess.PIPE, stderr=subprocess.PIPE, env=env, text=True
        ).stdout.splitlines()[0]
        print(f"{C_CYA}• Usando {mvn_ver}{C_RST}")
    except Exception:
        print(f"{C_YLW}• Não foi possível detectar versão do Maven{C_RST}")

    # Loop de retry em 403/409
    while True:
        cmd = [str(mvn_exec), "-s", str(settings)] + goal.split()
        print(f"{C_YLW}▶ Executando: {' '.join(cmd)}{C_RST}")
        proc = subprocess.Popen(cmd, cwd=proj["path"], env=env, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
        out, _ = proc.communicate()

        if proc.returncode == 0:
            print(f"{C_GRN}✅ {name} OK{C_RST}")
            bump_version(Path(proj["path"]) / "pom.xml", Path(proj["path"]) / "pom.xml.template")
            break

        print(out)
        if any(code in out for code in ("409", "403")):
            newv = bump_version(Path(proj["path"]) / "pom.xml", Path(proj["path"]) / "pom.xml.template")
            print(f"{C_YLW}⚠ Bumped to {newv}, retrying...{C_RST}")
            time.sleep(1)
            continue

        print(f"{C_RED}❌ Abort {name}{C_RST}")
        break


def bump_version_in_json(project_name: str, config_path: Path) -> str:
    cfg = json.loads(config_path.read_text(encoding="utf-8"))
    entry = next(p for p in cfg["projects"] if p["name"] == project_name)
    nums, suf = re.match(r"^(\d+(?:\.\d+)*)(.*)$", entry["version"]).groups()
    parts = nums.split(".")
    parts[-1] = str(int(parts[-1]) + 1)
    newv = ".".join(parts) + suf
    entry["version"] = newv
    config_path.write_text(json.dumps(cfg, indent=2, ensure_ascii=False), encoding="utf-8")
    return newv


# ───────────────────────── Batch Deploy All ─────────────────────────
def deploy_all(settings: Path):
    cfg = json.loads(DEFAULT_CONFIG_PATH.read_text(encoding="utf-8"))
    original_versions = {p["name"]: p["version"] for p in cfg["projects"]}

    enabled = [p for p in cfg["projects"] if p.get("deploy", False)]
    for proj in enabled:
        proj["goal"] = proj.get("goal", "deploy")
        proj["jdk"] = proj.get("jdk", "8")
        proj["maven"] = proj.get("maven", "3.9.5")

    for project in topo_sort(enabled):
        run_single_build(project, settings, original_versions)

    # bump extra (pós-deploy) para preparar versões futuras
    for proj in enabled:
        newv = bump_version_in_json(proj["name"], DEFAULT_CONFIG_PATH)
        print(f"{C_YLW}⚙️  Versão de {proj['name']} agora é {newv}{C_RST}")

    print(f"{C_GRN}🎉 Batch deploy concluído!{C_RST}")


# ───────────────────────────────── main ──────────────────────────────
if __name__ == "__main__":
    settings = Path.cwd() / ".maven" / "settings.xml"
    settings.parent.mkdir(exist_ok=True)
    settings.touch(exist_ok=True)

    if input(f"{C_CYA}▶ Deploy all projects? (y/N): {C_RST}").strip().lower() == "y":
        deploy_all(settings)
    else:
        print("Abortando batch deploy.")
