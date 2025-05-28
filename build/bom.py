#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Script para gerar pom.xml do BOM a partir de um template e um arquivo JSON de versões.
Uso:
  python bom.py [--template TEMPLATE] [--versions VERSIONS_JSON] [--output OUTPUT_POM]

Exemplo (a partir de pasta build):
  python bom.py \
    --template ../mule-parent-bom-main/bom/pom.xml.template \
    --versions versions-bom.json \
    --output ../mule-parent-bom-main/bom/pom.xml
"""
import argparse
import json
from pathlib import Path

def generate_bom_pom(template_file: Path, versions_file: Path, output_file: Path):
    if not template_file.exists():
        raise FileNotFoundError(f"Template não encontrado: {template_file}")
    if not versions_file.exists():
        raise FileNotFoundError(f"JSON de versões não encontrado: {versions_file}")

    output_file.parent.mkdir(parents=True, exist_ok=True)
    pom_template = template_file.read_text(encoding="utf-8")

    data = json.loads(versions_file.read_text(encoding="utf-8"))
    bom = data.get("bom", {})
    versions = data.get("versions", {})

    pom_content = pom_template
    # Substitui placeholders do BOM
    for key, val in {
        "GROUP_ID": bom.get("groupId"),
        "ARTIFACT_ID": bom.get("artifactId"),
        "VERSION": bom.get("version"),
        "NAME": bom.get("name", bom.get("artifactId"))
    }.items():
        if val:
            pom_content = pom_content.replace(f"{{{{{key}}}}}", val)

    # Substitui placeholders de versões
    for ver_key, ver_val in versions.items():
        placeholder = f"{{{{{ver_key}}}}}"
        pom_content = pom_content.replace(placeholder, ver_val)

    output_file.write_text(pom_content, encoding="utf-8")
    print(f"🔥 pom.xml gerado em: {output_file}")

if __name__ == '__main__':
    parser = argparse.ArgumentParser(
        description="Gerador de BOM pom.xml a partir de template e JSON de versões."
    )
    parser.add_argument(
        "--template",
        type=Path,
        default=Path("../mule-parent-bom-main/bom/pom.xml.template"),
        help="Caminho para o arquivo pom.xml.template"
    )
    parser.add_argument(
        "--versions",
        type=Path,
        default=Path("versions-bom.json"),
        help="Caminho para o JSON de versões (por padrão, versions-bom.json na pasta atual)"
    )
    parser.add_argument(
        "--output",
        type=Path,
        default=Path("../mule-parent-bom-main/bom/pom.xml"),
        help="Caminho de saída para o pom.xml gerado"
    )
    args = parser.parse_args()
    generate_bom_pom(args.template, args.versions, args.output)