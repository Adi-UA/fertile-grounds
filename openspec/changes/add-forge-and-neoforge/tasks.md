# Tasks

## 1. CI and API check

- [ ] 1.1 In `.github/workflows/build.yml`, run `./gradlew build -x runGameTest`, then `python3 scripts/run_gametests.py`, and upload `build/gametest-report.xml` and `build/gametest.log` with `actions/upload-artifact` when a step fails. Verify by pushing a branch with a deliberately broken tag: CI fails on the data-load scan and attaches the log. Then revert the tag and confirm CI goes green.
- [ ] 1.2 Run `genSources` on scratch NeoForge and Forge projects for 26.1, 26.2, and 26.3. Check each counterpart in the design's Context table against the real signatures, including tilling on 26.3, gamerule and POI registration, `RegisterGameTestsEvent`, `ForgeGameTestHooks`, and `@WailaPlugin`. Record any difference in design.md with `/opsx:update`. Verified when every row names a signature confirmed in the sources.

## 2. Split main into common and fabric

- [x] 2.1 Add `settings.gradle`, a root `build.gradle`, and `buildSrc` convention scripts from MultiLoader-Template (branch `26.1.1`) for `common` and `fabric`. Take Loom, Fabric API, and NeoForm versions from `meta.fabricmc.net` and `maven.neoforged.net`. Verified when `./gradlew :common:compileJava` succeeds on an empty `common`.
- [x] 2.2 Move `src/main`, `src/client`, `src/test`, and `src/gametest` under `common/`, and keep Fabric's split client source set for the client classes. Keep spotless on every subproject. Verified when `./gradlew :fabric:build -x runGameTest` passes, including `spotlessCheck` and JUnit.
- [x] 2.3 Map each registry to the `Mod*` class that fills it in `FertileGrounds.REGISTRATIONS`, move block items into `ModItems`, and have the Fabric entry point run every registration at startup. Verified with `python3 scripts/run_gametests.py`: every GameTest passes.
- [x] 2.4 Move each remaining Fabric API call into the Fabric entry points (ticks, lifecycle, join, tilling, POI, attributes, creative tab, model layers), with saved data and `isModLoaded` behind `platform.Platform`. Build gamerules with vanilla's constructor. Verified when `rg 'net.fabricmc' common/` finds nothing and every GameTest passes.
- [x] 2.5 Diff the new Fabric jar's file list and `fabric.mod.json` against `fertilegrounds-2.0.0+26.3.jar` from the v2.0.0 release with a small script in `scripts/`. Only the new platform classes and service files differ. Then run `python3 scripts/client_log_check.py` and `./gradlew :fabric:runGameTest -PnoJade`. Verified when all three pass.

## 3. One GameTest source for every loader

- [x] 3.1 Add the common `@FertileTest` annotation and `TestCatalog`, register its functions on Fabric, generate the test-instance files at build time, and convert the gametest structures to `.nbt`. Verified when `SetupTests` runs from the catalog and `--filter` still selects single tests.
- [x] 3.2 Convert all 26 test classes to `@FertileTest`, and add JUnit coverage for `TestCatalog`'s annotation reading. Verified when `run_gametests.py` reports the same pass count as before the change (97) and `rg 'fabricmc' common/src/gametest` finds nothing.
- [x] 3.3 Commit groups 1 to 3 on `main` as separate Conventional Commits and confirm CI is green.

## 4. NeoForge build on main

- [x] 4.1 Add the `neoforge` subproject with ModDevGradle, `neoforge.mods.toml` (Minecraft range limited to 26.3, Jade optional), the `@Mod` entry point, and the `RegisterEvent` registrar. Verified when the NeoForge GameTest server starts with no registration error.
- [x] 4.2 Add the shared vanilla `SavedData` classes from design decision 3 in `common`, plus NeoForge implementations for every platform service, and load the shared mixin config. Add GameTests that run on every loader for:
  - each loader hook in the `forge-loaders` spec: hoe tilling, guide book once, gamerule defaults, and fairy attributes
  - a codec round trip of each saved-data record

  Verified when those GameTests pass on Fabric and NeoForge.
- [x] 4.3 Register the Jade plugins with `@WailaPlugin` and the client renderer and model layer through NeoForge's client events, with Jade for NeoForge as a dev-only runtime dependency. Verified when `JadeDataTests` passes on NeoForge.
- [x] 4.4 Register `TestCatalog` in `RegisterGameTestsEvent`, and add `--loader neoforge` to `run_gametests.py`, using a JUnit report or the log parser from design decision 6. Confirm the data-load markers still match NeoForge's log by planting a broken tag. Verified when `python3 scripts/run_gametests.py --loader neoforge` passes all 97 tests and fails on the planted tag.
- [x] 4.5 Run the NeoForge GameTests with `-PnoJade`, and start the NeoForge dedicated server. Verified when the GameTests pass without Jade and the server log has no class-loading error for `fertilegrounds`.
- [x] 4.6 Extend `AssetConsistencyTest` to compare the id, name, description, license, icon, and Minecraft version in `fabric.mod.json` and `neoforge.mods.toml`. Verified with `./gradlew test`, plus a check that fails on a planted description mismatch.
- [x] 4.7 Turn the CI job into a matrix over `fabric` and `neoforge`, with each loader's jar as an artifact. Verified when a push to `main` shows two green jobs with 97 tests each.

