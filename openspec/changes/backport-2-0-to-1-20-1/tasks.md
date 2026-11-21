# Tasks

All work happens in a worktree on the local `1.20.1` branch. Nothing gets pushed or published until group 7.

## 1. Branch and build setup

- [x] 1.1 Pick the `common` and `forge` build plugins from the design's table by building an empty Forge 47.4.28 jar with one mixin, every plugin pinned exactly. Verify: the reobfuscated jar boots in `:forge:runClient` with the mixin applied, and the porting table records which plugin worked.
- [x] 1.2 Check out `1.20.1`, replace its tree with `origin/1.21.1`, delete the `neoforge` project and 1.2.0-only files such as `src/`, and commit nothing yet. Verify: `git status` shows only the tree swap, and the `origin/1.20.1` history is intact.
- [x] 1.3 Set the 1.20.1 pins from the design in `gradle.properties` (Java 17, `minecraft_version_range`, `data_pack_format` and `DataVersion` read from the 1.20.1 client jar's `version.json`), and set the `fabric.mod.json` and `mods.toml` ranges. Verify: `LoaderMetadataTest` passes after it drops the NeoForge file.
- [x] 1.4 Switch `common`, `fabric`, and `forge` to the plugins from 1.1, with Jade 11.13.3 behind `-PnoJade` on both loaders. Verify: `./gradlew :fabric:genSources` runs and `:common:compileJava` reaches source errors, not setup errors.

## 2. Shared code compiles on 1.20.1

- [x] 2.1 Confirm each "check" row of the design's table in the 1.20.1 sources jar, then apply the renames (`ResourceLocation`, `MobEffect`, `Blocks.GRASS`, Java 17 list methods). Verify: `./gradlew :common:compileJava` passes, and each confirmed form goes into the porting skill's `differences.md`.
- [x] 2.2 Move item components to item NBT in `JarredFairyItem`, `JarredFairies`, and `GuideBook`, with a Minecraft-free class for the value rules (ranges, defaults) and the same keys the components used. Verify: a JUnit test covers the value rules, and the `FairyCatchTests`, `JarredFairyExpiryTests`, and `GuideBookTests` GameTests pass in group 4.
- [x] 2.3 Port Forge saved data to 1.20.1's `SavedData` API and Fabric attachments to Fabric API 0.92, keeping every id and codec. Verify: `GuideBookTests` rejoin, death, and End-exit checks pass on both loaders, and `OldSaveTests` passes on Fabric.
- [x] 2.4 Port the Jade plugin to Jade 11's API and load it on Forge through `@WailaPlugin`. Jade 11 sends server data only for block entities, so the three server-data lines and `JadeDataTests` go (the maintainer chose this). Verify: the client plugin compiles on both loaders, and `-PnoJade` runs still pass.
- [x] 2.5 Convert the data files to 1.20.1 formats (`pack_format`, recipes, advancements, loot tables), checking each against vanilla 1.20.1 JSON in the client jar. Verify: `AssetConsistencyTest` passes, and `run_gametests.py` reports no data-load errors in group 4.

## 3. Loader entry points

- [x] 3.1 Fabric: tilling, trades, gamerules, attachments, renderer, and model layers on Fabric API 0.92.12. Verify: `./gradlew :fabric:build -x runGameTest` passes.
- [x] 3.2 Forge: Forge 47 events (tilling, `WandererTradesEvent`, `FarmlandTrampleEvent`), registration, `ForgeSpawnEggItem`, and the mixin config with its refmap. Verify: `./gradlew :forge:build` passes, and the jar's manifest lists the mixin config and the refmap is inside the jar.

## 4. GameTests on 1.20.1

- [x] 4.1 Port `TestCatalog`, `TestEnvironments`, `TestPlayer`, and `TestSupport` to 1.20.1's `GameTestHelper`, and re-save the structures with 1.20.1's `DataVersion`. Verify: `python3 scripts/run_gametests.py --loader all --filter 'setup*'` passes on Fabric and Forge.
- [x] 4.2 Port every test class. Verify: `python3 scripts/run_gametests.py --loader all` passes with the same test count per loader as 1.21.1.
- [x] 4.3 Add a GameTest that saves and reloads a Jarred Fairy and a Guide Book and checks their NBT. Verify: it passes on both loaders.
- [x] 4.4 Run the Forge jar's GameTests on NeoForge 47.1.106 (`-PneoforgeCompat`, not a NeoForge project). Verify: every test passes, or the failures go to the maintainer and the listing stays Forge only. The mod doesn't load on NeoForge 47.1 (locked registries); the maintainer chose Forge only.

## 5. Full verification

- [x] 5.1 `./gradlew build` and `python3 scripts/run_gametests.py --loader all` pass twice in a row. Verify: both runs are green, and any flaky test gets fixed, not retried.
- [x] 5.2 `python3 scripts/client_log_check.py --loader <each>` passes for Fabric and Forge. Verify: no mod warnings on either loader.
- [x] 5.3 Compare each 1.20.1 jar's file list with its 1.21.1 counterpart and explain every difference. Verify: only the changes the design names differ.

## 6. Docs and the playtest gate

- [x] 6.1 Add every new difference to the porting skill's table (now `differences.md`, since `SKILL.md` reached the 150-line cap) under "1.20.1 (from 1.21.1)", and update the `1.20.1` README per the `release-2-0` spec. Verify: each row names code checked in this port.
- [x] 6.2 Commit the port on `1.20.1` as `feat: Backport 2.0.1 to Minecraft 1.20.1`, with smaller commits per group where they build. Verify: `git log` shows the commits on top of `origin/1.20.1`.
- [x] 6.3 Check the design's Look and sound table in `runClient` on Fabric and Forge, and give the maintainer an in-game checklist: fairy look and wings, soil and hotel textures, unlit glow layers, the glow and chime heralds, the Glow Berries hotel recipe, Jade on Forge, a 1.2.0 Fabric world opened in 2.0.1, and sleeping through a visit night. Verify: the maintainer marks every item.

## 7. Release (only after the gate passes)

- [x] 7.1 Update the README, `docs/DETAILS.md`, `docs/ROADMAP.md`, `openspec/config.yaml`, and the porting and release skills on `main` and every 2.0 branch to list 1.20.1, drop "1.20.1 stays on 1.2.0", and add NeoForge on 1.20.1 as TBD to `docs/ROADMAP.md`. Verify: the `release-2-0` scenarios read true from those files.
- [x] 7.2 The maintainer pushes and publishes, then Claude mirrors the jars to Modrinth and CurseForge with the store skills, listing the Forge jar for Forge only, and leaving the 1.2.0 file in place. Verify: the store skills' read-back steps.
