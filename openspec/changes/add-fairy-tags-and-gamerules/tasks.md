# Tasks

## 1. Tags

- [x] 1.1 Add `ModTags` with `FAIRY_CROPS` (block) and `FAIRY_FLOWERS` (block and item), plus default tag JSON files; verify `./gradlew build` passes
- [x] 1.2 Switch `CropGrowthUtil.canGrow` to `FAIRY_CROPS`; verify the smoke test still sees a fairy grow wheat
- [x] 1.3 Extend the smoke test to load a datapack that removes carrots from `fairy_crops` and confirm Fairy Dust on carrots fails
- [x] 1.4 Verify the smoke test resolves `#fertilegrounds:fairy_flowers` with and without `c:flowers/small` defined, with no tag load error

## 2. Gamerules

- [ ] 2.1 Read the gamerule API in the 26.3 Fabric API jar with `javap` and note the registration call in design.md
- [ ] 2.2 Add `ModGameRules` with `fairy_visit_chance`, `enriched_boost_chance`, and `super_enriched_boost_chance`; verify `/gamerule fertilegrounds:fairy_visit_chance` reads 1 in the smoke test server
- [ ] 2.3 Make `FairyTiming` take the chance as a parameter; verify new JUnit cases cover 0, 1, and 100
- [ ] 2.4 Wire the soil blocks and scheduler to the gamerules; verify the smoke test sets `fairy_visit_chance 100` and gets a natural visit on the first night
- [ ] 2.5 Document the tags and gamerules in `README.md` under a "For modpack makers" section; verify every name in the README matches the code
