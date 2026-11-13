#!/usr/bin/env python3
"""Check asset source shapes without network dependencies."""
from pathlib import Path
from xml.etree import ElementTree
import json
root = Path(__file__).resolve().parents[1]
icons = sorted((root / "assets/icons").glob("*.svg"))
assert len(icons) >= 20, f"Expected 20+ first-party icons, got {len(icons)}"
for svg in icons:
    node = ElementTree.parse(svg).getroot()
    assert node.tag.endswith("svg") and node.attrib.get("viewBox") == "0 0 24 24", svg
    assert node.find("{http://www.w3.org/2000/svg}path") is not None, svg
tokens = json.loads((root / "assets/tokens.json").read_text())
assert tokens["colors"]["ink"] == "#080809"
assert tokens["colors"]["paper"] == "#F2F2EE"
assert tokens["colors"]["brass"] == "#CDA664"
print(f"ATELIER geometry checks passed: {len(icons)} SVG icons, shared tokens intact.")
