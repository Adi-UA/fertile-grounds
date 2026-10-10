# Tasks

## 1. Setup

- [ ] 1.1 Install `elan-init` with Homebrew and create the `proofs/` Lake project with Mathlib pinned to its latest release tag and the matching `lean-toolchain`; git-ignore `proofs/.lake`; verify `lake exe cache get` and `lake build` succeed on an empty `FertileGrounds.lean`
- [ ] 1.2 Add `scripts/check_proofs.py`; verify it exits 0 on the empty project and 1 after adding a file with `sorry`, then remove that file

## 2. Proofs

- [ ] 2.1 `Fairy/FairyTiming.lean`: model `transition`, `visitChance` (over `ℚ`), `isDue`, `isHeraldTime`; prove the Visit chance requirement and mirror `FairyTimingTest`; verify `check_proofs.py`
- [ ] 2.2 `Block/SoilUpgrades.lean` and `Block/SoilTier.lean`: prove the Soil requirement and mirror `SoilUpgradesTest` and `SoilTierTest`; verify `check_proofs.py`
- [ ] 2.3 `Hotel/ChimeRules.lean`: prove the Chime Hotel rings requirement over crop-count lists and mirror `ChimeRulesTest`; verify `check_proofs.py`
- [ ] 2.4 `Hotel/FairyHotelRules.lean`: prove the Hotel clusters and dawn requirement and mirror `FairyHotelRulesTest`; verify `check_proofs.py`
- [ ] 2.5 `Hotel/JarredFairyRules.lean` and `Fairy/FairyGifts.lean`: prove the Jarred Fairies and gifts requirement and mirror `JarredFairyRulesTest`, `FairyGiftsTest`, and `FairyGiftCooldownsTest`; verify `check_proofs.py`
- [ ] 2.6 `Fairy/GrowthRings.lean` and `Fairy/ShyFlight.lean` (over `ℝ`): prove the Growth rings and shy flight requirement and mirror `GrowthRingsTest` and `ShyFlightTest`; verify `check_proofs.py`

## 3. Finish

- [ ] 3.1 Mutation check: change one constant in a model (Chime minimum 4 to 5) and confirm `check_proofs.py` fails on the mirrored examples, then revert
- [ ] 3.2 Add the `proofs/` row, the `check_proofs.py` command, and the "a rule-class change updates its Lean file" rule to `AGENTS.md`; add step 17 to `docs/ROADMAP.md`; verify every path named exists
