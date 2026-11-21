# Proposal

## Why

The smoke test (`scripts/server_smoke_test.py`) drives a headless server through console commands and reads `say` markers from the log. It has no players, so every check that needs one (using Fairy Dust or Fertilizer by hand, owned visits, gifts, cooldowns, messages, advancements) has moved to the user's end-of-run checklist: about 25 items across changes 2 to 7, with 8 more changes still to come. Fabric API ships a GameTest module that runs Java tests inside the game with real assertions, and vanilla 26.3's test helper can spawn mock server players, so most of those checks can run automatically on every build.

## What Changes

- Enable Fabric API's GameTest support (`fabric-gametest-api-v1`, already inside Fabric API and dev-only, so players install nothing new) through Loom's `fabricApi.configureTests`, which adds a `gametest` source set and a `runGameTest` task.
- Port the 11 smoke suites to GameTests, one test class per suite, set up in code inside empty structures.
- Add GameTests with mock players for the checks that moved to the checklist: Fairy Dust by hand, owned visits and their saved owner, gifts dropping with unlimited lifetime, gift cooldowns, overlay and chat messages, advancements earned through their real actions, Fertilizer by hand, and the natural visit at 100% chance.
- Replace `server_smoke_test.py` with `scripts/run_gametests.py`, which runs `runGameTest` and fails on the data-load error markers the smoke test checks today (tag, advancement, loot table, and recipe errors that don't crash the server).
- Keep `scripts/client_log_check.py` unchanged.
- Update `AGENTS.md` (commands, verification, regression rule), `openspec/config.yaml` task rules, the porting skill, and the checklist items in changes 2 to 7 that GameTests now cover.

Out of scope: client GameTests (`enableClientGameTests`, which could take screenshots of models and textures; a later change), looks and sounds (still a checklist item, since a test can't judge them), sleeping through the night (vanilla needs every real player asleep), and porting the suite to 1.21.11, 26.1, and 26.2 (the backport phase).

New art: none.

## Capabilities

### New Capabilities

- `in-world-tests`: The automated in-world checks every change must pass, run as GameTests with mock players, and what still needs a person.

### Modified Capabilities

None. `verification-suite` from `add-regression-evals` is not archived yet, so this change adds `in-world-tests` alongside it and the smoke test requirement there is superseded when both archive.

## Impact

`build.gradle` (Loom test configuration), a new `src/gametest/` source set with its own `fabric.mod.json` entrypoint, `scripts/` (new runner, smoke script removed), `AGENTS.md`, `CLAUDE.md`, `openspec/config.yaml`, `.claude/settings.json` allowlist, the porting skill, `docs/ROADMAP.md` (this change goes before `add-fairy-jar`), and the tasks files of changes 2 to 7. No mod code changes ship to players.
