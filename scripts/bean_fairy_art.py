# /// script
# requires-python = ">=3.11"
# dependencies = ["pillow"]
# ///
"""Generates Bean Fairy art files for the Fertile Grounds mod.

Outputs, under the repo given as the first argument:
  docs/art/bean_fairy_template.png  32x32, every model face a flat color (paint over this)
  docs/art/bean_fairy_guide.png     the template at 16x zoom with each face labeled
  src/main/resources/.../textures/entity/bean_fairy.png        placeholder fairy skin
  src/main/resources/.../textures/item/fairy_dust.png          vanilla glowstone dust, recolored
  src/main/resources/.../textures/item/bean_fairy_spawn_egg.png vanilla allay egg, recolored

The second argument is the Minecraft client jar to pull vanilla textures from.

Usage (from the repo root): uv run scripts/bean_fairy_art.py . <minecraft-client.jar>
"""

import colorsys
import io
import sys
import zipfile
from pathlib import Path

from PIL import Image, ImageDraw, ImageFont

TEX = 32
ZOOM = 16
LEGEND_HEIGHT = 230

# Must match BeanFairyModel.createBodyLayer(): name, texOffs u, v, box size w, h, d.
PARTS = [
    ("left bean", 0, 0, 5, 4, 4),
    ("right bean", 0, 8, 5, 4, 4),
    ("stem", 20, 0, 1, 1, 1),
    ("wings", 0, 16, 5, 6, 0),
]


def faces(name, u, v, w, h, d):
    """Yields (label, x, y, width, height) for each face, following vanilla's ModelPart.Cube."""
    left_end = "inner end (hidden)" if name == "right bean" else "outer end"
    right_end = "inner end (hidden)" if name == "left bean" else "outer end"
    if name == "wings":
        yield ("wing, seen from front", u + d, v + d, w, h)
        yield ("wing, seen from behind", u + 2 * d + w, v + d, w, h)
        return
    yield ("top", u + d, v, w, d)
    yield ("bottom", u + d + w, v, w, d)
    yield (left_end if name != "stem" else "side", u, v + d, d, h)
    yield ("FRONT (face)" if name != "stem" else "front", u + d, v + d, w, h)
    yield (right_end if name != "stem" else "side", u + d + w, v + d, d, h)
    yield ("back", u + 2 * d + w, v + d, w, h)


TEMPLATE_COLORS = [
    (230, 25, 75), (60, 180, 75), (255, 225, 25), (0, 130, 200), (245, 130, 48),
    (145, 30, 180), (70, 240, 240), (240, 50, 230), (210, 245, 60), (250, 190, 212),
    (0, 128, 128), (220, 190, 255), (170, 110, 40), (128, 0, 0), (170, 255, 195),
    (128, 128, 0), (255, 215, 180), (0, 0, 128), (128, 128, 128), (255, 255, 255),
]

POD = (127, 184, 62)
POD_SHADOW = (90, 143, 42)
POD_HIGHLIGHT = (166, 214, 91)
STEM = (74, 110, 38)
EYE = (34, 40, 30)
WING = (230, 247, 255)
WING_VEIN = (169, 216, 238)


