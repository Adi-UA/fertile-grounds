"""Boots the dev server, runs in-game checks through the console, and reports pass/fail.

`./gradlew build` only proves the code compiles. Broken data files (recipes, advancements, tags)
and broken entity behavior only show up once a world loads, so this script loads one.

Each check is a console command that prints a marker with `say` when it passes. The script exits
1 if any marker is missing or the server logs a registry/datapack error.

Usage (from the repo root): python3 scripts/server_smoke_test.py
"""

import shutil
import subprocess
import sys
import time
from pathlib import Path

BOOT_TIMEOUT_SECONDS = 300

# (command, seconds to wait after it). Commands run at y=200 so terrain never interferes.
SETUP = [
    ("forceload add -16 -16 31 31", 2),
    ("gamerule randomTickSpeed 0", 0),  # only fairies may grow crops
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
]

# marker -> command that prints the marker only when the check passes.
CHECKS = {
    "CENTER_GROWN": "execute if block 0 201 0 minecraft:wheat[age=7] run say CENTER_GROWN",
    "CORNER_GROWN": "execute if block 2 201 2 minecraft:wheat[age=7] run say CORNER_GROWN",
    "OUTSIDE_UNTOUCHED": "execute if block 3 201 0 minecraft:carrots[age=0] run say OUTSIDE_UNTOUCHED",
    "ROOFED_GROWN": "execute if block 12 201 0 minecraft:wheat[age=7] run say ROOFED_GROWN",
    "FAIRIES_GONE": "execute unless entity @e[type=fertilegrounds:bean_fairy] run say FAIRIES_GONE",
}

ERROR_MARKERS = ("Registry loading errors", "Failed to load datapacks", "Exception in server tick")


def main() -> int:
    run_dir = Path("run")
    run_dir.mkdir(exist_ok=True)
    # Always start from a fresh world: an older version can't load a world a newer one saved.
    shutil.rmtree(run_dir / "world", ignore_errors=True)
    (run_dir / "eula.txt").write_text("eula=true\n")

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

    booted = read_until(lambda l: "Done (" in l or any(m in l for m in ERROR_MARKERS), BOOT_TIMEOUT_SECONDS)
    if not booted or any(m in l for l in log for m in ERROR_MARKERS):
        server.kill()
        print("".join(log[-60:]))
        print("FAIL: server did not boot cleanly")
        return 1

    for command, wait in SETUP:
        send(command)
        time.sleep(wait)
    for command in CHECKS.values():
        send(command)
    time.sleep(3)
    send("stop")
    read_until(lambda l: False, 60)
    server.wait(timeout=60)

    output = "".join(log)
    failed = [marker for marker in CHECKS if f"[Server] {marker}" not in output]
    errors = [m for m in ERROR_MARKERS if m in output]
    for marker in CHECKS:
        print(f"{'FAIL' if marker in failed else 'pass'}: {marker}")
    for error in errors:
        print(f"FAIL: log contains '{error}'")
    return 1 if failed or errors else 0


if __name__ == "__main__":
    sys.exit(main())
