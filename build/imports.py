# build/imports.py
#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from __future__ import annotations

"""
Centraliza importações, instala dependências sob demanda e expõe variáveis
"""

# ─────────────── Imports básicos de sistema ───────────────
import os
import sys
import subprocess
import json
import re
import time
import zipfile
import shutil
import ctypes
import itertools
import argparse
import pathlib
from pathlib import Path
from datetime import datetime
from typing import Any, List, Dict, Optional
import xml.dom.minidom as minidom
from xml.etree import ElementTree as ET

# ─────────────── Lazy Import of External Modules ───────────────
MODULES_FILE = Path("imports.txt")
__all__: list[str] = []
if MODULES_FILE.exists():
    for line in MODULES_FILE.read_text().splitlines():
        name = line.strip()
        if not name or name.startswith("#"):
            continue
        try:
            globals()[name] = __import__(name)
            __all__.append(name)
        except ImportError:
            pass

# ─────────────── Lazy Install of jinja2 ───────────────
try:
    from jinja2 import Environment, FileSystemLoader, select_autoescape
    __all__.extend(["Environment", "FileSystemLoader", "select_autoescape"])
except ImportError:
    print("📦 Instalando dependência: jinja2…")
    subprocess.check_call([sys.executable, "-m", "pip", "install", "jinja2"])
    from jinja2 import Environment, FileSystemLoader, select_autoescape
    __all__.extend(["Environment", "FileSystemLoader", "select_autoescape"])

# ─────────────── Lazy Install of requests ───────────────
try:
    import requests
    __all__.append("requests")
except ImportError:
    print("📦 Instalando dependência: requests…")
    subprocess.check_call([sys.executable, "-m", "pip", "install", "requests"])
    import requests
    __all__.append("requests")

# ─────────────── Exposed Imports List ───────────────
__all__.extend([
    # módulos built-in
    "os", "sys", "subprocess", "json", "re", "time", "zipfile", "shutil", "ctypes", "itertools", "argparse",
    "pathlib", "Path", "datetime", "Any", "List", "Dict", "Optional", "ET", "minidom",
    # módulos externos
    "requests", "Environment", "FileSystemLoader", "select_autoescape",
    # constantes de Exchange
    "EXCHANGE_USER", "EXCHANGE_PASS", "ORG_ID",
    # cores ANSI
    "C_RST", "C_CYA", "C_GRN", "C_RED", "C_YLW",
    # catálogos JDK e Maven
    "JDKS", "MAVENS", "ADD_OPENS_EXTRA",
    # configuração padrão
    "DEFAULT_CONFIG_PATH", "ROOT_DIR", "JDK_DIR", "MAVEN_DIR", "LOG_DIR",
    "WORKSPACE_DIR", "DEFAULT_PARENT_JSON", "DEFAULT_PARENT_TPL", "DEFAULT_PARENT_OUT",
    "DEFAULT_BOM_JSON", "DEFAULT_BOM_TPL", "DEFAULT_BOM_OUT",
    # candidatos de config
    "DEFAULT_CONFIG_CANDIDATES",
])

# ─────────────── Anypoint Exchange Credenciais ───────────────
EXCHANGE_USER = os.environ.get("EXCHANGE_USER", "~~~Client~~~")
EXCHANGE_PASS = os.environ.get("EXCHANGE_PASS", "9fd0d4f3ba7749e98beb8177116fb492~?~bCD2fa42dE1048Bc8b7876e5bAE8B89a")
ORG_ID = os.environ.get("ORG_ID", "806818a4-2582-4d63-a6ef-021f493715a0")

os.environ.setdefault("EXCHANGE_USER", EXCHANGE_USER)
os.environ.setdefault("EXCHANGE_PASS", EXCHANGE_PASS)

# ─────────────── ANSI Color Codes ───────────────
C_RST = "\033[0m"   # Reset
C_CYA = "\033[96m"  # Cyan
C_GRN = "\033[92m"  # Green
C_RED = "\033[91m"  # Red
C_YLW = "\033[93m"  # Yellow

