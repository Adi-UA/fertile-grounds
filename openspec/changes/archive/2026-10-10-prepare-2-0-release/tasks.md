# Tasks

## 1. Version support and docs

- [x] 1.1 Update `docs/ROADMAP.md`, `openspec/config.yaml`, `README.md`, and `docs/DETAILS.md` on `main` to 26.1, 26.2, and 26.3, with 1.21.11 and older on 1.2.0; verify every doc names the same versions
- [x] 1.2 Update the porting skill's targets and both release skills' branch loops to the 26.x branches; verify every path and branch named exists
- [x] 1.3 Add a README note to the `1.21.11`, `1.21.10`, `1.21.1`, and `1.20.1` branches saying they stay on 1.2.0, linking `main`; one commit per branch, never pushed

## 2. Release builds

- [x] 2.1 Archive the 13 changes with every task done with `/opsx:archive`; verify `openspec list` shows only this change, the changes waiting on art, and `add-translations` (waiting on Crowdin)
- [x] 2.2 Backport 2.0 to `26.2`, then `26.1`, with the porting skill; verify `./gradlew build` (GameTests included) and `client_log_check.py` on each
- [x] 2.3 Bump `mod_version` to 2.0.0 on `main`, `26.2`, and `26.1`; verify each jar is named `fertilegrounds-2.0.0+<mc>.jar`

## 3. Demo world

- [x] 3.1 Add `-Pshaders` (Sodium and Iris as dev-only `localRuntime`, versions pinned in `gradle.properties`); verify `runClient -Pshaders` boots with Iris in the log and plain `runClient` boots without it, and the mod jar contains neither
- [x] 3.2 Write `scripts/demo/build_demo_world.py` and its datapack; pick a seed with a flower forest or cherry grove; verify the script builds the world on a headless server with no errors in the log
- [x] 3.3 Add the `fgdemo` test functions (`build/*` reused by `reset/*`, `mode/*`), the spawn sign board, and the test lot; verify on the headless server that changing blocks in the grove area then running `reset/grove` restores them (compared with `execute if blocks` against a fresh copy), `mode/test` sets every listed gamerule, and `mode/default` restores them
- [x] 3.4 On the headless server, check the built world against the spec scenarios (the Chime Hotel field ripens and the hotel turns ready, read from its block state; Fairy Dust needs a player, so that one goes to the user's checklist) by reading block states and the log; then copy the world to `~/Library/Application Support/minecraft/saves/Fertile Grounds Demo`
- [x] 3.5 Ask the user before copying the 2.0.0 jar and Fabric API into the real client's `mods/` folder (the user declined on 2026-10-10: the dev client with `-Pshaders` is enough); add "load the demo world, record the core features" to the user's checklist

## 4. Publish

- [x] 4.0 The user replaces the placeholder textures with hand-drawn art (Fertilizer; Empty, Occupied, and Chime Fairy Hotel blocks and items; Jarred Fairy; each glow layer) on `main`, then Claude merges it into `26.2`, `26.1`, and `26.3`; verify `client_log_check.py` and the asset test pass on each, close the art tasks in `add-fertilizer`, `add-fairy-jar`, and `add-chime-hotel`, and archive those three
- [x] 4.1 Push the branches and creates the GitHub release with the three 2.0.0 jars
- [x] 4.2 Update Modrinth with `updating-modrinth`: three new versions and a description that says older versions stay on 1.2.0; verify through the public API
- [x] 4.3 Update CurseForge with `updating-curseforge` the same way; verify the page shows the same message