## 5. Forge build on main

- [x] 5.1 Add the `forge` subproject with ForgeGradle 7 following Forge's 26.3 MDK. Add `META-INF/mods.toml` (Minecraft range limited to 26.3, no Jade entry), the `@Mod` entry point, the `RegisterEvent` registrar, and the shared mixin config. Verified when the Forge GameTest server starts with no registration error.
- [x] 5.2 Add Forge implementations for every platform service, reusing the shared `SavedData` classes, and keep Jade classes out of the Forge jar. Verified when the loader-hook and saved-data GameTests from 4.2 pass on Forge, and `unzip -l` on the Forge jar lists no `compat/jade` class.
- [x] 5.3 Register `TestCatalog` through Forge's GameTest hooks with the `fertilegrounds` namespace enabled. Mark `JadeDataTests` to skip on Forge, with the reason. Add `--loader forge` to `run_gametests.py` and confirm the data-load markers with a planted broken tag. Verified when `python3 scripts/run_gametests.py --loader forge` passes every test except the Jade ones and fails on the planted tag.
- [x] 5.4 Start the Forge dedicated server, and extend `AssetConsistencyTest` to Forge's `mods.toml`. Verified when the server log has no class-loading error for `fertilegrounds`, and the metadata check fails on a planted mismatch.
- [x] 5.5 Add `forge` to the CI matrix, and add `--loader all` to `run_gametests.py`, which runs the three loaders' test servers in parallel in separate run folders. Verified when a push to `main` shows three green jobs running at the same time, and `--loader all` locally finishes in less than the three separate runs added together.

## 6. Client and in-game checks

- [x] 6.1 Add `--loader neoforge` and `--loader forge` to `client_log_check.py`. Verified when it boots each dev client with no missing model, texture, or sound warning.
- [x] 6.2 Add these to the maintainer's in-game checklist for NeoForge and Forge, since the test server can't restart a world: "a half-built Fairy Grove finishes after a world restart" and "a pending fairy visit survives a world restart".
- [x] 6.3 Add "the creative tab lists every item in the same order as Fabric" to the checklist for both loaders.
- [ ] 6.4 Check the design's Look and sound table in the NeoForge and Forge `runClient`, and add each row to the in-game checklist as "matches Fabric".

## 7. Docs and skills

- [x] 7.1 Update the README, `docs/DETAILS.md`, `docs/ROADMAP.md`, and `openspec/config.yaml` to name all three loaders, what each jar needs, and that Jade tooltips need Fabric or NeoForge. Verified with a read-through against the `release-2-0` spec's Consistent docs scenarios.
- [x] 7.2 Update AGENTS.md (layout, per-loader Gradle tasks, the `--loader` flags) and keep it under 150 lines. Verified when every path and command it names resolves.
- [x] 7.3 Add a Loader column and the NeoForge and Forge rows to the porting divergence table in `.claude/skills/porting-minecraft-version/SKILL.md`, and add a step to port all loaders one version at a time.
- [x] 7.4 Add NeoForge and Forge steps to the Modrinth and CurseForge skills: the version numbers `2.0.0+<mc>-neoforge` and `2.0.0+<mc>-forge`, each loader's tag, no Fabric API dependency, and the Beta type while a loader has no final build for that version.

## 8. Port to 26.2 and 26.1

- [x] 8.1 Port groups 2 to 7 to `26.2` with the porting skill, using NeoForge 26.2.0.x and Forge 65.1.x. Verified when `run_gametests.py` and `client_log_check.py` pass for all three loaders and CI is green on `26.2`.
- [ ] 8.2 After the 26.2 and 26.3 release (9.1, 9.2), port from `26.2` to `26.1` with NeoForge 26.1.2.x and Forge 64.1.x, verified the same way on `26.1`.

## 9. Release

- [x] 9.1 Build the four NeoForge and Forge jars for 26.2 and 26.3 from the pushed branch heads, attach them to the v2.0.0 GitHub release, and name in the release notes the commit each one was built from. Verified with `gh release view v2.0.0` listing seven jars.
- [x] 9.2 Upload the new jars to Modrinth and then CurseForge with the updated skills, oldest Minecraft version first, with each 26.3 file as Beta while its loader has no final build, leaving the existing Fabric files untouched. Verified through each skill's verify step.
- [ ] 9.3 After 8.2, add the two 26.1 jars the same way (9.1 and 9.2), so the release lists nine jars.
