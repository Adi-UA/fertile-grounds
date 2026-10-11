# Tasks

## 1. Chance math

- [x] 1.1 Add `FairyTiming.visitChance(...)` with JUnit tests for every spec scenario (no bonus, 3 flowers, cap, hive, full moon, 100% cap)
- [x] 1.2 Add gamerules `fairy_flower_bonus`, `fairy_hive_bonus`, `harvest_moon_multiplier`; verify they read their defaults in the smoke test server

## 2. Scheduler

- [x] 2.1 Make `rollNaturalVisits` pick the crop first, scan flowers and hives, then roll; verify JUnit covers every chance scenario and the full smoke run passes, and add to the user's in-game checklist: with `fairy_visit_chance 10` on night 0 (a full moon) and 10 flower types plus a hive near a crop, a fairy visits that night (natural visits need a player, which the smoke server can't have)
- [x] 2.2 Look up the moon phase call in the 26.3 sources and note it in design.md; verify a JUnit or smoke test check reads full moon on night 0

## 3. Harvest Moon message

- [x] 3.1 Send the translated message at full-moon nightfall; verify the smoke test log shows it once, and add "see the Harvest Moon message" to the in-game checklist
- [x] 3.2 Add a Harvest Moon note to `README.md`; verify the numbers match the gamerule defaults
- [x] 3.3 Add every row of the design's Look and sound table to the user's in-game checklist; the user plays through the checklist at the end of the 2.0 run
