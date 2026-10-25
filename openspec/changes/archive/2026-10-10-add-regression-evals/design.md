# Design

## Context

`scripts/server_smoke_test.py` runs one flat list of setup commands and `say`-marker checks. JUnit covers only `FairyTiming`. Every 2.0.0 target (1.21.11 and newer) shares the same recipe, item model, and gamerule formats, so one set of shape checks fits all of them.

## Goals / Non-Goals

**Goals:** fast checks first (JUnit runs in `./gradlew build`, no game launch), world checks second, client checks last.

**Non-Goals:** Fabric GameTest. It runs inside the game with real assertions, but the existing console-marker pattern already works on every branch, and switching frameworks mid-release adds porting risk. Revisit after 2.0.0.

## Decisions

- **`AssetConsistencyTest` reads resources as plain JSON** with the JDK and a small JSON parser already on the test classpath (Gson ships with Minecraft's dependencies). It derives the list of items from `assets/fertilegrounds/items/*.json` plus registration names parsed from the lang file, and cross-checks both directions so a missing file on either side fails.
- **Lang check scans Java source** for `Component.translatable("...")` and `"message.fertilegrounds.` strings with a regex, so keys used in code are covered too.
- **Smoke suites as a list of `Suite(name, setup, checks, regressions)`** in the same script; each 2.0.0 change appends one suite. A `--suite name` flag runs one suite for fast iteration. Suites use separate areas of the force-loaded region so they never interfere.
- **Dry-out check runs in its own phase** with `random_tick_speed` raised, after the fairy phase, since the fairy checks need random ticks off.
- **Error markers**: add the real log strings for recipe, advancement, loot table, and tag parse failures, copied from the 26.3 sources (look them up; don't guess).

## Risks / Trade-offs

- [Trample check is random] → a 10-block fall makes vanilla's trample roll always pass; confirm in the 26.3 `FarmlandBlock.fallOn` source.
- [Client log check is slow (about a minute)] → run it only for changes that touch assets, as the task lists say.
