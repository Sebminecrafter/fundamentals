"""Entry point for the docs macros (loaded by Zensical via `module_name`).

Zensical loads this file with `spec_from_file_location`, so sibling modules are
NOT importable by default. We put this directory on `sys.path` and keep the
real code in the `docgen` package. This file only wires macros up.
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
