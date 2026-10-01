"""Refresh the README download badge with today's Modrinth + CurseForge totals.

Run before pushing: python3 scripts/update_downloads.py
"""

import datetime
import json
import pathlib
import re
import urllib.request

MODRINTH_URL = "https://api.modrinth.com/v2/project/fertile-grounds"
# CurseForge's own API needs a key; cfwidget mirrors the public total without one.
CURSEFORGE_URL = "https://api.cfwidget.com/1627313"
README = pathlib.Path(__file__).resolve().parent.parent / "README.md"
BUILD_BADGE = "[![build]"
BADGE_PREFIX = "[![downloads]"


def fetch_json(url: str) -> dict:
    request = urllib.request.Request(url, headers={"User-Agent": "fertile-grounds-readme"})
    with urllib.request.urlopen(request, timeout=20) as response:
        return json.load(response)


def badge_line(modrinth: int, curseforge: int) -> str:
    total = modrinth + curseforge
    today = datetime.date.today().isoformat()
    return (
        f"{BADGE_PREFIX}(https://img.shields.io/badge/downloads-{total}-brightgreen)]"
        f"(https://modrinth.com/mod/fertile-grounds) "
        f"{total} downloads as of {today}: {modrinth} on Modrinth, {curseforge} on CurseForge."
    )


def main() -> None:
    modrinth = fetch_json(MODRINTH_URL)["downloads"]
    curseforge = fetch_json(CURSEFORGE_URL)["downloads"]["total"]
    line = badge_line(modrinth, curseforge)

    lines = README.read_text().split("\n")
    existing = [i for i, text in enumerate(lines) if text.startswith(BADGE_PREFIX)]
    if existing:
        lines[existing[0]] = line
    else:
        build = next(i for i, text in enumerate(lines) if text.startswith(BUILD_BADGE))
        lines[build + 1 : build + 1] = ["", line]
    README.write_text("\n".join(lines))
    print(line)


if __name__ == "__main__":
    main()
