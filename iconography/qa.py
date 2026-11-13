#!/usr/bin/env python3
"""Rasterize the canonical ATELIER symbols for optical review.

Creates PNG contact sheets and a machine-readable coverage report.
Does not alter original SVG geometry and requires no third-party icon art.
"""
from __future__ import annotations

import json
from collections import defaultdict
from pathlib import Path

import cairosvg
from PIL import Image, ImageDraw, ImageFont

ROOT = Path(__file__).resolve().parents[1]
CATALOG = json.loads((ROOT / "iconography/catalog.json").read_text())
OUT = ROOT / "dist/iconography/previews"
OUT.mkdir(parents=True, exist_ok=True)
COLORS = {"ink": "#080809", "card": "#151516", "line": "#353538",
          "paper": "#F2F2EE", "fog": "#99999B", "gold": "#CDA664"}
STYLES = (("contour",24),("core",20),("etch",32),("inlay",64))


def font(size: int, bold: bool = False):
    paths = [
        "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf",
        "/usr/share/fonts/truetype/liberation2/LiberationSans-Regular.ttf"
    ]
    for path in paths:
        try:
            return ImageFont.truetype(path, size)
        except OSError:
            continue
    return ImageFont.load_default()


def render(name: str, family: str, output_size: int) -> Image.Image:
    source = ROOT / "iconography/svg" / family / (name + ".svg")
    result = cairosvg.svg2png(url=str(source), output_width=output_size,
                             output_height=output_size)
    from io import BytesIO
    return Image.open(BytesIO(result)).convert("RGBA")


def paint_card(canvas: Image.Image, name: str, family: str,
               x: int, y: int, w: int = 165, h: int = 150) -> None:
    draw = ImageDraw.Draw(canvas)
    draw.rounded_rectangle((x, y, x+w, y+h), radius=13, fill=COLORS["card"],
                           outline=COLORS["line"], width=1)
    size = 64 if family == "inlay" else 40
    icon = render(name, family, size)
    canvas.alpha_composite(icon, (x+(w-size)//2, y+22))
    draw.text((x+w//2, y+103), name, font=font(13), anchor="mm",
              fill=COLORS["paper"])
    draw.text((x+w//2, y+126), family.upper(), font=font(10),
              anchor="mm", fill=COLORS["fog"])


def contact_sheet(category: str, items: list[dict]) -> None:
    cols=7
    cell_w, cell_h, left, top=173, 159, 25, 92
    rows=(len(items)+cols-1)//cols
    w=left*2+cols*cell_w
    h=top+rows*cell_h+38
    image=Image.new("RGBA", (w,h), COLORS["ink"])
    draw=ImageDraw.Draw(image)
    draw.text((left,27),"ATELIER / "+category.upper(),font=font(26),
              fill=COLORS["paper"])
    draw.line((left,68,w-left,68),fill=COLORS["line"],width=1)
    draw.line((left,68,left+40,68),fill=COLORS["gold"],width=2)
    for idx,entry in enumerate(items):
        x=left+(idx%cols)*cell_w
        y=top+(idx//cols)*cell_h
        paint_card(image,entry["name"],"contour",x,y,w=165,h=150)
    image.convert("RGB").save(OUT/(category+".png"),quality=95)


def optical_report(items: list[dict]):
    report=[]
    for entry in items:
        sizes={}
        for family in ("contour","core","etch"):
            # Same raster size for a fair weight comparison.
            rgba=render(entry["name"],family,96)
            alpha=rgba.getchannel("A")
            threshold=alpha.point(lambda n: 255 if n > 24 else 0)
            bounds=threshold.getbbox()
            coverage=sum(1 for n in threshold.getdata() if n>0)/(96*96)
            sizes[family]={"bounds":list(bounds) if bounds else None,
                           "coverage":round(coverage,4)}
            if bounds is None:
                raise RuntimeError("Blank glyph: "+entry["name"]+" / "+family)
        if sizes["core"]["coverage"] + .001 < sizes["contour"]["coverage"]:
            raise RuntimeError("Core lighter than Contour: "+entry["name"])
        report.append({"name":entry["name"],"category":entry["category"],
                       "optical":sizes})
    (OUT/"optical-report.json").write_text(
        json.dumps({"count":len(report),"icons":report},indent=2)+"\n")
    return report


def compare_hero(items: list[dict]) -> None:
    picks=["home","search","settings","wifi","calendar","camera",
           "mail","terminal","repository","database","focus","music"]
    lookup={item["name"]:item for item in items}
    sheet=Image.new("RGBA",(1280,700),COLORS["ink"])
    draw=ImageDraw.Draw(sheet)
    draw.text((34,32),"ATELIER / THE SYMBOL SYSTEM",font=font(27),fill=COLORS["paper"])
    draw.text((34,67),"155 ORIGINAL SYMBOLS / FOUR INTENTIONAL TREATMENTS",
              font=font(13),fill=COLORS["fog"])
    draw.line((34,99,1246,99),fill=COLORS["line"],width=1)
    families=("contour","core","etch","inlay")
    for idx,family in enumerate(families):
        y=133+idx*130
        draw.text((33,y+12),family.upper(),font=font(18),fill=COLORS["paper"])
        draw.text((33,y+38),{ "contour":"REGULAR / 24 DP",
                             "core":"DENSE / 16-20 DP",
                             "etch":"EDITORIAL / 32+ DP",
                             "inlay":"LAUNCHER / 64 UNIT" }[family],
                  font=font(10),fill=COLORS["fog"])
        for i,name in enumerate(picks):
            px=226+i*86
            if name not in lookup:
                continue
            icon_size=64 if family=="inlay" else 40
            sample=render(name,family,icon_size)
            sheet.alpha_composite(sample,(px+(64-icon_size)//2,y))
            if idx==3:
                draw.text((px+31,y+77),name,font=font(10),anchor="mm",
                          fill=COLORS["fog"])
    sheet.convert("RGB").save(OUT/"overview.png",quality=96)


def main() -> None:
    icons=CATALOG["icons"]
    grouped=defaultdict(list)
    for icon in icons:
        grouped[icon["category"]].append(icon)
    for name,group in grouped.items():
        contact_sheet(name,group)
    report=optical_report(icons)
    compare_hero(icons)
    total_coverage=[i["optical"]["contour"]["coverage"] for i in report]
    print(f"Rendered {len(icons)} original glyphs across seven categories.")
    print("Contours coverage range:",
          round(min(total_coverage)*100,2),"%-",
          round(max(total_coverage)*100,2),"%")
    print("Generated contact sheets and optical metrics in",OUT)


if __name__=="__main__":
    main()
