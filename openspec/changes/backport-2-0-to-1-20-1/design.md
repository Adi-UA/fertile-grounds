# Design

## Context

The `1.20.1` branch holds 1.2.0: one Fabric project with 21 classes, built with Loom 1.17-SNAPSHOT and Fabric API 0.92.11+1.20.1. It saves pending visits with the Fabric attachment API (`AttachmentRegistry.createPersistent` in `FairyVisits.java`), so Fabric API 0.92 already has attachments on 1.20.1. The `1.21.1` branch holds 2.0.1: the multiloader layout with 78 main classes in `common`, 33 GameTest files, Java 21, and a build where only Fabric remaps names. See proposal.md for why.

Pins (looked up 2026-11-20 from `meta.fabricmc.net`, `maven.fabricmc.net`, Forge's `promotions_slim.json`, and Modrinth's API): Fabric Loader 0.20.1, Fabric API 0.92.12+1.20.1, Forge 47.4.28 (`1.20.1-recommended`), Jade 11.13.3+forge and 11.13.3+fabric, and Java 17.

## Goals / Non-Goals

**Goals:** every 2.0.1 feature and GameTest on 1.20.1 for Fabric and Forge; 1.2.0 worlds on 1.20.1 Fabric keep their pending visits; Jade on both loaders; and every new divergence goes into the porting table.

**Non-Goals:** a separate NeoForge 1.20.1 jar, behavior changes to suit 1.20.1, and publishing before the gate.

## Decisions

### Start from the 1.21.1 tree in one hop

The port replaces the `1.20.1` tree with `git checkout origin/1.21.1 -- .` and commits on top of the 1.2.0 history, the same move the 1.21.1 backport made from 26.1. 1.21.1 is the closest version with 2.0, and its fixes (glow particle heralds, Glow Berries recipe, `@BeforeBatch` GameTests, plain Mixin, `Platform.intRule`) already match what 1.20.1 has. The table rows dated "1.20.2 to 1.21.1" carry over as they are, and the hop gets the name "1.20.1 (from 1.21.1)".

### Build setup

1.20.1 Forge runs with Forge's own (SRG) names, so the Forge jar needs reobfuscation and a mixin refmap; the 1.21.1 Forge project needs neither. That makes the build the costliest decision in this port, and task 1 settles it before any source work.

