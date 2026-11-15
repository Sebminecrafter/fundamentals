"""
Entry point for docs macros.
Imports modules live due to Zensical limitations.
"""

import sys
from pathlib import Path

_HERE = str(Path(__file__).resolve().parent)
if _HERE not in sys.path:
    sys.path.insert(0, _HERE)

from docgen import commands, nav, permissions  # noqa: E402


def define_env(env):
    nav.register(env)          # {{ contents() }}
    commands.register(env)     # {{ commands() }}
    permissions.register(env)  # {{ permissions() }}