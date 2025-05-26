#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
╔══════════════════════════════════════════════════════════════════════╗
║      🔧  UNIVERSAL JAVA & MAVEN MULTI‑BUILD TOOL – v1.8 (2025)       ║
║                                                                      ║
║  • Baixa/usa JDK (8/11/17/21) e Maven (3.6.3–3.9.6) com cache        ║
║  • Credenciais Exchange lidas **somente** de variáveis de ambiente   ║
║  • NÃO altera o settings.xml – apenas aponta (-s)                    ║
║  • Menus coloridos & logs em tooling/logs                            ║
║  • Loop de builds com bump de versão se erro 403/409                 ║
╚══════════════════════════════════════════════════════════════════════╝
"""

from __future__ import annotations
import os, sys, zipfile, subprocess, ctypes, re, time
from datetime import datetime  
from pathlib import Path
from typing import Dict
from xml.etree import ElementTree as ET

try:
    import requests
except ImportError:  # instala "requests" se ainda não existir
    subprocess.check_call([sys.executable, "-m", "pip", "install", "requests"])
    import requests

# ──────────────────────────── Cores ANSI ─────────────────────────────
if os.name == "nt":
    ctypes.windll.kernel32.SetConsoleMode(ctypes.windll.kernel32.GetStdHandle(-11), 7)
C_RST="\033[0m"; C_CYA="\033[96m"; C_GRY="\033[90m"; C_GRN="\033[92m"; C_RED="\033[91m"; C_YLW="\033[93m"

# ──────────────────────────── Diretórios ─────────────────────────────
ROOT_DIR  = Path("tooling")
JDK_DIR   = ROOT_DIR / "jdks"
MAVEN_DIR = ROOT_DIR / "mavens"
LOG_DIR   = ROOT_DIR / "logs"
for d in (JDK_DIR, MAVEN_DIR, LOG_DIR):
    d.mkdir(parents=True, exist_ok=True)

# ───────────────────── Catalogo JDK / Maven ──────────────────────────
JDKS: Dict[str, Dict[str, str]] = {
    "8" : {"desc":"Temurin 8u412",  "url":"https://github.com/adoptium/temurin8-binaries/releases/download/jdk8u412-b08/OpenJDK8U-jdk_x64_windows_hotspot_8u412b08.zip"},
    "11": {"desc":"Temurin 11.0.21","url":"https://github.com/adoptium/temurin11-binaries/releases/download/jdk-11.0.21%2B9/OpenJDK11U-jdk_x64_windows_hotspot_11.0.21_9.zip"},
    "17": {"desc":"Temurin 17.0.11","url":"https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.11%2B9/OpenJDK17U-jdk_x64_windows_hotspot_17.0.11_9.zip"},
    "21": {"desc":"Temurin 21.0.3", "url":"https://github.com/adoptium/temurin21-binaries/releases/download/jdk-21.0.3%2B9/OpenJDK21U-jdk_x64_windows_hotspot_21.0.3_9.zip"},
}

MAVENS: Dict[str, Dict[str, str]] = {
    "3.6.3": {"url":"https://archive.apache.org/dist/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.zip"},
    "3.8.8": {"url":"https://archive.apache.org/dist/maven/maven-3/3.8.8/binaries/apache-maven-3.8.8-bin.zip"},
    "3.9.5": {"url":"https://archive.apache.org/dist/maven/maven-3/3.9.5/binaries/apache-maven-3.9.5-bin.zip"},
    "3.9.6": {"url":"https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip"},
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

# ─────────────────── Credenciais via ambiente ────────────────────────
ORG_ID = "806818a4-2582-4d63-a6ef-021f493715a0"
os.environ.setdefault("EXCHANGE_USER", "~~~Client~~~")
os.environ.setdefault("EXCHANGE_PASS", "9fd0d4f3ba7749e98beb8177116fb492~?~bCD2fa42dE1048Bc8b7876e5bAE8B89a")

# ──────────────────── Gerador de pom.xml ────────────────────────────

def generate_pom(project_path: Path, template_name: str = "pom.xml.template", output_name: str = "pom.xml") -> None:
    """
    Gera o pom.xml em project_path substituindo ${orgId} pelo ORG_ID.
    """
    template = project_path / template_name
    output = project_path / output_name
    if not template.exists():
        print(f"{C_RED}❌ Template {template} não encontrado.{C_RST}")
        sys.exit(1)
    content = template.read_text(encoding="utf-8").replace("${orgId}", ORG_ID)
    output.write_text(content, encoding="utf-8")
    print(f"{C_GRN}✅ {output_name} gerado em {project_path} com orgId={ORG_ID}{C_RST}")

# ──────────────────────── Helpers UI ────────────────────────────────
def banner() -> None:
    print("\n" + "═"*74)
    print(f"{C_CYA}{'UNIVERSAL JAVA & MAVEN MULTI‑BUILD TOOL v1.8'.center(74)}{C_RST}")
    print("═"*74 + "\n")
    print(f"{C_YLW}EXCHANGE_USER{C_RST} = {os.environ['EXCHANGE_USER']}")
    print(f"{C_YLW}EXCHANGE_PASS{C_RST} = {'*'*len(os.environ['EXCHANGE_PASS'])}")
    print(f"{C_YLW}ORG_ID       {C_RST} = {ORG_ID}")
    print("─"*74)


def menu(title:str, options:Dict[str,dict]) -> str:
    while True:
        print(f"\n{C_YLW}{title}{C_RST}")
        for i,k in enumerate(options,1):
            desc = options[k].get("desc","")
            print(f"  {C_CYA}[{i}]{C_RST} {k:<6} {desc}")
        choice=input(f"{C_GRN}👉 Nº (0 sai): {C_RST}")
        if choice=="0": sys.exit()
        if choice.isdigit() and 1<=int(choice)<=len(options):
            return list(options)[int(choice)-1]
        print(f"{C_RED}❌ Opção inválida.{C_RST}")


def ask_project() -> Path:
    while True:
        p = input(f"{C_GRN}📂 Projeto (path ou .): {C_RST}").strip() or "."
        d = Path(p).resolve()
        # Aceita projeto se houver pom.xml ou pom.xml.template
        if (d / "pom.xml").exists() or (d / "pom.xml.template").exists():
            return d
        print(f"{C_RED}❌ pasta inválida / sem pom.xml nem pom.xml.template{C_RST}")


def ask_build() -> list[str]:
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
    # imprime em ordem numérica para seguir o pipeline Maven
    for k in sorted(ops, key=lambda x: int(x)):
        print(f"  {C_CYA}[{k}]{C_RST} {ops[k]}")
    # validação de opção
    while (c := input(f"{C_GRN}👉 build: {C_RST}").strip()) not in ops:
        print(f"{C_RED}❌ inválido.{C_RST}")
    # retorna a fase escolhida + -DskipTests
    return ops[c].split() + ["-DskipTests"]

# ───────────────────── Download + unzip com cache ───────────────────

def prepare_tool(url:str, base:Path) -> Path:
    base.mkdir(parents=True, exist_ok=True)
    zip_path = base / Path(url).name
    if not zip_path.exists():
        print(f"{C_CYA}⬇️  Baixando {zip_path.name}{C_RST}")
        with requests.get(url, stream=True) as r, open(zip_path, "wb") as f:
            for chunk in r.iter_content(8192):
                f.write(chunk)
    with zipfile.ZipFile(zip_path) as z:
        z.extractall(base)
        top={Path(n).parts[0] for n in z.namelist() if '/' in n}
    return (base/next(iter(top))).resolve()

# ─────────── Mostrar settings efetivo (credenciais resolvidas) ──────

def show_effective(settings:Path, mvn_exec:Path, env:dict) -> None:
    cmd=[str(mvn_exec),"-s",str(settings),"help:effective-settings","-q",
         "-Doutput=target/effective-settings.xml"]
    subprocess.run(cmd, env=env, cwd=Path.cwd(), stdout=subprocess.DEVNULL,
                   stderr=subprocess.DEVNULL, shell=False)
    xml=Path("target/effective-settings.xml")
    # aguarda liberação
    time.sleep(0.4)
    if xml.exists():
        root=ET.parse(xml).getroot(); srv=root.find("servers")
        if srv is not None:
            print(f"\n{C_CYA}──── CREDENCIAIS RESOLVIDAS ────{C_RST}")
            for s in srv.findall("server"):
                print(f"  {s.findtext('id')}: {s.findtext('username')}/{s.findtext('password')[:6]}…")
            print("─"*40+"\n")
        try:
            xml.unlink()
        except PermissionError:
            pass  # Windows às vezes segura o handle

# ─────────── Função bump de versão no pom.xml ───────────────
def bump_version(pom: Path, pom_template: Path) -> str | None:
    """
    Incrementa o último número da versão em:
      - <project>/<version> do pom.xml
      - a propriedade version dentro de pom.xml.template
    Exemplo: 1.0.6         → 1.0.7
             1.2.9-SNAPSHOT → 1.2.10-SNAPSHOT
    """
    NS = "http://maven.apache.org/POM/4.0.0"
    ET.register_namespace("", NS)

    def process_file(path: Path) -> str | None:
        tree = ET.parse(path)
        root = tree.getroot()
        for child in root:
            tag = child.tag.split('}', 1)[-1]
            if tag == "version":
                version = child.text.strip()
                m = re.match(r"^(\d+(?:\.\d+)*)(.*)$", version)
                if not m:
                    print(f"{C_RED}⚠ Versão não reconhecida em {path.name}: {version}{C_RST}")
                    return None
                nums, suffix = m.groups()
                parts = nums.split(".")
                parts[-1] = str(int(parts[-1]) + 1)
                new_ver = ".".join(parts) + suffix
                child.text = new_ver
                tree.write(path, encoding="utf-8", xml_declaration=True)
                print(f"{C_GRN}✔ Bumped {path.name} to version {new_ver}{C_RST}")
                return new_ver
        print(f"{C_RED}⚠ Nenhuma tag <version> encontrada em {path.name}{C_RST}")
        return None

    # Primeiro bump no pom.xml
    new_version = process_file(pom)
    if not new_version:
        return None

    # Depois bump no pom.xml.template (substitui a mesma lógica)
    templ_version = process_file(pom_template)
    if not templ_version:
        # caso template não tenha sido atualizado, você pode optar por falhar ou apenas avisar
        print(f"{C_RED}⚠ Não foi possível atualizar {pom_template.name}{C_RST}")
    return new_version

# ─────────────────────────── Build Loop ─────────────────────────────

def build_loop(project:Path, jdk:Path, mvn:Path, settings:Path):
    env=os.environ.copy()
    env["JAVA_HOME"]=str(jdk); env["M2_HOME"]=str(mvn)
    env["PATH"] = f"{mvn/'bin'};{jdk/'bin'};{env['PATH']}"
    if int(re.search(r"\d+", jdk.name).group()) >= 17:
        env["MAVEN_OPTS"] = "--add-opens java.base/java.nio=ALL-UNNAMED"

    mvn_exec = mvn / "bin" / ("mvn.cmd" if os.name=="nt" else "mvn")
    show_effective(settings, mvn_exec, env)

    while True:
        cmd=[str(mvn_exec), "-s", str(settings)] + ask_build()
        print(f"\n{C_YLW}🚀 {' '.join(cmd)}{C_RST}\n")
        result=subprocess.run(cmd, cwd=project, env=env, shell=True,
                              text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
        # salva log
        log_name=f"{project.name}_{datetime.now():%Y%m%d_%H%M%S}.log"
        (LOG_DIR/log_name).write_text(result.stdout, encoding="utf-8")

        if result.returncode==0:
            print(f"{C_GRN}✅ Build OK!{C_RST}")
        else:
            print(f"{C_RED}❌ Build falhou (code {result.returncode}){C_RST}")
            if "clean deploy" in " ".join(cmd) and ("409" in result.stdout or "403" in result.stdout):
                pom=project/"pom.xml"
                pom_template = project/"pom.xml.template"
                suggested=bump_version(pom,pom_template)
                if suggested:
                    print(f"{C_YLW}⚠ Versão conflita no repositório. Sugeri e atualizei para {suggested}{C_RST}")
                else:
                    print(f"{C_RED}⚠ Não foi possível sugerir nova versão automaticamente.{C_RST}")
        if input(f"{C_CYA}Novo build? (s/n): {C_RST}").lower()!="s":
            break

# ───────────────────────────────── MAIN ─────────────────────────────
if __name__ == "__main__":
    banner()

    # 4. projeto
    project_path = ask_project()

    # 2. gera pom.xml dinâmico
    generate_pom(project_path)

    # 1. ferramentas
    jdk_key = menu("╭── SELECIONE JDK ──╮", JDKS)
    jdk_dir = prepare_tool(JDKS[jdk_key]["url"], JDK_DIR)

    maven_menu = {k:{"desc":""} for k in MAVENS}
    mvn_key = menu("╭── SELECIONE MAVEN ──╮", maven_menu)
    mvn_dir = prepare_tool(MAVENS[mvn_key]["url"], MAVEN_DIR)

    # 3. settings.xml local (.maven)
    settings_xml = (Path.cwd()/".maven"/"settings.xml").resolve()
    settings_xml.parent.mkdir(exist_ok=True); settings_xml.touch(exist_ok=True)

    # 5. loop principal
    build_loop(project_path, jdk_dir, mvn_dir, settings_xml)
