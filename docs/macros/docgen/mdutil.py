from html import escape

def cell(text) -> str:
    """Markdown-safe text"""
    return escape(str(text), quote=False).replace("|", "&#124;").replace("\n", " ")

def code(text) -> str:
    """Inline code that is safe inside a table cell."""
    return f"<code>{cell(text)}</code>"

def table(headers: list[str], rows: list[list[str]]) -> str:
    out = [
        "| " + " | ".join(headers) + " |",
        "| " + " | ".join("---" for _ in headers) + " |",
    ]
    out += ["| " + " | ".join(row) + " |" for row in rows]
    return "\n".join(out)
