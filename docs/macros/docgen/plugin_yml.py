"""plugin.yml to dataclasses"""

import re
import yaml
from dataclasses import dataclass, field

from .paths import PLUGIN_YML

@dataclass
class Command:
    name: str
    description: str
    usage: list[str]
    permission: str | None
    aliases: list[str]

    @property
    def group(self) -> str:
        """`fundamentals.staff.command` -> `staff`"""
        parts = (self.permission or "").split(".")
        return parts[1] if len(parts) > 2 else "other"

@dataclass
class Permission:
    node: str
    description: str
    default: str # "true" / "false" / "op" / "not op"
    parent: str | None = None
    children: list[str] = field(default_factory=list)

def _load() -> dict:
    return yaml.safe_load(PLUGIN_YML.read_text(encoding="utf-8")) or {}

def _usage_lines(name: str, raw: str | None) -> list[str]:
    raw = (raw or "").strip()
    raw = re.sub(r"^usage( varies)?:\s*", "", raw, flags=re.I)
    # Detects varied usages
    parts = re.split(r",\s*(?=/)", raw)
    return [p.strip().replace("<command>", name) for p in parts if p.strip()]

def commands() -> list[Command]:
    result = []
    for name, spec in (_load().get("commands") or {}).items():
        spec = spec or {}
        result.append(
            Command(
                name=name,
                description=str(spec.get("description", "")),
                usage=_usage_lines(name, spec.get("usage")),
                permission=spec.get("permission"),
                aliases=[str(a) for a in (spec.get("aliases") or [])],
            )
        )
    return result

def _norm_default(value) -> str:
    if value is True:
        return "true"
    if value is False:
        return "false"
    return str(value).lower()

def permissions() -> dict[str, Permission]:
    """
    All permission nodes (parents and children), keyed by node, in file order.
    """
    data = _load()
    fallback = _norm_default(data.get("default-permission", "op"))
    nodes: dict[str, Permission] = {}

    def walk(node: str, spec, parent: str | None, inherited: str) -> None:
        spec = spec if isinstance(spec, dict) else {}
        own = spec.get("default")
        effective = _norm_default(own) if own is not None else inherited

        raw_children = spec.get("children") or {}
        if isinstance(raw_children, list): # could also be plain list
            raw_children = {c: {} for c in raw_children}

        existing = nodes.get(node)
        if existing is None:
            nodes[node] = Permission(
                node=node,
                description=str(spec.get("description", "")),
                default=effective,
                parent=parent,
                children=list(raw_children),
            )
        else: # when double-defined chooses whichever has description/more specific
            existing.description = existing.description or str(spec.get("description", ""))
            existing.parent = existing.parent or parent

        for child, child_spec in raw_children.items():
            walk(child, child_spec, node, effective)

    for node, spec in (data.get("permissions") or {}).items():
        walk(node, spec, None, fallback)
    return nodes
