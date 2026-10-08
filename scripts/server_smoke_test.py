"""Boots the dev server, runs in-game checks through the console, and reports pass/fail.

`./gradlew build` only proves the code compiles. Broken data files (recipes, advancements, tags)
and broken entity behavior only show up once a world loads, so this script loads one.

Checks are grouped into suites. Each suite builds its own test area, then runs console commands
that print a marker with `say` when they pass. The script exits 1 if any marker is missing or the
server logs a data-load error. Each 2.0.0 change appends its own suite to SUITES.

Usage (from the repo root):
    python3 scripts/server_smoke_test.py                 # every suite
    python3 scripts/server_smoke_test.py --suite NAME    # one suite, for fast iteration
"""

import argparse
import json
import shutil
import subprocess
import sys
import time
from dataclasses import dataclass, field
from pathlib import Path

BOOT_TIMEOUT_SECONDS = 300


@dataclass(frozen=True)
class Suite:
    name: str
    setup: list[tuple[str, float]]  # (command, seconds to wait after it)
    checks: dict[str, str]  # marker -> command that prints the marker only when the check passes
    guards: str = ""  # the fixed bugs this suite catches, by commit
    # Files for a test datapack, as path under data/ -> JSON. It loads for the whole run, so it
    # must not break other suites.
    datapack: dict[str, dict] = field(default_factory=dict)


# Runs before every suite. All test areas sit at y=200 inside this region, so terrain never
# interferes and chunks stay loaded with no player online.
COMMON_SETUP = [
    ("forceload add -16 -16 31 31", 2),
    ("weather clear", 0),  # rain keeps farmland moist and would block the dry-out check
]

FAIRY_VISIT = Suite(
    name="fairy_visit",
    guards="88d5657 (fairy stuck under a roof or with no crop nearby)",
    setup=[
        ("gamerule random_tick_speed 0", 0),  # only fairies may grow crops
        # Open field: a 5x5 wheat patch plus a carrot just outside the fairy's reach.
        ("fill -2 200 -2 3 200 2 minecraft:farmland", 0),
        ("fill -2 201 -2 2 201 2 minecraft:wheat[age=0]", 0),
        ("setblock 3 201 0 minecraft:carrots[age=0]", 0),
        # Roofed: one wheat under glass, which the fairy has to fly through.
        ("setblock 12 200 0 minecraft:farmland", 0),
        ("setblock 12 201 0 minecraft:wheat[age=0]", 0),
        ("fill 11 203 -1 13 203 1 minecraft:glass", 0),
        # No crops within reach: this fairy has nothing to do and should leave.
        ("fill 28 200 -1 30 200 1 minecraft:stone", 1),
        ("summon fertilegrounds:bean_fairy 0 205 0", 0),
        ("summon fertilegrounds:bean_fairy 12 206 0", 0),
        ("summon fertilegrounds:bean_fairy 29 201 0", 12),
    ],
    checks={
        "CENTER_GROWN": "execute if block 0 201 0 minecraft:wheat[age=7] run say CENTER_GROWN",
        "CORNER_GROWN": "execute if block 2 201 2 minecraft:wheat[age=7] run say CORNER_GROWN",
        "OUTSIDE_UNTOUCHED": "execute if block 3 201 0 minecraft:carrots[age=0] run say OUTSIDE_UNTOUCHED",
        "ROOFED_GROWN": "execute if block 12 201 0 minecraft:wheat[age=7] run say ROOFED_GROWN",
        "FAIRIES_GONE": "execute unless entity @e[type=fertilegrounds:bean_fairy] run say FAIRIES_GONE",
    },
)

