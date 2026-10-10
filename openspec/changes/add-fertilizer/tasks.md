# Tasks

## 1. Item and recipe

- [ ] 1.1 Ask the user for the hand-drawn Fertilizer texture; verify the PNG sits in `textures/item/` and loads with no missing-texture warning in the `runClient` log
- [x] 1.2 Register `FERTILIZER` in `ModItems`, add it to the creative tab, add lang keys; verify `client_log_check.py` reports no warnings, and add "Fertilizer shows in the creative tab" to the user's in-game checklist
- [x] 1.3 Add the recipe and recipe advancement; verify the recipe loads with no parse error and its result count is 4, and add an in-game checklist item for the mixed-flower craft

## 2. In-place upgrade

- [x] 2.1 Add `SoilUpgrades` with JUnit tests for every mapping and for "no upgrade" on Super Enriched blocks
- [x] 2.2 Add `FertilizerItem.useOn` with sound and particles; verify the smoke test places farmland with wheat, applies Fertilizer through a powered dispenser (which calls the same `FertilizerItem.upgrade` as a player's use), and sees Super Enriched Farmland with the same wheat age and moisture and one Fertilizer used; add the by-hand use to the in-game checklist, since the smoke server has no player

## 3. Dispenser

- [x] 3.1 Register the dispenser behavior; verify the smoke test powers a dispenser facing farmland and sees the upgrade and one Fertilizer gone
- [ ] 3.2 Update `README.md` with Fertilizer; verify the recipe text matches the JSON
- [ ] 3.3 Check every row of the design's Look and sound table in `runClient` and add it to the in-game checklist
