from . import plugin_yml
from .mdutil import cell, code, table

GROUP_ORDER = ["player", "staff", "privileged"]
GROUP_DESCRIPTIONS = {
    "player": "For all players (by default).",
    "staff": "For staff/admins/mods. (needs op by default)",
    "privileged": "For admins, ranks, etc. (needs op by default)",
}
HEADERS = ["Command", "Description", "Usage", "Alias(es)", "Permission"]
UNKNOWN_GROUP_RANK = len(GROUP_ORDER)


def _permission_link(node: str | None, page: str) -> str:
    if not node:
        return "None"
    return f'<a href="{page}#{node}">{code(node)}</a>'

def _command_row(command: plugin_yml.Command, permissions_page: str) -> list[str]:
    usage = "<br>".join(code(u) for u in command.usage) or "None"
    aliases = ", ".join(code("/" + a) for a in command.aliases) or "None"
    return [
        code("/" + command.name),
        cell(command.description),
        usage,
        aliases,
        _permission_link(command.permission, permissions_page),
    ]

def _group_sort_key(group: str) -> tuple[int, str]:
    # Known groups first in GROUP_ORDER, then unknown ones a-z
    rank = GROUP_ORDER.index(group) if group in GROUP_ORDER else UNKNOWN_GROUP_RANK
    return rank, group

def _commands_by_group() -> dict[str, list[plugin_yml.Command]]:
    groups: dict[str, list[plugin_yml.Command]] = {}
    for command in plugin_yml.commands():
        groups.setdefault(command.group, []).append(command)
    return groups

def _render_group(
    group: str,
    commands: list[plugin_yml.Command],
    permissions_page: str,
    include_heading: bool,
) -> list[str]:
    lines = []
    if include_heading:
        lines.append(f"## {group.title()} commands\n")
        if group in GROUP_DESCRIPTIONS:
            lines.append(GROUP_DESCRIPTIONS[group] + "\n")

    rows = [_command_row(c, permissions_page) for c in commands]
    lines.append(table(HEADERS, rows))
    lines.append("")
    return lines

def render(group: str | None = None, permissions_page: str = "permissions.md") -> str:
    """
    MD Tables of every command, grouped by permissions.

    `{{ commands() }}` -> all groups
    `{{ commands("group") }}` -> one group
    """
    by_group = _commands_by_group()
    selected = [group] if group else sorted(by_group, key=_group_sort_key)

    # Only one group -> no heading
    include_heading = group is None

    lines = []
    for name in selected:
        commands = sorted(by_group.get(name, []), key=lambda c: c.name)
        if commands:
            lines += _render_group(name, commands, permissions_page, include_heading)
    return "\n".join(lines)

def register(env):
    @env.macro
    def commands(group: str | None = None):
        return render(group)