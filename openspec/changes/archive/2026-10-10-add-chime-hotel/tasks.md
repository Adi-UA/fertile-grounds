# Tasks

## 1. Rules

- [x] 1.1 Add `ChimeRules` with JUnit tests for every spec scenario (ready at 80%, quiet while ready, re-arm after harvest, too few crops)
- [x] 1.2 Add the `chime_ready_percent` gamerule; verify its default in a GameTest

## 2. Block

- [x] 2.1 Ask the user for the Chime Hotel texture and emissive layer (offer a UV guide); verify no missing-texture warnings in the `runClient` log
- [x] 2.2 Register the Chime Hotel block, item, loot table, POI membership, recipe, sound event, and lang keys; verify a GameTest crafts it from an occupied hotel, places it, and a `HotelEffectTests` check sees it boost crops

## 3. Chime

- [x] 3.1 Add the scheduled tick and chime; verify a `ChimeHotelTests` GameTest plants a fully grown field and sees `READY` turn true once, then false after breaking the crops
- [x] 3.2 Add every row of the Look and sound table, and "wait for a field to ripen near a Chime Hotel", to the in-game checklist
- [x] 3.3 Document the Chime Hotel in `README.md` and add any scheduled tick or sound difference to the porting divergence table
