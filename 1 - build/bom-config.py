#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from imports import *

# ═══ Caminhos Absolutos ═══
JSON_PATH = DEFAULT_BOM_JSON
TEMPLATE_PATH = DEFAULT_BOM_TPL
OUT_PATH = DEFAULT_BOM_OUT

# ═══ Validação ═══
if not JSON_PATH.exists():
    print(f"❌ Arquivo JSON não encontrado: {JSON_PATH}")
    sys.exit(1)
if not TEMPLATE_PATH.exists():
    print(f"❌ Template Jinja não encontrado: {TEMPLATE_PATH}")
    sys.exit(1)

# 🔢 Carrega dados JSON e injeta groupId
data = json.loads(JSON_PATH.read_text(encoding="utf-8"))
data["groupId"] = ORG_ID

# ═══ Prepara Ambiente Jinja ═══
env = Environment(
    loader=FileSystemLoader(str(TEMPLATE_PATH.parent)),
    autoescape=select_autoescape(disabled_extensions=("template",)),
    trim_blocks=True,
    lstrip_blocks=True
)
template = env.get_template(TEMPLATE_PATH.name)

# ═══ Renderiza e grava sem formatação extra ═══
raw_xml = template.render(**data).strip()
OUT_PATH.write_text(raw_xml, encoding="utf-8")

print(f"\n✅ Arquivo pom.xml gerado com sucesso em:\n{OUT_PATH}")
