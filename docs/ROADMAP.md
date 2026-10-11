# Roadmap

Fertile Grounds is a cozy, lightweight vanilla+ mod that complements big farming mods instead of copying them. Features read shared tags so other mods' crops and flowers work without extra code, optional integrations load only when the other mod is present, and nothing changes how the game looks beyond a few new items and blocks. Every feature also has to look and sound alive: each design carries a "Look and sound" table of vanilla sounds, particles, and glow, checked in `runClient`. Each feature's design lives in `openspec/`: what the mod does today in `openspec/specs/`, planned work in `openspec/changes/`, and finished designs in `openspec/changes/archive/`.

## In 2.0

Version 2.0 runs on Minecraft 26.1, 26.2, and 26.3. Minecraft 1.21.11 and older stay on 1.2.0, since porting further back means an obfuscated build setup and rewriting four features.

| Feature | Spec |
|---|---|
| Enriched soils and Fertilizer | `soil-fertilizer` |
| Bean Fairy visits: odds, Harvest Moon, the visit scene, gifts | `fairy-visit-chance`, `fairy-visit-behavior`, `fairy-visit-feedback`, `fairy-gifts` |
| Jarred Fairies, Fairy Hotels, and their effects | `fairy-hotel`, `fairy-hotel-effects` |
| Chime Hotels and redstone output | `chime-hotel`, `redstone-signals` |
| Fairy Grove | `fairy-grove` |
| Trader trades, Jade tooltips, guide book, advancements | `trader-offers`, `jade-tooltips`, `guide-book`, `mod-advancements` |
| Tags and gamerules for every number | `fairy-content-tags`, `tuning-gamerules` |
| Unit tests, GameTests, and the client log check | `verification-suite`, `in-world-tests` |

## Next

| Change | What it adds |
|---|---|
| `add-translations` | A Crowdin project so players can translate the mod without Git |

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
| Blighted Dirt (spreading cursed soil from witches, craftable for sabotage) | Too punishing for a cozy farming mod |
