# Tasks

## 0. Before implementation

- [ ] 0.1 The user replaces the placeholder textures with hand-drawn art (Fertilizer; Empty, Occupied, and Chime Fairy Hotel blocks and items; Jarred Fairy; each glow layer; Blighted Dirt); verify `client_log_check.py` and the asset test pass, and close the art tasks in `add-fertilizer`, `add-fairy-jar`, `add-chime-hotel`, and `add-blight`

## 1. Version support and docs

- [ ] 1.1 Update `docs/ROADMAP.md`, `openspec/config.yaml`, `README.md`, and `docs/DETAILS.md` on `main` to 26.1, 26.2, and 26.3, with 1.21.11 and older on 1.2.0; verify every doc names the same versions
- [ ] 1.2 Update the porting skill's targets and both release skills' branch loops to the 26.x branches; verify every path and branch named exists
- [ ] 1.3 Add a README note to the `1.21.11`, `1.21.10`, `1.21.1`, and `1.20.1` branches saying they stay on 1.2.0, linking `main`; one commit per branch, never pushed

## 2. Release builds

- [ ] 2.1 Archive the 18 completed changes with `/opsx:archive`; verify `openspec list` shows none active except this one
- [ ] 2.2 Backport 2.0 to `26.2`, then `26.1`, with the porting skill; verify `./gradlew build` (GameTests included) and `client_log_check.py` on each
- [ ] 2.3 Bump `mod_version` to 2.0.0 on `main`, `26.2`, and `26.1`; verify each jar is named `fertilegrounds-2.0.0+<mc>.jar`

## 3. Demo world

- [ ] 3.1 Add `-Pshaders` (Sodium and Iris as dev-only `localRuntime`, versions pinned in `gradle.properties`); verify `runClient -Pshaders` boots with Iris in the log and plain `runClient` boots without it, and the mod jar contains neither
- [ ] 3.2 Write `scripts/demo/build_demo_world.py` and its datapack; pick a seed with a flower forest or cherry grove; verify the script builds the world on a headless server with no errors in the log
- [ ] 3.3 On the headless server, check the built world against the spec scenarios (Fairy Dust on the fairy farm at night brings a fairy; the Chime Hotel field ripens and the lamp blinks) by reading block states and the log; then copy the world to `~/Library/Application Support/minecraft/saves/Fertile Grounds Demo`
- [ ] 3.4 Ask the user before copying the 2.0.0 jar and Fabric API into the real client's `mods/` folder; add "load the demo world, record the core features" to the user's checklist

## 4. Publish

- [ ] 4.1 The user pushes the branches and creates the GitHub release with the three 2.0.0 jars
- [ ] 4.2 Update Modrinth with `updating-modrinth`: three new versions and a description that says older versions stay on 1.2.0; verify through the public API
- [ ] 4.3 Update CurseForge with `updating-curseforge` the same way; verify the page shows the same message
