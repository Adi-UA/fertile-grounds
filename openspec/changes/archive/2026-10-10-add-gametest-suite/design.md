# Design

## Context

`scripts/server_smoke_test.py` has 11 suites: `fairy_visit`, `fairy_linger`, `soil_regressions`, `fairy_content_tags`, `tuning_gamerules`, `fertilizer`, `harvest_moon`, `advancements`, `gift_tables`, plus test datapacks and tick functions that record values each tick. It also turns off the 1.21.2+ empty-server pause and scans the log for data-load errors. The project uses Loom 1.17.17 and Fabric API 0.161.0+26.3, which bundles `fabric-gametest-api-v1` 4.0.32.

Checked in the 26.3 jars and sources:

- Fabric's `@GameTest` annotation (`net.fabricmc.fabric.api.gametest.v1.GameTest`) marks test methods taking a `GameTestHelper`, with `structure` (default: Fabric's empty 8x8x8), `maxTicks`, `environment`, `skyAccess`, and `padding`. Test classes are listed under the `fabric-gametest` entrypoint.
- Loom's `fabricApi { configureTests { createSourceSet, modId, enableGameTests, enableClientGameTests, eula } }` creates the `gametest` source set and the `runGameTest` task.
- Vanilla `GameTestHelper.makeMockServerPlayerInLevel()` joins a real `ServerPlayer` to the player list through an embedded connection, so `level.players()`, messages, and advancements all see it. Its game mode is hard-coded to creative, where items aren't used up.
- 26.3 test environments are data-driven (`test_environment` registry, types `game_rules`, `clock_time`, `weather`, `all_of`, `function`).

## Goals / Non-Goals

**Goals:** every check the smoke test makes, plus every checklist item that only needed a player, runs from one command on every build. Tests read like the feature's spec scenarios.

**Non-Goals:** client GameTests and screenshots (later change); judging looks or sounds; porting the tests to older branches (backport phase).

## Decisions

- **Fabric GameTest over a homemade fake-player harness.** It is the framework Fabric API itself and most large Fabric mods test with, it is inside the Fabric API we already depend on, and its tests are Java with assertions instead of console markers. Keeping the Python harness and bolting a fake player onto it would mean test-only code inside the shipping mod.
- **Separate `gametest` source set** (Loom's `createSourceSet = true`) with its own `fabric.mod.json` declaring the `fabric-gametest` entrypoint, so test classes never ship in the mod jar.
- **One test class per smoke suite**, named after it (`FairyVisitTests`, `SoilRegressionTests`, and so on), with the guarded commit in a comment on each regression test. Setup happens in code with `helper.setBlock` and `helper.spawn`, relative to the test's own structure.
- **Empty structures sized per need.** Fabric's empty 8x8x8 fits a single visit (5x5 patch, fairy spawning 6 blocks up). Gift tier tests need a 33x10x33 farm, so `src/gametest/resources` adds generated empty structures (`.snbt`, size only, no blocks) at `fertilegrounds:empty_medium` and `fertilegrounds:empty_large`.
- **Test environments in data** (`data/fertilegrounds/test_environment/*.json`): `night` (`clock_time`), `no_random_ticks` (`game_rules`), and `all_of` combinations, so tests that need the same world state batch together and don't fight over the shared clock. Each test sets the mod's own gamerules it depends on through the environment too, so a test never relies on another test's leftovers.
- **Survival mock player.** `TestPlayers.survival(helper)` copies vanilla's `makeMockServerPlayerInLevel` pattern but returns survival mode, for checks where an item must be used up. Tests that only need a player nearby use vanilla's creative mock.
- **Per-tick tracking moves into Java.** The open-field fairy's lowest Y and the "center grew before the corner" order become `helper.onEachTick` assertions, replacing the datapack tick functions.
- **Data-load errors still need the log.** A tag with a missing reference logs an error without failing any test, so `scripts/run_gametests.py` runs `./gradlew runGameTest`, streams its output, and exits 1 on the same `ERROR_MARKERS` list, or when `runGameTest` fails. It forwards a name filter for running one feature's tests; how Fabric filters tests on 26.3 is the first thing task 1.1 looks up.
- **Porting.** 1.21.11 uses the same Fabric `@GameTest` module line (4.x) but its test environment registry and `GameTestHelper` method names must be checked in its mappings during the backport; any difference goes in the divergence table then.

## Risks / Trade-offs

- [Tests share one level's clock and day-night state] → night-dependent tests use the `night` environment, and the scheduler's day-night transition tests run in their own batch with `clock_time` steps.
- [Mock players trigger natural visit rolls for every test at nightfall] → environments set `fairy_visit_chance` to 0 except in the natural visit test.
- [Spawn-egg fairies search 8 blocks and could find a neighbor test's crop] → tests that summon fairies use `padding` of at least 8.
- [GameTest output is less readable than the old pass/fail list] → `run_gametests.py` prints one line per test with its result.
