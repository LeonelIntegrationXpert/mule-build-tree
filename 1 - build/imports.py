#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from __future__ import annotations

# ╭──────────────────────────────────────────────────────────────────╮
# │ Centralização de imports, instalação preguiçosa e imports externos │
# └──────────────────────────────────────────────────────────────────┘

import subprocess
import sys
from pathlib import Path

# ─── Leitura dinâmica de módulos do imports.txt ───
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

import pathlib
Path = pathlib.Path
__all__.append("Path")

# ─── Instalação preguiçosa do Jinja2 ───
try:
    from jinja2 import Environment, FileSystemLoader, select_autoescape
    __all__.extend(["Environment", "FileSystemLoader", "select_autoescape"])
except ImportError:
    print("📦 Instalando dependência: jinja2…")
    subprocess.check_call([sys.executable, "-m", "pip", "install", "jinja2"])
    from jinja2 import Environment, FileSystemLoader, select_autoescape
    __all__.extend(["Environment", "FileSystemLoader", "select_autoescape"])

import os; __all__.append("os")
import sys as _sys; globals()["sys"] = _sys; __all__.append("sys")
import json; __all__.append("json")
import re; __all__.append("re")
import time; __all__.append("time")
import zipfile; __all__.append("zipfile")
import shutil; __all__.append("shutil")
import requests; __all__.append("requests")
import ctypes; __all__.append("ctypes")
import itertools; __all__.append("itertools")
import argparse; __all__.append("argparse")
import pathlib; __all__.append("pathlib")

from datetime import datetime; __all__.append("datetime")
from typing import Any, List, Dict, Optional; __all__.extend(["Any", "List", "Dict", "Optional"])
from xml.etree import ElementTree as ET; __all__.append("ET")

# ─── Credenciais Anypoint Exchange ───
EXCHANGE_USER = "~~~Client~~~"
EXCHANGE_PASS = "9fd0d4f3ba7749e98beb8177116fb492~?~bCD2fa42dE1048Bc8b7876e5bAE8B89a"
ORG_ID = "806818a4-2582-4d63-a6ef-021f493715a0"

# Seta variáveis de ambiente a partir dos valores importados
os.environ.setdefault("EXCHANGE_USER", EXCHANGE_USER)
os.environ.setdefault("EXCHANGE_PASS", EXCHANGE_PASS)

__all__.extend(["EXCHANGE_USER", "EXCHANGE_PASS", "ORG_ID"])

# ─── Cores ANSI ───
C_RST = "\033[0m"; __all__.append("C_RST")
C_CYA = "\033[96m"; __all__.append("C_CYA")
C_GRN = "\033[92m"; __all__.append("C_GRN")
C_RED = "\033[91m"; __all__.append("C_RED")
C_YLW = "\033[93m"; __all__.append("C_YLW")

# ─── Catálogos JDK/Maven ───
JDKS: Dict[str, Dict[str, str]] = {
    "8":  {"desc": "Temurin 8u412",  "url": "https://github.com/adoptium/temurin8-binaries/releases/download/jdk8u412-b08/OpenJDK8U-jdk_x64_windows_hotspot_8u412b08.zip"},
    "11": {"desc": "Temurin 11.0.21", "url": "https://github.com/adoptium/temurin11-binaries/releases/download/jdk-11.0.21%2B9/OpenJDK11U-jdk_x64_windows_hotspot_11.0.21_9.zip"},
    "17": {"desc": "Temurin 17.0.11", "url": "https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.11%2B9/OpenJDK17U-jdk_x64_windows_hotspot_17.0.11_9.zip"},
    "21": {"desc": "Temurin 21.0.3",  "url": "https://github.com/adoptium/temurin21-binaries/releases/download/jdk-21.0.3%2B9/OpenJDK21U-jdk_x64_windows_hotspot_21.0.3_9.zip"},
}
__all__.append("JDKS")

MAVENS: Dict[str, Dict[str, str]] = {
    "3.6.3": {"url": "https://archive.apache.org/dist/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.zip"},
    "3.8.8": {"url": "https://archive.apache.org/dist/maven/maven-3/3.8.8/binaries/apache-maven-3.8.8-bin.zip"},
    "3.9.5": {"url": "https://archive.apache.org/dist/maven/maven-3/3.9.5/binaries/apache-maven-3.9.5-bin.zip"},
    "3.9.6": {"url": "https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip"},
    "4.0.0": {"url": "https://archive.apache.org/dist/maven/maven-4/4.0.0/binaries/apache-maven-4.0.0-bin.zip"},
    "4.0.1": {"url": "https://archive.apache.org/dist/maven/maven-4/4.0.1/binaries/apache-maven-4.0.1-bin.zip"},
}
__all__.append("MAVENS")

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
__all__.append("ADD_OPENS_EXTRA")

DEFAULT_CONFIG_PATH = Path("deploy-config.json"); __all__.append("DEFAULT_CONFIG_PATH")
ROOT_DIR = Path("tooling"); __all__.append("ROOT_DIR")
JDK_DIR = ROOT_DIR / "jdks"; __all__.append("JDK_DIR")
MAVEN_DIR = ROOT_DIR / "mavens"; __all__.append("MAVEN_DIR")

# Caminhos Padrão do Workspace
WORKSPACE_DIR = Path(r"C:/Users/leonel.d.porto/Desktop/my-workspace/mule-build-tree")
DEFAULT_PARENT_JSON = WORKSPACE_DIR / "1 - build/parent-config.json"
DEFAULT_PARENT_TPL  = WORKSPACE_DIR / "2 - Projetos/mule-parent-bom-main/parent-pom/pom.xml.template"
DEFAULT_PARENT_OUT  = WORKSPACE_DIR / "2 - Projetos/mule-parent-bom-main/parent-pom/pom.xml"

DEFAULT_BOM_JSON = WORKSPACE_DIR / "1 - build/bom-config.json"
DEFAULT_BOM_TPL  = WORKSPACE_DIR / "2 - Projetos/mule-parent-bom-main/bom/pom.xml.template"
DEFAULT_BOM_OUT  = WORKSPACE_DIR / "2 - Projetos/mule-parent-bom-main/bom/pom.xml"

__all__.extend([
    "WORKSPACE_DIR", "DEFAULT_PARENT_JSON", "DEFAULT_PARENT_TPL", "DEFAULT_PARENT_OUT",
    "DEFAULT_BOM_JSON", "DEFAULT_BOM_TPL", "DEFAULT_BOM_OUT"
])

# Adição que faltava: Caminhos candidatos para o deploy-config.json
DEFAULT_CONFIG_CANDIDATES = [
    Path.cwd() / "deploy-config.json",
    Path(__file__).parent / "deploy-config.json"
]
__all__.append("DEFAULT_CONFIG_CANDIDATES")

