from . import plugin_yml
from .mdutil import cell, code, table

DEFAULT_LABELS = {
    "true": "Everyone",
    "false": "Nobody (must be granted)",
    "op": "Operators",
    "not op": "Non-operators",
}

def _anchor(node: str) -> str:
    return f'<span id="{node}"></span>'

def render() -> str:
    """One table per top-level permission, children underneath it."""
    nodes = plugin_yml.permissions()

    used_by: dict[str, list[str]] = {}
    for c in plugin_yml.commands():
        if c.permission:
            used_by.setdefault(c.permission, []).append(c.name)

    def row(p: plugin_yml.Permission) -> list[str]:
        cmds = ", ".join(code("/" + n) for n in used_by.get(p.node, [])) or "None"
        return [
            _anchor(p.node) + code(p.node),
            cell(DEFAULT_LABELS.get(p.default, p.default)),
            cell(p.description),
            cmds,
        ]

    headers = ["Permission", "Default", "Description", "Commands"]
    out = []

    roots = [p for p in nodes.values() if p.parent is None]
    parents = [p for p in roots if p.children]
    loose = [p for p in roots if not p.children]

    for parent in parents:
        title = parent.node.rsplit(".", 1)[-1].title()
        out.append(f"## {title} permissions\n")
        rows = [row(parent)] + [row(nodes[c]) for c in parent.children]
        out.append(table(headers, rows))
        out.append("")

    if loose:
        out.append("## Standalone permissions\n")
        out.append(
            "Not granted by a group permission, so these have to be manually added. (Mostly for security reasons, example: sudo lets admins execute as owner)\n"
        )
        out.append(table(headers, [row(p) for p in loose]))
        out.append("")

    return "\n".join(out)

def register(env):
    @env.macro
    def permissions():
        return render()
