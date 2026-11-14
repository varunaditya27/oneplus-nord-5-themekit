#!/usr/bin/env python3
"""Build PNG derivatives and a portable ATELIER brand archive from SVG masters.

Requires CairoSVG. Run: python scripts/brand/export.py dist/release
All source vectors are committed; generated rasters are release derivatives.
"""
from __future__ import annotations
from pathlib import Path
from shutil import copy2
from zipfile import ZipFile, ZIP_DEFLATED
import hashlib
import subprocess
import sys

import cairosvg

ROOT=Path(__file__).resolve().parents[2]
OUTPUT=Path(sys.argv[1]) if len(sys.argv)>1 else ROOT/"dist"/"brand"
OUTPUT.mkdir(parents=True,exist_ok=True)
ARCHIVE=OUTPUT/"ATELIER-brand-kit-v0.9.0-preview.2.zip"
SOURCE=ROOT/"assets"/"brand"
ASSETS=[
    ("mark",[(256,256),(512,512),(1024,1024)]),
    ("wordmark",[(836,150)]),
    ("lockup",[(880,280)]),
    ("favicon",[(32,32),(64,64),(128,128)]),
    ("app-icon",[(192,192),(512,512),(1024,1024)]),
    ("avatar",[(640,640)]),
    ("repo-cover",[(1280,640)]),
    ("social-card",[(1200,630)]),
    ("splash",[(1080,2400)]),
    ("pattern",[(1280,300)]),
]
pngdir=OUTPUT/"png"
pngdir.mkdir(exist_ok=True)
for name,sizes in ASSETS:
    src=SOURCE/f"{name}.svg"
    for w,h in sizes:
        dest=pngdir/f"{name}-{w}x{h}.png"
        cairosvg.svg2png(url=str(src),write_to=str(dest),
                         output_width=w,output_height=h)
        assert dest.stat().st_size>200, dest
        print("RASTER",dest.name)

with ZipFile(ARCHIVE,"w",compression=ZIP_DEFLATED,compresslevel=9) as z:
    for f in sorted(SOURCE.glob("*.svg")):
        z.write(f,"svg/"+f.name)
    for f in sorted(pngdir.glob("*.png")):
        z.write(f,"png/"+f.name)
    for path in ("docs/BRAND_IDENTITY.md","docs/LICENSING.md"):
        z.write(ROOT/path,path)
print("BRAND PACK",ARCHIVE,ARCHIVE.stat().st_size,"bytes")
