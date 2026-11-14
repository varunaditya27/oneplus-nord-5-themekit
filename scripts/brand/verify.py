#!/usr/bin/env python3
"""Validate source-of-truth ATELIER SVGs without network dependencies."""
from pathlib import Path
from xml.etree import ElementTree as ET

ROOT=Path(__file__).resolve().parents[2]
FILES=("mark","wordmark","lockup","favicon","app-icon","avatar",
       "repo-cover","social-card","splash","pattern","atelier-cover")
NS="{http://www.w3.org/2000/svg}"
for name in FILES:
    src=ROOT/"assets"/"brand"/f"{name}.svg"
    data=src.read_text(encoding="utf-8")
    root=ET.fromstring(data)
    assert root.tag==NS+"svg",f"{name}: not SVG"
    assert root.attrib.get("viewBox"),f"{name}: missing viewBox"
    assert len(data)>150,f"{name}: suspiciously empty source"
    for e in root.iter():
        assert e.tag not in (NS+"script",NS+"foreignObject"),f"{name}: executable content"
        for k,v in e.attrib.items():
            assert not k.lower().endswith("href") or (
                not v.startswith(("http:","https:","data:"))
            ),f"{name}: external reference forbidden"
    print("VALID",src.relative_to(ROOT))
word=(ROOT/"assets"/"brand"/"wordmark.svg").read_text()
assert "<text" not in word, "Wordmark must use actual paths, not unavailable fonts"
android=(ROOT/"app/src/main/AndroidManifest.xml").read_text()
assert 'android:icon="@mipmap/atelier_launcher"' in android
assert (ROOT/"app/src/main/res/mipmap-anydpi-v26/atelier_launcher.xml").exists()
print("All first-party brand assets and native icon wiring passed.")
