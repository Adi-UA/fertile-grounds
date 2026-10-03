# Tasks

## 1. Item and recipe

- [ ] 1.1 Ask the user for the hand-drawn Fertilizer texture; verify the PNG sits in `textures/item/` and loads with no missing-texture warning in the `runClient` log
- [ ] 1.2 Register `FERTILIZER` in `ModItems`, add it to the creative tab, add lang keys; verify it shows in the creative tab in `runClient`
- [ ] 1.3 Add the recipe and recipe advancement; verify the recipe loads with no parse error and its result count is 4, and add an in-game checklist item for the mixed-flower craft

## 2. In-place upgrade

- [ ] 2.1 Add `SoilUpgrades` with JUnit tests for every mapping and for "no upgrade" on Super Enriched blocks
- [ ] 2.2 Add `FertilizerItem.useOn` with sound and particles; verify the smoke test places farmland with wheat, applies Fertilizer through a fake use, and sees Super Enriched Farmland with the same wheat age and moisture

## 3. Dispenser

- [ ] 3.1 Register the dispenser behavior; verify the smoke test powers a dispenser facing farmland and sees the upgrade and one Fertilizer gone
- [ ] 3.2 Update `README.md` with Fertilizer; verify the recipe text matches the JSON
