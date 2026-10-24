# Roadmap

Fertile Grounds is a cozy, lightweight vanilla+ mod that complements big farming mods instead of copying them. Features read shared tags so other mods' crops and flowers work without extra code, optional integrations load only when the other mod is present, and nothing changes how the game looks beyond a few new items and blocks. Every feature also has to look and sound alive: each design carries a "Look and sound" table of vanilla sounds, particles, and glow, checked in `runClient`. Each feature's design lives in `openspec/`: what the mod does today in `openspec/specs/`, planned work in `openspec/changes/`, and finished designs in `openspec/changes/archive/`.

## In 2.0

Version 2.0 runs on Minecraft 26.1, 26.2, 26.3, and 1.21.1, on Fabric, NeoForge, and Forge. 1.21.10 and 1.21.11 get 2.0.0 next, more slowly, since each older version means an obfuscated build setup and a few compromises. Versions from 1.21.1 up to 26.1 get 2.0.0 only; 1.20.1 stays on 1.2.0.

| Feature | Spec |
|---|---|
| Enriched soils and Fertilizer | `soil-fertilizer` |
| Bean Fairy visits: odds, Harvest Moon, the visit scene, gifts | `fairy-visit-chance`, `fairy-visit-behavior`, `fairy-visit-feedback`, `fairy-gifts` |
| Jarred Fairies, Fairy Hotels, and their effects | `fairy-hotel`, `fairy-hotel-effects` |
| Chime Hotels and redstone output | `chime-hotel`, `redstone-signals` |
| Fairy Grove | `fairy-grove` |
| Trader trades, Jade tooltips, guide book, advancements | `trader-offers`, `jade-tooltips`, `guide-book`, `mod-advancements` |
| Tags and gamerules for every number | `fairy-content-tags`, `tuning-gamerules` |
| NeoForge and Forge jars | `forge-loaders` |
| Unit tests, GameTests, and the client log check | `verification-suite`, `in-world-tests` |

## Next

| Change | What it adds |
|---|---|
| `add-translations` | A Crowdin project so players can translate the mod without Git |
