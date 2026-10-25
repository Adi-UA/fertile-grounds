"""Boots the dev client to the title screen and fails on any warning or error about this mod.

The test server never loads models, textures, or sounds, so run this for changes that touch
assets. It takes about a minute.

Usage (from the repo root):
    python3 scripts/client_log_check.py                    # the Fabric client
    python3 scripts/client_log_check.py --loader neoforge  # or forge
"""

import argparse
import os
import queue
import re
import signal
import subprocess
import sys
import threading
import time

MOD_ID = "fertilegrounds"
LOADERS = ("fabric", "neoforge", "forge")
BOOT_TIMEOUT_SECONDS = 300
# Atlases are built once resource loading finishes, after every model and texture warning, so the
# block atlas line means the client reached the title screen.
LOADED_MARKER = "textures/atlas/blocks.png-atlas"
SETTLE_SECONDS = 5  # keep reading a little longer for late sound warnings

# Missing texture warnings continue on the next lines, which hold the texture name.
ENTRY_START = re.compile(r"^\[\d{2}:\d{2}:\d{2}\] \[[^\]]+/(\w+)\]")


def entries(lines: list[str]) -> list[str]:
    """Joins each log entry with its continuation lines."""
    grouped: list[str] = []
    for line in lines:
        if ENTRY_START.match(line) or not grouped:
            grouped.append(line)
        else:
            grouped[-1] += line
    return grouped


def is_problem(entry: str) -> bool:
    match = ENTRY_START.match(entry)
    return bool(match) and match.group(1) in ("WARN", "ERROR") and MOD_ID in entry


def pump(stream, into: queue.Queue) -> None:
    """Copies each line of the stream into the queue, then None once the stream closes."""
    for line in stream:
        into.put(line)
    into.put(None)


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--loader", choices=LOADERS, default="fabric", help="which loader's dev client to boot")
    args = parser.parse_args()
    # A new session lets us stop Gradle and the game process it starts in one signal.
    client = subprocess.Popen(
        ["./gradlew", f":{args.loader}:runClient", "--console=plain"],
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        text=True,
        start_new_session=True,
    )
    # The idle title screen prints nothing, so readline() would block; a thread feeds a queue instead.
    lines: queue.Queue[str | None] = queue.Queue()
    threading.Thread(target=pump, args=(client.stdout, lines), daemon=True).start()
    log: list[str] = []
    initialized = loaded = False
    deadline = time.time() + BOOT_TIMEOUT_SECONDS
    try:
        while (remaining := deadline - time.time()) > 0:
            try:
                line = lines.get(timeout=remaining)
            except queue.Empty:
                break
            if line is None:
                break  # the client exited, as after a crash
            log.append(line)
            initialized |= "Fertile Grounds initializing" in line
            if initialized and not loaded and LOADED_MARKER in line:
                loaded = True
                deadline = time.time() + SETTLE_SECONDS
    finally:
        try:
            os.killpg(client.pid, signal.SIGTERM)
        except (ProcessLookupError, PermissionError):
            pass  # the client already exited, and its process group with it
        client.wait(timeout=60)

    problems = [entry.rstrip() for entry in entries(log) if is_problem(entry)]
    for problem in problems:
        print(f"FAIL: {problem}")
    if not loaded:
        print("".join(log[-60:]))
        print("FAIL: client never reached the title screen")
        return 1
    print("pass: client reached the title screen" + ("" if problems else " with no warnings for " + MOD_ID))
    return 1 if problems else 0


if __name__ == "__main__":
    sys.exit(main())
