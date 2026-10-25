#!/usr/bin/env python3
"""Builds the "Fertile Grounds Demo" world on a headless dev server.

Writes an `fgdemo` datapack that builds every station, checks the result over local RCON, and
copies the world into the Minecraft saves folder and each loader's dev client saves. The datapack
stays in the save, so `/function fgdemo:reset/<station>` and `/function fgdemo:mode/<mode>` work
while testing.

    python3 scripts/demo/build_demo_world.py            # build, check, copy to saves
    python3 scripts/demo/build_demo_world.py --no-copy  # build and check only

Exits 1 if a check fails.
"""

from __future__ import annotations

import argparse
import gzip
import json
import random
import re
import secrets
import shutil
import socket
import struct
import subprocess
import sys
import time
import zipfile
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
SERVER_DIR = ROOT / "run" / "demo-server"
LEVEL_NAME = "Fertile Grounds Demo"
WORLD = SERVER_DIR / LEVEL_NAME
SAVES = Path.home() / "Library" / "Application Support" / "minecraft" / "saves"
# Each loader's dev client keeps its own saves (see the runs in each loader's build.gradle).
DEV_SAVES = (ROOT / "run" / "saves", ROOT / "run" / "neoforge" / "saves", ROOT / "run" / "forge-client" / "saves")
SEED = "fertilegrounds"
RCON_PORT = 25599
SERVER_PORT = 25598
MOD = "fertilegrounds"

# Vanilla limits one fill to 32,768 blocks.
FILL_LIMIT = 32768

# The plot, relative to the origin at spawn: stations run east along a path at z = 0.
PLOT = (-58, -26, 186, 26)
FLOOR_DEPTH = 8  # dirt this deep under the grass, so the grove's safety scan sees only dirt
CLEAR_HEIGHT = 48  # air this high above the grass

# Ground cover for the meadow and path edges; every one is natural, so the grove can clear it.
EDGE_PLANTS = [
    "short_grass", "short_grass", "pink_petals[flower_amount=3]", "pink_petals[flower_amount=4]",
    "wildflowers[flower_amount=3]", "lily_of_the_valley", "azure_bluet",
]  # fmt: skip
MEADOW_PLANTS = EDGE_PLANTS + [
    "short_grass", "short_grass", "fern", "bush", "firefly_bush", "oxeye_daisy", "cornflower",
]  # fmt: skip

AZALEA = ["flowering_azalea_leaves[persistent=true]", "azalea_leaves[persistent=true]"]

FLOWERS = [
    "dandelion", "poppy", "blue_orchid", "allium", "azure_bluet",
    "red_tulip", "orange_tulip", "white_tulip", "oxeye_daisy", "cornflower",
]  # fmt: skip


# ---- Coordinates and commands ----