SOIL_REGRESSIONS = Suite(
    name="soil_regressions",
    guards="83fd1fb (farmland reverted to vanilla dirt), 1e5d483 (sugar cane refused Enriched Sand)",
    setup=[
        # Trample: a cow dropped 10 blocks always passes vanilla's trample roll in
        # FarmlandBlock.fallOn (random < fallDistance - 0.5), and mob_griefing is on by default.
        ("setblock -12 200 -12 fertilegrounds:enriched_farmland", 0),
        ("setblock -8 200 -12 fertilegrounds:super_enriched_farmland", 0),
        ("summon minecraft:cow -12 210 -12", 0),
        ("summon minecraft:cow -8 210 -12", 0),
        # Dry-out: dry farmland with no water, rain, or crop turns back into dirt on a random tick.
        ("setblock -12 200 -6 fertilegrounds:enriched_farmland[moisture=0]", 0),
        ("setblock -8 200 -6 fertilegrounds:super_enriched_farmland[moisture=0]", 0),
        ("gamerule random_tick_speed 3000", 4),
        ("gamerule random_tick_speed 0", 0),
        ("setblock -4 200 -12 fertilegrounds:enriched_sand", 0),
    ],
    checks={
        "ENRICHED_TRAMPLED": "execute if block -12 200 -12 fertilegrounds:enriched_dirt run say ENRICHED_TRAMPLED",
        "SUPER_TRAMPLED": "execute if block -8 200 -12 fertilegrounds:super_enriched_dirt run say SUPER_TRAMPLED",
        "ENRICHED_DRIED": "execute if block -12 200 -6 fertilegrounds:enriched_dirt run say ENRICHED_DRIED",
        "SUPER_DRIED": "execute if block -8 200 -6 fertilegrounds:super_enriched_dirt run say SUPER_DRIED",
        "SAND_TAGGED": "execute if block -4 200 -12 #minecraft:sand run say SAND_TAGGED",
    },
)

FAIRY_CONTENT_TAGS = Suite(
    name="fairy_content_tags",
    datapack={
        # A pack that replaces fairy_crops with a list that leaves out carrots.
        "fertilegrounds/tags/block/fairy_crops.json": {
            "replace": True,
            "values": ["minecraft:wheat", "minecraft:beetroots", "minecraft:potatoes"],
        },
    },
    setup=[
        ("gamerule random_tick_speed 0", 0),
        # Wheat with carrots on each side, all inside the fairy's 5x5 growth area.
        ("fill 19 200 20 21 200 20 minecraft:farmland", 0),
        ("setblock 19 201 20 minecraft:carrots[age=0]", 0),
        ("setblock 20 201 20 minecraft:wheat[age=0]", 0),
        ("setblock 21 201 20 minecraft:carrots[age=0]", 0),
        ("setblock 20 201 23 minecraft:poppy", 0),
        ("summon minecraft:item 20 202 24 {Item:{id:\"minecraft:poppy\"},NoGravity:1b}", 0),
        ("summon fertilegrounds:bean_fairy 20 205 20", 12),
    ],
    checks={
        "TAGGED_WHEAT_GROWN": "execute if block 20 201 20 minecraft:wheat[age=7] run say TAGGED_WHEAT_GROWN",
        "UNTAGGED_CARROTS_SKIPPED": "execute if block 19 201 20 minecraft:carrots[age=0]"
        " if block 21 201 20 minecraft:carrots[age=0] run say UNTAGGED_CARROTS_SKIPPED",
        "FLOWER_BLOCK_TAGGED": "execute if block 20 201 23 #fertilegrounds:fairy_flowers run say FLOWER_BLOCK_TAGGED",
        "FLOWER_ITEM_TAGGED": "execute if items entity @e[type=item,x=20,y=202,z=24,distance=..2] contents"
        " #fertilegrounds:fairy_flowers run say FLOWER_ITEM_TAGGED",
    },
)

TUNING_GAMERULES = Suite(
    name="tuning_gamerules",
    setup=[
        # `gamerule <name>` returns the value, so store it in a scoreboard to compare it.
        ("scoreboard objectives add smoke dummy", 0),
        ("execute store result score #visit smoke run gamerule fertilegrounds:fairy_visit_chance", 0),
        ("execute store result score #enriched smoke run gamerule fertilegrounds:enriched_boost_chance", 0),
        ("execute store result score #super smoke run gamerule fertilegrounds:super_enriched_boost_chance", 0),
        ("gamerule fertilegrounds:fairy_visit_chance 150", 0),  # out of range, must be rejected
        ("execute store result score #after smoke run gamerule fertilegrounds:fairy_visit_chance", 0),
    ],
    checks={
        "GAMERULE_DEFAULTS": "execute if score #visit smoke matches 1 if score #enriched smoke matches 15"
        " if score #super smoke matches 50 run say GAMERULE_DEFAULTS",
        "OUT_OF_RANGE_REJECTED": "execute if score #after smoke matches 1 run say OUT_OF_RANGE_REJECTED",
    },
)

