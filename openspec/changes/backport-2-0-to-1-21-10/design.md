# Design

## Context

The `1.21.10` branch holds 1.2.0: one Fabric project built with Loom and Mojang mappings. The `1.21.11` branch holds 2.0.0 in the MultiLoader layout, built for 1.21.11 and already carrying the 2.0.1 fixes (stem mixins, the `"recipes"` reward key). 1.21.10 sits one release before 1.21.11, so the porting table's "1.21.11" rows name what this hop undoes, and the "1.21.2 to 1.21.11" rows already match.

## Goals / Non-Goals

**Goals:** every 2.0.0 feature and GameTest on 1.21.10 for all three loaders; worlds saved by 1.2.0 on 1.21.10 keep their pending visits; new divergences go into the porting table.

**Non-Goals:** behavior changes, publishing before the gate.

## Decisions

### One hop from the 1.21.11 tree

The port checks out `1.21.10` and cherry-picks the three 1.21.11 commits after its 1.2.0 history (`10928c9` the backport, `a0c05af` the recipe and creeper tests, `6590736` the night clock). Both branches share the same 1.2.0 README commits before that point, so the picks only conflict where 1.21.10's 1.2.0 code differs from 1.21.11's; those conflicts resolve toward the 2.0 tree. The jar version is 2.0.0, as on 1.21.11, since the tree already holds the 2.0.1 fixes and 1.21.10 never had a 2.0.0 to patch.

### What 1.21.10 lacks

Known from the porting table before the first build; the compiler and the 1.21.10 sources will add to this list:

- **`Identifier`:** 1.21.10 still names it `ResourceLocation` (`net.minecraft.resources`), so every reference goes back to the old name.
- **Gamerules:** 1.21.10 has `GameRules.Key<IntegerValue>` with camelCase names, not the `GameRule` registry. The mod's gamerules go through 1.21.1's `Platform.intRule` and `boolRule` (Fabric `GameRuleRegistry`, NeoForge and Forge the `IntegerRuleInvoker` mixin), and test environments and `TestSupport` use `randomTickSpeed` and `doDaylightCycle`.
- **Forge 60:** `loaderVersion "[60,)"` in both `mods.toml` files and `data_pack_format` 88 (1.21.10's `version.json`). Forge 60's EventBus, MixinExtras, and `lwjgl-freetype` repository fix get checked against its sources and build, not assumed from Forge 61.
- **Jade 20:** 1.21.10 has Jade 20.x, where 1.21.11 has 21.1.x, so `compat/jade/` gets checked against the Jade 20 API jar for changed provider methods.

### Old worlds

1.2.0 on 1.21.10 stores pending visits in the Fabric attachment `fertilegrounds:pending_fairy_visits` with `FairyVisit.CODEC`, the same id and codec 2.0.0 uses, and `FairyVisitCodecTest` already decodes the 1.2.0 format. 1.2.0 shipped no NeoForge or Forge jars.

## Server safety

Nothing new: no packets, the same saved keys and codecs, per-player data as on 1.21.11 (`GuideBookTests` covers rejoin, death, and leaving the End), bounded block scans.

## Look and sound

1.21.10 has the firefly particle, Firefly Bush, pale moss, and element `light_emission`, so if the build confirms them, the in-game checklist matches 1.21.11's with no compromises.

## Risks / Trade-offs

- [Behavior differences no compile error shows] → the full GameTest run on all three loaders and the client log check per loader.
- [Jade 20 differs from 21.1] → compile `common` against the Jade 20 NeoForge jar and run the Jade GameTests on Fabric and NeoForge.
