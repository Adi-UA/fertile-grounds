"""Runs the mod's in-world GameTests and reports pass/fail per test.

The tests live in src/gametest and run on a headless test server through `./gradlew runGameTest`.
Gradle already fails when a test fails; this script adds what tests can't see: data files that
fail to load without crashing (a broken tag or advancement only logs an error), so it also exits 1
if the log shows one of those.

Usage (from the repo root):
    python3 scripts/run_gametests.py                     # every test
    python3 scripts/run_gametests.py --filter '*fertilizer*'   # only tests whose names match
"""

import argparse
import subprocess
import sys
import xml.etree.ElementTree as ElementTree
from pathlib import Path

REPORT = Path("build/gametest-report.xml")
TEST_NAMESPACE = "fertilegrounds-gametest"

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


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--filter", help="only run tests whose ids match this pattern, e.g. '*fertilizer*'")
    args = parser.parse_args()

    command = ["./gradlew", "runGameTest", "--console=plain"]
    if args.filter:
        # Test ids are namespace:path, and the matcher's * doesn't cross the colon.
        pattern = args.filter if ":" in args.filter else f"{TEST_NAMESPACE}:{args.filter}"
        command.append(f"-PgametestFilter={pattern}")
    REPORT.unlink(missing_ok=True)
    result = subprocess.run(command, capture_output=True, text=True)
    log = (result.stdout + result.stderr).splitlines()
    Path("build/gametest.log").write_text("\n".join(log))

    failed = result.returncode != 0
    if REPORT.exists():
        for case in ElementTree.parse(REPORT).iter("testcase"):
            problem = case.find("failure")
            if problem is None:
                problem = case.find("error")
            name = case.get("name", "").removeprefix(f"{TEST_NAMESPACE}:")
            print(f"{'pass' if problem is None else 'FAIL'}: {name}")
            if problem is not None:
                print(f"      {problem.get('message', '').strip()}")
    elif any("found no tests" in line for line in log):
        print(f"FAIL: no test matches '{args.filter}'")
    else:
        print("FAIL: no test report; the test server likely didn't start (see build/gametest.log)")
        failed = True
    for line in log:
        if any(marker in line for marker in ERROR_MARKERS):
            print(f"FAIL: {line.strip()}")
            failed = True
    return 1 if failed else 0


if __name__ == "__main__":
    sys.exit(main())
