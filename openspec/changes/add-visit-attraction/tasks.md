# Tasks

## 1. Chance math

- [ ] 1.1 Add `FairyTiming.visitChance(...)` with JUnit tests for every spec scenario (no bonus, 3 flowers, cap, hive, full moon, 100% cap)
- [ ] 1.2 Add gamerules `fairy_flower_bonus`, `fairy_hive_bonus`, `harvest_moon_multiplier`; verify they read their defaults in the smoke test server

## 2. Scheduler

- [ ] 2.1 Make `rollNaturalVisits` pick the crop first, scan flowers and hives, then roll; verify the smoke test places 10 flower types and a hive, sets base chance to 10 on a full moon (`/time set` to a full-moon night), and sees a visit
- [ ] 2.2 Look up the moon phase call in the 26.3 sources and note it in design.md; verify a JUnit or smoke test check reads full moon on night 0

## 3. Harvest Moon message

- [ ] 3.1 Send the translated message at full-moon nightfall; verify the smoke test log shows it once, and add "see the Harvest Moon message" to the in-game checklist
- [ ] 3.2 Add a Harvest Moon note to `README.md`; verify the numbers match the gamerule defaults
