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
╚══════════════════════════════════════════════════════════════════════╝
"""
from build.imports import *  # noqa: F403, F401

def extract_parent_from_default(parent_pom_path: Path) -> dict:
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

# Extrai artifactId e version de um pom.xml simples

def extract_pom_coords(project_path: Path) -> tuple[str, str]:
    pom_file = project_path / "pom.xml"
    if not pom_file.exists():
        return "", ""
    try:
        tree = ET.parse(pom_file)
        root = tree.getroot()
        ns = {}
        if '}' in root.tag:
            uri = root.tag.split('}')[0].strip('{')
            ns = {'m': uri}
        if ns:
            artifact = root.findtext('m:artifactId', namespaces=ns) or ""
            version = root.findtext('m:version', namespaces=ns) or ""
        else:
            artifact = root.findtext('artifactId') or ""
            version = root.findtext('version') or ""
        return artifact, version
    except Exception:
        return "", ""

# Lê o bom_config.json para extrair repositórios e distribuição

def load_bom_settings(bom_json_path: Path) -> tuple[list, list, dict]:
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

# Grava JSON no deploy_config.json

def write_deploy_config(cfg: dict, config_path: Path) -> None:
    config_path.write_text(json.dumps(cfg, indent=2, ensure_ascii=False), encoding="utf-8")

# Formata XML

def _pretty_xml(raw: str, indent: int = 2) -> str:
    parsed = minidom.parseString(raw.encode("utf-8"))
    pretty = parsed.toprettyxml(indent=" " * indent, newl="\n")
    lines = [line.rstrip() for line in pretty.splitlines() if line.strip()]
    pretty = "\n".join(lines)
    pretty = re.sub(
        r"<project\s+([^>]+?)\s*>",
        lambda m: "<project " + re.sub(r"\s{2,}", " ", m.group(1).strip()) + ">",
        pretty,
        flags=re.DOTALL,
    )
    return pretty

# Encontra deploy_config.json

def find_config_path(project_path: Path) -> Path | None:
    candidates = [project_path / "deploy_config.json", project_path.parent / "deploy_config.json", *DEFAULT_CONFIG_CANDIDATES]
    return next((p for p in candidates if p.exists()), None)

# Gera pom.xml via template, inserindo parent, repositórios e distribuição adequados

def generate_pom(
    project_path: Path,
    template_name: str = "pom.xml.template",
    output_name: str = "pom.xml",
) -> Path:
    config_path = find_config_path(project_path)
    if not config_path:
        print(f"{C_RED}❌ deploy_config.json não encontrado!{C_RST}")
        sys.exit(1)
    try:
        cfg = json.loads(config_path.read_text(encoding="utf-8"))
    except json.JSONDecodeError as err:
        print(f"{C_RED}❌ JSON inválido em '{config_path}': {err}{C_RST}")
        sys.exit(1)
    base_cfg_dir = config_path.parent
    entry = None
    for p in cfg.get("projects", []):
        raw = p.get("path", "")
        cand = Path(raw)
        cand = cand.resolve() if cand.is_absolute() else (base_cfg_dir / raw).resolve()
        if cand == project_path.resolve():
            entry = p
            break
    if entry is None:
        entry = next((p for p in cfg.get("projects", []) if p.get("name") == project_path.name), None)
    # Se não existir, insere entrada mínima genérica
    if entry is None:
        artifact, version = extract_pom_coords(project_path)
        relative_path = os.path.relpath(project_path, base_cfg_dir).replace('\\', '/')
        new_entry = {"name": project_path.name, "path": relative_path, "jdk": "8", "maven": "3.9.5", "goal": "clean deploy -DskipTests", "deploy": True, "version": version or ""}
        cfg["projects"].append(new_entry)
        write_deploy_config(cfg, config_path)
        cfg = json.loads(config_path.read_text(encoding="utf-8"))
        entry = next((p for p in cfg["projects"] if p["name"] == project_path.name), None)
        if entry is None:
            print(f"{C_RED}❌ Falha ao inserir no JSON{C_RST}")
            sys.exit(1)
    if not entry.get("version"):
        print(f"{C_YLW}[WARN] Projeto '{project_path.name}' sem 'version'; gerando POM vazio.{C_RST}")
    dependencies: list[dict] = []
    for dep_name in entry.get("depends_on", []):
        dep_entry = next((pp for pp in cfg.get("projects", []) if pp["name"] == dep_name), None)
        if dep_entry:
            dependencies.append({"groupId": ORG_ID, "artifactId": dep_entry["name"], "version": dep_entry["version"], "classifier": "mule-plugin"})
    dependencies.extend(entry.get("dependencies", []))
    developers = entry.get("developers", [])
    # Determina parent: se entry tiver parent próprio, usa campos e adiciona repositórios, pluginRepos e distribuição do bom_config.json
    if entry.get("parent"):
        parent = entry["parent"]
        # Carrega repos do bom_config.json
        repos, plugin_repos, dist_mgmt = load_bom_settings(DEFAULT_BOM_JSON)
        repositories = repos
        pluginRepositories = plugin_repos
        distributionManagement = dist_mgmt
    else:
        parent = extract_parent_from_default(DEFAULT_PARENT_OUT)
        repositories = entry.get("repositories", [])
        pluginRepositories = entry.get("pluginRepositories", [])
        distributionManagement = entry.get("distributionManagement", None)
    context = {
        "groupId": ORG_ID,
        "artifactId": entry["name"],
        "version": entry.get("version", ""),
        "name": entry.get("displayName", entry["name"]),
        "developers": developers,
        "dependencies": dependencies,
        "parent": parent,
        "properties": entry.get("properties", {}),
        "dependencyManagement": entry.get("dependencyManagement", []),
        "repositories": repositories,
        "pluginRepositories": pluginRepositories,
        "distributionManagement": distributionManagement,
    }
    TEMPLATE_DIR = WORKSPACE_DIR / "build" / "templates" / "pom_templates"
    if not TEMPLATE_DIR.exists():
        print(f"{C_RED}❌ Diretório de templates não encontrado: {TEMPLATE_DIR}{C_RST}")
        sys.exit(1)
    env = Environment(loader=FileSystemLoader(searchpath=str(TEMPLATE_DIR)), autoescape=False, trim_blocks=True, lstrip_blocks=True)
    try:
        template = env.get_template(template_name)
    except Exception as e:
        print(f"{C_RED}❌ Template não encontrado: {e}{C_RST}")
        sys.exit(1)
    content_raw = template.render(**context).strip()
    try:
        content_pretty = _pretty_xml(content_raw, indent=2)
    except Exception:
        content_pretty = content_raw
    output_file = project_path / output_name
    output_file.write_text(content_pretty, encoding="utf-8")
    print(f"{C_GRN}✅ {output_name} gerado em {project_path}: groupId={ORG_ID} version={entry.get('version','')}{C_RST}")
    return config_path

# ──────────────────────── Helpers UI ────────────────────────────────

def banner() -> None:
    clear_screen()
    print("\n" + "═" * 80)
    print(f"{C_CYA}{'UNIVERSAL JAVA & MAVEN MULTI-BUILD TOOL v2.0'.center(80)}{C_RST}")
    print("═" * 80 + "\n")
    print(f"{C_YLW}🔑 EXCHANGE_USER{C_RST} = {os.environ.get('EXCHANGE_USER', '[não definido]')}" )
    print(f"{C_YLW}🔑 EXCHANGE_PASS{C_RST} = {os.environ.get('EXCHANGE_PASS', '[não definido]')}" )
    print(f"{C_YLW}🏷️  ORG_ID       {C_RST} = {ORG_ID}")
    print("─" * 80)
    print(f"📂 Diretório atual: {WORKSPACE_DIR / 'projects'}")
    print("─" * 80)


def clear_screen():
    os.system('cls' if os.name == 'nt' else 'clear')

# ─────────────────── Menu Principal & Submenus ─────────────────────

def menu_principal() -> str:
    options = {
        'build_all': {'desc': '🚀 Build TODOS os projetos'},
        'list_projects': {'desc': '📂 Listar projetos (filtrar/paginar)'},
        'build_bom_parent': {'desc': '🛠️ Build BOM & Parent POM'},
        'auto_commit': {'desc': '🔄 Commit automático no Git'},
        'exit': {'desc': 'Sair'}
    }
    title = 'MENU PRINCIPAL'
    return menu_generic(title, options)


def menu_generic(title: str, options: dict) -> str:
    keys = list(options.keys())
    descs = [options[k].get('desc') for k in keys]
    lines = [f"[{i+1:>2}] {descs[i]}" for i in range(len(keys))]
    max_line = max(len(line) for line in lines)
    width = max(len(title), max_line) + 4
    top = f"╔{'═'*width}╗"
    head = f"║{title.center(width)}║"
    sep = f"╠{'═'*width}╣"
    bottom = f"╚{'═'*width}╝"
    while True:
        clear_screen()
        print(f"{C_CYA}{top}{C_RST}")
        print(f"{C_CYA}{head}{C_RST}")
        print(f"{C_CYA}{sep}{C_RST}")
        for line in lines:
            print(f"{C_CYA}║{C_RST} {line.ljust(width-2)} {C_CYA}║{C_RST}")
        print(f"{C_CYA}{bottom}{C_RST}")
        choice = input(f"\n{C_GRN}👉 Escolha: {C_RST}").strip().lower()
        if choice.isdigit():
            idx = int(choice)
            if 1 <= idx <= len(keys):
                return keys[idx-1]
        if choice in ['s', 'exit'] and 'exit' in keys:
            return 'exit'
        print(f"\n{C_RED}❌ Opção inválida. Tente novamente.{C_RST}")
        time.sleep(1)

# ─────────────────── Submenu Listar Projetos ─────────────────────

def ask_list_projects() -> Path | None:
    print(f"\n{C_YLW}ℹ️ Digite parte do nome do projeto para filtrar (ou Enter para listar todos em páginas, exceto 'mule-parent-bom-main'):{C_RST}")
    termo = input(f"{C_GRN}👉 Filtro: {C_RST}").strip().lower()
    projects_dir = WORKSPACE_DIR / "projects"
    # Obtém todos os diretórios, exceto mule-parent-bom-main
    project_paths = sorted([p.name for p in projects_dir.iterdir() if p.is_dir() and p.name != 'mule-parent-bom-main'])
    if termo:
        filtrados = [name for name in project_paths if termo in name.lower()]
        if not filtrados:
            print(f"{C_RED}❌ Nenhum projeto encontrado para '{termo}'.{C_RST}")
            time.sleep(1)
            return None
        return submenu_selecao_lista(filtrados)
    else:
        return submenu_paginacao_lista(project_paths)

def submenu_selecao_lista(lista: list[str]) -> Path | None:
    title = f"RESULTADOS DA BUSCA ({len(lista)} itens)"
    keys = lista
    descs = lista
    return menu_selecao_dinamica(title, keys, descs)


def submenu_paginacao_lista(lista: list[str], page_size: int = 20) -> Path | None:
    total = len(lista)
    pages = (total + page_size - 1) // page_size
    page = 1
    while True:
        start = (page-1)*page_size
        end = min(start + page_size, total)
        subset = lista[start:end]
        clear_screen()
        print("═" * 80)
        print(f"LISTA DE PROJETOS (Página {page}/{pages}) — Exibindo {start+1} a {end} de {total}".center(80))
        print("═" * 80)
        for idx, name in enumerate(subset, start=start+1):
            print(f"[{idx:>3}] {name}")
        print("═" * 80)
        print(f"[N] Próxima página   [P] Página anterior   [0] Voltar ao Menu Principal")
        choice = input(f"{C_GRN}👉 Digite o número do projeto (ou N/P/0): {C_RST}").strip().lower()
        if choice == 'n' and page < pages:
            page += 1
            continue
        if choice == 'p' and page > 1:
            page -= 1
            continue
        if choice == '0':
            return None
        if choice.isdigit():
            idx = int(choice)
            if 1 <= idx <= total:
                return (WORKSPACE_DIR / "projects" / lista[idx-1]).resolve()
        print(f"{C_RED}❌ Opção inválida. Tente novamente.{C_RST}")
        time.sleep(1)


def menu_selecao_dinamica(title: str, keys: list[str], descs: list[str]) -> Path | None:
    max_line = max(len(f"[{i+1:>3}] {descs[i]}") for i in range(len(keys)))
    width = max(len(title), max_line) + 4
    top = f"╔{'═'*width}╗"
    head = f"║{title.center(width)}║"
    sep = f"╠{'═'*width}╣"
    bottom = f"╚{'═'*width}╝"
    while True:
        clear_screen()
        print(f"{C_CYA}{top}{C_RST}")
        print(f"{C_CYA}{head}{C_RST}")
        print(f"{C_CYA}{sep}{C_RST}")
        for i, desc in enumerate(descs, start=1):
            line = f"[{i:>3}] {desc}"
            print(f"{C_CYA}║{C_RST} {line.ljust(width-2)} {C_CYA}║{C_RST}")
        print(f"{C_CYA}{bottom}{C_RST}")
        choice = input(f"\n{C_GRN}👉 Selecione o número (ou 0 para voltar): {C_RST}").strip()
        if choice == '0':
            return None
        if choice.isdigit():
            idx = int(choice)
            if 1 <= idx <= len(keys):
                return (WORKSPACE_DIR / "projects" / keys[idx-1]).resolve()
        print(f"{C_RED}❌ Opção inválida. Tente novamente.{C_RST}")
        time.sleep(1)

# ─────────────────── Submenu Build BOM & Parent ─────────────────────

def submenu_build_bom_parent() -> list[str] | None:
    ops: dict[str, str] = {
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

        choice = input(f"\n{C_GRN}👉 Número (I=Início, S=Sair): {C_RST}").strip().lower()

        if choice == 'i':
            return None
        if choice == 's':
            print(f"{C_GRN}👋 Até mais!{C_RST}")
            sys.exit(0)

        if choice.isdigit() and choice in ops:
            print(f"{C_CYA}✔ Selecionado: {ops[choice]}{C_RST}")
            time.sleep(0.4)
            return ops[choice].split() + ["-DskipTests"]

        print(f"\n{C_RED}❌ Opção inválida. Tente novamente.{C_RST}")
        time.sleep(1)

# ─────────────────── Submenu Commit Automático ─────────────────────

def submenu_auto_commit() -> None:
    clear_screen()
    print("═" * 80)
    print(f"{'COMMIT AUTOMÁTICO NO GIT'.center(80)}")
    print("═" * 80)
    repo_dir = WORKSPACE_DIR
    print(f"🗂️  Diretório do Git atual: {repo_dir}")
    msg = input(f"{C_GRN}✏️  Mensagem de commit (ou Enter para padrão '[auto] build'): {C_RST}").strip()
    if not msg:
        msg = "[auto] build"
    try:
        subprocess.run(["git", "-C", str(repo_dir), "add", "."], check=True)
        subprocess.run(["git", "-C", str(repo_dir), "commit", "-m", msg], check=True)
        subprocess.run(["git", "-C", str(repo_dir), "push"], check=True)
        print(f"{C_GRN}✅ Commit e push bem-sucedidos!{C_RST}")
    except subprocess.CalledProcessError as e:
        print(f"{C_RED}❌ Erro no Git: {e}{C_RST}")
    input("\nPressione Enter para voltar ao Menu Principal...")

# ─────────────────── Paginação e Build Loop ─────────────────────────

def bump_version(pom: Path, config_path: Path, build_phase: list[str]) -> str | None:
    """
    Se config_path for bom_config.json ou parent_config.json, incrementa a chave "version"
    top-level desse JSON e grava de volta.
    Caso contrário, assume que config_path é um deploy_config.json (com lista "projects")
    e faz o bump no projeto cujo nome corresponda a project_dir.name.
    """

    # 1) Somente faz bump se houver "deploy" na build_phase
    if "deploy" not in build_phase:
        return None

    # 2) Carrega o JSON inteiro
    try:
        cfg = json.loads(config_path.read_text(encoding="utf-8"))
    except Exception as e:
        print(f"{C_RED}❗️ Erro lendo JSON '{config_path}': {e}{C_RST}")
        return None

    # 3) Se for bom_config.json ou parent_config.json, incrementa "version" top-level
    if config_path.name in ("bom_config.json", "parent_config.json"):
        old_ver = cfg.get("version", None)
        if not old_ver:
            print(f"{C_RED}❗️ Chave 'version' não encontrada em {config_path.name}{C_RST}")
            return None

        m = re.match(r"^(\d+(?:\.\d+)*)(.*)$", old_ver)
        if not m:
            print(f"{C_RED}⚠️ Versão não reconhecida em {config_path.name}: {old_ver}{C_RST}")
            return None

        nums, suffix = m.groups()
        parts = nums.split('.')
        parts[-1] = str(int(parts[-1]) + 1)
        new_ver = ".".join(parts) + suffix
        cfg["version"] = new_ver

        try:
            config_path.write_text(json.dumps(cfg, indent=2, ensure_ascii=False), encoding="utf-8")
            print(f"{C_GRN}✔ Versão bumpada em '{config_path.name}': de {old_ver} para {new_ver}{C_RST}")
            return new_ver
        except Exception as e:
            print(f"{C_RED}❗️ Falha ao escrever '{config_path}': {e}{C_RST}")
            return None

    # 4) Caso contrário, trata como deploy_config.json: procura em cfg["projects"]
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

    if entry is None:
        entry = next((proj for proj in cfg.get("projects", [])
                      if proj.get("name") == project_dir.name), None)

    if not entry or not entry.get("version"):
        valid = [p.get("name") for p in cfg.get("projects", [])]
        print(f"{C_RED}❗️ Projeto '{project_dir.name}' não encontrado em 'projects'! Entradas válidas: {valid}{C_RST}")
        return None

    old_ver = entry["version"]
    m = re.match(r"^(\d+(?:\.\d+)*)(.*)$", old_ver)
    if not m:
        print(f"{C_RED}⚠️ Versão não reconhecida: {old_ver}{C_RST}")
        return None

    nums, suffix = m.groups()
    parts = nums.split('.')
    parts[-1] = str(int(parts[-1]) + 1)
    new_ver = ".".join(parts) + suffix
    entry["version"] = new_ver

    try:
        config_path.write_text(json.dumps(cfg, indent=2, ensure_ascii=False), encoding="utf-8")
        print(f"{C_GRN}✔ Versão bumpada de {old_ver} para {new_ver} em '{config_path.name}'{C_RST}")
        return new_ver
    except Exception as e:
        print(f"{C_RED}❗️ Falha ao escrever '{config_path}': {e}{C_RST}")
        return None


def show_effective(settings: Path, mvn_exec: Path, env: dict) -> None:
    settings_dir = settings.parent
    settings_dir.mkdir(exist_ok=True)
    effective = settings_dir / "effective-settings.xml"
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
            print(f"\n{C_CYA}──── CREDENCIAIS RESOLVIDAS ────{C_RST}")
            for s in srv.findall("server"):
                user = s.findtext("username")
                pwd  = s.findtext("password")[:6] + "…"
                print(f"  {s.findtext('id')}: {user}/{pwd}")
            print("─" * 40 + "\n")
        try:
            effective.unlink()
        except Exception:
            pass


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
        top = {Path(n).parts[0] for n in z.namelist() if '/' in n}
    return (base / next(iter(top))).resolve()


def build_loop(
    project: Path,
    jdk: Path | None,
    mvn: Path,
    settings: Path,
    config_path: Path,
    build_phase: list[str]
) -> bool:
    env = os.environ.copy()

    # ── Se jdk for None ou Path("") vazio, não força JAVA_HOME nem altera PATH
    if jdk and str(jdk).strip():
        env["JAVA_HOME"] = str(jdk)
        env["PATH"] = f"{mvn/'bin'};{jdk/'bin'};{env['PATH']}"
        env["M2_HOME"] = str(mvn)
        nome_jdk = jdk.name
        versao_num = re.search(r"\d+", nome_jdk)
        if versao_num and int(versao_num.group()) >= 17:
            env["MAVEN_OPTS"] = ""  # adicionar flags se necessário
    else:
        # jdk não informado → usa JAVA_HOME atual do sistema, só configura M2_HOME e mvn/bin no PATH
        existing_home = os.environ.get("JAVA_HOME", "")
        if existing_home:
            env["JAVA_HOME"] = existing_home
        env["M2_HOME"] = str(mvn)
        env["PATH"] = f"{mvn/'bin'};{env['PATH']}"

    mvn_exec = mvn / "bin" / ("mvn.cmd" if os.name == "nt" else "mvn")
    show_effective(settings, mvn_exec, env)

    cmd = [str(mvn_exec), "-s", str(settings)] + build_phase
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

    spinner = itertools.cycle(["|", "/", "-", "\\"])
    log_buf: list[str] = []

    while True:
        line = proc.stdout.readline()
        if line:
            sys.stdout.write(line)
            sys.stdout.flush()
            log_buf.append(line)
        else:
            if proc.poll() is not None:
                break
            sys.stdout.write(f"\r{C_YLW}{next(spinner)} Aguarde...{C_RST}")
            sys.stdout.flush()
            time.sleep(0.1)

    remaining = proc.stdout.read()
    if remaining:
        sys.stdout.write(remaining)
        log_buf.append(remaining)

    full_output = "".join(log_buf)
    LOG_DIR.mkdir(parents=True, exist_ok=True)
    log_name = f"{project.name}_{time.strftime('%Y%m%d_%H%M%S')}.log"
    (LOG_DIR / log_name).write_text(full_output, encoding="utf-8")

    if proc.returncode == 0:
        print(f"{C_GRN}✅ Build OK!{C_RST}")

        # ── Aqui, só faz bump depois de "deploy" — bump_version já verifica isso
        new_version = bump_version(project / "pom.xml", config_path, build_phase)
        if new_version:
            print(f"{C_YLW}✔ Versão atualizada em {config_path.name}: {new_version}{C_RST}")

        return True

    else:
        print(f"{C_RED}❌ Build falhou (code {proc.returncode}){C_RST}")
        return False


def resolve_build_order(cfg: dict) -> list[dict]:
    projects = {p['name']: p for p in cfg.get("projects", [])}
    resolved = []
    visited = set()
    def visit(name):
        if name in visited:
            return
        project = projects.get(name)
        if not project:
            print(f"{C_RED}❌ Projeto '{name}' não encontrado no config!{C_RST}")
            sys.exit(1)
        for dep in project.get("depends_on", []):
            visit(dep)
        visited.add(name)
        resolved.append(project)
    for name in projects:
        visit(name)
    return resolved


def build_all_projects(config_path: Path) -> None:
    try:
        cfg = json.loads(config_path.read_text(encoding="utf-8"))
    except Exception as e:
        print(f"{C_RED}❗️ Erro lendo JSON '{config_path}': {e}{C_RST}")
        return
    build_phase = submenu_build_bom_parent()
    if not build_phase:
        print(f"{C_YLW}⚠️ Nenhuma fase selecionada, abortando...{C_RST}")
        return
    ordered_projects = resolve_build_order(cfg)
    for proj in ordered_projects:
        if not proj.get("deploy", True):
            print(f"{C_YLW}🚫 Projeto {proj['name']} marcado como deploy: false. Pulando...{C_RST}")
            continue
        print(f"\n{C_YLW}🔨 Build do projeto: {proj['name']}{C_RST}")
        project_dir = (WORKSPACE_DIR / "projects" / proj["name"]).resolve()
        if not (project_dir / "pom.xml").exists():
            generate_pom(project_dir)
        jdk_key = proj.get("jdk")
        maven_key = proj.get("maven")
        jdk_dir = prepare_tool(JDKS[jdk_key]["url"], JDK_DIR)
        mvn_dir = prepare_tool(MAVENS[maven_key]["url"], MAVEN_DIR)
        settings_xml = (Path.cwd() / ".maven" / "settings.xml").resolve()
        settings_xml.parent.mkdir(exist_ok=True)
        settings_xml.touch(exist_ok=True)
        if not build_loop(project_dir, jdk_dir, mvn_dir, settings_xml, config_path, build_phase):
            print(f"{C_RED}⚠️ Build falhou para {proj['name']}. Encerrando sequência.{C_RST}")
            break
    print(f"{C_GRN}\n🎉 Todos os projetos foram processados!{C_RST}")

# ───────────────────────────────── MAIN ─────────────────────────────

if __name__ == "__main__":
    while True:
        banner()
        escolha = menu_principal()
        if escolha == 'exit':
            print(f"{C_GRN}👋 Até mais!{C_RST}")
            sys.exit(0)

        if escolha == 'build_all':
            config_path = find_config_path(WORKSPACE_DIR / 'projects')
            if not config_path:
                print(f"{C_RED}❌ Configuração não encontrada!{C_RST}")
                sys.exit(1)
            build_all_projects(config_path)
            input("\nPressione Enter para voltar ao Menu Principal...")

        elif escolha == 'list_projects':
            selecionado = ask_list_projects()
            if selecionado:
                config_path = generate_pom(selecionado)
                jdk_key = menu_generic('SELECIONE JDK', {k: {'desc': k} for k in JDKS})
                if not jdk_key:
                    continue
                jdk_dir = prepare_tool(JDKS[jdk_key]['url'], JDK_DIR)
                mvn_key = menu_generic('SELECIONE MAVEN', {k: {'desc': k} for k in MAVENS})
                if not mvn_key:
                    continue
                mvn_dir = prepare_tool(MAVENS[mvn_key]['url'], MAVEN_DIR)
                settings_xml = (Path.cwd() / ".maven" / "settings.xml").resolve()
                settings_xml.parent.mkdir(exist_ok=True)
                settings_xml.touch(exist_ok=True)
                phase = submenu_build_bom_parent()
                if phase:
                    build_loop(selecionado, jdk_dir, mvn_dir, settings_xml, config_path, phase)
                    input("\nPressione Enter para voltar ao Menu Principal...")

        elif escolha == 'build_bom_parent':
            # Etapa 1: gerar BOM e Parent POM
            selecionado = WORKSPACE_DIR / "projects" / "mule-parent-bom-main"
            if not selecionado.exists():
                print(f"{C_RED}❌ Projeto 'mule-parent-bom-main' não encontrado em 'projects'.{C_RST}")
                continue

            bom_script = WORKSPACE_DIR / "build" / "bom_config.py"
            if bom_script.exists():
                print(f"{C_YLW}🔄 Executando bom_config.py...{C_RST}")
                subprocess.run([
                    sys.executable,
                    str(bom_script),
                    "--json", str(DEFAULT_BOM_JSON),
                    "--tpl",  str(DEFAULT_BOM_TPL),
                    "--out",  str(DEFAULT_BOM_OUT)
                ], check=True)
            else:
                print(f"{C_RED}⚠️ bom_config.py não encontrado: {bom_script}{C_RST}")
                continue

            parent_script = WORKSPACE_DIR / "build" / "parent_config.py"
            if parent_script.exists():
                print(f"{C_YLW}🔄 Executando parent_config.py...{C_RST}")
                subprocess.run([
                    sys.executable,
                    str(parent_script),
                    "--json", str(DEFAULT_PARENT_JSON),
                    "--tpl",  str(DEFAULT_PARENT_TPL),
                    "--out",  str(DEFAULT_PARENT_OUT)
                ], check=True)
            else:
                print(f"{C_RED}⚠️ parent_config.py não encontrado: {parent_script}{C_RST}")
                continue

            # Etapa 2: selecionar Maven e comando build
            mvn_key = menu_generic('SELECIONE MAVEN', {k: {'desc': k} for k in MAVENS})
            if not mvn_key:
                continue
            mvn_dir = prepare_tool(MAVENS[mvn_key]['url'], MAVEN_DIR)

            phase = submenu_build_bom_parent()
            if not phase:
                continue

            # Etapa 3: preparar settings.xml
            settings_xml = (Path.cwd() / ".maven" / "settings.xml").resolve()
            settings_xml.parent.mkdir(exist_ok=True)
            settings_xml.touch(exist_ok=True)

            # Etapa 4: executar build de parent-pom e bom com Maven escolhido e fase
            parent_dir = selecionado / "parent-pom"
            bom_dir    = selecionado / "bom"

            build_loop(parent_dir, None, mvn_dir, settings_xml, DEFAULT_PARENT_JSON, phase)
            build_loop(bom_dir,    None, mvn_dir, settings_xml, DEFAULT_BOM_JSON,    phase)

            input("\nPressione Enter para voltar ao Menu Principal...")

        elif escolha == 'auto_commit':
            submenu_auto_commit()

        else:
            continue
