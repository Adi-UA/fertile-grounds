"""Runs the mod's in-world GameTests and reports pass/fail per test.

Also exits 1 when the log shows a data file that failed to load without failing a test (a broken
tag or advancement only logs an error).

Usage (from the repo root):
    python3 scripts/run_gametests.py                          # every test on Fabric
    python3 scripts/run_gametests.py --loader neoforge        # every test on NeoForge
    python3 scripts/run_gametests.py --loader all             # all three loaders at the same time
    python3 scripts/run_gametests.py --filter '*fertilizer*'  # only tests whose names match
"""

import argparse
import subprocess
import sys
import xml.etree.ElementTree as ElementTree
from pathlib import Path

LOADERS = ("fabric", "neoforge", "forge")
TASKS = {"fabric": ":fabric:runGameTest", "neoforge": ":neoforge:runGameTestServer", "forge": ":forge:runGametestGameTestServer"}
TEST_NAMESPACE = "fertilegrounds-gametest"
LOG = Path("build/gametest.log")

# Log lines that mean data silently failed to load, from Minecraft's data loaders and validators.
# validators.
ERROR_MARKERS = (
    "Registry loading errors",
    "Failed to load datapacks",
    "Exception in server tick",
    "Couldn't parse data file",
    "Couldn't load tag",
    "Couldn't read tag list",
    "Found validation problems in advancement",
    "Found loot table element validation problem",
    "Parsing error loading recipe",
    "Parsing error loading custom advancement",
    "Game test server crashed",
    "Couldn't load pack metadata",
)


def report(loader: str) -> Path:
    """Where each loader's build writes its JUnit-style report."""
    return Path(f"build/gametest-{loader}-report.xml")


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--loader", choices=(*LOADERS, "all"), default="fabric", help="which loader's test server to run")
    parser.add_argument("--filter", help="only run tests whose ids match this pattern, e.g. '*fertilizer*'")
    args = parser.parse_args()
    loaders = LOADERS if args.loader == "all" else (args.loader,)

    # One Gradle run, so tasks from different projects run their test servers in parallel.
    command = ["./gradlew", *(TASKS[loader] for loader in loaders), "--continue", "--console=plain"]
    if args.filter:
        # Test ids are namespace:path, and the matcher's * doesn't cross the colon.
        pattern = args.filter if ":" in args.filter else f"{TEST_NAMESPACE}:{args.filter}"
        command.append(f"-PgametestFilter={pattern}")
    for loader in loaders:
        report(loader).unlink(missing_ok=True)
    result = subprocess.run(command, capture_output=True, text=True)
    log = (result.stdout + result.stderr).splitlines()
    LOG.parent.mkdir(exist_ok=True)
    LOG.write_text("\n".join(log))

    failed = result.returncode != 0
    for loader in loaders:
        prefix = f"{loader}: " if len(loaders) > 1 else ""
        if report(loader).exists():
            for case in ElementTree.parse(report(loader)).iter("testcase"):
                problem = case.find("failure")
                if problem is None:
                    problem = case.find("error")
                name = case.get("name", "").removeprefix(f"{TEST_NAMESPACE}:")
                print(f"{prefix}{'pass' if problem is None else 'FAIL'}: {name}")
                if problem is not None:
                    print(f"      {problem.get('message', '').strip()}")
        elif any("No test functions were given" in line for line in log):
            print(f"{prefix}FAIL: no test matches '{args.filter}'")
        else:
            print(f"{prefix}FAIL: no test report; the test server likely didn't start (see {LOG})")
            failed = True
    for line in log:
        if any(marker in line for marker in ERROR_MARKERS):
            print(f"FAIL: {line.strip()}")
            failed = True
    return 1 if failed else 0


if __name__ == "__main__":
    sys.exit(main())
