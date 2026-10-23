# Tasks

All work happens on the local `1.21.1` branch. Nothing gets pushed or published until group 7.

## 1. Branch and build setup

- [x] 1.1 Check out `1.21.1`, replace its tree with `origin/26.1` (deleting 1.2.0-only files such as `src/` and `scripts/server_smoke_test.py`), and commit nothing yet. Verify: `git status` shows only the tree swap, and the `origin/1.21.1` history is intact.
- [ ] 1.2 Set the 1.21.1 pins from the design in `gradle.properties` (Java 21, `minecraft_version_range`, `data_pack_format` read from the 1.21.1 client jar's `version.json`), and set the `fabric.mod.json` ranges and both `mods.toml` `loaderVersion` ranges (`[52,)` for Forge, `[4,)` for NeoForge). Verify: `LoaderMetadataTest` passes.
- [x] 1.3 Switch `common` to NeoForm 1.21.1 and `fabric` to Loom with Mojang mappings (`modImplementation`, Jade through `modCompileOnly`/`modLocalRuntime` behind `-PnoJade`). Verify: `./gradlew :fabric:genSources` runs and `:common:compileJava` reaches source errors, not setup errors.
- [x] 1.4 Set up `forge` on Forge 52.1.20, trying ForgeGradle 7.0.40 first and falling back to ForgeGradle 6 pinned exactly with `reobf = false`; set up `neoforge` on NeoForge 21.1.264. Verify: `./gradlew :forge:compileJava :neoforge:compileJava` reaches source errors, and the porting table records which ForgeGradle worked.

## 2. Shared code compiles on 1.21.1

- [ ] 2.1 Apply the mechanical renames and formats from the design (`ResourceLocation`, ids, `EntityType.create`, `isNight`, `getDayTime`, `FarmBlock`, bone meal, overlay message, `getMoonPhase`), checking each against the 1.21.1 sources jar. Verify: `./gradlew :common:compileJava` passes.
- [ ] 2.2 Rewrite `ModGameRules` on `Platform.intRule` and `boolRule` (Fabric `GameRuleRegistry`, NeoForge and Forge `IntegerRuleInvoker`) with the same names, defaults, and ranges, loaded from each loader's entry point. Verify: `TuningGameruleTests` (ported in 4.x) shows defaults and that out-of-range values get rejected on every loader.
- [ ] 2.3 Reuse 1.2.0's 1.21.1 Bean Fairy model and renderer (`HierarchicalModel`, `MobRenderer<BeanFairy, BeanFairyModel>`), whose `texOffs` match 26.1's. Verify: `:common:compileClientJava` (or the client source set) passes, and `client_log_check.py` in 5.2.
- [ ] 2.4 Let crops stand and grow on the farmland tiers (shared `CropBlockMixin` and `PitcherCropBlockMixin`, Fabric-only growth mixin, `isFertile` for Forge) and protect farmland near hotels (Fabric-only trample mixin, `FarmlandTrampleEvent` on NeoForge and Forge). Verify: the `SoilRegressionTests`, `HotelEffectTests`, and visit tests that plant on enriched farmland pass on all three loaders in group 4.
- [ ] 2.5 Move the Wandering Trader offers from `villager_trade` JSON to `WanderingTraderOffers`. Verify: `TraderTests` (rewritten for 1.21.1's rare list of 7 vanilla offers) passes on all three loaders in group 4.
- [ ] 2.6 Convert the data files to 1.21.1 formats (recipe ingredients, `recipes` unlock rewards, no `items/` models, glow as `layer1`, `mob_effects` atlas, no 26.x-only blocks or tags, Glow Berries in the hotel recipe). Verify: `AssetConsistencyTest` passes after its folder and format expectations are updated for 1.21.1, and `run_gametests.py` reports no data-load errors in group 4.

## 3. Loader entry points

- [ ] 3.1 Fabric: tilling through `TillableBlockRegistry`, trades through `TradeOfferHelper`, attachments under the same ids, gamerules through `GameRuleRegistry`, and renderer and model layer registration on Fabric API 0.116. Verify: `./gradlew :fabric:build -x runGameTest` passes.
- [ ] 3.2 NeoForge: `BlockToolModificationEvent`, `WandererTradesEvent`, attachments, `SavedData`-free level data, and registration events on 21.1. Verify: `./gradlew :neoforge:build` passes.
- [ ] 3.3 Forge: EventBus 6 listeners, `BlockToolModificationEvent`, `WandererTradesEvent`, UUID-keyed player data and `SavedData.Factory` level data, `pack.mcmeta`, and mixins through `MixinConfigs`. Verify: `./gradlew :forge:build` passes, and the jar's manifest lists the mixin config.

## 4. GameTests on the 1.21.1 API

- [ ] 4.1 Rewrite `TestCatalog` to build `TestFunction`s from `@FertileTest` through `@GameTestGenerator`, map each 26.1 test environment to a batch with a `@BeforeBatch` method in `TestEnvironments`, drop `padding`, and set the structures' `DataVersion` to 3955. Verify: `SetupTests` passes on Fabric.
- [ ] 4.2 Register the generator on each loader (the `fabric-gametest` entrypoint, and `RegisterGameTestsEvent` on NeoForge and Forge), pass the report and filter through `fertilegrounds.gametest.*` properties, and update `run_gametests.py` (`ERROR_MARKERS` checked against 1.21.1 sources, plus the summary parsing). Verify: `python3 scripts/run_gametests.py --loader all --filter 'setup*'` passes on all three.
- [ ] 4.3 Port `TestPlayer`, `TestSupport`, and every test class to 1.21.1's `GameTestHelper`. Verify: `python3 scripts/run_gametests.py --loader all` passes with the same test count per loader as 26.1 (Forge without Jade).
- [ ] 4.4 Add a GameTest that decodes a pending-visit list in 1.2.0's format through the Fabric attachment id. Verify: it passes on Fabric.

## 5. Full verification

- [ ] 5.1 `./gradlew build` and `python3 scripts/run_gametests.py --loader all` pass twice in a row. Verify: both runs are green, and any flaky test gets fixed, not retried.
- [ ] 5.2 `python3 scripts/client_log_check.py --loader <each>` passes for Fabric, NeoForge, and Forge. Verify: no mod warnings on any loader.
- [ ] 5.3 Compare each 1.21.1 jar's file list with its 26.1 counterpart (`scripts/compare_release_jar.py` or a zip listing) and explain every difference. Verify: only the format changes from the design differ.

## 6. Docs and the playtest gate

- [ ] 6.1 Add every new difference to the porting skill's table (hop "1.21.1 (from 26.1)") and update the `1.21.1` branch README to say it carries 2.0 and gets updates after 26.x. Verify: each row names code checked in this port.
- [ ] 6.2 Commit the port on `1.21.1` as `feat: Backport 2.0.0 to Minecraft 1.21.1` (with smaller commits per group where they build). Verify: `git log` shows the commits on top of `origin/1.21.1`.
- [ ] 6.3 Give the maintainer an in-game checklist for 1.21.1 on all three loaders: fairy look and wing animation, soil and hotel textures, the unlit glow layers, the glow-particle and chime heralds, the Glow Berries hotel recipe, sounds, and sleeping through a visit night. Verify: the maintainer marks every item.

## 7. Release (only after the gate passes)

- [ ] 7.1 Update the README, `docs/DETAILS.md`, `docs/ROADMAP.md`, `openspec/config.yaml`, and the porting and release skills on `main` to list 1.21.1 and say it gets updates later. Verify: the `release-2-0` scenarios read true from those files.
- [ ] 7.2 The maintainer pushes and publishes the jars to GitHub, then Claude mirrors them to Modrinth and CurseForge with the store skills, leaving every 1.2.0 file in place. Verify: the store skills' read-back steps.