class Site:
    """Turns coordinates relative to the spawn origin into absolute ones."""

    def __init__(self, x: int, y: int, z: int) -> None:
        self.x, self.y, self.z = x, y, z

    def at(self, dx: int, dy: int, dz: int) -> str:
        return f"{self.x + dx} {self.y + dy} {self.z + dz}"

    def fill(self, a: tuple, b: tuple, block: str) -> list[str]:
        """One or more fills covering box a..b, each under vanilla's block limit."""
        (x1, y1, z1), (x2, y2, z2) = (
            tuple(map(min, a, b)),
            tuple(map(max, a, b)),
        )
        slab = (y2 - y1 + 1) * (z2 - z1 + 1)
        step = max(1, FILL_LIMIT // slab)
        commands = []
        for x in range(x1, x2 + 1, step):
            end = min(x + step - 1, x2)
            commands.append(f"fill {self.at(x, y1, z1)} {self.at(end, y2, z2)} {block}")
        return commands

    def set(self, dx: int, dy: int, dz: int, block: str) -> str:
        return f"setblock {self.at(dx, dy, dz)} {block}"


def sign(lines: list[str], command: str | None = None, wall: str | None = None, rotation: int = 0):
    """A waxed oak sign; clicking it runs `command` when there is one.

    On 26.3 a sign runs click commands only with `allow_op_features`, which only an operator's
    command (such as this setblock) can set.
    """
    first = json.dumps(lines[0])
    extra = ""
    if command:
        extra = ",allow_op_features:1b"
        first = (
            "{text:" + json.dumps(lines[0]) + ',color:"dark_green",'
            'click_event:{action:"run_command",command:' + json.dumps(command) + "}}"
        )
    messages = ",".join([first] + [json.dumps(line) for line in lines[1:4]])
    state = f"oak_wall_sign[facing={wall}]" if wall else f"oak_sign[rotation={rotation}]"
    return f"{state}{{is_waxed:1b{extra},front_text:{{messages:[{messages}]}}}}"


def snbt(value) -> str:
    """JSON value to SNBT, for item components copied from a recipe."""
    if isinstance(value, dict):
        return "{" + ",".join(f"{json.dumps(k)}:{snbt(v)}" for k, v in value.items()) + "}"
    if isinstance(value, list):
        return "[" + ",".join(snbt(v) for v in value) + "]"
    if isinstance(value, bool):
        return "true" if value else "false"
    return json.dumps(value)


def chest(items: list[tuple[str, int]], facing: str = "north") -> str:
    slots = ",".join(
        f'{{Slot:{slot}b,id:"{item}",count:{count}}}' for slot, (item, count) in enumerate(items)
    )
    return f"chest[facing={facing}]{{Items:[{slots}]}}"


# ---- Stations ----
# Each station builds inside its own box, so reset/<name> clears the box and builds it again.

BOXES = {
    "spawn": ((-7, -6), (10, 6)),
    "path": ((-2, -2), (97, 2)),
    "soil": ((11, -14), (24, -3)),
    "fairy_farm": ((25, 3), (39, 16)),
    "hotels": ((41, -16), (58, -3)),
    "chime": ((59, 3), (71, 14)),
    "grove": ((99, -20), (137, 20)),
    "test_lot": ((-57, -19), (-9, 19)),
    "hero": ((143, -20), (181, 20)),
}


def build_floor(s: Site, box) -> list[str]:
    (x1, z1), (x2, z2) = box
    return (
        s.fill((x1, 1, z1), (x2, CLEAR_HEIGHT, z2), "air")
        + s.fill((x1, -FLOOR_DEPTH, z1), (x2, -1, z2), "dirt")
        + s.fill((x1, 0, z1), (x2, 0, z2), "grass_block")
    )


def build_spawn(s: Site) -> list[str]:
    c = [s.set(0, 0, 0, "moss_block")]
    # A wall of clickable signs facing the path.
    c += s.fill((-5, 1, -4), (-5, 4, 4), "stripped_birch_wood")
    c += [s.set(-5, 5, z, "lantern") for z in (-4, 4)]
    board = [
        (["Fertile Grounds", "Test World", "Click a sign", "to run it"], None),
        (["Test mode", "fast ticks,", "fairies nightly", "mode/test"], "mode/test"),
        (["Demo mode", "still scene", "for recording", "mode/demo"], "mode/demo"),
        (["Defaults", "every gamerule", "back to normal", "mode/default"], "mode/default"),
        (["Reset all", "rebuild every", "station", "reset/all"], "reset/all"),
        (["Reset soil", "replant the", "soil rows", "reset/soil"], "reset/soil"),
        (["Reset farm", "replant the", "fairy farm", "reset/fairy_farm"], "reset/fairy_farm"),
        (["Reset hotels", "rebuild the", "hotel garden", "reset/hotels"], "reset/hotels"),
        (["Reset chime", "replant the", "Chime field", "reset/chime"], "reset/chime"),
        (["Reset grove", "shrine and", "hillside back", "reset/grove"], "reset/grove"),
        (["Reset test lot", "flat grass", "again", "reset/test_lot"], "reset/test_lot"),
        (["Reset hero", "the hero grove", "and its fairies", "reset/hero"], "reset/hero"),
    ]
    for i, (lines, path) in enumerate(board):
        row, col = divmod(i, 4)
        command = f"/function fgdemo:{path}" if path else None
        c.append(s.set(-4, 3 - row, -3 + 2 * col, sign(lines, command, wall="east")))
    # The guide book on a lectern, built from the guide book recipe's own result.
    recipe = json.loads(
        (ROOT / f"common/src/main/resources/data/{MOD}/recipe/guide_book.json").read_text()
    )
    book = recipe["result"]
    item = f'{{id:"{book["id"]}",count:1,components:{snbt(book["components"])}}}'
    c.append(s.set(3, 1, -4, f"lectern[facing=south,has_book=true]{{Book:{item}}}"))
    c.append(s.set(3, 1, -5, sign(["Guide book", "every feature,", "page by page", ""], rotation=0)))
    supplies = [
        [
            ("diamond", 64), (f"{MOD}:fairy_dust", 64), (f"{MOD}:fertilizer", 64),
            ("glass_bottle", 16), ("brush", 1), ("brush", 1),
            (f"{MOD}:empty_fairy_hotel", 16), (f"{MOD}:fairy_hotel", 16),
            (f"{MOD}:chime_fairy_hotel", 8), (f"{MOD}:jarred_fairy", 1), (f"{MOD}:jarred_fairy", 1),
            (f"{MOD}:bean_fairy_spawn_egg", 16), ("bee_spawn_egg", 8),
        ],
        [
            (f"{MOD}:enriched_dirt", 64), (f"{MOD}:super_enriched_dirt", 64),
            (f"{MOD}:enriched_sand", 64), ("wheat_seeds", 64), ("carrot", 64), ("potato", 64),
            ("beetroot_seeds", 64), ("bamboo", 16), ("bone_meal", 64), ("water_bucket", 1),
            ("diamond_hoe", 1), ("diamond_shovel", 1), ("beehive", 4),
        ],
        [(f, 16) for f in FLOWERS]
        + [("note_block", 8), ("spyglass", 8), ("comparator", 8), ("redstone_lamp", 8),
           ("redstone", 32), ("lantern", 16), ("oak_fence", 32), ("oak_slab", 32)],
    ]  # fmt: skip
    for i, items in enumerate(supplies):
        c.append(s.set(3 + 2 * i, 1, 4, chest(items)))
    c.append(s.set(9, 1, 4, sign(["Supplies", "seeds, soil,", "hotels, dust,", "diamonds"])))
    return c


def build_path(s: Site) -> list[str]:
    c = s.fill((-1, 0, -1), (96, 0, 1), "dirt_path")
    rng = random.Random(3)
    for x in range(-1, 97):
        for z in (-1, 0, 1):
            if rng.random() < 0.18:
                c.append(s.set(x, 0, z, rng.choice(["coarse_dirt", "moss_block", "gravel"])))
    for x in range(10, 91, 10):
        for z in (-2, 2):
            c += [
                s.set(x, 1, z, "spruce_fence"),
                s.set(x, 2, z, "spruce_fence"),
                s.set(x, 3, z, "lantern"),
            ]
    # A soft edge of petals, grass, and scattered flowers between the lamp posts.
    for x in range(-1, 97):
        for z in (-2, 2):
            if x % 10 and rng.random() < 0.6:
                c.append(s.set(x, 1, z, rng.choice(EDGE_PLANTS)))
    return c


def station_sign(s: Site, dx: int, dz: int, lines: list[str], rotation: int) -> str:
    return s.set(dx, 1, dz, sign(lines, rotation=rotation))


def build_soil(s: Site) -> list[str]:
    rows = [
        (-5, "farmland[moisture=7]", "wheat"),
        (-6, f"{MOD}:enriched_farmland[moisture=7]", "wheat"),
        (-8, f"{MOD}:super_enriched_farmland[moisture=7]", "wheat"),
        (-10, f"{MOD}:enriched_sand", "bamboo_sapling"),
    ]
    c = s.fill((13, 0, -7), (21, 0, -7), "water")
    c += s.fill((13, 0, -9), (21, 0, -9), "water")
    for z, soil, plant in rows:
        c += s.fill((13, 0, z), (21, 0, z), soil)
        c += s.fill((13, 1, z), (21, 1, z), plant)
    c += s.fill((12, 0, -11), (22, 0, -4), "stone_bricks replace grass_block")
    c.append(
        station_sign(s, 17, -3, ["Soil rows", "vanilla, Enriched,", "Super Enriched,", "Enriched Sand"], 0)
    )
    return c


def build_fairy_farm(s: Site) -> list[str]:
    crops = ["wheat", "carrots", "potatoes", "beetroots"]
    c = s.fill((27, 0, 5), (35, 0, 13), "farmland[moisture=7]")
    for i, x in enumerate(range(27, 36)):
        c += s.fill((x, 1, 5), (x, 1, 13), crops[i % 4])
    c += [s.set(31, 0, 9, "water"), s.set(31, 1, 9, "air")]
    ring = [(x, 4) for x in range(26, 37)] + [(36, z) for z in range(5, 15)]
    ring += [(x, 14) for x in range(35, 25, -1)] + [(26, z) for z in range(13, 4, -1)]
    for i, (x, z) in enumerate(ring):
        c.append(s.set(x, 1, z, FLOWERS[i % len(FLOWERS)]))
    c.append(s.set(38, 1, 9, "beehive[facing=west]"))
    c.append(station_sign(s, 31, 3, ["Fairy farm", "Fairy Dust on a", "crop, then wait", "for night"], 8))
    return c


def build_hotels(s: Site) -> list[str]:
    c = s.fill((47, 0, -10), (51, 0, -6), "farmland[moisture=7]")
    c += s.fill((47, 1, -10), (51, 1, -6), "carrots")
    c += [s.set(49, 0, -8, "water"), s.set(49, 1, -8, "air")]
    for x, z in ((43, -5), (55, -5), (43, -13), (55, -13)):
        c += s.fill((x, 1, z), (x, 3, z), "oak_fence")
    c += s.fill((43, 4, -13), (55, 4, -5), "oak_slab[type=bottom]")
    for x, z in ((46, -6), (52, -6), (46, -12), (52, -12), (49, -9)):
        c.append(s.set(x, 3, z, f"{MOD}:fairy_hotel[hanging=true]"))
    c += [
        s.set(44, 1, -9, f"{MOD}:fairy_hotel"),
        s.set(54, 1, -9, f"{MOD}:empty_fairy_hotel"),
        s.set(57, 1, -9, "beehive[facing=west]"),
    ]
    c.append(station_sign(s, 49, -3, ["Fairy Hotels", "standing and", "hanging, one", "still empty"], 0))
    return c


def build_chime(s: Site) -> list[str]:
    c = s.fill((61, 0, 6), (67, 0, 12), f"{MOD}:enriched_farmland[moisture=7]")
    # Young wheat, so the field ripens and the hotel rings while you watch, not before you arrive.
    c += s.fill((61, 1, 6), (67, 1, 12), "wheat[age=3]")
    c += [s.set(64, 0, 9, "water"), s.set(64, 1, 9, "air")]
    # The comparator goes down first, so the hotel's placement updates it.
    c += [
        s.set(65, 1, 4, "comparator[facing=west]"),
        s.set(66, 1, 4, "redstone_lamp"),
        s.set(64, 1, 4, f"{MOD}:chime_fairy_hotel"),
    ]
    c.append(station_sign(s, 62, 3, ["Chime Hotel", "rings when the", "field is ripe;", "lamp blinks"], 8))
    return c


def build_grove(s: Site) -> list[str]:
    cx = 118
    rng = random.Random(7)  # fixed, so every reset builds the same hillside
    c: list[str] = []
    # Gentle mounds of grass over dirt.
    for mx, mz, radius, height in ((106, -11, 5, 3), (128, 9, 6, 2), (125, -12, 4, 2), (108, 12, 4, 1)):
        for dx in range(-radius, radius + 1):
            for dz in range(-radius, radius + 1):
                d = (dx * dx + dz * dz) ** 0.5
                h = round(height * (1 - d / (radius + 1)))
                if h > 0:
                    # Dirt from the floor up: covered grass would turn to dirt on its own later.
                    c += s.fill((mx + dx, 0, mz + dz), (mx + dx, h - 1, mz + dz), "dirt")
                    c.append(s.set(mx + dx, h, mz + dz, "grass_block"))
    # Two small oaks and an azalea, with leaves that never decay.
    for tx, tz in ((112, 6), (130, -4)):
        c += s.fill((tx - 2, 4, tz - 2), (tx + 2, 5, tz + 2), "oak_leaves[persistent=true]")
        c += s.fill((tx - 1, 6, tz - 1), (tx + 1, 6, tz + 1), "oak_leaves[persistent=true]")
        c += s.fill((tx, 1, tz), (tx, 5, tz), "oak_log")
        c.append(s.set(tx, 0, tz, "dirt"))
    c.append(s.set(121, 1, 13, "azalea"))
    # Wild plants scattered over the hillside, away from the shrine.
    for _ in range(120):
        x, z = rng.randint(cx - 17, cx + 17), rng.randint(-17, 17)
        if abs(x - cx) <= 4 and abs(z) <= 4:
            continue
        plant = rng.choice(MEADOW_PLANTS + FLOWERS)
        # Only on open grass, so plants never land on a log or in a mound's side.
        c.append(
            f"execute positioned {s.at(x, 1, z)} if block ~ ~ ~ air if block ~ ~-1 ~ grass_block"
            f" run setblock ~ ~ ~ {plant}"
        )
    # The shrine: a Chime Hotel on Super Enriched Dirt, a hotel 3 blocks out on each side, flowers.
    c.append(s.set(cx, 0, 0, f"{MOD}:super_enriched_dirt"))
    c.append(s.set(cx, 1, 0, f"{MOD}:chime_fairy_hotel"))
    for dx, dz in ((3, 0), (-3, 0), (0, 3), (0, -3)):
        c.append(s.set(cx + dx, 1, dz, f"{MOD}:fairy_hotel"))
    for i, (dx, dz) in enumerate(((1, 1), (-1, 1), (1, -1), (-1, -1), (2, 2), (-2, 2), (2, -2), (-2, -2))):
        c.append(s.set(cx + dx, 1, dz, FLOWERS[i]))
    c.append(station_sign(s, 99, -3, ["Fairy Grove", "use a Diamond", "on the Chime", "Hotel ahead"], 4))
    return c


HERO_X = 162
GROVE_RADIUS, CENTER_RADIUS, INNER_LINE, OUTER_LINE, PLOT_SPACING = 17, 4, 5, 15, 10


def grove_cell(dx: int, dz: int) -> str:
    """Mirrors GroveLayout.cell, so the hero grove looks like one the fairies built."""
    ax, az = abs(dx), abs(dz)
    if ax > GROVE_RADIUS or az > GROVE_RADIUS:
        return "outside"
    if ax <= CENTER_RADIUS and az <= CENTER_RADIUS:
        return "center"
    if ax > OUTER_LINE or az > OUTER_LINE:
        return "meadow"
    on_x, on_z = ax in (INNER_LINE, OUTER_LINE), az in (INNER_LINE, OUTER_LINE)
    if on_x and on_z:
        return "hive" if ax == INNER_LINE and az == INNER_LINE else "hedge"
    if on_x or on_z:
        return "border"

    def plot_center(d: int) -> int:
        return 0 if abs(d) <= CENTER_RADIUS else (1 if d > 0 else -1) * PLOT_SPACING

    return "water" if dx == plot_center(dx) and dz == plot_center(dz) else "farmland"


HERO_CROPS = ["wheat[age=7]", "carrots[age=7]", "potatoes[age=7]", "beetroots[age=3]"]


def build_hero(s: Site) -> list[str]:
    rng = random.Random(21)
    c: list[str] = []
    for dx in range(-GROVE_RADIUS, GROVE_RADIUS + 1):
        for dz in range(-GROVE_RADIUS, GROVE_RADIUS + 1):
            x, cell = HERO_X + dx, grove_cell(dx, dz)
            if cell == "farmland":
                # One crop per plot, so the plots read as a patchwork from above.
                plot = (dx // PLOT_SPACING + 2) * 3 + (dz // PLOT_SPACING + 2)
                crop = HERO_CROPS[plot % len(HERO_CROPS)]
                c += [s.set(x, 0, dz, f"{MOD}:enriched_farmland[moisture=7]"), s.set(x, 1, dz, crop)]
            elif cell == "water":
                c.append(s.set(x, 0, dz, "water"))
            elif cell == "border" and rng.randrange(4):
                c.append(s.set(x, 1, dz, rng.choice(FLOWERS)))
            elif cell in ("hive", "hedge"):
                middle = "beehive[facing=south]" if cell == "hive" else AZALEA[0]
                c += [s.set(x, 1, dz, middle), s.set(x, 2, dz, "lantern")]
            elif cell == "meadow":
                roll = rng.randrange(20)
                if roll == 0:
                    c += [s.set(x, 1, dz, AZALEA[0]), s.set(x, 2, dz, "lantern")]
                elif roll < 4:
                    c.append(s.set(x, 1, dz, rng.choice(AZALEA)))
                elif roll < 10:
                    c.append(s.set(x, 1, dz, rng.choice(FLOWERS + EDGE_PLANTS)))
                elif roll < 16:
                    c.append(s.set(x, 1, dz, "short_grass"))
    # The shrine in the middle, ringed by a flower bed and four blossoming azaleas.
    c.append(s.set(HERO_X, 0, 0, f"{MOD}:super_enriched_dirt"))
    c.append(s.set(HERO_X, 1, 0, f"{MOD}:chime_fairy_hotel"))
    for dx, dz in ((3, 0), (-3, 0), (0, 3), (0, -3)):
        c.append(s.set(HERO_X + dx, 1, dz, f"{MOD}:fairy_hotel"))
    for dx in range(-CENTER_RADIUS, CENTER_RADIUS + 1):
        for dz in range(-CENTER_RADIUS, CENTER_RADIUS + 1):
            if (dx, dz) == (0, 0) or (abs(dx) == 3 and dz == 0) or (abs(dz) == 3 and dx == 0):
                continue
            if abs(dx) == 4 and abs(dz) == 4:
                c.append(s.set(HERO_X + dx, 1, dz, "flowering_azalea"))
            elif rng.random() < 0.6:
                c.append(s.set(HERO_X + dx, 1, dz, rng.choice(FLOWERS + ["pink_petals[flower_amount=4]"])))
    c.append("function fgdemo:hero/fairies")
    c.append(station_sign(s, 142, -3, ["Hero grove", "a finished grove", "and its fairies", ""], 4))
    return c


def hero_fairies(s: Site) -> list[str]:
    """About 30 Bean Fairies with no AI posed over the hero grove. The mod never saves fairies to
    disk, so hero/tick summons them again when a player comes near and none are there."""
    rng = random.Random(33)
    spots = [(dx, dz) for dx in (-10, 0, 10) for dz in (-10, 0, 10) if (dx, dz) != (0, 0)]
    c = []
    for dx, dz in spots:
        for _ in range(3):
            fx, fz = dx + rng.uniform(-3.5, 3.5), dz + rng.uniform(-3.5, 3.5)
            c.append(fairy(s, HERO_X + fx, rng.uniform(1.6, 3.2), fz, rng.uniform(0, 360)))
    for dx, dz in ((3, 0), (-3, 0), (0, 3), (0, -3), (1.5, 1.5), (-1.5, -1.5)):
        c.append(fairy(s, HERO_X + dx + 0.5, rng.uniform(2.4, 3.4), dz + 0.5, rng.uniform(0, 360)))
    return c


def fairy(s: Site, x: float, y: float, z: float, yaw: float) -> str:
    return (
        f"summon {MOD}:bean_fairy {s.x + x:.2f} {s.y + y:.2f} {s.z + z:.2f} "
        f"{{NoAI:1b,NoGravity:1b,PersistenceRequired:1b,Invulnerable:1b,Rotation:[{yaw:.0f}f,0f],"
        f'Tags:["fgdemo_hero"]}}'
    )


def build_test_lot(s: Site) -> list[str]:
    c = s.fill((-56, 0, -3), (-53, 0, 3), "water")
    c += [s.set(-55, 1, z, "lily_pad") for z in (-2, 1)] + [s.set(-54, 1, -1, "lily_pad")]
    c += [s.set(-57, 1, z, "sugar_cane") for z in (-1, 0, 2)]
    c.append(station_sign(s, -10, 3, ["Test lot", "flat grass,", "room for", "a second grove"], 12))
    return c


def tree(s: Site, x: int, z: int, wood: str, height: int) -> list[str]:
    """A small hand-shaped tree; leaves are persistent so they never decay."""
    leaves = f"{wood}_leaves[persistent=true]"
    c = [s.set(x, 0, z, "dirt")]
    for dy, radius in ((height - 1, 2), (height, 2), (height + 1, 1)):
        for dx in range(-radius, radius + 1):
            for dz in range(-radius, radius + 1):
                if radius == 2 and abs(dx) == 2 and abs(dz) == 2:
                    continue  # round off the corners
                c.append(s.set(x + dx, dy, z + dz, leaves))
    c += s.fill((x, 1, z), (x, height, z), f"{wood}_log")
    if wood == "cherry":
        for dx, dz in ((1, 1), (-1, 2), (2, -1), (-2, -1), (0, -2)):
            c.append(
                f"execute positioned {s.at(x + dx, 1, z + dz)} if block ~ ~ ~ air"
                " run setblock ~ ~ ~ pink_petals[flower_amount=4]"
            )
    return c


def in_station(x: int, z: int, margin: int = 1) -> bool:
    if -2 - margin <= z <= 2 + margin and -2 <= x <= 97:
        return True
    return any(
        x1 - margin <= x <= x2 + margin and z1 - margin <= z <= z2 + margin
        for (x1, z1), (x2, z2) in BOXES.values()
    )


def build_scenery(s: Site) -> list[str]:
    """Cherry and birch trees along both edges and a flower meadow between the stations."""
    c: list[str] = []
    for i, x in enumerate(range(-52, PLOT[2] - 3, 11)):
        for z in (-23, 23):
            wood = "cherry" if (i + (z > 0)) % 3 else "birch"
            c += tree(s, x + (3 if z > 0 else 0), z, wood, 5 if wood == "cherry" else 6)
    c += tree(s, 6, -10, "cherry", 5) + tree(s, 6, 10, "cherry", 5)
    rng = random.Random(11)
    for _ in range(1400):
        x, z = rng.randint(PLOT[0] + 1, PLOT[2] - 1), rng.randint(PLOT[1] + 1, PLOT[3] - 1)
        if in_station(x, z):
            continue
        c.append(
            f"execute positioned {s.at(x, 1, z)} if block ~ ~ ~ air if block ~ ~-1 ~ grass_block"
            f" run setblock ~ ~ ~ {rng.choice(MEADOW_PLANTS + FLOWERS)}"
        )
    return c


BUILDERS = {
    "spawn": build_spawn,
    "path": build_path,
    "soil": build_soil,
    "fairy_farm": build_fairy_farm,
    "hotels": build_hotels,
    "chime": build_chime,
    "grove": build_grove,
    "test_lot": build_test_lot,
    "hero": build_hero,
    "scenery": build_scenery,  # outside every station box, so only reset/all rebuilds it
}


# ---- Gamerules ----


def mod_gamerule_defaults() -> dict[str, str]:
    """Every mod gamerule and its default, read from ModGameRules.java so the list stays in step."""
    source = (ROOT / f"common/src/main/java/com/{MOD}/util/ModGameRules.java").read_text()
    rules = {
        name: default
        for name, default in re.findall(r'(?:percent|integer)\(\s*"([a-z_]+)",\s*(\d+)', source)
    }
    for name, default in re.findall(r'bool\(\s*"([a-z_]+)",\s*(true|false)', source):
        rules[name] = default
    return rules


def gamerule_modes() -> dict[str, list[str]]:
    defaults = mod_gamerule_defaults()
    default = [f"gamerule {MOD}:{k} {v}" for k, v in defaults.items()] + [
        "gamerule random_tick_speed 3",
        "gamerule advance_weather true",
        "gamerule spawn_mobs true",
    ]
    test = [
        "gamerule random_tick_speed 30",
        f"gamerule {MOD}:fairy_visit_chance 100",
        f"gamerule {MOD}:jarred_fairy_days 1",
        "weather clear",
        "time set 13000",
    ]
    demo = [
        "gamerule random_tick_speed 3",
        f"gamerule {MOD}:fairy_visit_chance 0",
        f"gamerule {MOD}:jarred_fairy_days {defaults['jarred_fairy_days']}",
        "gamerule advance_weather false",
        "gamerule spawn_mobs false",
        "weather clear",
    ]
    say = 'tellraw @a {"text":"Fertile Grounds: %s","color":"green"}'
    return {
        "test": test + [say % "test mode (fast ticks, fairies every night)"],
        "demo": demo + [say % "demo mode (still scene for recording)"],
        "default": default + [say % "every gamerule back to its default"],
    }


# ---- Datapack ----


def minecraft_version() -> str:
    """The branch's Minecraft version, from gradle.properties."""
    return re.search(r"^minecraft_version=(.+)$", (ROOT / "gradle.properties").read_text(), re.M).group(1)


def data_pack_format() -> int:
    jar = Path.home() / f".gradle/caches/fabric-loom/{minecraft_version()}/minecraft-client.jar"
    with zipfile.ZipFile(jar) as z:
        return json.loads(z.read("version.json"))["pack_version"]["data_major"]


def write_datapack(site: Site) -> None:
    pack = WORLD / "datapacks" / "fgdemo"
    if pack.exists():
        shutil.rmtree(pack)
    functions = pack / "data" / "fgdemo" / "function"
    fmt = data_pack_format()
    (pack).mkdir(parents=True)
    (pack / "pack.mcmeta").write_text(
        json.dumps(
            {"pack": {"description": "Fertile Grounds demo and test world", "min_format": fmt, "max_format": fmt}},
            indent=2,
        )
    )

    def write(path: str, lines: list[str]) -> None:
        file = functions / f"{path}.mcfunction"
        file.parent.mkdir(parents=True, exist_ok=True)
        file.write_text("\n".join(lines) + "\n")

    kill = f"kill @e[type=!player,x={site.x + PLOT[0]},y={site.y - 4},z={site.z + PLOT[1]},dx={PLOT[2] - PLOT[0]},dy=60,dz={PLOT[3] - PLOT[1]}]"
    for name, builder in BUILDERS.items():
        write(f"build/{name}", builder(site))
        if name not in BOXES:
            continue
        (x1, z1), (x2, z2) = BOXES[name]
        box_kill = f"kill @e[type=!player,x={site.x + x1},y={site.y - 4},z={site.z + z1},dx={x2 - x1},dy=60,dz={z2 - z1}]"
        # The path runs through the spawn box, so every reset lays it again.
        reset = [box_kill] + build_floor(site, BOXES[name]) + [f"function fgdemo:build/{name}"]
        if name != "path":
            reset.append("function fgdemo:build/path")
        reset.append(f'tellraw @a {{"text":"Fertile Grounds: reset {name}","color":"green"}}')
        write(f"reset/{name}", reset)
    write(
        "build/all",
        build_floor(site, ((PLOT[0], PLOT[1]), (PLOT[2], PLOT[3])))
        + [f"function fgdemo:build/{name}" for name in BUILDERS],
    )
    write("reset/all", [kill, "function fgdemo:build/all"])
    write("hero/fairies", hero_fairies(site))
    hero = site.at(HERO_X, 2, 0)
    write(
        "hero/tick",
        [
            f"execute positioned {hero} if entity @a[distance=..96]"
            " unless entity @e[tag=fgdemo_hero,distance=..40] run function fgdemo:hero/fairies"
        ],
    )
    tick = pack / "data" / "minecraft" / "tags" / "function" / "tick.json"
    tick.parent.mkdir(parents=True)
    tick.write_text(json.dumps({"values": ["fgdemo:hero/tick"]}, indent=2))
    for mode, lines in gamerule_modes().items():
        write(f"mode/{mode}", lines)


# ---- Server and RCON ----


class Rcon:
    def __init__(self, port: int, password: str) -> None:
        self.sock = socket.create_connection(("127.0.0.1", port), timeout=120)
        self.next_id = 1
        if self._send(3, password)[0] == -1:
            raise RuntimeError("RCON login refused")

    def _send(self, kind: int, body: str) -> tuple[int, str]:
        request = self.next_id
        self.next_id += 1
        payload = struct.pack("<ii", request, kind) + body.encode() + b"\x00\x00"
        self.sock.sendall(struct.pack("<i", len(payload)) + payload)
        length = struct.unpack("<i", self._read(4))[0]
        data = self._read(length)
        response_id = struct.unpack("<i", data[:4])[0]
        return response_id, data[8:-2].decode(errors="replace")

    def _read(self, n: int) -> bytes:
        data = b""
        while len(data) < n:
            chunk = self.sock.recv(n - len(data))
            if not chunk:
                raise RuntimeError("RCON connection closed")
            data += chunk
        return data

    def run(self, command: str) -> str:
        return self._send(2, command)[1]


def prepare_server(password: str) -> None:
    SERVER_DIR.mkdir(parents=True, exist_ok=True)
    if WORLD.exists():
        shutil.rmtree(WORLD)
    (SERVER_DIR / "eula.txt").write_text("eula=true\n")
    (SERVER_DIR / "server.properties").write_text(
        "\n".join(
            [
                f"level-name={LEVEL_NAME}",
                f"level-seed={SEED}",
                "gamemode=creative",
                "difficulty=peaceful",
                "online-mode=false",
                "server-ip=127.0.0.1",
                f"server-port={SERVER_PORT}",
                "enable-rcon=true",
                f"rcon.port={RCON_PORT}",
                f"rcon.password={password}",
                "spawn-protection=0",
                "max-tick-time=-1",
                "",
            ]
        )
    )


def start_server(log_path: Path) -> subprocess.Popen:
    log = log_path.open("w")
    process = subprocess.Popen(
        [
            "./gradlew",
            ":fabric:runDemoServer",
            "--console=plain",
            # Jade names the mod's client tooltip plugin, which the dev server's classpath leaves out.
            "-PnoJade",
        ],
        cwd=ROOT,
        stdout=log,
        stderr=subprocess.STDOUT,
        stdin=subprocess.DEVNULL,
    )
    deadline = time.time() + 600
    while time.time() < deadline:
        text = log_path.read_text(errors="replace")
        if "RCON running on" in text:
            return process
        if process.poll() is not None:
            raise RuntimeError(f"server exited early, see {log_path}")
        time.sleep(2)
    process.kill()
    raise RuntimeError("server didn't start in 10 minutes")


def wait_loaded(rcon: Rcon, site: Site) -> None:
    corners = [(PLOT[0], PLOT[1]), (PLOT[2], PLOT[3]), (PLOT[0], PLOT[3]), (PLOT[2], PLOT[1]), (40, 0)]
    for _ in range(120):
        if all("passed" in rcon.run(f"execute if loaded {site.at(x, 0, z)}") for x, z in corners):
            return
        time.sleep(1)
    raise RuntimeError("plot chunks never loaded")


def find_site(rcon: Rcon) -> tuple[int, int]:
    best = None
    for biome in ("minecraft:flower_forest", "minecraft:cherry_grove", "minecraft:meadow"):
        reply = rcon.run(f"locate biome {biome}")
        match = re.search(r"\[(-?\d+), ~?-?\d*~?, (-?\d+)\] \((\d+) blocks", reply)
        print(f"  {biome}: {reply.strip()}")
        if match:
            x, z, distance = map(int, match.groups())
            if best is None or distance < best[2]:
                best = (x, z, distance)
    if best is None:
        raise RuntimeError("no flower forest, cherry grove, or meadow found")
    return best[0], best[1]


def surface_height(rcon: Rcon, x: int, z: int) -> int:
    rcon.run(f"forceload add {x} {z}")
    for _ in range(60):
        if "passed" in rcon.run(f"execute if loaded {x} 0 {z}"):
            break
        time.sleep(1)
    rcon.run(f"summon marker {x} 0 {z} {{Tags:[\"fgdemo_probe\"]}}")
    rcon.run("execute as @e[tag=fgdemo_probe] at @s positioned over motion_blocking_no_leaves run tp @s ~ ~ ~")
    reply = rcon.run("data get entity @e[tag=fgdemo_probe,limit=1] Pos[1]")
    rcon.run("kill @e[tag=fgdemo_probe]")
    match = re.search(r"(-?\d+(?:\.\d+)?)d?\s*$", reply.strip())
    if not match:
        raise RuntimeError(f"couldn't read the surface height: {reply}")
    return int(float(match.group(1)))


# ---- Checks ----


def check(rcon: Rcon, label: str, command: str, failures: list[str]) -> bool:
    reply = rcon.run(command)
    ok = "passed" in reply.lower()
    print(f"  {'pass' if ok else 'FAIL'}: {label}" + ("" if ok else f" ({reply.strip()})"))
    if not ok:
        failures.append(label)
    return ok


def find_mismatch(rcon: Rcon, s: Site, lo: tuple, hi: tuple, far: int):
    """Halves the box until one differing block is left, for diagnosing a failed reset."""

    def same(a: tuple, b: tuple) -> bool:
        reply = rcon.run(
            f"execute if blocks {s.at(*a)} {s.at(*b)} {s.at(a[0], a[1], a[2] + far)} all"
        )
        return "passed" in reply.lower()

    if same(lo, hi):
        return None
    while lo != hi:
        axis = max(range(3), key=lambda i: hi[i] - lo[i])
        mid = (lo[axis] + hi[axis]) // 2
        left_hi = tuple(mid if i == axis else hi[i] for i in range(3))
        if not same(lo, left_hi):
            hi = left_hi
        else:
            lo = tuple(mid + 1 if i == axis else lo[i] for i in range(3))
    return lo


def run_checks(rcon: Rcon, s: Site) -> list[str]:
    f: list[str] = []
    blocks = {
        "spawn sign board": (-4, 3, -3, "oak_wall_sign"),
        "guide book on the lectern": (3, 1, -4, "lectern[has_book=true]"),
        "supply chest": (3, 1, 4, "chest"),
        "Super Enriched row": (13, 0, -8, f"{MOD}:super_enriched_farmland"),
        "bamboo on Enriched Sand": (13, 1, -10, "bamboo_sapling"),
        "fairy farm crops": (27, 1, 5, "wheat"),
        "fairy farm hive": (38, 1, 9, "beehive"),
        "hanging hotel": (49, 3, -9, f"{MOD}:fairy_hotel[hanging=true]"),
        "Chime Hotel": (64, 1, 4, f"{MOD}:chime_fairy_hotel"),
        "comparator to the lamp": (65, 1, 4, "comparator"),
        "shrine soil": (118, 0, 0, f"{MOD}:super_enriched_dirt"),
        "shrine Chime Hotel": (118, 1, 0, f"{MOD}:chime_fairy_hotel"),
        "test lot pond": (-55, 0, 0, "water"),
        "edge tree": (-52, 1, -23, "birch_log"),
        "cherry by the spawn": (6, 1, -10, "cherry_log"),
        "hero shrine": (162, 1, 0, f"{MOD}:chime_fairy_hotel"),
        "hero ripe crop": (154, 1, -12, "#minecraft:crops"),
    }
    for label, (dx, dy, dz, block) in blocks.items():
        check(rcon, label, f"execute if block {s.at(dx, dy, dz)} {block}", f)
    sign_check = "front_text.messages[0].click_event"
    if tuple(map(int, minecraft_version().split("."))) >= (26, 3):
        sign_check = "{allow_op_features:1b}"  # see sign()
    check(rcon, "Test mode sign runs its command", f"execute if data block {s.at(-4, 3, -1)} {sign_check}", f)
    reply = rcon.run("execute if entity @e[tag=fgdemo_hero]")
    match = re.search(r"count: (\d+)", reply, re.IGNORECASE)
    count = int(match.group(1)) if match else 0
    print(f"  {'pass' if count >= 25 else 'FAIL'}: {count} hero fairies hovering")
    if count < 25:
        f.append("hero fairies")
    for dx, dz in ((3, 0), (-3, 0), (0, 3), (0, -3)):
        check(rcon, f"shrine hotel at {dx},{dz}", f"execute if block {s.at(118 + dx, 1, dz)} {MOD}:fairy_hotel", f)

    for dx in (101, 135):
        for dz in (-17, 17):
            check(rcon, f"grove corner {dx},{dz} is grass", f"execute if block {s.at(dx, 0, dz)} grass_block", f)

    # reset/grove: copy the area away, scribble over it, reset, and compare with the copy. Random
    # ticks stop meanwhile, so grass can't creep onto the copy's dirt.
    far = 400
    layers = ((-8, 4), (5, 16))
    rcon.run("gamerule random_tick_speed 0")
    rcon.run(f"forceload add {s.x + 99} {s.z + far - 20} {s.x + 137} {s.z + far + 20}")
    for _ in range(60):
        if all(
            "passed" in rcon.run(f"execute if loaded {s.at(x, 0, far + z)}")
            for x in (99, 137)
            for z in (-20, 20)
        ):
            break
        time.sleep(1)
    for y0, y1 in layers:
        print("  " + rcon.run(f"clone {s.at(99, y0, -20)} {s.at(137, y1, 20)} {s.at(99, y0, far - 20)}").strip())
    rcon.run(f"fill {s.at(110, 0, -5)} {s.at(115, 3, 5)} stone")
    rcon.run(f"setblock {s.at(118, 1, 0)} air")
    rcon.run("function fgdemo:reset/grove")
    # Placed blocks settle first: leaves work out their distance, and the Chime Hotel stops ringing.
    time.sleep(5)
    for y0, y1 in layers:
        check(
            rcon,
            f"reset/grove restores layers {y0} to {y1}",
            f"execute if blocks {s.at(99, y0, -20)} {s.at(137, y1, 20)} {s.at(99, y0, far - 20)} all",
            f,
        )
    if f:
        print("  first mismatch:", find_mismatch(rcon, s, (99, -8, -20), (137, 16, 20), far))
    rcon.run(f"forceload remove {s.x + 99} {s.z + far - 20} {s.x + 137} {s.z + far + 20}")
    rcon.run("gamerule random_tick_speed 3")

    # Modes set and restore every gamerule.
    def rule_is(name: str, value: str) -> None:
        reply = rcon.run(f"gamerule {name}")
        ok = reply.strip().endswith(value) or f": {value}" in reply
        print(f"  {'pass' if ok else 'FAIL'}: {name} is {value}" + ("" if ok else f" ({reply.strip()})"))
        if not ok:
            f.append(f"{name} {value}")

    rcon.run("function fgdemo:mode/test")
    for name, value in (("random_tick_speed", "30"), (f"{MOD}:fairy_visit_chance", "100"), (f"{MOD}:jarred_fairy_days", "1")):
        rule_is(name, value)
    rcon.run("function fgdemo:mode/default")
    for name, value in mod_gamerule_defaults().items():
        rule_is(f"{MOD}:{name}", value)
    rule_is("random_tick_speed", "3")

    # The Chime Hotel field ripens and the hotel marks itself ready (fast ticks to save time).
    rcon.run("gamerule random_tick_speed 300")
    ready = False
    for _ in range(60):
        if "passed" in rcon.run(f"execute if block {s.at(64, 1, 4)} {MOD}:chime_fairy_hotel[ready=true]"):
            ready = True
            break
        time.sleep(1)
    print(f"  {'pass' if ready else 'FAIL'}: Chime Hotel field ripens and the hotel is ready")
    if not ready:
        f.append("chime field ripens")
    rcon.run("gamerule random_tick_speed 3")
    rcon.run("function fgdemo:reset/chime")
    return f


# ---- level.dat ----


def allow_commands(level_dat: Path) -> None:
    """Turns cheats on: a dedicated server always saves allowCommands as false."""
    data = gzip.decompress(level_dat.read_bytes())
    key = b"\x01" + struct.pack(">H", len("allowCommands")) + b"allowCommands"
    at = data.find(key)
    if at < 0:
        raise RuntimeError("allowCommands not found in level.dat")
    value = at + len(key)
    data = data[:value] + b"\x01" + data[value + 1 :]
    level_dat.write_bytes(gzip.compress(data))


# ---- Main ----


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__.splitlines()[0])
    parser.add_argument("--no-copy", action="store_true", help="don't copy the world to saves")
    args = parser.parse_args()

    password = secrets.token_hex(16)
    prepare_server(password)
    log_path = SERVER_DIR / "build_demo_world.log"
    print("Starting the demo server...")
    process = start_server(log_path)
    failures: list[str] = []
    try:
        rcon = Rcon(RCON_PORT, password)
        print("Finding a pretty spot:")
        bx, bz = find_site(rcon)
        center_y = surface_height(rcon, bx, bz)
        site = Site(bx - (PLOT[0] + PLOT[2]) // 2, max(64, min(center_y, 110)), bz)
        print(f"Building at {site.at(0, 0, 0)} (surface {center_y})")
        write_datapack(site)
        print(rcon.run("reload").strip() or "reloaded")
        if "fgdemo" not in rcon.run("datapack list enabled"):
            rcon.run('datapack enable "file/fgdemo"')
        rcon.run(f"forceload add {site.x + PLOT[0] - 16} {site.z + PLOT[1] - 16} {site.x + PLOT[2] + 16} {site.z + PLOT[3] + 16}")
        wait_loaded(rcon, site)
        reply = rcon.run("function fgdemo:build/all")
        print(f"  build/all: {reply.strip()}")
        rcon.run(f"setworldspawn {site.at(0, 1, 0)}")
        rcon.run("gamerule respawn_radius 0")
        rcon.run("gamerule send_command_feedback true")
        print("Checking:")
        failures = run_checks(rcon, site)
        rcon.run("function fgdemo:mode/demo")
        rcon.run("time set 10500")
        rcon.run(f"forceload remove all")
        rcon.run("save-all flush")
        rcon.run("stop")
    finally:
        try:
            process.wait(timeout=120)
        except subprocess.TimeoutExpired:
            process.kill()
    errors = [line for line in log_path.read_text(errors="replace").splitlines() if "/ERROR]" in line]
    for line in errors:
        print(f"  log error: {line}")
    if failures or errors:
        print(f"FAILED: {len(failures)} checks, {len(errors)} log errors; see {log_path}")
        return 1
    allow_commands(WORLD / "level.dat")
    if not args.no_copy:
        for saves in (SAVES, *DEV_SAVES):
            target = saves / LEVEL_NAME
            if target.exists():
                backup = saves / f"{LEVEL_NAME} (old {time.strftime('%Y%m%d-%H%M%S')})"
                target.rename(backup)
                print(f"Moved the old demo world to {backup}")
            saves.mkdir(parents=True, exist_ok=True)
            shutil.copytree(WORLD, target)
            print(f"Copied the world to {target}")
    print("pass: demo world built")
    return 0


if __name__ == "__main__":
    sys.exit(main())
