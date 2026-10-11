# Roadmap

Fertile Grounds grows into a cozy, lightweight vanilla+ mod that complements big farming mods instead of copying them. Features read shared tags so other mods' crops and flowers work without extra code, optional integrations load only when the other mod is present, and nothing changes how the game looks beyond a few new items and blocks. Every feature also has to look and sound alive: each design carries a "Look and sound" table of vanilla sounds, particles, and glow, checked in `runClient`. Every design lives in `openspec/changes/<name>/` (proposal, WHEN/THEN specs, design, tasks with verification), moving to `openspec/changes/archive/` once done, with its specs merged into `openspec/specs/`; this file only orders them.

## 2.0.0

One big release, numbered 2.0.0 because Fairy Hotels and the reworked fairy visit change how the mod plays. It targets Minecraft 26.1, 26.2, and 26.3, about 57% of Fabric players; 1.21.11 and older stay on 1.2.0, because a 1.21.11 port needs an obfuscated build setup and four features rewritten. Every change below is built and verified on 26.3 (`main`) first. Backporting starts only after all of them work on 26.3, then goes one version at a time (26.2, then 26.1) with `.claude/skills/porting-minecraft-version/SKILL.md`.

### Build order

Each step depends on the ones before it.

| # | Change | What it adds | New art |
|---|---|---|---|
| 1 | `add-regression-evals` | JUnit asset checks, smoke test suites, client log check, regressions for every past bug | None |
| 2 | `add-fairy-tags-and-gamerules` | `fairy_crops` and `fairy_flowers` tags (flowers also read `c:flowers/small`), gamerules for existing numbers | None |
| 3 | `add-fertilizer` | Fertilizer upgrades soil to Super Enriched in place, by hand or dispenser | Fertilizer item |
| 4 | `add-visit-attraction` | Flowers and beehives raise visit odds; Harvest Moon triples them on full moons | None |
| 5 | `add-visit-feedback` | Arrival chime, dawn message, advancement tab, `fairy_visit` trigger | None |
| 6 | `add-fairy-visit-polish` | Firefly heralds, growth dance with ripple, 60-second linger with Allay hum, shy fairies, no dive into the ground | None |
| 7 | `add-fairy-gifts` | Occasional gifts scaled by farm size, with cooldown and Harvest Moon tier boost | None |
| 8 | `add-gametest-suite` | Fabric GameTests with mock players replace the smoke test, so player actions, messages, gifts, and advancements are checked on every build | None |
| 9 | `add-fairy-jar` | Catch a lingering fairy in a bottle; it slips away after 3 days leaving a flower unless moved into a Fairy Hotel (7 flowers, firefly bush, lantern), where it stays forever; hotels are portable | Jarred Fairy, Empty Fairy Hotel, Occupied Fairy Hotel (each with an emissive layer) |
| 10 | `add-jar-garden-effects` | Occupied hotels boost crops, add honey to hives, give nearby players 10% Fairy Speed, scare creepers, stop trampling; brush once a day to pet for a bigger boost | None |
| 11 | `add-chime-hotel` | Occupied hotel + note block + spyglass; chimes once when 80% of nearby crops are grown | Chime Hotel (with an emissive layer) |
| 12 | `add-redstone-integration` | Comparator pulse when a Chime Hotel chimes; comparator output for soil tiers | None |
| 13 | `add-wandering-trader-trades` | Trader sells Fairy Dust and Fertilizer | None |
| 14 | `add-jade-integration` | Optional Jade tooltips for soil, pending visits, hotel petting, and Chime Hotel crop counts | None |
| 15 | `add-guide-book` | Vanilla written guide on first join, craftable copy | None |
| 16 | `add-translations` | Translation-ready text and contributor guide | None |
| 17 | `add-fairy-grove` | Diamond on a Chime Hotel shrine: fairies level a 35x35 area into lit, watered farm plots with flower borders, hives, and leaf hedges | None |

Visit polish comes before hotels because catching needs the linger window. The GameTest suite comes before hotels so their tests can use mock players from the start. The guide book comes late so its pages describe final behavior, and translations come next so every string so far has a key. The grove comes last because it builds on Chime Hotels; it adds its own lang keys and guide page.

### Release steps

`prepare-2-0-release` holds them: after step 17 works on 26.3, backport to 26.2, then 26.1, running the full verification suite on each; bump `mod_version` to 2.0.0 on all three branches; build the demo world; swap in the user's art; then publish to GitHub, Modrinth, and CurseForge with the release skills.

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
| Recipe viewer info pages, Mod Menu screen | The guide book and gamerules cover them |
| Fairy Wing, gifts from escaping fairies | A slipping-away fairy leaves only a flower |
| Jar friendship (8 flowers over 4 days), 7-day jars | Replaced by Fairy Hotels |
| Patchouli guide | No support for 26.2 or 26.3 |
| Resource crops, machines, chunk-loading wards, seed sorting, Tidy Hotel | Out of scope for a lightweight vanilla+ mod; moving player items risks loss |
| Seed Pouch | Overlaps vanilla bundles |
| Waving, note block echo, thank-you notes, cookies for fairies, lit-candle traces | Fairies stay a little mysterious |
| Returning named fairies, fairy painting, banner pattern | Long-term bonds and keepsakes deferred |
| Per-farm opt-out, Fairy Dust on flowers, fairy stats | Not wanted, or low value |
| Spyglass watching, sniffer-diggable soil, ancient flowers counting double | Not picked; spyglass went to the Chime Hotel instead |
| Lean proofs of the rule classes | Dropped; JUnit and GameTests cover the rules |
| Blighted Dirt (spreading cursed soil from witches, craftable for sabotage) | Built, then removed before release (2026-10-10): the user wants the mod to stay cozy |

## Next steps

All 17 changes are built on `main`, backported to 26.2 and 26.1 at 2.0.0, and archived except `add-translations` (waiting on a Crowdin project). The user's textures are in, and `scripts/demo/build_demo_world.py` builds the demo and test world. What's left before 2.0.0: the user's in-game check of the Fairy Grove and the demo world, then publishing (see `prepare-2-0-release`).
