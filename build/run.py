#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
╔══════════════════════════════════════════════════════════════════════╗
║      🔧  UNIVERSAL JAVA & MAVEN MULTI‑BUILD TOOL – v1.8 (2025)       ║
║                                                                      ║
║  • Baixa/usa JDK (8/11/17/21) e Maven (3.6.3–4.x) com cache          ║
║  • Credenciais Exchange lidas **somente** de variáveis de ambiente   ║
║  • NÃO altera o settings.xml – apenas aponta (-s)                    ║
║  • Menus coloridos & logs em tooling/logs                            ║
║  • Loop de builds com bump de versão se erro 403/409                 ║
╚══════════════════════════════════════════════════════════════════════╝
"""

from __future__ import annotations
import os, sys, zipfile, subprocess, ctypes, re, time, itertools, json
from datetime import datetime
from pathlib import Path
from typing import List, Optional, Dict
from xml.etree import ElementTree as ET

# Try importing requests, install if missing
try:
    import requests
except ImportError:
    subprocess.check_call([sys.executable, "-m", "pip", "install", "requests"])
    import requests

# ──────────────────────────── ANSI Colors ─────────────────────────────
if os.name == "nt":
    ctypes.windll.kernel32.SetConsoleMode(ctypes.windll.kernel32.GetStdHandle(-11), 7)
C_RST = "\033[0m"
C_CYA = "\033[96m"
C_GRN = "\033[92m"
C_RED = "\033[91m"
C_YLW = "\033[93m"

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

# ───────────────────── Configuração Default ──────────────────────────
DEFAULT_CONFIG_CANDIDATES = [
    Path.cwd() / "deploy-config.json",
    Path(__file__).parent / "deploy-config.json"
]

def load_org_id() -> str:
    for path in DEFAULT_CONFIG_CANDIDATES:
        if path.exists():
            try:
                cfg = json.loads(path.read_text(encoding="utf-8"))
                org = cfg.get("groupId")
                if org:
                    return org
                print(f"{C_RED}❌ 'groupId' não encontrado em {path}!{C_RST}")
                sys.exit(1)
            except Exception as e:
                print(f"{C_RED}❌ Erro lendo {path}: {e}{C_RST}")
                sys.exit(1)
    print(f"{C_RED}❌ Nenhum deploy-config.json encontrado!{C_RST}")
    sys.exit(1)

ORG_ID = load_org_id()
os.environ.setdefault("EXCHANGE_USER", "~~~Client~~~")
os.environ.setdefault("EXCHANGE_PASS", "9fd0d4f3ba7749e98beb8177116fb492~?~bCD2fa42dE1048Bc8b7876e5bAE8B89a")

# ──────────────────── Funções Principais ──────────────────────────
def generate_pom(
    project_path: Path,
    template_name: str = "pom.xml.template",
    output_name: str = "pom.xml"
) -> None:
    """
    Gera o pom.xml em project_path substituindo:
      - {{GROUP_ID}}, {{ARTIFACT_ID}}, {{VERSION}} a partir do deploy-config.json
    Procura deploy-config.json em:
      1) projeto
      2) pasta-pai
      3) cwd
      4) diretório do script
    Faz match do projeto por:
      A) path absoluto
      B) nome da pasta (entry['name'])
    """
    # 1) carrega config
    candidates = [
        project_path / "deploy-config.json",
        project_path.parent / "deploy-config.json",
        *DEFAULT_CONFIG_CANDIDATES
    ]
    config_path = next((p for p in candidates if p.exists()), None)
    if not config_path:
        print(f"{C_RED}❌ deploy-config.json não encontrado em <projeto>, <pai> ou cwd!{C_RST}")
        sys.exit(1)

    try:
        cfg = json.loads(config_path.read_text(encoding="utf-8"))
    except json.JSONDecodeError as err:
        print(f"{C_RED}❌ JSON inválido em '{config_path}': {err}{C_RST}")
        sys.exit(1)

    group_id = cfg.get("groupId", ORG_ID)
    base = config_path.parent

    # 2) tenta achar entry por path
    entry = None
    for p in cfg.get("projects", []):
        raw = p.get("path", "")
        cand = Path(raw)
        cand = cand.resolve() if cand.is_absolute() else (base / raw).resolve()
        if cand == project_path.resolve():
            entry = p
            break

    # 3) fallback por nome do artifactId/pasta
    if entry is None:
        entry = next((p for p in cfg.get("projects", [])
                      if p.get("name") == project_path.name), None)

    if not entry or not entry.get("name") or not entry.get("version"):
        names = [p.get("name") for p in cfg.get("projects", [])]
        print(f"{C_RED}❌ Projeto '{project_path.name}' não encontrado em 'projects'! "
              f"Entradas válidas: {names}{C_RST}")
        sys.exit(1)

    # 4) monta blocos de <dependency>
    deps = []
    for dep in entry.get("depends_on", []):
        dep_ent = next((pp for pp in cfg.get("projects", []) if pp["name"] == dep), None)
        if dep_ent:
            deps.append(
                "        <dependency>\n"
                f"            <groupId>{group_id}</groupId>\n"
                f"            <artifactId>{dep_ent['name']}</artifactId>\n"
                f"            <version>{dep_ent['version']}</version>\n"
                "            <classifier>mule-plugin</classifier>\n"
                "        </dependency>"
            )
    dep_xml = "\n".join(deps)

    # 5) processa template
    tpl = project_path / template_name
    if not tpl.exists():
        print(f"{C_RED}❌ Template '{template_name}' não encontrado em {project_path}{C_RST}")
        sys.exit(1)

    content = tpl.read_text(encoding="utf-8")
    content = (
        content
        .replace("{{GROUP_ID}}", group_id)
        .replace("{{ARTIFACT_ID}}", entry["name"])
        .replace("{{VERSION}}", entry["version"])
    )

    # 6) injeta dependências
    if "<!-- AUTO_DEPENDENCIES -->" in content:
        content = content.replace("<!-- AUTO_DEPENDENCIES -->", dep_xml)
    else:
        content = re.sub(r"(</dependencies>)", dep_xml + r"\n\1", content, flags=re.IGNORECASE)

    # 7) grava e reporta
    (project_path / output_name).write_text(content, encoding="utf-8")
    print(
        f"{C_GRN}✅ {output_name} gerado em {project_path}:\n"
        f"   • groupId:    {group_id}\n"
        f"   • artifactId: {entry['name']}\n"
        f"   • version:    {entry['version']}\n"
        f"   • deps:       {', '.join(entry.get('depends_on', [])) or 'nenhuma'}{C_RST}"
    )


# ──────────────────────── Helpers UI ────────────────────────────────
def banner() -> None:
    clear_screen()
    print("\n" + "═"*74)
    print(f"{C_CYA}{'UNIVERSAL JAVA & MAVEN MULTI‑BUILD TOOL v1.8'.center(74)}{C_RST}")
    print("═"*74 + "\n")
    print(f"{C_YLW}EXCHANGE_USER{C_RST} = {os.environ['EXCHANGE_USER']}")
    print(f"{C_YLW}EXCHANGE_PASS{C_RST} = {'*'*len(os.environ['EXCHANGE_PASS'])}")
    print(f"{C_YLW}ORG_ID       {C_RST} = {ORG_ID}")
    print("─"*74)

# cores (exemplo)
C_CYA = "\033[96m"
C_YLW = "\033[93m"
C_GRN = "\033[92m"
C_RED = "\033[91m"
C_RST = "\033[0m"

def clear_screen():
    os.system('cls' if os.name == 'nt' else 'clear')

def menu(title: str, options: Dict[str, dict]) -> Optional[str]:
    """
    Exibe um menu com largura dinâmica baseada em título + desc (ou key).
    Retorna:
      - a chave escolhida (str), seja pelo nº ou pelo próprio key
      - None, se o usuário digitar 'I' (reinício do fluxo)
      - encerra o programa se digitar 'S'
    """
    def draw_bar(done: int, total: int, width: int = 30) -> str:
        filled = int(done / total * width)
        return "[" + "#" * filled + "-" * (width - filled) + "]"

    keys = list(options.keys())
    # lista de descrições: desc ou, se vazio, key
    descs = [options[k].get("desc") or k for k in keys]

    # monta linhas já formatadas
    lines = [f"[{i+1:>2}] {descs[i]}" for i in range(len(keys))]

    # calcula largura da caixa
    max_line = max(len(line) for line in lines)
    width    = max(len(title), max_line) + 4  # margens

    top     = f"╔{'═'*width}╗"
    head    = f"║{title.center(width)}║"
    sep     = f"╠{'═'*width}╣"
    bottom  = f"╚{'═'*width}╝"

    while True:
        clear_screen()
        print(f"{C_CYA}{top}{C_RST}")
        print(f"{C_CYA}{head}{C_RST}")
        print(f"{C_CYA}{sep}{C_RST}")
        for line in lines:
            print(f"{C_CYA}║{C_RST} {line.ljust(width-2)} {C_CYA}║{C_RST}")
        print(f"{C_CYA}{bottom}{C_RST}")

        choice = input(f"\n{C_GRN}👉 Escolha [I=início, S=sair]: {C_RST}").strip()

        # reinício ou saída
        if choice.lower() == "i":
            return None
        if choice.lower() == "s":
            print(f"\n{C_GRN}👋 Até mais!{C_RST}")
            sys.exit(0)

        # escolha por índice
        if choice.isdigit():
            idx = int(choice)
            if 1 <= idx <= len(keys):
                sel_key = keys[idx-1]
            else:
                sel_key = None
        # escolha por key direto
        else:
            sel_key = choice if choice in options else None

        if sel_key:
            # animação de carregamento
            total = 30
            for i in range(total+1):
                bar = draw_bar(i, total)
                pct = i * 100 // total
                print(f"\r{C_YLW}Carregando {descs[keys.index(sel_key)]} {bar} {pct:3d}%{C_RST}", end="", flush=True)
                time.sleep(0.02)
            print("\n")

            # detalhes
            details = options[sel_key]
            print(f"{C_GRN}✔ Selecionado: {sel_key}{C_RST}")
            print(f"  Descrição: {details.get('desc', sel_key)}")
            if url := details.get("url"):
                fname = Path(url).name
                print(f"  Arquivo:   {fname}")
                print(f"  URL:       {url}")
            # outros campos
            for k,v in details.items():
                if k not in ("desc", "url"):
                    print(f"  {k.capitalize()}: {v}")
            time.sleep(1)
            return sel_key

        # inválido
        print(f"\n{C_RED}❌ Opção inválida. Tenta de novo!{C_RST}")
        time.sleep(1)

def ask_project() -> Path:
    """
    Pede ao usuário o path do projeto ('.' por padrão) ou 0 para sair.
    Retorna o Path válido contendo pom.xml ou pom.xml.template.
    """
    while True:
        resposta = input(f"{C_GRN}📂 Projeto (path ou . | 0 sair): {C_RST}").strip() or "."
        if resposta == "0":
            print(f"{C_GRN}👋 Até mais!{C_RST}")
            sys.exit(0)

        d = Path(resposta).resolve()
        # Aceita projeto se houver pom.xml ou pom.xml.template
        if (d / "pom.xml").exists() or (d / "pom.xml.template").exists():
            return d

        print(f"{C_RED}❌ Pasta inválida / sem pom.xml nem pom.xml.template{C_RST}")

def ask_build() -> Optional[List[str]]:
    """
    Exibe um menu de fases Maven em um quadro bonito.
    Retorna:
      - lista de argumentos do Maven (ex: ["clean","install","-DskipTests"])
      - None, se o usuário digitar 0 (para voltar ao início).
      - Encerra o programa se o usuário digitar S (para sair).
    """
    ops: Dict[str, str] = {
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

    title = "SELECIONE A FASE DO BUILD"
    lines = [f"[{k.rjust(2,'0')}] {ops[k]}" for k in sorted(ops, key=lambda x: int(x))]
    max_line = max(len(line) for line in lines)
    width = max(len(title), max_line) + 4

    top    = f"╔{'═'*width}╗"
    head   = f"║{title.center(width)}║"
    sep    = f"╠{'═'*width}╣"
    bottom = f"╚{'═'*width}╝"

    while True:
        clear_screen()
        print(f"{C_CYA}{top}{C_RST}")
        print(f"{C_CYA}{head}{C_RST}")
        print(f"{C_CYA}{sep}{C_RST}")
        for line in lines:
            print(f"{C_CYA}║{C_RST} {line.ljust(width-2)} {C_CYA}║{C_RST}")
        print(f"{C_CYA}{bottom}{C_RST}")

        escolha = input(f"\n{C_GRN}👉 Número (I=Início, S=Sair): {C_RST}").strip()

        if escolha.lower() == "i":
            # sinaliza “voltar ao início” sem encerrar
            return None
        if escolha.lower() == "s":
            print(f"{C_GRN}👋 Até mais!{C_RST}")
            sys.exit(0)

        if escolha in ops:
            print(f"{C_CYA}✔ Selecionado: {ops[escolha]}{C_RST}")
            time.sleep(0.4)
            return ops[escolha].split() + ["-DskipTests"]

        print(f"\n{C_RED}❌ Opção inválida, tenta de novo!{C_RST}")
        time.sleep(1)

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

def show_effective(settings: Path, mvn_exec: Path, env: dict) -> None:
    """
    Executa `mvn help:effective-settings` apontando a um arquivo
    temporário dentro de .maven, evitando criar 'target/' no projeto.
    """
    # garante que .maven exista
    settings_dir = settings.parent
    settings_dir.mkdir(exist_ok=True)

    # define o arquivo de saída dentro de .maven
    effective = settings_dir / "effective-settings.xml"

    # executa o comando
    cmd = [
        str(mvn_exec),
        "-s", str(settings),
        "help:effective-settings",
        "-q",
        f"-Doutput={effective}"
    ]
    subprocess.run(cmd, env=env, cwd=Path.cwd(),
                   stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)

    # dá uma breve pausa até o arquivo ser criado
    time.sleep(0.2)

    if effective.exists():
        root = ET.parse(effective).getroot()
        srv  = root.find("servers")
        if srv is not None:
            print(f"\n{C_CYA}──── CREDENCIAIS RESOLVIDAS ────{C_RST}")
            for s in srv.findall("server"):
                user = s.findtext("username")
                pwd  = s.findtext("password")[:6] + "…"
                print(f"  {s.findtext('id')}: {user}/{pwd}")
            print("─" * 40 + "\n")
        # remove o arquivo temporário
        try:
            effective.unlink()
        except Exception:
            pass  # cupom temporário limpo

# ─────────── Função bump de versão no pom.xml ───────────────
def bump_version(pom: Path, pom_template: Path) -> str | None:
    """
    Incrementa a última parte da versão no deploy-config.json após um build OK.
    Procura deploy-config.json em:
      - cwd
      - script-dir
      - pasta do projeto
      - pasta-pai do projeto
    Faz match do projeto por path absoluto ou nome da pasta.
    """
    # 1) encontra o JSON de configuração
    candidates = [
        Path.cwd() / "deploy-config.json",
        Path(__file__).parent / "deploy-config.json",
        pom.parent / "deploy-config.json",
        pom.parent.parent / "deploy-config.json"
    ]
    config_path = next((p for p in candidates if p.exists()), None)
    if not config_path:
        print(f"{C_RED}❌ deploy-config.json não encontrado!{C_RST}")
        return None

    # 2) carrega JSON
    try:
        cfg = json.loads(config_path.read_text(encoding="utf-8"))
    except Exception as e:
        print(f"{C_RED}❌ Erro lendo JSON '{config_path}': {e}{C_RST}")
        return None

    # 3) resolve entry pelo path
    base = config_path.parent
    project_dir = pom.parent.resolve()
    entry = None
    for proj in cfg.get("projects", []):
        raw = proj.get("path", "")
        cand = Path(raw)
        cand = cand.resolve() if cand.is_absolute() else (base / raw).resolve()
        if cand == project_dir:
            entry = proj
            break

    # 4) fallback por nome da pasta/artifactId
    if entry is None:
        entry = next(
            (proj for proj in cfg.get("projects", [])
             if proj.get("name") == project_dir.name),
            None
        )

    if not entry or not entry.get("version"):
        valid = [p.get("name") for p in cfg.get("projects",[])]
        print(f"{C_RED}❌ Projeto '{project_dir.name}' não encontrado em 'projects'! Entradas válidas: {valid}{C_RST}")
        return None

    # 5) bump da versão
    old_ver = entry["version"]
    m = re.match(r"^(\d+(?:\.\d+)*)(.*)$", old_ver)
    if not m:
        print(f"{C_RED}⚠ Versão não reconhecida: {old_ver}{C_RST}")
        return None
    nums, suffix = m.groups()
    parts = nums.split(".")
    parts[-1] = str(int(parts[-1]) + 1)
    new_ver = ".".join(parts) + suffix
    entry["version"] = new_ver

    # 6) salva de volta
    try:
        config_path.write_text(json.dumps(cfg, indent=2, ensure_ascii=False), encoding="utf-8")
        print(f"{C_GRN}✔ Versão bumpada de {old_ver} para {new_ver} no {config_path}{C_RST}")
        return new_ver
    except Exception as e:
        print(f"{C_RED}❌ Falha ao escrever '{config_path}': {e}{C_RST}")
        return None

# ─────────────────────────── Build Loop ─────────────────────────────

def build_loop(project: Path, jdk: Path, mvn: Path, settings: Path) -> bool:
    """
    Executa o loop de build com saída em tempo real e spinner quando não há novas linhas.
    Retorna:
      - True  => reiniciar o fluxo principal
      - False => encerrar o script
    """
    # configurações de ambiente
    env = os.environ.copy()
    env["JAVA_HOME"] = str(jdk)
    env["M2_HOME"]   = str(mvn)
    env["PATH"]      = f"{mvn/'bin'};{jdk/'bin'};{env['PATH']}"

    # adiciona --add-opens se JDK >= 17
    if int(re.search(r"\d+", jdk.name).group()) >= 17:
        env["MAVEN_OPTS"] = "--add-opens java.base/java.nio=ALL-UNNAMED"

    mvn_exec = mvn / "bin" / ("mvn.cmd" if os.name == "nt" else "mvn")
    show_effective(settings, mvn_exec, env)

    while True:
        cmd_args: Optional[List[str]] = ask_build()
        if cmd_args is None:
            return True

        cmd = [str(mvn_exec), "-s", str(settings)] + cmd_args
        print(f"\n{C_YLW}🚀 Executando: {' '.join(cmd)}{C_RST}")

        proc = subprocess.Popen(
            cmd,
            cwd=project,
            env=env,
            shell=False,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            text=True
        )
        spinner = itertools.cycle(['|','/','-','\\'])
        log_buf: List[str] = []

        # lê a saída em tempo real, mostra linhas ou spinner
        while True:
            line = proc.stdout.readline()
            if line:
                # imprime linha de log
                sys.stdout.write(line)
                sys.stdout.flush()
                log_buf.append(line)
            else:
                if proc.poll() is not None:
                    # processo terminou
                    break
                # sem nova linha, mostra spinner
                sys.stdout.write(f"\r{C_YLW}{next(spinner)} Aguarde...{C_RST}")
                sys.stdout.flush()
                time.sleep(0.1)

        # captura restante após fim
        remaining = proc.stdout.read()
        if remaining:
            sys.stdout.write(remaining)
            log_buf.append(remaining)

        # salva log completo
        full_output = ''.join(log_buf)
        log_name = f"{project.name}_{datetime.now():%Y%m%d_%H%M%S}.log"
        (LOG_DIR / log_name).write_text(full_output, encoding="utf-8")

        # relatório final
        if proc.returncode == 0:
            print(f"{C_GRN}✅ Build OK!{C_RST}")
            # Sempre que o build for OK, faz o bump no deploy-config.json
            new_version = bump_version(project/"pom.xml", project/"pom.xml.template")
            if new_version:
                print(f"{C_YLW}✔ Versão do projeto atualizada no deploy-config.json: {new_version}{C_RST}")
        else:
            print(f"{C_RED}❌ Build falhou (code {proc.returncode}){C_RST}")
            if "clean deploy" in ' '.join(cmd) and any(e in full_output for e in ("409","403")):
                suggested = bump_version(project/"pom.xml", project/"pom.xml.template")
                if suggested:
                    print(f"{C_YLW}⚠ Versão conflitante. Atualizei para {suggested}{C_RST}")
                else:
                    print(f"{C_RED}⚠ Não foi possível sugerir nova versão automaticamente.{C_RST}")
        input(f"{C_CYA}\nPressione Enter para continuar...{C_RST}")

# ───────────────────────────────── MAIN ─────────────────────────────
if __name__ == "__main__":
    while True:
        # 0. banner inicial
        banner()

        # 1. seleção de projeto
        project_path = ask_project()
        if project_path is None:
            continue  # reinicia se pressionou I

        # 2. gera pom.xml dinâmico
        generate_pom(project_path)

        # 3. escolha do JDK
        jdk_key = menu("SELECIONE JDK", JDKS)
        if jdk_key is None:
            continue  # reinicia se pressionou I
        jdk_dir = prepare_tool(JDKS[jdk_key]["url"], JDK_DIR)

        # 4. escolha do Maven
        mvn_key = menu("SELECIONE MAVEN", MAVENS)
        if mvn_key is None:
            continue  # reinicia se pressionou I
        mvn_dir = prepare_tool(MAVENS[mvn_key]["url"], MAVEN_DIR)

        # 5. prepara settings.xml local
        settings_xml = (Path.cwd() / ".maven" / "settings.xml").resolve()
        settings_xml.parent.mkdir(exist_ok=True)
        settings_xml.touch(exist_ok=True)

        # 6. entra no loop de builds
        #    retorna False para sair, True para reiniciar
        if not build_loop(project_path, jdk_dir, mvn_dir, settings_xml):
            print(f"{C_GRN}👋 Encerrando o Universal Build Tool. Até mais!{C_RST}")
            break
