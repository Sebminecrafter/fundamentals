from pathlib import Path

# docs/macros/docgen/paths.py -> repository root
# fixes importing
ROOT = Path(__file__).resolve().parents[3]
ZENSICAL_TOML = ROOT / "zensical.toml"
PLUGIN_YML = ROOT / "src" / "main" / "resources" / "plugin.yml"
