#!/usr/bin/env python3
"""Validate ATELIER's public repository profile and Markdown navigation.

Uses Python's standard library; no network or account access required.
"""
from __future__ import annotations
import json
import re
from pathlib import Path
from urllib.parse import unquote, urlsplit

ROOT=Path(__file__).resolve().parents[2]
PROFILE=ROOT/".github"/"REPOSITORY_PROFILE.json"
PUBLIC_MD=[
    "README.md","CHANGELOG.md","CONTRIBUTING.md","SECURITY.md",
    *[str(p.relative_to(ROOT)) for p in sorted((ROOT/"docs").glob("*.md"))],
]
RELEASE="v0.9.0-preview.2"

profile=json.loads(PROFILE.read_text(encoding="utf-8"))
assert profile["repository"]=="varunaditya27/oneplus-nord-5-themekit"
assert 40<=len(profile["description"])<=350
homepage=urlsplit(profile["homepage"])
assert homepage.scheme=="https" and homepage.netloc=="varunaditya27.github.io"
assert homepage.path.endswith("/oneplus-nord-5-themekit/")
topics=profile["topics"]
assert 5<=len(topics)<=20 and len(set(topics))==len(topics)
assert all(re.fullmatch(r"[a-z0-9][a-z0-9-]{0,49}",t) for t in topics)
assert "open-source" not in topics, "Choose a project license before claiming open source"
print("PASS GitHub About profile:",profile["description"])

markdown_link=re.compile(r"(?<!!)\[[^\]]*\]\(([^)]+)\)")
html_src=re.compile(r"""<img\b[^>]*?\bsrc=["']([^"']+)["']""",re.I)
checked=0
for filename in PUBLIC_MD:
    source=ROOT/filename
    content=source.read_text(encoding="utf-8")
    targets=[*markdown_link.findall(content),*html_src.findall(content)]
    for raw in targets:
        raw=raw.strip().split(" ",1)[0].strip("<>")
        if not raw or raw.startswith("#"):
            continue
        parsed=urlsplit(raw)
        if parsed.scheme in ("https","http","mailto") or raw.startswith("//"):
            continue
        assert not parsed.scheme and not raw.startswith("/"),(filename,raw)
        target=unquote(parsed.path)
        if not target:
            continue
        resolved=(source.parent/target).resolve()
        assert resolved.is_relative_to(ROOT),(filename,raw,"escapes repository")
        assert resolved.exists(),(filename,raw,"broken local link")
        checked+=1
print(f"PASS {len(PUBLIC_MD)} Markdown documents; {checked} local references")

readme=(ROOT/"README.md").read_text(encoding="utf-8")
assert "releases/tag/"+RELEASE in readme
assert "github.io/oneplus-nord-5-themekit/" in readme
assert "physical" in readme.lower() and "debug-signed" in readme.lower()
assert "pre-alpha" not in readme.lower()
assert "## Install the preview" in readme
print("PASS release / install / transparency checks")
