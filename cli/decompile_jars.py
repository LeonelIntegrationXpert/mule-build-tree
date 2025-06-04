#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
🛠️ Universal Java Decompiler – v4.1 (refatorado para ficar dentro de decode_projects)
───────────────────────────────────────────────────────────────────────
• Extrai todos os *.jar* em **decode_projects/jars**
• Decompila classes (ignora inner classes com "$" no nome)
• Copia o pom.xml original (se existir) ou gera um mínimo
• Move projetos decompilados para **decode_projects/projects/**
• Remove diretórios temporários após cada JAR
• Logging estruturado com cores e níveis de severidade
• Cria pastas faltantes automaticamente
───────────────────────────────────────────────────────────────────────
"""
import re
import shutil
import subprocess
import sys
import zipfile
import argparse
import logging
from pathlib import Path
from xml.etree import ElementTree as ET

# ─────────────── ANSI CORES PARA LOG ───────────────
class _AnsiColor:
    RESET = "\033[0m"
    CYAN = "\033[96m"
    GREEN = "\033[92m"
    RED = "\033[91m"
    YELLOW = "\033[93m"

# ─────────────── CONFIGURAÇÕES E PADRÕES ───────────────
DEFAULT_BASE_DIR     = Path(__file__).parent / "decode_projects"
DEFAULT_JAR_DIR      = DEFAULT_BASE_DIR / "jars"
DEFAULT_CFR_JAR_PATH = DEFAULT_BASE_DIR / "cfr-0.152.jar"
DEFAULT_OUTPUT_DIR   = DEFAULT_BASE_DIR / "decompiled"
# Agora os projetos finais ficam dentro de decode_projects/projects
DEFAULT_PROJECTS_DIR = DEFAULT_BASE_DIR / "projects"

COORDS_REGEX = re.compile(r"^(?P<artifact>.+?)-(?P<ver>\d[\d.]*)$")

# ─────────────── LOGGER CONFIG ───────────────
logger = logging.getLogger("decompiler")
handler = logging.StreamHandler()
formatter = logging.Formatter("%(message)s")
handler.setFormatter(formatter)
logger.addHandler(handler)
logger.setLevel(logging.INFO)

def _log_status(msg: str, colour: str = _AnsiColor.CYAN, icon: str = "🔹") -> None:
    logger.info(f"{colour}{icon} {msg}{_AnsiColor.RESET}")

def _log_error(msg: str) -> None:
    logger.error(f"{_AnsiColor.RED}❌ {msg}{_AnsiColor.RESET}")

def _log_success(msg: str) -> None:
    logger.info(f"{_AnsiColor.GREEN}✅ {msg}{_AnsiColor.RESET}")

def _log_warning(msg: str) -> None:
    logger.warning(f"{_AnsiColor.YELLOW}⚠️  {msg}{_AnsiColor.RESET}")

# ─────────────── FUNÇÕES AUXILIARES ───────────────
def _derive_coordinates(jar_name: str) -> tuple[str, str, str]:
    """
    Deriva groupId/artifactId/version a partir do nome do JAR.
    Exemplo: "foo-1.2.3.jar" → artifact="foo", version="1.2.3".
    """
    stem = Path(jar_name).stem
    m = COORDS_REGEX.match(stem)
    artifact = m.group("artifact") if m else stem
    version = m.group("ver") if m else "1.0.0"
    return "com.decompiled", artifact, version

def _generate_minimal_pom(group: str, artifact: str, version: str) -> str:
    """
    Gera um pom.xml mínimo baseado em coordenadas.
    """
    return f"""<project xmlns="http://maven.apache.org/POM/4.0.0"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
      http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>{group}</groupId>
  <artifactId>{artifact}</artifactId>
  <version>{version}</version>
  <packaging>jar</packaging>
  <name>{artifact} (Decompiled)</name>
</project>
"""

def _extract_jar(jar_path: Path, temp_dir: Path) -> None:
    """
    Extrai todos os arquivos (exceto inner classes ou diretórios) do JAR para temp_dir.
    """
    _log_status(f"Extraindo '{jar_path.name}' em '{temp_dir}'", _AnsiColor.YELLOW, "📦")
    try:
        with zipfile.ZipFile(jar_path, 'r') as zf:
            for member in zf.infolist():
                # pula diretórios e inner classes (com "$" no nome)
                if member.is_dir() or "$" in member.filename:
                    continue
                target = temp_dir / member.filename
                target.parent.mkdir(parents=True, exist_ok=True)
                with zf.open(member) as src, open(target, "wb") as dst:
                    shutil.copyfileobj(src, dst)
    except zipfile.BadZipFile:
        _log_error(f"Falha ao extrair '{jar_path.name}': arquivo corrompido.")
        raise

def _decompile_classes(temp_dir: Path, output_dir: Path, cfr_jar: Path, verbose: bool) -> None:
    """
    Para cada .class em temp_dir (ignorando inner classes), invoca o CFR e
    coloca o .java gerado em output_dir.
    """
    for class_file in temp_dir.rglob("*.class"):
        if "$" in class_file.name:
            continue
        cmd = ["java", "-jar", str(cfr_jar), str(class_file), "--outputdir", str(output_dir)]
        proc = subprocess.run(
            cmd,
            stdout=(None if verbose else subprocess.DEVNULL),
            stderr=(None if verbose else subprocess.DEVNULL)
        )
        if proc.returncode != 0:
            _log_warning(f"Erro ao decompilar '{class_file.relative_to(temp_dir)}'")

def _copy_or_generate_pom(temp_dir: Path, project_dir: Path, jar_name: str) -> None:
    """
    Copia o pom.xml encontrado em META-INF/maven, ou gera um POM mínimo caso não exista.
    """
    pom_candidates = list(temp_dir.glob("**/META-INF/maven/**/pom.xml"))
    if pom_candidates:
        src_pom = pom_candidates[0]
        shutil.copy2(src_pom, project_dir / "pom.xml")
        _log_success("pom.xml copiado do JAR")
    else:
        group, artifact, version = _derive_coordinates(jar_name)
        content = _generate_minimal_pom(group, artifact, version)
        (project_dir / "pom.xml").write_text(content, encoding="utf-8")
        _log_success("pom.xml mínimo gerado (não encontrado no JAR)")

