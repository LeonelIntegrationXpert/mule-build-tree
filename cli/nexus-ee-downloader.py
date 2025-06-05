#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
recursive_pom_downloader_filtered.py

Este script faz crawling recursivo em um repositório Nexus EE e
baixa apenas os arquivos .pom de projetos que:
  1) NÃO possuem dependências relacionadas ao Enterprise Edition (padrão definido em IGNORE_DEP_PATTERN),
     exceto se IGNORE_DEP_PATTERN for None (neste caso, ignora esse filtro).
  2) possuem <packaging> correspondente a REQUIRED_PACKAGING (ou todos, se REQUIRED_PACKAGING=None).

Todos os POMs aprovados serão salvos diretamente na pasta DOWNLOAD_DIR,
sem replicar a estrutura de subpastas do repositório.
Possui opção para habilitar um limite de downloads.
"""

try:
    import requests
    from bs4 import BeautifulSoup
    from requests.auth import HTTPBasicAuth
except ImportError:
    import subprocess, sys
    subprocess.check_call([sys.executable, "-m", "pip", "install", "requests", "beautifulsoup4"])
    import requests
    from bs4 import BeautifulSoup
    from requests.auth import HTTPBasicAuth

import os
import sys
import xml.etree.ElementTree as ET
from urllib.parse import urljoin, urlparse
from pathlib import Path
import re

# ========= CONFIGURAÇÕES =========
BASE_URL            = "https://repository.mulesoft.org/nexus-ee/content/repositories/releases-ee/"
# Diretório absoluto onde todos os POMs aprovados serão salvos, sem estrutura de pastas
DOWNLOAD_DIR        = r"C:\Users\leonel.d.porto\Desktop\my-workspace\mule-build-tree\cli\decode_projects\jars"
USERNAME            = "telefonica.brasil.sa.nexus"
PASSWORD            = "5514U6DT4ykV8qF"
# Queremos baixar somente arquivos .pom que passem nos filtros. Então:
EXTS_VALIDAS        = (".pom",)

# Padrão para identificar EE em dependências. Se None, não filtra nada por EE.
IGNORE_DEP_PATTERN  = None      # OU: "ee" para filtrar qualquer dependency contendo "ee"

# Packaging desejado (filtrar projetos que contenham esta tag no POM).
# Se setar REQUIRED_PACKAGING = None, todos os packaging serão aceitos.
REQUIRED_PACKAGING  = "pom"     # Ex: "pom" ou "jar"; ou None para aceitar qualquer

# URLs a serem ignoradas (não navegar nem baixar). Se None, ignora esse filtro.
# Exemplo de entrada:
# IGNORE_URLS = [
#   "https://repository-master.mulesoft.org/nexus/content/repositories/ci-releases/",
#   "https://repository-master.mulesoft.org/nexus/content/repositories/ci-snapshots/",
#   ...
# ]
IGNORE_URLS         = None

# Habilita limite de downloads
ENABLE_LIMIT        = True   # Defina como False para desativar limite
# Quantidade máxima de arquivos .pom a baixar quando ENABLE_LIMIT=True
DOWNLOAD_LIMIT      = 10     # Exemplo: 10 arquivos
# =================================

# Garante que o diretório de download existe
os.makedirs(DOWNLOAD_DIR, exist_ok=True)
visited_dirs = set()
downloaded    = []
empty_dirs    = []
erro_dirs     = []

# Cores para logs
C_GRN = "\033[92m"
C_RED = "\033[91m"
C_YLW = "\033[93m"
C_CYA = "\033[96m"
C_RST = "\033[0m"


def log_info(msg: str):
    print(f"{C_CYA}[INFO]{C_RST} {msg}")


def log_success(msg: str):
    print(f"{C_GRN}[OK]{C_RST} {msg}")


def log_warning(msg: str):
    print(f"{C_YLW}[WARN]{C_RST} {msg}")


def log_error(msg: str):
    print(f"{C_RED}[ERR]{C_RST} {msg}")


def baixar_pom(url: str):
    """
    Baixa o .pom (EXTS_VALIDAS = (".pom",)) e salva diretamente em DOWNLOAD_DIR,
    usando o nome do arquivo final. Respeita o limite de downloads se habilitado.
    Retorna True para continuar; False se o limite foi atingido e deve abortar.
    """
    global downloaded

    # Verifica limite
    if ENABLE_LIMIT and len(downloaded) >= DOWNLOAD_LIMIT:
        log_warning(f"Limite de downloads atingido ({DOWNLOAD_LIMIT}).")
        return False

    # Se IGNORE_URLS estiver definido, checa cada padrão
    if IGNORE_URLS:
        for ign in IGNORE_URLS:
            if url.startswith(ign):
                log_info(f"Ignorando URL (em IGNORE_URLS): {url}")
                return True  # continua o crawler, mas não baixa

    filename = url.rstrip("/").split("/")[-1]
    destino = os.path.join(DOWNLOAD_DIR, filename)
    if os.path.exists(destino):
        log_info(f"Já existe, pulando: {filename}")
        return True

    log_info(f"Baixando POM: {url}")
    try:
        with requests.get(url, stream=True, auth=HTTPBasicAuth(USERNAME, PASSWORD)) as r:
            r.raise_for_status()
            with open(destino, "wb") as f:
                for chunk in r.iter_content(chunk_size=8192):
                    if chunk:
                        f.write(chunk)
        downloaded.append(destino)
        log_success(f"Concluído: {filename}")
        # Verifica limite após download
        if ENABLE_LIMIT and len(downloaded) >= DOWNLOAD_LIMIT:
            log_warning(f"Limite de downloads atingido ({DOWNLOAD_LIMIT}). Encerrando crawler.")
            return False
        return True
    except Exception as e:
        log_error(f"Falha ao baixar {url}: {e}")
        return True  # continua, mas não conta como download


def pom_indica_ee_ou_packaging(pom_url: str) -> (bool, bool):
    """
    Baixa o .pom em memória e verifica:
      - has_ee: True se há dependência com padrão EE (ignorando caso IGNORE_DEP_PATTERN seja None).
      - is_required_packaging: True se o <packaging> corresponde ao REQUIRED_PACKAGING
        (ou passa se REQUIRED_PACKAGING=None).
    Retorna (has_ee, is_required_packaging).
    """
    # 1) Se IGNORE_URLS estiver definido, já ignora este POM por completo
    if IGNORE_URLS:
        for ign in IGNORE_URLS:
            if pom_url.startswith(ign):
                # simulamos que tem EE (para impedir download) e packaging inválido
                return True, False

    try:
        r = requests.get(pom_url, auth=HTTPBasicAuth(USERNAME, PASSWORD), timeout=30)
        r.raise_for_status()
        xml_root = ET.fromstring(r.content)

        # Detecta namespace, se houver
        ns = {'m': xml_root.tag.split('}')[0].strip('{')} if '}' in xml_root.tag else {}

        # 2) Verifica <packaging>
        packaging = xml_root.findtext('m:packaging', namespaces=ns) if ns else xml_root.findtext('packaging')
        # Se REQUIRED_PACKAGING for None, não filtra packaging
        is_packaging_ok = (REQUIRED_PACKAGING is None) or (packaging == REQUIRED_PACKAGING)

        # 3) Verifica dependências EE (caso IGNORE_DEP_PATTERN não seja None)
        has_ee = False
        if IGNORE_DEP_PATTERN:
            # procura dentro de cada <dependency>
            # Usamos o path `.//m:dependency` ou `.//dependency` para encontrar em qualquer nível
            xpath = './/m:dependency' if ns else './/dependency'
            for dep in xml_root.findall(xpath, namespaces=ns):
                gid = dep.findtext('m:groupId', namespaces=ns) if ns else dep.findtext('groupId') or ''
                aid = dep.findtext('m:artifactId', namespaces=ns) if ns else dep.findtext('artifactId') or ''
                if IGNORE_DEP_PATTERN.lower() in gid.lower() or IGNORE_DEP_PATTERN.lower() in aid.lower():
                    has_ee = True
                    break

        return has_ee, is_packaging_ok

    except Exception:
        # Em caso de falha na rede ou parsing, assume inválido
        return False, False


def crawler(url_atual: str):
    """
    Faz crawling recursivo, verificando POMs e baixando aqueles que passam nos filtros,
    até atingir o limite (se habilitado).
    """
    # Se limite atingido, para
    if ENABLE_LIMIT and len(downloaded) >= DOWNLOAD_LIMIT:
        return

    if url_atual in visited_dirs:
        return
    visited_dirs.add(url_atual)

    # 1) Se IGNORE_URLS estiver definido, ignora completamente este diretório
    if IGNORE_URLS:
        for ign in IGNORE_URLS:
            if url_atual.startswith(ign) and url_atual != BASE_URL:
                log_info(f"Ignorando URL (em IGNORE_URLS): {url_atual}")
                return

    log_info(f"Acessando: {url_atual}")
    try:
        resp = requests.get(url_atual, auth=HTTPBasicAuth(USERNAME, PASSWORD), timeout=30)
        resp.raise_for_status()
    except Exception as e:
        log_error(f"Erro ao acessar {url_atual}: {e}")
        erro_dirs.append(url_atual)
        return

    soup = BeautifulSoup(resp.text, "html.parser")
    subdirs = []
    pom_urls = []

    # 2) Reúne todos os links relevantes
    for a in soup.find_all("a", href=True):
        href = a["href"]
        if href in ("../", "./"):
            continue

        # Constrói URL absoluto
        full_url = href if urlparse(href).scheme else urljoin(url_atual, href)

        # Se terminar em ".pom", guardamos para análise
        if full_url.lower().endswith(".pom"):
            pom_urls.append(full_url)
            continue

        # Se for subdiretório (termina em "/"), guardamos para recursão
        if full_url.endswith("/"):
            subdirs.append(full_url)
            continue

    # 3) Se houver POMs neste diretório, analisamos cada um
    if pom_urls:
        for pom_url in pom_urls:
            log_info(f"  → Encontrado POM: {pom_url}")

            has_ee, is_pack = pom_indica_ee_ou_packaging(pom_url)
            if has_ee:
                log_warning(f"    • Pulando este POM por dependência EE: {pom_url}")
                # Continua analisando os outros POMs (não retorna)
                continue
            if not is_pack:
                log_warning(f"    • Pulando este POM por packaging != '{REQUIRED_PACKAGING}': {pom_url}")
                continue

            # Se passou nos dois filtros, faz o download deste POM
            cont = baixar_pom(pom_url)
            if not cont:
                # Se baixar_pom retornou False, é porque atingiu limite → aborta toda a recursão
                return

    else:
        # Se não havia nenhum POM aqui, marca como “vazio” (para estatísticas)
        empty_dirs.append(url_atual)

    # 4) Continua recursivamente em cada subdiretório
    for sub_url in subdirs:
        if ENABLE_LIMIT and len(downloaded) >= DOWNLOAD_LIMIT:
            return
        crawler(sub_url)


if __name__ == "__main__":
    try:
        crawler(BASE_URL)
    finally:
        # Ao finalizar (por erro ou limpeza), grava os logs em arquivos separados
        with open("downloaded_poms.txt", "w", encoding="utf-8") as f:
            f.write("\n".join(downloaded))
        with open("empty_dirs.txt", "w", encoding="utf-8") as f:
            f.write("\n".join(empty_dirs))
        with open("erro_dirs.txt", "w", encoding="utf-8") as f:
            f.write("\n".join(erro_dirs))

        print(f"\n{C_GRN}✅ Fim!{C_RST}")
        print(f"  ▶️  POMs baixados         : {len(downloaded)}")
        print(f"  ▶️  Pastas sem POMs       : {len(empty_dirs)}")
        print(f"  ▶️  Diretórios com erro   : {len(erro_dirs)}")
        sys.exit(0)
