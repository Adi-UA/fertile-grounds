# Tasks

## 1. GameTest setup

- [x] 1.1 Enable `fabricApi.configureTests` in `build.gradle`, add the `gametest` source set's `fabric.mod.json` with one always-passing test, and look up how Fabric 4.0.32 filters tests by name; verify `./gradlew runGameTest` runs the test and exits 0, and exits non-zero after making it fail
- [x] 1.2 Add `scripts/run_gametests.py` (runs `runGameTest`, prints one line per test, fails on the data-load error markers, forwards a name filter); verify it exits 1 on a temporary tag that references a missing block
- [x] 1.3 Add the `night` and `no_random_ticks` test environments, the empty medium and large structures, and `TestPlayers.survival`; verify a test using each one passes

## 2. Port the smoke suites

- [x] 2.1 Port `fairy_visit`, `fairy_linger`, and `harvest_moon` (lowest-Y and ring-order tracking as per-tick assertions); verify each passes, and the sinking check fails with the open-sky floor removed
- [x] 2.2 Port `soil_regressions`, `fairy_content_tags` (with a test datapack in the gametest resources), `tuning_gamerules`, `fertilizer`, `advancements`, and `gift_tables`; verify each passes, and the trample check fails with the revert target set to vanilla dirt

## 3. Checks that need a player

- [x] 3.1 Add mock-player tests for Fairy Dust (pending owned visit, one dust used, already-coming message, no-night message in the Nether), Fertilizer by hand (upgrade, one used, crop and moisture kept), and the natural visit at `fairy_visit_chance 100`; verify each passes and fails when its feature line is commented out
- [ ] 3.2 Add mock-player tests for visit feedback (arrival overlay within 32 blocks and not at 50, dawn message once for two overnight visits), gifts (drop at 100% with unlimited lifetime, gift message, cooldown next night, none for unowned), and advancements earned through real actions (root, first visit, Fertilizer, first gift, Harvest Moon visit); verify each passes
- [ ] 3.3 Add a mock-player test for shy flight (a non-sneaking player within 4 blocks makes a lingering fairy move at least 4 blocks away; a sneaking one doesn't); verify it passes

## 4. Switch over

- [ ] 4.1 Delete `scripts/server_smoke_test.py`; update `AGENTS.md` (commands, verification, layout, regression rule), `CLAUDE.md` (sandbox note), `.claude/settings.json` allowlist, and the porting skill; verify every file and path these docs name exists and `AGENTS.md` stays under 150 lines
- [ ] 4.2 Update `openspec/config.yaml` task rules to name GameTests and the checklist-only scope for looks and sounds; reword the tasks in changes 2 to 7 whose checklist items GameTests now cover; verify `openspec validate` passes for every change
- [ ] 4.3 Move `add-gametest-suite` before `add-fairy-jar` in `docs/ROADMAP.md`; verify the full `run_gametests.py` run and `client_log_check.py` both pass
