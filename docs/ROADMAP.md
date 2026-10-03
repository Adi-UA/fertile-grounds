# Roadmap

Fertile Grounds grows into a cozy, lightweight vanilla+ mod that complements big farming mods instead of copying them. Features read shared tags so other mods' crops and flowers work without extra code, optional integrations load only when the other mod is present, and nothing changes how the game looks beyond a few new items and blocks. Every design lives in `openspec/changes/<name>/` (proposal, WHEN/THEN specs, design, tasks with verification); this file only orders them.

## 1.3.0

One big release. It targets Minecraft 1.21.11, 26.1, 26.2, and 26.3, where most downloads come from; 1.20.1, 1.21.1, and 1.21.10 stay on 1.2.0. Every change below is built and verified on 26.3 (`main`) first. Backporting starts only after all of them work on 26.3, then goes one version at a time (26.2, 26.1, 1.21.11) with `.claude/skills/porting-minecraft-version/SKILL.md`.

### Build order

Each step depends on the ones before it.

| # | Change | What it adds | New art |
|---|---|---|---|
| 1 | `add-regression-evals` | JUnit asset checks, smoke test suites, client log check, regressions for every past bug | None |
| 2 | `add-fairy-tags-and-gamerules` | `fairy_crops` and `fairy_flowers` tags, gamerules for existing numbers | None |
| 3 | `add-fertilizer` | Fertilizer upgrades soil to Super Enriched in place, by hand or dispenser | Fertilizer item |
| 4 | `add-visit-attraction` | Flowers and beehives raise visit odds; Harvest Moon triples them on full moons | None |
| 5 | `add-visit-feedback` | Arrival chime, dawn message, advancement tab, `fairy_visit` trigger | None |
| 6 | `add-fairy-gifts` | Occasional gifts scaled by farm size, with cooldown and Harvest Moon tier boost | None |
| 7 | `add-fairy-jar` | Catch a fairy; 7-day jar, flower feeding, friendship (8 flowers over 4+ days) makes it permanent; expired fairies fly away | Filled jar (green lantern), empty jar (Bottle o' Enchanting look) |
| 8 | `add-jar-garden-effects` | Jar boosts nearby crops, bees help, creepers avoid it, no trampling nearby | None |
| 9 | `add-redstone-integration` | Comparator output for jars and soil; dispensers feed jars (adds days, not friendship) | None |
| 10 | `add-wandering-trader-trades` | Trader sells Fairy Dust and Fertilizer | None |
| 11 | `add-jade-integration` | Optional Jade tooltips for soil, pending visits, and jars | None |
| 12 | `add-guide-book` | Vanilla written guide on first join, craftable copy | None |
| 13 | `add-translations` | Translation-ready text and contributor guide | None |

The guide book comes late so its pages describe final behavior, and translations come last so every string exists.

### Release steps

After step 13 works on 26.3: backport to 26.2, 26.1, then 1.21.11, running the full verification suite on each; bump `mod_version` to 1.3.0 on all four branches; then publish to GitHub, Modrinth, and CurseForge with the release skills.

## Considered and skipped

| Idea | Why skipped |
|---|---|
| Hydrated soil tier | Sprinklerz, Moisturization, and Farming for Blockheads already cover watering |
| Blessed harvest bonus drops | Needs per-crop tracking and fights auto-harvesters |
| Fairy variants, new soils, new biomes | Kept to one fairy, the existing soil tiers, and vanilla biomes |
| Fairy Rings, biome gifts | Rejected with the no-new-biomes rule |
| Wider fairy reach (berries, cocoa, saplings) | Crops are enough for now; packs can extend `fairy_crops` |
| Sculk sensors detect fairy arrival | Not needed for now |
| Seasons-aware fairies | Deferred; revisit with Serene Seasons later |
| Recipe viewer info pages | The guide book covers it |
| Fairy Wing, gifts from escaping fairies | Replaced by flower friendship |
| Patchouli guide | No support for 1.21.11, 26.2, or 26.3 |
| Resource crops, machines, chunk-loading wards | Out of scope for a lightweight vanilla+ mod |

## Next steps

Start `add-regression-evals` on `main` with `/opsx:apply`.
