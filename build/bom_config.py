#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from imports import *

# ─── Helper de formatação XML ─────────────────────────────────
def _pretty_xml(raw: str, indent: int = 2) -> str:
    """
    Indenta o XML usando minidom, depois:
      1) Remove linhas vazias
      2) Remove espaços à direita
      3) Recompõe atributos de <project …> numa única linha
    """
    parsed = minidom.parseString(raw.encode("utf-8"))
    pretty = parsed.toprettyxml(indent=" " * indent, newl="\n")

    # 1+2) filtra linhas vazias e retira espaços à direita
    lines = [
        line.rstrip()
        for line in pretty.splitlines()
        if line.strip()
    ]
    pretty = "\n".join(lines)

    # 3) compacta atributos de <project ...> numa linha só
    pretty = re.sub(
        r"<project\s+([^>]+?)\s*>",
        lambda m: "<project " + re.sub(r"\s{2,}", " ", m.group(1).strip()) + ">",
        pretty,
        flags=re.DOTALL,
    )

    return pretty

# ═══ Caminhos Absolutos ═══
JSON_PATH     = DEFAULT_BOM_JSON
TEMPLATE_PATH = DEFAULT_BOM_TPL
OUT_PATH      = DEFAULT_BOM_OUT

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

# ═══ Prepara Ambiente Jinja ─────────────────────────────────────────────────
env = Environment(
    loader=FileSystemLoader(str(TEMPLATE_PATH.parent)),
    autoescape=False,
    trim_blocks=True,
    lstrip_blocks=True,
)
template = env.get_template(TEMPLATE_PATH.name)

# ═══ Renderiza ────────────────────────────────────────────────────────────────
raw_xml = template.render(**data).strip()

# ═══ Aplica formatação limpa ─────────────────────────────────────────────────
try:
    formatted_xml = _pretty_xml(raw_xml, indent=2)
except Exception as e:
    print(f"⚠ Erro formatando XML: {e}. Salvando sem formatação extra...")
    formatted_xml = raw_xml

# ═══ Grava o pom.xml formatado ─────────────────────────────────────────────────
OUT_PATH.write_text(formatted_xml, encoding="utf-8")

print(f"\n✅ Arquivo pom.xml gerado com sucesso em:\n{OUT_PATH}")
