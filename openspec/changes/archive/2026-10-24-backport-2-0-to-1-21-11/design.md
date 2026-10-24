# Design

## Context

The `1.21.11` branch holds 1.2.0: one Fabric project built with Loom and Mojang mappings. The `26.1` branch holds 2.0.0 in the MultiLoader layout. 1.21.11 sits one release before 26.1, so most of the 26.1 code compiles unchanged: it already has `Identifier`, the `GameRule` registry with snake_case names, render states, item ids, data-driven test instances and environments, `light_emission` on model elements, the firefly particle, and pale moss. The `common` project had 35 compile errors on the first build, against several hundred for 1.21.1.

## Goals / Non-Goals

**Goals:** every 2.0.0 feature and GameTest on 1.21.11 for all three loaders; worlds saved by 1.2.0 on 1.21.11 keep their pending visits; new divergences go into the porting table.

**Non-Goals:** 1.21.10, behavior changes, publishing before the gate.

## Decisions

### One hop from the 26.1 tree

The port checks out `1.21.11`, replaces its tree with `origin/26.1`, and commits on top of the 1.2.0 history, as the 1.21.1 port did. This time the hop is a real single step, so the table rows it adds read "1.21.11" and need no "(from 26.1)" note.

### Build setup

The build files come from the `1.21.1` branch, since 1.21.11 also ships obfuscated: Loom's remapping plugin with `loom.officialMojangMappings()` and `mod*` configurations on Fabric, `common` compiled against Jade's NeoForge jar (Mojang names), and Java 21. NeoForge 21.11 runs on ModDevGradle 2.0.148 like 26.1. Forge 61 already uses EventBus 7 and bundles MixinExtras (0.5.4), so its 26.1 build and shared mixins carry over; it only needs the 1.21.1 fix that keeps Maven Central from claiming `lwjgl-freetype`, whose Apple Silicon build is only on Mojang's library maven.

### What 1.21.11 lacks

Each of these follows vanilla 1.21.11 or the 1.21.1 branch, checked against the 1.21.11 sources:

- **Renames:** `FarmBlock`, `getDayTime()`, `displayClientMessage(…, true)`, `Recipe.assemble(input, registries)`, `Player.interactOn(entity, hand)`, and `server.overworld().getGameRules()` for Jade's soil chance, since 1.21.11's `MinecraftServer` has no `getGameRules`.
- **Wandering Trader offers:** no `villager_trade` data. Vanilla's `WANDERING_TRADER_TRADES` holds three pools, and index 1 is the rare one (15 offers, 2 picked). `ItemsForEmeralds` is package-private in vanilla (Fabric API widens it, so Fabric's decompiled sources show it public), so `WanderingTraderOffers` keeps 1.21.1's `ItemListing` lambdas with the 1.21.11 three-argument `getOffer`. Fabric adds them through `addOffersToPool(SELL_SPECIAL_ITEMS_POOL, …)`, NeoForge through `getRareTrades()`, and Forge through `getPools().get(1)`. All three write back into vanilla's list, so `TraderTests` reads it on every loader.
- **Crops on the farmland tiers:** vanilla crops check `Blocks.FARMLAND`, and there are no `supports_crops` or `grows_crops` tags, so those files go and 1.21.1's shared `CropBlockMixin` and `PitcherCropBlockMixin` come back. Melon and pumpkin stems check `Blocks.FARMLAND` too (26.x reads `supports_crops` through `supports_stem_crops`), so `StemBlockMixin` and `AttachedStemBlockMixin` do the same for them, which the 1.21.1 port missed; `SoilRegressionTests.stemsStandOnModFarmland` failed on Fabric before them. NeoForge already accepts any `FarmBlock` for placement and growth, and Forge's default `isFertile` does too, so only Fabric needs the growth mixin (`fertilegrounds.fabric.mixins.json`). 1.21.1's `isFertile` override isn't needed. Trampling stays on 26.1's shared `FarmlandBlockMixin`, renamed to `FarmBlock`.
- **Fabric API 0.141:** `FabricItemGroup`, `PointOfInterestHelper`, `END_WORLD_TICK`, and `EntityModelLayerRegistry`, the names 26.1's Fabric API renamed.
- **Forge saved data:** `SavedDataType` takes a file name, not an id, so Forge stores `fertilegrounds_<path>.dat`, as on 1.21.1.
- **Data:** advancement rewards use `"recipes"`, as vanilla does on every version (the `26.1` and `26.2` branches name them `"recipe"`, which vanilla skips, so those recipe-book unlocks never fire there; `AssetConsistencyTest` now checks the key on this branch); the grove tag drops `#minecraft:grass_blocks`, since 1.21.11's `#minecraft:dirt` still holds grass, podzol, and mycelium; the `night` test environment uses `time_of_day` instead of `clock_time`.
- **Test spacing:** 1.21.11's test instances have no `padding`, so tests sit 5 blocks apart. As on 1.21.1, `FertileTest` drops the field, `TestSupport.fairies` looks only 4 blocks to each side, and `ShyFlightTests` keeps the fairy it spawned instead of searching for one.

### Old worlds

1.2.0 on 1.21.11 stores pending visits in the Fabric attachment `fertilegrounds:pending_fairy_visits` with `FairyVisit.CODEC`, the same id and codec 2.0.0 uses, and `FairyVisitCodecTest` already decodes the 1.2.0 format. 1.2.0 shipped no NeoForge or Forge jars.

### Release run

After the port passed, three bugs surfaced and the release changed shape (maintainer decisions, 2026-10-23):

- **Stems on mod farmland** (1.21.1 lacked the stem mixins): fixed on 1.21.1 and shipped as 2.0.1 there, as a new upload, since CurseForge can't replace a file.
- **Recipe rewards** (the 26.1 and 26.2 advancements said `"recipe"`, which vanilla skips): fixed and shipped as 2.0.1 on 26.1 and 26.2. 26.3 never had it.
- **Creeper test** (it waited for more than 7 blocks, and the goal stops at 6): a test-only fix on every branch, so it needs no release.
- 1.21.11 went out as 2.0.0 without the maintainer's in-game playtest, by the maintainer's choice.
- New guards on every branch: `AdvancementTests.recipeAdvancementsUnlockTheirRecipes`, `SoilRegressionTests.stemsStandOnModFarmland`, and a rewards-key check in `AssetConsistencyTest`; the test-instance generator now starts from an empty folder, since a stale file from a renamed test failed a run.

## Server safety

Nothing new: no packets, the same saved keys and codecs, per-player data as on 26.1 (`GuideBookTests` covers rejoin, death, and leaving the End), bounded block scans.

## Look and sound

No gaps: 1.21.11 has every model, particle, sound, and block 26.1 uses, so the in-game checklist covers the usual looks and sounds, not compromises.

## Risks / Trade-offs

- [Behavior differences no compile error shows] → the full GameTest run on all three loaders and the client log check per loader.
