"""Lists how a freshly built jar differs from the same jar in a published GitHub release.

Use it after build changes that shouldn't change what players get: it prints every file added or
removed and any change to the loader metadata, so an unexpected difference stands out.

Usage (from the repo root, after building):
    python3 scripts/compare_release_jar.py v2.0.0 fabric/build/libs/fertilegrounds-2.0.0+26.3.jar
"""

import difflib
import subprocess
import sys
import tempfile
import zipfile
from pathlib import Path

REPO = "Adi-UA/fertile-grounds"
METADATA = ("fabric.mod.json", "META-INF/neoforge.mods.toml", "META-INF/mods.toml")


def main() -> int:
    if len(sys.argv) != 3:
        print(__doc__)
        return 2
    tag, built = sys.argv[1], Path(sys.argv[2])
    with tempfile.TemporaryDirectory() as folder:
        subprocess.run(
            ["gh", "release", "download", tag, "-R", REPO, "-D", folder, "-p", built.name],
            check=True,
        )
        released = Path(folder, built.name)
        with zipfile.ZipFile(released) as old, zipfile.ZipFile(built) as new:
            old_files, new_files = set(old.namelist()), set(new.namelist())
            for name in sorted(old_files - new_files):
                print(f"removed: {name}")
            for name in sorted(new_files - old_files):
                print(f"added:   {name}")
            for name in METADATA:
                if name in old_files and name in new_files:
                    diff = difflib.unified_diff(
                        old.read(name).decode().splitlines(),
                        new.read(name).decode().splitlines(),
                        f"{tag}/{name}",
                        f"built/{name}",
                        lineterm="",
                    )
                    for line in diff:
                        print(line)
        print(f"size: {released.stat().st_size} bytes released, {built.stat().st_size} bytes built")
    return 0


if __name__ == "__main__":
    sys.exit(main())