def _move_project(output_dir: Path, projects_dir: Path, project_name: str) -> None:
    """
    Move a pasta 'project_name' de output_dir para projects_dir.
    Se já existir na projects_dir, remove antes de mover.
    """
    source = output_dir / project_name
    dest   = projects_dir / project_name
    if dest.exists():
        shutil.rmtree(dest)
    shutil.move(str(source), str(dest))
    _log_status(f"Projeto '{project_name}' movido para '{projects_dir}'", _AnsiColor.GREEN, "📂")

def _clean_directory(path: Path) -> None:
    """
    Remove o diretório 'path', caso exista. Silencia erros.
    """
    try:
        if path.exists():
            shutil.rmtree(path)
            _log_status(f"Diretório temporário '{path}' removido.", _AnsiColor.YELLOW, "🗑️")
    except Exception as e:
        _log_warning(f"Falha ao remover '{path}': {e}")

# ─────────────── FUNÇÃO MAIN ───────────────
def main():
    parser = argparse.ArgumentParser(
        description="Universal Java Decompiler – v4.1 (refatorado para ficar em decode_projects)",
        formatter_class=argparse.ArgumentDefaultsHelpFormatter
    )
    parser.add_argument(
        "--base-dir",
        type=Path,
        default=DEFAULT_BASE_DIR,
        help="Diretório base que contém 'jars', 'cfr-*.jar' e subpastas."
    )
    parser.add_argument(
        "--jar-dir",
        type=Path,
        default=DEFAULT_JAR_DIR,
        help="Subdiretório onde estão os arquivos .jar a serem decompilados."
    )
    parser.add_argument(
        "--cfr-jar",
        type=Path,
        default=DEFAULT_CFR_JAR_PATH,
        help="Caminho completo para o JAR do CFR decompilador."
    )
    parser.add_argument(
        "--output-dir",
        type=Path,
        default=DEFAULT_OUTPUT_DIR,
        help="Pasta temporária onde ficam os projetos decompilados antes de mover."
    )
    parser.add_argument(
        "--projects-dir",
        type=Path,
        default=DEFAULT_PROJECTS_DIR,
        help="Pasta final onde os projetos decompilados serão salvos (decode_projects/projects)."
    )
    parser.add_argument(
        "--verbose",
        action="store_true",
        help="Mostra saída completa dos subprocessos de decompilação."
    )
    args = parser.parse_args()

    BASE_DIR     = args.base_dir.resolve()
    JAR_DIR      = args.jar_dir.resolve()
    CFR_JAR      = args.cfr_jar.resolve()
    OUTPUT_DIR   = args.output_dir.resolve()
    PROJECTS_DIR = args.projects_dir.resolve()
    VERBOSE      = args.verbose

    _log_status("🛠️ Iniciando Universal Java Decompiler – v4.1", _AnsiColor.CYAN, "🚀")

    # ─── Cria as pastas principais (caso não existam) ──────────────────
    try:
        BASE_DIR.mkdir(parents=True, exist_ok=True)
        _log_status(f"Pasta base garantida: {BASE_DIR}", _AnsiColor.GREEN, "📁")
    except Exception as e:
        _log_error(f"Não foi possível criar BASE_DIR '{BASE_DIR}': {e}")
        sys.exit(1)

    try:
        JAR_DIR.mkdir(parents=True, exist_ok=True)
        _log_status(f"Pasta de JARs garantida: {JAR_DIR}", _AnsiColor.GREEN, "📁")
    except Exception as e:
        _log_error(f"Não foi possível criar JAR_DIR '{JAR_DIR}': {e}")
        sys.exit(1)

    # Verifica se o CFR JAR realmente existe (não criamos automaticamente)
    if not CFR_JAR.is_file():
        _log_error(f"CFR JAR não encontrado em: {CFR_JAR}")
        _log_status("Coloque 'cfr-0.152.jar' dentro de 'decode_projects/' ou use --cfr-jar", _AnsiColor.YELLOW, "ℹ️")
        sys.exit(1)

    # Verifica se o Java está disponível no PATH
    try:
        subprocess.run(["java", "-version"], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL, check=True)
        _log_success("Java encontrado no PATH")
    except Exception:
        _log_error("Java não encontrado no PATH")
        sys.exit(1)

    # Cria diretórios de saída
    try:
        OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
        _log_status(f"Pasta temporária garantida: {OUTPUT_DIR}", _AnsiColor.GREEN, "📁")
    except Exception as e:
        _log_error(f"Não foi possível criar OUTPUT_DIR '{OUTPUT_DIR}': {e}")
        sys.exit(1)

    try:
        PROJECTS_DIR.mkdir(parents=True, exist_ok=True)
        _log_status(f"Pasta final de projetos garantida: {PROJECTS_DIR}", _AnsiColor.GREEN, "📁")
    except Exception as e:
        _log_error(f"Não foi possível criar PROJECTS_DIR '{PROJECTS_DIR}': {e}")
        sys.exit(1)

    # ─── Lista todos os JARs a processar ───────────────────────────────
    jars = sorted(JAR_DIR.glob("*.jar"))
    if not jars:
        _log_warning("Nenhum arquivo .jar encontrado em 'decode_projects/jars/'.")
        _log_status("Coloque seus .jar em 'decode_projects/jars/' e execute novamente.", _AnsiColor.YELLOW, "ℹ️")
        sys.exit(0)

    # ─── Processa cada JAR individualmente ────────────────────────────
    for jar_path in jars:
        project_name       = jar_path.stem.replace(" ", "_")
        temp_classes_dir   = BASE_DIR / "classes" / project_name
        project_output_dir = OUTPUT_DIR / project_name

        # Limpa qualquer resquício anterior
        _clean_directory(temp_classes_dir)
        _clean_directory(project_output_dir)

        # 1) Extrai classes (sem inner classes)
        try:
            temp_classes_dir.mkdir(parents=True, exist_ok=True)
            _extract_jar(jar_path, temp_classes_dir)
        except Exception:
            _log_warning(f"Pulando JAR '{jar_path.name}' devido a erro na extração.")
            _clean_directory(temp_classes_dir)
            continue

        # 2) Decompila classes
        try:
            project_output_dir.mkdir(parents=True, exist_ok=True)
            _log_status(f"Iniciando decompilação de '{project_name}'", _AnsiColor.CYAN, "🔧")
            _decompile_classes(temp_classes_dir, project_output_dir, CFR_JAR, VERBOSE)
        except Exception as e:
            _log_warning(f"Falha ao decompilar '{project_name}': {e}")

        # 3) Copia ou gera pom.xml
        try:
            _copy_or_generate_pom(temp_classes_dir, project_output_dir, jar_path.name)
        except Exception as e:
            _log_warning(f"Não foi possível tratar pom.xml para '{project_name}': {e}")

        # 4) Move projeto decompilado para pasta final (dentro de decode_projects/projects/)
        try:
            _move_project(OUTPUT_DIR, PROJECTS_DIR, project_name)
        except Exception as e:
            _log_warning(f"Falha ao mover projeto '{project_name}': {e}")

        # 5) Remove diretório temporário de classes
        _clean_directory(temp_classes_dir)

    # Após todos os JARs, limpa o OUTPUT_DIR (que estava em decode_projects/decompiled/)
    _clean_directory(OUTPUT_DIR)

    _log_success("Processamento concluído com sucesso!")

if __name__ == "__main__":
    main()
