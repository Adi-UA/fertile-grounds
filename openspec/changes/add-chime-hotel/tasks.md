# Tasks

## 1. Rules

- [ ] 1.1 Add `ChimeRules` with JUnit tests for every spec scenario (ready at 80%, quiet while ready, re-arm after harvest, too few crops)
- [ ] 1.2 Add the `chime_ready_percent` gamerule; verify its default in a GameTest

## 2. Block

- [ ] 2.1 Ask the user for the Chime Hotel texture and emissive layer (offer a UV guide); verify no missing-texture warnings in the `runClient` log
- [ ] 2.2 Register the Chime Hotel block, item, loot table, POI membership, recipe, sound event, and lang keys; verify a GameTest crafts it from an occupied hotel, places it, and a `HotelEffectTests` check sees it boost crops

## 3. Chime

- [ ] 3.1 Add the scheduled tick and chime; verify a `ChimeHotelTests` GameTest grows a field with `/fill` to full age and sees `READY` turn true once, then false after breaking the crops
- [ ] 3.2 Check every row of the Look and sound table in `runClient` and add "wait for a field to ripen near a Chime Hotel" to the in-game checklist
- [ ] 3.3 Document the Chime Hotel in `README.md` and add any scheduled tick or sound difference to the porting divergence table
