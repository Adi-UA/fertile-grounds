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
| `fabric` | Loom, no mappings, `implementation` | Loom with `loom.officialMojangMappings()`, `modImplementation`, `modCompileOnly`/`modLocalRuntime` for Jade |
| `neoforge` | ModDevGradle, NeoForge 26.1.2.116 | Same plugin, NeoForge 21.1.264, `loaderVersion "[4,)"` |
| `forge` | ForgeGradle 7.0.40, EventBus 7 | ForgeGradle 7.0.40 if it can set up 52.1.20, else ForgeGradle 6 pinned exactly (template: `[6.0.24,6.2)`), with `reobf = false`; EventBus 6 (`MinecraftForge.EVENT_BUS`, `@SubscribeEvent`) |

Mixins keep the manifest `MixinConfigs` entry on Forge. The template's `org.spongepowered.mixin` `0.7-SNAPSHOT` plugin stays out, since AGENTS.md forbids snapshot dependencies, and no refmap is needed when Forge runs with Mojang names. The Java toolchain and the mixin config's `compatibilityLevel` go to 21. Loom keeps the Jade plugin off the dev server's classpath, so `-PnoJade` behaves the same as on 26.x.

### Version-specific code

The table rows below 26.1 say what changes. Each one gets the 1.21.1 form, copied from vanilla or 1.2.0 code where they already have it. The four features that need rewriting, not renaming, are these:

- **Wandering Trader offers:** 1.21.1 has no data-driven villager trades. Fabric uses `TradeOfferHelper.registerWanderingTraderOffers`, and NeoForge and Forge use their `WandererTradesEvent`. The 26.1 `villager_trade` JSON values (price, count, uses) move into one shared `common` table that all three read.
- **Farmland tiers:** `FarmBlock` hardcodes its revert to dirt, so the mod tiers bring back 1.2.0's own moisture and trample handling, and `FarmlandBlockMixin` wraps the static `turnToDirt` (table row "Farmland trample call").
- **Bean Fairy rendering:** before render states, `BeanFairyRenderer` extends `MobRenderer<BeanFairy, BeanFairyModel>`, and the model becomes a `HierarchicalModel` whose `setupAnim` gets the entity and resets the pose itself. The `texOffs` values stay as they are, so no texture moves.
- **Saved data and gamerules:** 1.21.1 has no codec `SavedDataType` or `GameRule` registry. Forge's `LevelData` wraps a `SavedData` subclass that encodes the same codec into NBT (`SavedData.Factory`). Gamerules go through `GameRules.register` with `IntegerValue`/`BooleanValue` and keep the snake_case names (`fairy_visit_chance`) the docs use, since 1.21.1 accepts any rule name. Fabric uses `GameRuleRegistry`, so rule ranges stay enforced. NeoForge and Forge need a way to enforce ranges, and the port picks the one their sources support.

Everything else is a mechanical rename or format change: `Identifier` to `ResourceLocation`; item and entity ids without `setId`; `EntityType.create(level)`; plural data folders (`recipes/`, `advancements/`, `loot_tables/`, `tags/blocks/`); recipe `{"item"}` ingredients and `{"id"}` results; no `items/` model files; tinted spawn eggs; `isNight()`; `getDayTime()` instead of world clocks; `FarmBlock`; `displayClientMessage(…, true)`; bone meal without `BonemealSource`; and loot tag entries in the `{"type": "minecraft:tag"}` form. Fabric's tilling uses `TillableBlockRegistry` as 1.2.0 does. NeoForge's and Forge's use `BlockToolModificationEvent`, as 26.1 already does, on their 1.21.1 buses. The moon phase is read with `level.getMoonPhase()`. Any API with no 1.21.1 equivalent stops the work and goes to the maintainer, never a silent drop.

### Code style

The port keeps the 26.1 layout: one job per class, registration only in the `Mod*` classes, Minecraft-free rules in their own classes, and short per-loader copies instead of awkward shared abstractions. Every rewritten piece copies an idiom from vanilla 1.21.1 or a well-known 1.21.1 multiloader mod, starting with MultiLoader-Template's `1.21.1` branch and Jade's 1.21.1 sources. Sources get named in commit messages, not code comments; comments stay as sparse as on 26.1. The diff against 26.1 stays limited to what 1.21.1 forces.

### Old worlds

1.2.0 on 1.21.1 stores pending visits in the Fabric attachment `fertilegrounds:pending_fairy_visits` with `FairyVisit.CODEC`. 2.0.0 keeps that id and codec on Fabric, so a 1.2.0 world keeps its visits. Block, item, and entity ids match. A GameTest loads a visit list encoded the 1.2.0 way and checks it decodes. 1.2.0 shipped no NeoForge or Forge jars, so those loaders have no old data.

### GameTests on the 1.21.1 API

On 1.21.1, a test is a `TestFunction` built from a `@GameTestGenerator` method. Batches run one after another, and `@BeforeBatch` runs setup on the level. `TestCatalog` keeps `@FertileTest` and the 26.1 test ids, but builds `TestFunction`s instead of writing test-instance files:

| 26.1 | 1.21.1 |
|---|---|
| `environment` (a `test_environment` JSON) | Batch name. The batch's `@BeforeBatch` sets the same time, weather, and gamerules from one Java table that replaces the JSON files |
| `structure` (`fertilegrounds-gametest:empty`) | Structure name. Its `.nbt` gets re-saved with 1.21.1's `DataVersion` (3955) |
| `maxTicks`, `skyAccess`, `padding` | `TestFunction` fields with the same names (padding comes from the loader's runner, so the port checks it there) |

Fabric lists the generator class under the `fabric-gametest` entrypoint. NeoForge and Forge register it through `RegisterGameTestsEvent`. `run_gametests.py` keeps its interface. Its `ERROR_MARKERS` get re-checked against 1.21.1 sources, and its summary parser gets checked against 1.21.1's test reporter output. The mock player (`TestPlayer`) and `GameTestHelper` calls get fixed against 1.21.1's helper.

## Server safety

The mod still sends no packets, and nothing new is saved. Saved data keeps the same keys and codecs on every loader. Per-player data stays as it is on 26.1: Fabric and NeoForge attachments set to `persistent` and `copyOnDeath`, and Forge keyed by UUID in overworld saved data. `GuideBookTests` runs the rejoin, death, and End-exit checks on 1.21.1 too. Block scans keep their sizes (the Chime Hotel's 17 by 5 by 17), and Jade data sends only what the player could see.

## Look and sound

Nothing new. The 26.1 sounds and particles all exist in 1.21.1 except any the port finds missing. A missing one gets the closest vanilla 1.21.1 sound or particle, and it's listed for the maintainer's in-game check.

## Risks / Trade-offs

- [Skipping two hops hides differences no table row names] → each compile error gets checked against 1.21.1 sources, and the GameTests catch behavior gaps.
- [ForgeGradle 7 may not set up 52.1.20] → fall back to ForgeGradle 6 pinned exactly, and record which one worked in the table.
- [The mock player or `GameTestHelper` may lack methods the tests use] → write the missing helper in `TestSupport`, the way vanilla does it.
- [A failed trial costs the work] → the work stays on the local `1.21.1` branch until the gate passes. On failure the maintainer decides, and the branch can go back to `origin/1.21.1`.