def template_and_guide(out_dir: Path):
    template = Image.new("RGBA", (TEX, TEX), (0, 0, 0, 0))
    guide = Image.new("RGBA", (TEX * ZOOM, TEX * ZOOM + LEGEND_HEIGHT), (40, 40, 40, 255))
    draw_t = ImageDraw.Draw(template)
    draw_g = ImageDraw.Draw(guide)
    font = ImageFont.load_default()
    legend = []
    for part in PARTS:
        for label, x, y, w, h in faces(*part):
            if w == 0 or h == 0:
                continue
            color = TEMPLATE_COLORS[len(legend) % len(TEMPLATE_COLORS)]
            legend.append((color, f"{part[0]}: {label}" if part[0] != "stem" else f"stem: {label}"))
            draw_t.rectangle([x, y, x + w - 1, y + h - 1], fill=color + (255,))
            box = [x * ZOOM, y * ZOOM, (x + w) * ZOOM - 1, (y + h) * ZOOM - 1]
            draw_g.rectangle(box, fill=color + (255,), outline=(0, 0, 0, 255))
            draw_g.text((box[0] + 3, box[1] + 2), str(len(legend)), fill=(0, 0, 0, 255), font=font)
    # Pixel grid so the guide lines up with Aseprite's grid.
    for n in range(TEX + 1):
        draw_g.line([(n * ZOOM, 0), (n * ZOOM, TEX * ZOOM)], fill=(70, 70, 70, 255))
        draw_g.line([(0, n * ZOOM), (TEX * ZOOM, n * ZOOM)], fill=(70, 70, 70, 255))
    # Numbered legend below the texture grid.
    top = TEX * ZOOM
    for n, (color, text) in enumerate(legend):
        col, row = divmod(n, 12)
        x, y = 8 + col * 256, top + 6 + row * 18
        draw_g.rectangle([x, y, x + 12, y + 12], fill=color + (255,), outline=(0, 0, 0, 255))
        draw_g.text((x + 18, y), f"{n + 1}. {text}", fill=(235, 235, 235, 255), font=font)
    out_dir.mkdir(parents=True, exist_ok=True)
    template.save(out_dir / "bean_fairy_template.png")
    guide.save(out_dir / "bean_fairy_guide.png")


def placeholder(path: Path):
    img = Image.new("RGBA", (TEX, TEX), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    for part in PARTS:
        name = part[0]
        for label, x, y, w, h in faces(*part):
            if w == 0 or h == 0:
                continue
            rect = [x, y, x + w - 1, y + h - 1]
            if name == "stem":
                draw.rectangle(rect, fill=STEM)
            elif name == "wings":
                draw.rectangle(rect, fill=WING)
                draw.line([x, y + h - 1, x + w - 1, y], fill=WING_VEIN)
            elif label == "top":
                draw.rectangle(rect, fill=POD_HIGHLIGHT)
            elif label == "bottom":
                draw.rectangle(rect, fill=POD_SHADOW)
            else:
                draw.rectangle(rect, fill=POD)
                if label == "FRONT (face)":
                    # A pair of eyes per bean, plus a darker column where the beans meet.
                    draw.point((x + 1, y + 1), fill=EYE)
                    draw.point((x + 3, y + 1), fill=EYE)
                    seam_x = x + w - 1 if name == "left bean" else x
                    draw.line([seam_x, y, seam_x, y + h - 1], fill=POD_SHADOW)
    path.parent.mkdir(parents=True, exist_ok=True)
    img.save(path)


def recolor(jar: zipfile.ZipFile, vanilla: str, path: Path, hue: float, sat_boost: float):
    """Rotates every pixel's hue to `hue` (0-1), keeping its shading, like Aseprite's Hue slider."""
    img = Image.open(io.BytesIO(jar.read(vanilla))).convert("RGBA")
    px = img.load()
    for yy in range(img.height):
        for xx in range(img.width):
            r, g, b, a = px[xx, yy]
            if a == 0:
                continue
            _, s, v = colorsys.rgb_to_hsv(r / 255, g / 255, b / 255)
            nr, ng, nb = colorsys.hsv_to_rgb(hue, min(1.0, s * sat_boost), v)
            px[xx, yy] = (round(nr * 255), round(ng * 255), round(nb * 255), a)
    path.parent.mkdir(parents=True, exist_ok=True)
    img.save(path)


def main():
    repo = Path(sys.argv[1])
    textures = repo / "src/main/resources/assets/fertilegrounds/textures"
    template_and_guide(repo / "docs/art")
    placeholder(textures / "entity/bean_fairy.png")
    with zipfile.ZipFile(sys.argv[2]) as jar:
        recolor(jar, "assets/minecraft/textures/item/glowstone_dust.png",
                textures / "item/fairy_dust.png", hue=0.88, sat_boost=0.8)
        recolor(jar, "assets/minecraft/textures/item/allay_spawn_egg.png",
                textures / "item/bean_fairy_spawn_egg.png", hue=0.25, sat_boost=1.2)


if __name__ == "__main__":
    main()
