from imports import *

DEFAULT_JSON = DEFAULT_PARENT_JSON
DEFAULT_TPL = DEFAULT_PARENT_TPL
DEFAULT_OUT = DEFAULT_PARENT_OUT

# ═══ Helpers ═══
def _dict_to_xml(value: Any, indent: str = "    ", lvl: int = 0) -> str:
    """Converte recursivamente *dict* / *list* em blocos XML."""
    pad = indent * lvl
    if isinstance(value, dict):
        parts: list[str] = []
        for k, v in value.items():
            inner = _dict_to_xml(v, indent, lvl + 1)
            if "\n" in inner:
                parts.append(f"{pad}<{k}>\n{inner}\n{pad}</{k}>")
            else:
                parts.append(f"{pad}<{k}>{inner}</{k}>")
        return "\n".join(parts)
    if isinstance(value, (list, tuple)):
        return "\n".join(_dict_to_xml(v, indent, lvl) for v in value)
    return str(value)


def _pretty_xml(raw: str, indent: int = 4) -> str:
    """Aplica *pretty‑print* ao XML mantendo declaração e espaços."""
    try:
        from xml.dom.minidom import parseString
        parsed = parseString(raw)
        pretty = parsed.toprettyxml(indent=" " * indent, newl="\n")
        pretty = "\n".join(line for line in pretty.split("\n") if line.strip())
        return pretty
    except Exception:
        return raw  # fallback


# ═══ Main ═══
def main() -> None:
    parser = argparse.ArgumentParser(
        "Renderiza Parent‑POM a partir de template + JSON",
        formatter_class=argparse.ArgumentDefaultsHelpFormatter,
    )
    parser.add_argument("--json", "-j", type=Path, default=DEFAULT_JSON,
                        help="Arquivo parent-config.json")
    parser.add_argument("--tpl", "-t",  type=Path, default=DEFAULT_TPL,
                        help="Template Jinja do POM")
    parser.add_argument("--out", "-o",  type=Path, default=DEFAULT_OUT,
                        help="Arquivo pom.xml de saída")
    parser.add_argument("--indent", type=int, default=4,
                        help="Qtd. de espaços por nível de indentação")
    args = parser.parse_args()

    if not args.json.is_file():
        sys.exit(f"❌ Arquivo JSON não encontrado: {args.json}")
    if not args.tpl.is_file():
        sys.exit(f"❌ Template Jinja não encontrado: {args.tpl}")

    # 🔢 Carrega JSON e injeta groupId
    data = json.loads(args.json.read_text(encoding="utf-8"))
    data["groupId"] = ORG_ID
    if "parent" in data:
        data["parent"]["groupId"] = ORG_ID

    env = Environment(
        loader=FileSystemLoader(str(args.tpl.parent)),
        autoescape=select_autoescape(disabled_extensions=("jinja", "template")),
        trim_blocks=True,
        lstrip_blocks=True,
    )
    env.filters["dict2xml"] = lambda v, indent=" ": _dict_to_xml(v, " " * args.indent)

    rendered_raw = env.get_template(args.tpl.name).render(**data)
    rendered_pretty = _pretty_xml(rendered_raw, indent=args.indent)

    args.out.parent.mkdir(parents=True, exist_ok=True)
    args.out.write_text(rendered_pretty, encoding="utf-8")
    print(f"\n✅ pom.xml gerado com sucesso em: {args.out}\n")


if __name__ == "__main__":
    main()
