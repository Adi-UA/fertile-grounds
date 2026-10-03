# Proposal

## Why

Every 1.3.0 feature needs to know which blocks count as crops and flowers, and modpack makers need to tune balance without editing code. Today crop detection hardcodes `#minecraft:crops` and every chance is a Java constant, so other mods' content and server owners' preferences can't reach either one.

## What Changes

- Add the `fertilegrounds:fairy_crops` block tag (default `#minecraft:crops`) and the `fertilegrounds:fairy_flowers` block and item tags (default `#minecraft:small_flowers`).
- Fairy visits and Fairy Dust read `fairy_crops` instead of `#minecraft:crops`.
- Add gamerules for the existing tunable numbers: natural visit chance and both soil boost chances. Later changes add their own gamerules the same way.

Out of scope: new crops or flowers in the default tags (they stay equal to vanilla's), and a config file or config screen.

New art: none.

## Capabilities

### New Capabilities

- `fairy-content-tags`: The tags that decide which blocks and items count as fairy crops and fairy flowers.
- `tuning-gamerules`: The gamerules servers and modpacks use to tune the mod's numbers.

### Modified Capabilities

None.

## Impact

`CropGrowthUtil`, `FairyTiming`, `FairyVisitScheduler`, both boosted soil blocks, new tag JSON under `data/fertilegrounds/tags/`, and a new gamerule registration class. The smoke test gains a tag check and a gamerule check.
