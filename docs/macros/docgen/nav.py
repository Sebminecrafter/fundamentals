import re
import tomllib
from html import escape
from pathlib import Path
from urllib.parse import quote

from .paths import ZENSICAL_TOML

def render() -> str:
    config = tomllib.loads(ZENSICAL_TOML.read_text(encoding="utf-8"))
    project = config.get("project", {})
    docs = Path(project.get("docs_dir", "docs"))

    def title_of(path):
        file = docs / path
        if file.exists():
            text = file.read_text(encoding="utf-8")
            front = re.match(r"\A---\s*\n(.*?)\n---", text, re.S)
            if front and (m := re.search(r"^title:\s*[\"']?(.+?)[\"']?\s*$", front.group(1), re.M)):
                return m.group(1).strip("\"'")
            if m := re.search(r"^#\s+(.+)$", text, re.M):
                return m.group(1).strip()
        return Path(path).stem.replace("-", " ").title()

    def link(path, text):
        return f'<a href="{quote(path, safe="/")}">{escape(text)}</a>'

    def render_entries(entries, level=2):
        out = []
        for entry in entries:
            pairs = [(None, entry)] if isinstance(entry, str) else entry.items()

            for label, value in pairs:
                if isinstance(value, list):
                    index = next(
                        (i for i in value
                         if isinstance(i, str) and Path(i).name == "index.md"),
                        None,
                    )
                    heading = link(index, label) if index else escape(label or "")
                    out.append(f"<h{level}>{heading}</h{level}>")
                    out.append(render_entries([i for i in value if i != index], min(level + 1, 6)))

                elif isinstance(value, str) and value.endswith(".md") and not value == "index.md":
                    out.append(f"<p>{link(value, label or title_of(value))}</p>")

        return "\n".join(out)

    return render_entries(project.get("nav", []))

def register(env):
    @env.macro
    def contents():
        return render()