| Project | 1.21.1 | 1.20.1 |
|---|---|---|
| `common` | ModDevGradle on NeoForm 1.21.1 | ModDevGradle 2.0.148's `legacyforge` plugin on MCP 1.20.1, as MultiLoader-Template's `1.20.1` branch does |
| `fabric` | Loom remap, Mojang mappings | Same plugin, pinned exactly instead of 1.2.0's `1.17-SNAPSHOT` |
| `forge` | ForgeGradle 7.0.40, no reobf, no refmap | `legacyforge` 2.0.148 with `reobfJar` and its `mixin {}` refmap block, and the Mixin 0.8.7 annotation processor (a release, unlike the template's `0.8.5-SNAPSHOT`); a real Forge 47.4.28 server loaded a test jar's mixin through the refmap |
| `neoforge` | ModDevGradle, NeoForge 21.1 | Removed |

The Java toolchain and the mixin `compatibilityLevel` drop to 17. Three files call Java 21 `SequencedCollection` methods (`getFirst`, `getLast`, `reversed`) and get the Java 17 forms.

### What 1.20.1 lacks

Each row names the 1.21.1 code it hits (counts from `git grep` on `1.21.1`). Every row was confirmed against the 1.20.1 sources jar from `legacyforge`.

| 1.21.1 | 1.20.1 | Hits |
|---|---|---|
| Item components (`ModDataComponents`, `DataComponents`) | Item NBT through `ItemStack.getOrCreateTag()`, with the same keys and values; a JUnit-free helper reads and writes each field | `JarredFairyItem`, `JarredFairies`, `GuideBook`, `ModDataComponents`, 3 GameTests |
| Codecs on saved data and attachments | Codecs exist (DataFixerUpper); saved values go through `NbtOps`, so `FairyVisit.CODEC` and `GroveJob` keep their codecs | 11 files, no rewrite |
| Forge `SavedData.Factory` | `computeIfAbsent(load, create, name)` | `ForgePlatform` |
| Fabric attachments with `copyOnDeath` | Fabric API 0.92 attachments exist (1.2.0 uses them); `copyOnDeath` on `AttachmentRegistry.builder()` | `FabricPlatform` |
| Forge per-player data | Stays UUID-keyed overworld saved data, so Forge needs no capabilities | none |
| `ResourceLocation.fromNamespaceAndPath` | `new ResourceLocation(namespace, path)` | 3 files |
| `Holder<MobEffect>` in effect instances | Plain `MobEffect`, and `MULTIPLY_TOTAL` with a fixed UUID string for the speed modifier | 3 files |
| `Blocks.SHORT_GRASS` | `Blocks.GRASS` | 1 file |
| Jade on Forge | Jade 11 has a Forge build, so Forge loads the client plugin through `@WailaPlugin` | `forge/build.gradle` |
| GameTests: `@GameTestGenerator`, `@BeforeBatch`, `TestFunction` | Same annotations; structures re-stamped with `DataVersion` 3465 from the client jar's `version.json` | `TestCatalog`, `TestEnvironments`, structures |
| `pack_format` 48 | 15, from `version.json` | `pack.mcmeta`, `gradle.properties` |

| Block methods `protected` | Public, so overrides become public; no block codecs, so Enriched Sand drops its `CODEC` | 5 block classes |
| `useItemOn` and `ItemInteractionResult` | One `use` method returning `InteractionResult`, called for each hand | `FairyHotelBlock`, `ChimeHotelBlock` |
| `ItemStack.consume(1, player)` | `shrink(1)`; item use already restores creative stacks, and the diamond on a Chime Hotel checks `instabuild` as vanilla's cake does | 3 files |
| Trigger registry `TRIGGER_TYPES` | `CriteriaTriggers.register` through `CriteriaTriggersInvoker`, called from each entry point | `ModCriteria` |
| Ranged `IntegerValue.create` | None in vanilla, so Forge builds the rule with `create` and swaps in `IntegerArgumentType.integer(min, max)` through `GameRuleTypeAccessor` | `ForgePlatform` |
| `Attributes.SCALE` | None, so the caught fairy's shrink syncs a tick count and `BeanFairyRenderer.scale` draws it; the hitbox keeps full size for those 6 ticks (the maintainer chose this) | `BeanFairy`, `BeanFairyRenderer` |
| Recipe results with components | None, so `guide_book.json` names the `fertilegrounds:guide_book` type, whose serializer reads a shapeless recipe and swaps in the full guide; players get a plain shapeless recipe, so the recipe book shows it (the maintainer chose this) | `GuideBookRecipeSerializer` |
| `item_name` component | A non-italic custom name, so the guide reads the same | `GuideBook` |
| Jade 15 server data for any block | Jade 11 sends server data only for block entities, so the soil chance, "fairy coming", and Chime Hotel crop count lines are left out; the petted line stays (the maintainer chose this) | 4 Jade classes, `JadeDataTests` |
| `skyAccess` on test functions | No barrier roof over tests, so every test has sky and the flag goes | `FertileTest`, 13 tests |
| `GameTestHelper.assertValueEqual`, `getBounds`, `CraftingInput` | `TestSupport` helpers with the same messages; a crafting grid through `TransientCraftingContainer` | test classes |
| Singular data folders | Plural: `recipes/`, `advancements/`, `loot_tables/`, `structures/`, `tags/blocks/`, `tags/items/`; results and icons use `"item"`, and item predicates list `"items"` as arrays | data files, `AssetConsistencyTest` |

Everything not in the table gets checked as compile errors surface. Any API with no 1.20.1 equivalent stops the work and goes to the maintainer, never a silent drop.

### Old worlds

1.2.0 on 1.20.1 stores pending visits in the Fabric attachment `fertilegrounds:pending_fairy_visits` with `FairyVisit.CODEC`, the same id and codec as 1.2.0 on 1.21.1. 2.0.1 keeps both, so `OldSaveTests` carries over unchanged and checks that a 1.2.0 visit list decodes. Block, item, and entity ids match. 1.2.0 shipped no Forge jar for 1.20.1, so Forge has no old data.

## Server safety

The mod still sends no packets. Saved data keeps the same keys and codecs: Fabric attachments set to `persistent` and `copyOnDeath`, and Forge keyed by UUID in overworld saved data. `GuideBookTests` runs the rejoin, death, and End-exit checks on 1.20.1. Item NBT holds only the values the components held, and reading it rejects missing or out-of-range values the same way the component codecs do. Block scans keep their sizes (the Chime Hotel's 17 by 5 by 17), and Jade sends no server data on 1.20.1.

## Look and sound

Nothing new. 1.20.1 keeps every 1.21.1 compromise (unlit glow layers, glow particle and amethyst chime heralds, Glow Berries hotel recipe, rabbits raid only vanilla farmland). Two rows get checked in `runClient` on both loaders:

| Moment | 1.21.1 | 1.20.1 |
|---|---|---|
| Fairy visit herald | `ParticleTypes.GLOW`, `SoundEvents.AMETHYST_BLOCK_CHIME` | Same (both exist since 1.17) |
| Jade tooltip on an occupied hotel | Petted line, crop count on a Chime Hotel | Petted line only, on both loaders |
| Catching a fairy | Shrinks through the scale attribute | Same look through the renderer |

## Risks / Trade-offs

- [The Forge build needs reobf and refmaps, which no current branch has] → task 1 builds an empty Forge jar with one mixin and boots it in `runClient` before any source port starts; if no pinned plugin works, the port stops and goes to the maintainer.
- [Item NBT rewrite touches saved items] → GameTests check that a Jarred Fairy and the Guide Book keep their data through a save and reload.
- [Rows marked "check" may differ] → each gets confirmed in the 1.20.1 sources, and the porting table records the confirmed form.
- [A failed trial costs the work] → the work stays on the local `1.20.1` branch until the gate passes.

### No NeoForge listing

NeoForge's 1.20.1 builds (the last is 47.1.106) are an early fork of Forge 47. `-PneoforgeCompat` builds and runs the GameTests against it through `legacyForge.enable { neoForgeVersion }`, and the mod doesn't load: NeoForge 47.1 reports itself as Forge 47.1.106, below the jar's 47.4.28 minimum, and once that range is lowered it still rejects vanilla `Registry.register` calls inside `RegisterEvent` ("Can not register to a locked registry"), which Forge 47.4.28 accepts. Loading there would mean moving every `Mod*` class to `RegisterEvent.register` suppliers on Forge, a drift from the flagship code, so the maintainer chose a Forge-only listing for now; NeoForge on 1.20.1 is TBD on the roadmap.

Forge also turns GameTests off in production (`ForgeGameTestHooks.isGametestEnabled` checks `!FMLLoader.isProduction()`), so the release jar's check on a real Forge 47.4.28 server is a boot with no mod warnings or mixin errors; the GameTests run in the dev server.

### Forge's test world

Forge 47's test server loads its world like a dedicated server, from `server.properties` and the last run's save, while vanilla's always builds a fresh flat world; crops in a normal world sat underground at light 8. `runGameTestServer` deletes the old world and writes `level-type=minecraft:flat` and `generate-structures=false` first.
