#!/usr/bin/env python3
"""Original and reproducible 1080x2400 ATELIER wallpapers. No downloaded textures."""
from pathlib import Path
import numpy as np
from PIL import Image

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "assets" / "wallpapers"
OUT.mkdir(parents=True, exist_ok=True)
W, H = 1080, 2400
x = np.arange(W, dtype=np.float32)[None, :] / W
y = np.arange(H, dtype=np.float32)[:, None] / H
rng = np.random.default_rng(4101)

noise = rng.normal(0, 1.1, (H, W)).astype(np.float32)
silk = np.sin((x * 690 + y * 80) ** 1.04).astype(np.float32)
fold = .225 + .49 * (1 - y) ** 1.8 + .048 * np.sin(y * 5.6 + 1.2)
distance = x - fold
ridge = np.exp(-(distance / .035) ** 2) * 10
sheen = np.exp(-((distance + .13) / .19) ** 2) * 5
creases = np.sin((x - y * .27) * 110) * np.exp(-((distance + .11) / .23) ** 2) * 2
brightness = 7.6 + noise + silk * .55 + ridge + sheen + creases
brightness += 3.2 * np.exp(-((x - .67) / .31) ** 2 - ((y - .43) / .24) ** 2)
channels = np.broadcast_to(brightness[:, :, None], (H, W, 3)).copy()
channels[:, :, 1] += .10
channels[:, :, 2] += .45
# One narrow metal-light trace, not a gold gradient.
trace = np.exp(-(np.abs(distance + .009) / .0013) ** 2)
trace *= (.22 + .78 * np.exp(-((y - .68) / .21) ** 2))
channels[:, :, 0] += trace * 48
channels[:, :, 1] += trace * 34
channels[:, :, 2] += trace * 18

silkfield = Image.fromarray(np.clip(channels, 0, 255).astype(np.uint8), "RGB")
silkfield.save(OUT / "silkfield-ink.webp", quality=94, method=6)
silkfield.resize((540, 1200), Image.Resampling.LANCZOS).save(OUT / "silkfield-preview.png")

mineral_noise = rng.normal(0, 1.5, (H, W)).astype(np.float32)
mineral = np.clip(8.6 + mineral_noise
    + 4 * np.exp(-((x - .65 + y * .2) / .2) ** 2)
    + 3 * np.sin(x * 60 + y * 25) * np.exp(-((x - .44) / .3) ** 2), 0, 255)
mineral_rgb = np.broadcast_to(mineral[:, :, None], (H, W, 3)).copy()
Image.fromarray(mineral_rgb.astype(np.uint8), "RGB").save(OUT / "mineral-black.webp", quality=93)
Image.new("RGB", (W, H), (8, 8, 9)).save(OUT / "absolute-ink.webp", quality=96)

print("Generated three original wallpaper masters in", OUT)
