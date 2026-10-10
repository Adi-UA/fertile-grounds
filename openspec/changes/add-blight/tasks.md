# Tasks

## 1. Rules

- [x] 1.1 Add `BlightRules.spreadChance` with JUnit tests for plain, Enriched, and Super Enriched targets and a 0% base
- [x] 1.2 Add the `blight_spread_chance` and `witch_blight_chance` gamerules; verify their defaults in a GameTest

## 2. Block

- [ ] 2.1 Ask the user for the Blighted Dirt texture (placeholder and guide `art_source/blighted_dirt_block_guide.png` made from vanilla dirt); verify no missing-texture warnings with `client_log_check.py`
- [x] 2.2 Register Blighted Dirt with its block item, loot table, model, blockstate, lang keys, creative tab entry, and the `blightable`, `supports_dry_vegetation`, and `mineable/shovel` tags; verify a `BlightTests` GameTest breaks it and gets it back, and a mock player's hoe leaves it unchanged

## 3. Spreading and withering

- [x] 3.1 Add the random tick spread; verify GameTests with the chance at 100 see farmland, grass, and sand turn, stone and a dirt path stay, and nothing spreads at 0
- [x] 3.2 Add withering; verify a GameTest sees ripe wheat on blighted farmland become a dead bush with no item entities nearby

## 4. Starting and curing

- [x] 4.1 Add the witch curse; verify GameTests (in their own environment with `witch_blight_chance` 100) see a mock player's kill blight the grass and send the message, a kill over stone change nothing and send no message, and a non-player kill change nothing
- [x] 4.2 Add the recipe and its unlock advancement; verify a GameTest crafts it and `AssetConsistencyTest` passes
- [x] 4.3 Add the Fertilizer cure; verify GameTests for a mock player's use and a powered dispenser each turn Blighted Dirt into dirt and use one Fertilizer
- [x] 4.4 Add the fairy cure; verify GameTests for a Fairy Dust visit and a natural visit each turn Blighted Dirt in the patch into dirt

## 5. Polish and docs

- [ ] 5.1 Add every row of the Look and sound table; check them in `runClient` and add them, plus "watch blight creep for a few minutes", to the in-game checklist
- [x] 5.2 Add a guide book page in the Lexica Botania voice and a Blight section in `docs/DETAILS.md`; add a README line; verify `AssetConsistencyTest` passes