SUITES = [FAIRY_VISIT, SOIL_REGRESSIONS, FAIRY_CONTENT_TAGS, TUNING_GAMERULES]

# The data pack format 26.3 reads (pack_version.data_major in the game jar's version.json).
DATA_PACK_FORMAT = 121

# Log lines that mean data silently failed to load. Copied from the 26.3 sources: recipes,
# advancements, and loot tables load as registries (RegistryDataLoader), tags load in TagLoader,
# and the rest are validation passes that skip the broken entry and keep going.
ERROR_MARKERS = (
    "Registry loading errors",
    "Failed to load datapacks",
    "Exception in server tick",
    "Couldn't parse data file",
    "Couldn't load tag",
    "Couldn't read tag list",
    "Found validation problems in advancement",
    "Found loot table element validation problem",
    "can't be placed due to empty ingredients",
)


def has_error(line: str) -> bool:
    return any(marker in line for marker in ERROR_MARKERS)


def write_datapack(pack_dir: Path, files: dict[str, dict]) -> None:
    """A new world enables every pack found in its datapacks folder, so writing it is enough."""
    if not files:
        return
    meta = {"pack": {"description": "Smoke test", "min_format": DATA_PACK_FORMAT, "max_format": DATA_PACK_FORMAT}}
    for path, content in {"pack.mcmeta": meta, **{f"data/{p}": c for p, c in files.items()}}.items():
        target = pack_dir / path
        target.parent.mkdir(parents=True, exist_ok=True)
        target.write_text(json.dumps(content, indent=2))


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--suite", choices=[suite.name for suite in SUITES], help="run only this suite")
    args = parser.parse_args()
    suites = [suite for suite in SUITES if args.suite in (None, suite.name)]

    run_dir = Path("run")
    run_dir.mkdir(exist_ok=True)
    # Always start from a fresh world: an older version can't load a world a newer one saved.
    shutil.rmtree(run_dir / "world", ignore_errors=True)
    (run_dir / "eula.txt").write_text("eula=true\n")
    for suite in suites:
        write_datapack(run_dir / "world" / "datapacks" / f"smoke_{suite.name}", suite.datapack)

    server = subprocess.Popen(
        ["./gradlew", "runServer", "--args=nogui", "--console=plain"],
        stdin=subprocess.PIPE,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        text=True,
    )
    log: list[str] = []

    def send(command: str) -> None:
        server.stdin.write(command + "\n")
        server.stdin.flush()

    def read_until(predicate, timeout: float) -> bool:
        deadline = time.time() + timeout
        while time.time() < deadline:
            line = server.stdout.readline()
            if not line:
                return False
            log.append(line)
            if predicate(line):
                return True
        return False

    # A broken registry file can stop the boot outright, so bail on the first error marker.
    booted = read_until(lambda line: "Done (" in line or has_error(line), BOOT_TIMEOUT_SECONDS)
    if not booted or any(has_error(line) for line in log):
        read_until(lambda line: False, 5)  # the lines after the marker name the broken file
        server.kill()
        print("".join(log[-60:]))
        print("FAIL: server did not boot cleanly")
        return 1

    for command, wait in COMMON_SETUP:
        send(command)
        time.sleep(wait)
    for suite in suites:
        for command, wait in suite.setup:
            send(command)
            time.sleep(wait)
        for command in suite.checks.values():
            send(command)
        time.sleep(3)
    send("stop")
    read_until(lambda line: False, 60)
    server.wait(timeout=60)

    output = "".join(log)
    failed = False
    for suite in suites:
        print(f"== {suite.name}" + (f" (guards {suite.guards})" if suite.guards else ""))
        for marker in suite.checks:
            passed = f"[Server] {marker}" in output
            failed |= not passed
            print(f"{'pass' if passed else 'FAIL'}: {marker}")
    error_lines = [line.rstrip() for line in log if has_error(line)]
    for line in error_lines:
        print(f"FAIL: {line}")
    return 1 if failed or error_lines else 0


if __name__ == "__main__":
    sys.exit(main())
