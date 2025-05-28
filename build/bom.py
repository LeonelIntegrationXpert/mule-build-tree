#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Script para gerar pom.xml do BOM e do PARENT a partir de templates e arquivos JSON de versões,
e automaticamente fazer deploy no Anypoint Exchange usando um settings.xml personalizado.

Uso:
  python bom.py \
    --template ../mule-parent-bom-main/bom/pom.xml.template \
    --versions versions-bom.json \
    --output ../mule-parent-bom-main/bom/pom.xml \
    --parent-template ../mule-parent-bom-main/parent-pom/pom.xml.template \
    --parent-versions versions-parent.json \
    --parent-output ../mule-parent-bom-main/parent-pom/pom.xml \
    --settings .maven/settings.xml \
    --exchange-user myClientId \
    --exchange-pass myClientSecret
"""
import os
import argparse
import json
import subprocess
import shutil
import sys
import re
from pathlib import Path

# Credenciais padrao caso não passadas por argumentos
os.environ.setdefault("EXCHANGE_USER", "~~~Client~~~")
os.environ.setdefault("EXCHANGE_PASS", "9fd0d4f3ba7749e98beb8177116fb492~?~bCD2fa42dE1048Bc8b7876e5bAE8B89a")

_comment_re = re.compile(r"(^\s*//.*?$)|(/\*.*?\*/)", re.DOTALL | re.MULTILINE)

def load_json(path: Path) -> dict:
    txt = path.read_text(encoding="utf-8")
    txt = _comment_re.sub("", txt)
    return json.loads(txt)

def render(template: str, mapping: dict) -> str:
    for k, v in mapping.items():
        template = template.replace(f"{{{{{k}}}}}", str(v))
    return template

def generate_bom(template: Path, json_path: Path, output: Path):
    data = load_json(json_path)
    bom = data.get("bom", {})
    versions = data.get("versions", {})

    mapping = {
        "GROUP_ID": bom.get("groupId"),
        "ARTIFACT_ID": bom.get("artifactId"),
        "VERSION": bom.get("version"),
        "NAME": bom.get("name", bom.get("artifactId"))
    }
    mapping.update(versions)

    content = render(template.read_text("utf-8"), mapping)
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text(content, "utf-8")
    print(f"🔥 BOM gerado → {output}")

def generate_parent(template: Path, json_path: Path, output: Path):
    data = load_json(json_path)
    mapping = {}
    for section in ("parent", "project", "properties"):
        mapping.update(data.get(section, {}))

    content = render(template.read_text("utf-8"), mapping)
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text(content, "utf-8")
    print(f"🔥 Parent gerado → {output}")

def find_mvn() -> str:
    for cmd in ("mvn", "mvn.cmd", "mvn.bat"):
        p = shutil.which(cmd)
        if p:
            return p
    raise FileNotFoundError("Maven não encontrado no PATH")

def deploy(dirs, settings: Path):
    mvn = find_mvn()
    for d in dirs:
        if not d.is_dir():
            print(f"⚠️  Diretório {d} não existe, pulando")
            continue
        print(f"🚀 mvn deploy em {d}")
        subprocess.run([mvn, "-s", str(settings), "clean", "deploy", "-DskipTests"],
                       cwd=d, check=True, env=os.environ)
        print("✅ Deploy concluído")

if __name__ == "__main__":
    parser = argparse.ArgumentParser("Gera BOM + Parent POM e faz deploy")
    parser.add_argument("--template", type=Path, default=Path("../mule-parent-bom-main/bom/pom.xml.template"))
    parser.add_argument("--versions", type=Path, default=Path("versions-bom.json"))
    parser.add_argument("--output", type=Path, default=Path("../mule-parent-bom-main/bom/pom.xml"))

    parser.add_argument("--parent-template", type=Path, default=Path("../mule-parent-bom-main/parent-pom/pom.xml.template"))
    parser.add_argument("--parent-versions", type=Path, default=Path("versions-parent.json"))
    parser.add_argument("--parent-output", type=Path, default=Path("../mule-parent-bom-main/parent-pom/pom.xml"))

    parser.add_argument("--settings", type=Path, default=Path.cwd()/".maven"/"settings.xml")
    parser.add_argument("-u", "--exchange-user")
    parser.add_argument("-p", "--exchange-pass")
    args = parser.parse_args()

    if args.exchange_user:
        os.environ["EXCHANGE_USER"] = args.exchange_user
    if args.exchange_pass:
        os.environ["EXCHANGE_PASS"] = args.exchange_pass

    generate_bom(args.template, args.versions, args.output)
    generate_parent(args.parent_template, args.parent_versions, args.parent_output)

    deploy({args.output.parent, args.parent_output.parent}, args.settings)