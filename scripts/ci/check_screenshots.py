#!/usr/bin/env python3
"""Sanity-check actual emulator screenshots, not just a passing process ID."""
from pathlib import Path
from PIL import Image, ImageChops, ImageStat

folder = Path("dist/emulator")
photos = {}
for page in ("home", "library", "settings"):
    filename = folder / f"{page}.png"
    assert filename.is_file(), f"No {page} screenshot"
    photo = Image.open(filename).convert("RGB")
    assert photo.width >= 720 and photo.height >= 1280, f"Invalid resolution: {page}"
    # Avoid a repeat of the OEM bright wallpaper bleeding into our dark-only UI.
    # The checked region is away from status bar, clock numerals and title.
    patch = photo.crop((int(photo.width * .45), int(photo.height * .10),
                        int(photo.width * .55), int(photo.height * .14)))
    mean = sum(ImageStat.Stat(patch).mean) / 3
    assert mean < 60, f"{page} violates dark material: sampled mean {mean:.1f}"
    photos[page] = photo
    print(f"{page}: {photo.width}x{photo.height}; dark-material mean {mean:.2f}")
for start, end in (("home", "library"), ("library", "settings")):
    assert ImageChops.difference(photos[start], photos[end]).getbbox(), (
        f"{start} and {end} screenshots are identical; navigation may be blocked"
    )
print("ATELIER screenshots are dark and navigation generated distinct frames.")