# ─────────────── JDKs Disponíveis ───────────────
JDKS: Dict[str, Dict[str, str]] = {
    "8":  {"desc": "Temurin 8u412",  "url": "https://github.com/adoptium/temurin8-binaries/releases/download/jdk8u412-b08/OpenJDK8U-jdk_x64_windows_hotspot_8u412b08.zip"},
    "11": {"desc": "Temurin 11.0.21", "url": "https://github.com/adoptium/temurin11-binaries/releases/download/jdk-11.0.21%2B9/OpenJDK11U-jdk_x64_windows_hotspot_11.0.21_9.zip"},
    "17": {"desc": "Temurin 17.0.11", "url": "https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.11%2B9/OpenJDK17U-jdk_x64_windows_hotspot_17.0.11_9.zip"},
    "21": {"desc": "Temurin 21.0.3",  "url": "https://github.com/adoptium/temurin21-binaries/releases/download/jdk-21.0.3%2B9/OpenJDK21U-jdk_x64_windows_hotspot_21.0.3_9.zip"},
}

# ─────────────── Maven Distribuições ───────────────
MAVENS: Dict[str, Dict[str, str]] = {
    "3.6.3": {"url": "https://archive.apache.org/dist/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.zip"},
    "3.8.8": {"url": "https://archive.apache.org/dist/maven/maven-3/3.8.8/binaries/apache-maven-3.8.8-bin.zip"},
    "3.9.5": {"url": "https://archive.apache.org/dist/maven/maven-3/3.9.5/binaries/apache-maven-3.9.5-bin.zip"},
    "3.9.6": {"url": "https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip"},
    "4.0.0": {"url": "https://archive.apache.org/dist/maven/maven-4/4.0.0/binaries/apache-maven-4.0.0-bin.zip"},
    "4.0.1": {"url": "https://archive.apache.org/dist/maven/maven-4/4.0.1/binaries/apache-maven-4.0.1-bin.zip"},
}

# ─────────────── Opções --add-opens ───────────────
ADD_OPENS_EXTRA: List[str] = [
    "--add-opens", "java.base/java.lang=ALL-UNNAMED",
    "--add-opens", "java.base/java.util=ALL-UNNAMED",
    "--add-opens", "java.base/java.io=ALL-UNNAMED",
    "--add-opens", "java.base/java.lang.reflect=ALL-UNNAMED",
    "--add-opens", "java.base/java.util.concurrent=ALL-UNNAMED",
    "--add-opens", "java.base/java.net=ALL-NAMED",
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

# ─────────────── Caminhos Padrão ───────────────
DEFAULT_CONFIG_PATH = Path("build/configs/deploy_config.json")
ROOT_DIR = Path(__file__).resolve().parents[1]

# Diretório de tooling centralizado
TOOLING_DIR = ROOT_DIR / "tooling"

JDK_DIR = TOOLING_DIR / "jdks"
MAVEN_DIR = TOOLING_DIR / "mavens"
LOG_DIR = TOOLING_DIR / "logs"

# Cria diretórios se não existirem
JDK_DIR.mkdir(parents=True, exist_ok=True)
MAVEN_DIR.mkdir(parents=True, exist_ok=True)
LOG_DIR.mkdir(parents=True, exist_ok=True)

WORKSPACE_DIR = ROOT_DIR
DEFAULT_PARENT_JSON = ROOT_DIR / "build" / "configs" / "parent_config.json"
DEFAULT_PARENT_TPL  = ROOT_DIR / "build" / "templates" / "pom_templates" / "pom.xml.template"
DEFAULT_PARENT_OUT  = ROOT_DIR / "projects" / "mule-parent-bom-main" / "parent-pom" / "pom.xml"

DEFAULT_BOM_JSON = ROOT_DIR / "build" / "configs" / "bom_config.json"
DEFAULT_BOM_TPL  = ROOT_DIR / "build" / "templates" / "pom_templates" / "pom.xml.template"
DEFAULT_BOM_OUT  = ROOT_DIR / "projects" / "mule-parent-bom-main" / "bom" / "pom.xml"

# ─────────────── Candidatos para deploy_config.json ───────────────
DEFAULT_CONFIG_CANDIDATES: List[Path] = [
    Path.cwd() / "deploy_config.json",
    Path(__file__).parent / "deploy_config.json",
    Path(__file__).parent / "configs" / "deploy_config.json",
    ROOT_DIR / "build" / "configs" / "deploy_config.json",
]
