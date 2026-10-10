# Tasks

## 1. Tags

- [x] 1.1 Add `ModTags` with `FAIRY_CROPS` (block) and `FAIRY_FLOWERS` (block and item), plus default tag JSON files; verify `./gradlew build` passes
- [x] 1.2 Switch `CropGrowthUtil.canGrow` to `FAIRY_CROPS`; verify the smoke test still sees a fairy grow wheat
- [x] 1.3 Extend the smoke test to load a datapack that removes carrots from `fairy_crops` and confirm Fairy Dust on carrots fails
- [x] 1.4 Verify the smoke test resolves `#fertilegrounds:fairy_flowers` with and without `c:flowers/small` defined, with no tag load error

## 2. Gamerules

- [x] 2.1 Read the gamerule API in the 26.3 Fabric API jar with `javap` and note the registration call in design.md
- [x] 2.2 Add `ModGameRules` with `fairy_visit_chance`, `enriched_boost_chance`, and `super_enriched_boost_chance`; verify `/gamerule fertilegrounds:fairy_visit_chance` reads 1 in the smoke test server
- [x] 2.3 Make `FairyTiming` take the chance as a parameter; verify new JUnit cases cover 0, 1, and 100
- [x] 2.4 Wire the soil blocks and scheduler to the gamerules; verify JUnit shows `rollsNaturalVisit` always passes at 100, the smoke test sets `fairy_visit_chance 100` and reads back 100, and add to the user's in-game checklist: at 100, a fairy visits a crop near the player on the first night (the smoke server has no player, so it can't schedule natural visits)
- [x] 2.5 Document the tags and gamerules in `README.md` under a "For modpack makers" section; verify every name in the README matches the code
