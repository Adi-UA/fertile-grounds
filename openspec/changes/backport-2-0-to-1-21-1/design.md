# Design

## Context

The `1.21.1` branch holds 1.2.0: one Fabric project with 19 classes, built with Loom and Mojang mappings, and no GameTests. The `26.1` branch holds 2.0.0: a MultiLoader layout (`common`, `fabric`, `neoforge`, `forge`) with 73 main classes, 32 GameTest classes, and an unobfuscated build. Going from 26.1 back to 1.21.1 crosses every row in the porting skill's table dated 1.21.11 or earlier, plus whatever 1.21.11 and 1.21.10 changed that no row records yet, because those hops never happened in this direction.

Pins (looked up 2026-10-22): Fabric Loader 0.19.5 (newest stable), Fabric API 0.116.17+1.21.1, NeoForm 1.21.1-20240808.144430, NeoForge 21.1.264, Forge 52.1.20 (`1.21.1-recommended`), Jade 15.10.6 for Fabric and NeoForge (Modrinth lists no Forge 1.21.1 build), and Java 21.

## Goals / Non-Goals

**Goals:** every 2.0.0 feature and every GameTest on 1.21.1 for all three loaders; worlds saved by 1.2.0 on 1.21.1 load with their pending fairy visits; and the new divergences go into the porting table.

**Non-Goals:** 1.21.11, 1.21.10, or 1.20.1; changing behavior to suit 1.21.1; publishing before the gate.

## Decisions

### Start from the 26.1 tree in one hop

The skill's rule of one hop at a time exists so each hop's fixes feed the next. Here, no in-between branch would ever ship, so it would be throwaway work. The port checks out `1.21.1`, replaces its tree with `git checkout origin/26.1 -- .` (and removes 1.2.0 files that 26.1 lacks, such as `src/`), and commits the port on top of the 1.2.0 history, so `git log 1.21.1` still shows where it came from. Each fix gets checked against the 1.21.1 sources, never against memory, and the porting table names that hop "1.21.1 (from 26.1)" so readers know the in-between versions weren't checked one by one.

### Build setup per project

Minecraft 1.21.1 ships obfuscated, but NeoForge and Forge both run it with Mojang names (Forge since 1.20.6), so only Fabric remaps. That makes the template's 1.21.1 branch the reference (jaredlll08/MultiLoader-Template, branch `1.21.1`).

| Project | 26.1 | 1.21.1 |
|---|---|---|
| `common` | ModDevGradle on NeoForm 26.1 | ModDevGradle 2.0.148 on NeoForm 1.21.1-20240808.144430 |
| `fabric` | Loom (`net.fabricmc.fabric-loom`), no mappings, `implementation` | Loom's remapping plugin (`net.fabricmc.fabric-loom-remap`) with `loom.officialMojangMappings()`, `modImplementation`, `modCompileOnly`/`modLocalRuntime` for Jade |
| `neoforge` | ModDevGradle, NeoForge 26.1.2.116 | Same plugin, NeoForge 21.1.264, `loaderVersion "[4,)"` |
| `forge` | ForgeGradle 7.0.40, EventBus 7 | ForgeGradle 7.0.40 (it sets up 52.1.20); EventBus 6 (`MinecraftForge.EVENT_BUS`, mod-bus events through `getModEventBus()`); no MixinExtras |

Mixins keep the manifest `MixinConfigs` entry on Forge. The template's `org.spongepowered.mixin` `0.7-SNAPSHOT` plugin stays out, since AGENTS.md forbids snapshot dependencies, and no refmap is needed when Forge runs with Mojang names. The Java toolchain and the mixin config's `compatibilityLevel` go to 21. Loom keeps the Jade plugin off the dev server's classpath, so `-PnoJade` behaves the same as on 26.x. Jade's Fabric jar uses Fabric's intermediary names, so `common` compiles against Jade's NeoForge jar, which uses Mojang's names like `common` does.

### Version-specific code

The table rows below 26.1 say what changes. Each one gets the 1.21.1 form, copied from vanilla or 1.2.0 code where they already have it. The four features that need rewriting, not renaming, are these:

- **Wandering Trader offers:** 1.21.1 has no data-driven villager trades, and vanilla's `ItemsForEmeralds` is package-private. `WanderingTraderOffers` builds the two 26.1 offers (price, count, uses) as `ItemListing` lambdas, and each loader adds them to vanilla's rare list (level 2): Fabric through `TradeOfferHelper.registerWanderingTraderOffers`, NeoForge and Forge through `WandererTradesEvent.getRareTrades()`.
- **Crops on the farmland tiers:** 26.1 already reimplements moisture and trampling (it predates 26.3's `FarmlandBlock(baseBlock)`), so that code carries over. 1.21.1 crops check `Blocks.FARMLAND` instead of the `supports_crops` and `grows_crops` tags, so those tag files go. `CropBlockMixin` and `PitcherCropBlockMixin` (shared, plain `@Inject`) turn a `mayPlaceOn` no into a yes for any `FarmBlock`; NeoForge and Forge still ask `mayPlaceOn` when no other mod decides. For growth speed, NeoForge already counts any `FarmBlock`; Forge asks `isFertile`, which the tiers define; Fabric gets a Fabric-only `CropBlockMixin` that wraps vanilla's farmland check. Trample protection near hotels uses each loader's hook: `FarmlandTrampleEvent` on NeoForge and Forge, a Fabric-only mixin around `turnToDirt` on Fabric, all through `FairyHotelLookup.protectsFromTrampling`.
- **Bean Fairy rendering:** before render states, `BeanFairyRenderer` extends `MobRenderer<BeanFairy, BeanFairyModel>`, and the model is a `HierarchicalModel` whose `setupAnim` resets the pose itself. 1.2.0's 1.21.1 model and renderer already do this with the same `texOffs`, so the port reuses them and no texture moves.
- **Saved data and gamerules:** 1.21.1 has no codec `SavedDataType` or `GameRule` registry. NeoForge's attachments take the codec directly, and Forge's `LevelData` wraps a `SavedData` subclass that stores the codec's value as NBT in `fertilegrounds_<path>.dat` (`SavedData.Factory`). Gamerules keep their names (`fertilegrounds:fairy_visit_chance`), defaults, and ranges through two new `Platform` methods, `intRule` and `boolRule`: Fabric uses `GameRuleRegistry` with `GameRuleFactory.createIntRule(default, min, max)`; NeoForge and Forge call vanilla's private ranged `IntegerValue.create` through `IntegerRuleInvoker`, a Mixin `@Invoker`, since an access transformer would need a different name format per loader and ForgeGradle 7 doesn't apply one to Forge 52. Each loader loads `ModGameRules` from its entry point, as no registry event covers gamerules.

Everything else is a mechanical rename or format change: `Identifier` to `ResourceLocation`; item and entity ids without `setId`; `EntityType.create(level)`; recipe ingredients as `{"item"}` objects (1.21.1 already has singular data folders and `{"id"}` results, as the 1.2.0 files show); no `items/` model files; tinted spawn eggs; `isNight()`; `getDayTime()` instead of world clocks; `FarmBlock`; `displayClientMessage(…, true)`; bone meal without `BonemealSource`; and loot tag entries in the `{"type": "minecraft:tag"}` form. Fabric's tilling uses `TillableBlockRegistry` as 1.2.0 does. NeoForge's and Forge's use `BlockToolModificationEvent`, as 26.1 already does, on their 1.21.1 buses. The moon phase is read with `level.getMoonPhase()`. Any API with no 1.21.1 equivalent stops the work and goes to the maintainer, never a silent drop.

### Code style

Mixins stay few and stack with other mods' mixins: shared ones use plain Mixin (`@Inject`, `@Invoker`), since Forge 52 ships no MixinExtras, and MixinExtras only appears in the Fabric-only config. Where a loader has a hook, the loader uses it instead of a mixin. The port keeps the 26.1 layout: one job per class, registration only in the `Mod*` classes, Minecraft-free rules in their own classes, and short per-loader copies instead of awkward shared abstractions. Every rewritten piece copies an idiom from vanilla 1.21.1 or a well-known 1.21.1 multiloader mod, starting with MultiLoader-Template's `1.21.1` branch and Jade's 1.21.1 sources. Sources get named in commit messages, not code comments; comments stay as sparse as on 26.1. The diff against 26.1 stays limited to what 1.21.1 forces.

### Old worlds

1.2.0 on 1.21.1 stores pending visits in the Fabric attachment `fertilegrounds:pending_fairy_visits` with `FairyVisit.CODEC`. 2.0.0 keeps that id and codec on Fabric, so a 1.2.0 world keeps its visits. Block, item, and entity ids match. A GameTest loads a visit list encoded the 1.2.0 way and checks it decodes. 1.2.0 shipped no NeoForge or Forge jars, so those loaders have no old data.

### GameTests on the 1.21.1 API

On 1.21.1, a test is a `TestFunction` built from a `@GameTestGenerator` method. Batches run one after another, and a `@BeforeBatch` method runs setup on the level before a batch's tests start (setup at test start comes too late for checks like "the night is dark", since the sky darkens over ticks). `TestCatalog` keeps `@FertileTest` and the 26.1 test ids, and its `tests()` generator builds the `TestFunction`s:

| 26.1 | 1.21.1 |
|---|---|
| `environment` (a `test_environment` JSON) | Batch name. `TestEnvironments` has one `@BeforeBatch` method per environment, which first puts back the rules and time the test server started with, then sets the environment's time, weather, and gamerules |
| `structure` (`fertilegrounds-gametest:empty`) | Structure name. Its `.nbt` gets re-saved with 1.21.1's `DataVersion` (3955) |
| `maxTicks`, `skyAccess` | `TestFunction` fields with the same names |
| `padding` | Dropped: 1.21.1 spaces tests 5 blocks apart. A test whose mobs wander into a neighbor's area gets a bigger empty structure |

Fabric lists `TestCatalog` and `TestEnvironments` under the `fabric-gametest` entrypoint, and NeoForge and Forge register both through `RegisterGameTestsEvent`. NeoForge keeps only generated tests whose structure namespace it allows, so its run allows `fertilegrounds-gametest` and `fertilegrounds`; Forge's name filter stays off, since only the test mod registers tests. Only Fabric API has report and filter options in 1.21.1, so every loader's run passes `fertilegrounds.gametest.report` and `fertilegrounds.gametest.filter`, which `TestCatalog` reads. `run_gametests.py` keeps its interface. Its `ERROR_MARKERS` get re-checked against 1.21.1 sources, and its summary parser gets checked against 1.21.1's test reporter output. The mock player (`TestPlayer`) and `GameTestHelper` calls get fixed against 1.21.1's helper.

## Server safety

The mod still sends no packets, and nothing new is saved. Saved data keeps the same keys and codecs on every loader. Per-player data stays as it is on 26.1: Fabric and NeoForge attachments set to `persistent` and `copyOnDeath`, and Forge keyed by UUID in overworld saved data. `GuideBookTests` runs the rejoin, death, and End-exit checks on 1.21.1 too. Block scans keep their sizes (the Chime Hotel's 17 by 5 by 17), and Jade data sends only what the player could see.

## Look and sound

Nothing new. These are the gaps 1.21.1 forces, each on the maintainer's in-game checklist:

| 26.1 | 1.21.1 |
|---|---|
| Glow layers on hotel and Jarred Fairy models use element `light_emission` | No such field; the layers draw unlit. Hotels already give off light 5 (empty) or 12, so their glow pixels stay bright; item icons draw the glow as `layer1` of `item/generated` |
| Visit heralds: `ParticleTypes.FIREFLY` and `SoundEvents.FIREFLY_BUSH_IDLE` | `ParticleTypes.GLOW` and `SoundEvents.AMETHYST_BLOCK_CHIME` (volume 0.3, pitch 1.5) |
| Empty Fairy Hotel recipe: a Firefly Bush on top | Glow Berries on top (the maintainer approved); its unlock advancement, the guide book page, and `docs/` say so on this branch |
| Grove clears `#minecraft:grass_blocks`, pale moss, and firefly bushes | Those don't exist; `#minecraft:dirt` already holds grass, podzol, and mycelium |
| Rabbits raid crops on any `supports_crops` block | Rabbits only raid vanilla farmland |
| Fairy Speed icon in the `gui` atlas | In the `mob_effects` atlas |

## Risks / Trade-offs

- [Skipping two hops hides differences no table row names] → each compile error gets checked against 1.21.1 sources, and the GameTests catch behavior gaps.
- [The mock player or `GameTestHelper` may lack methods the tests use] → write the missing helper in `TestSupport`, the way vanilla does it.
- [A failed trial costs the work] → the work stays on the local `1.21.1` branch until the gate passes. On failure the maintainer decides, and the branch can go back to `origin/1.21.1`.
