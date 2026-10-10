# Tasks

## 1. Visit owner

- [x] 1.1 Add `source` and `owner` to `FairyVisit` with optional codec fields; verify a JUnit codec test decodes a 1.2.0-shaped entry as UNOWNED
- [x] 1.2 Record the owner in natural rolls and `FairyDustItem` (none for dispensers); verify JUnit shows a visit with no player is unowned, and add to the user's in-game checklist: a gift from your own Fairy Dust visit (owners need a player, which the smoke server can't have)

## 2. Gift rules

- [ ] 2.1 Add `FairyGifts` (chance, cooldown, tier with Harvest Moon step) with JUnit tests for every spec scenario
- [ ] 2.2 Add gamerules `fairy_gift_chance_natural`, `fairy_gift_chance_dust`, `fairy_gift_cooldown_days`; verify defaults in the smoke test server
- [ ] 2.3 Add the cooldown level attachment; verify the smoke test restarts the server and the cooldown persists

## 3. Delivery

- [ ] 3.1 Add the three loot tables from the spec's default gift tables; verify a JUnit asset test reads each JSON and matches every item, count range, and weight to the spec, and `/loot spawn ~ ~ ~ loot fertilegrounds:gameplay/fairy_gift/rare` drops an item in the smoke test
- [ ] 3.2 Drop gifts from `FairyVisitEvents.onVisitGrown` with particles and owner message; verify the smoke test sets gift chance to 100, runs a dust visit, and finds a gift item entity with unlimited lifetime
- [ ] 3.3 Add the first gift and Harvest Moon advancements; verify the smoke test grants them through real gifts and visits
- [ ] 3.4 Document gifts and their gamerules in `README.md`; verify numbers match the defaults
- [ ] 3.5 Check every row of the design's Look and sound table in `runClient` and add it to the in-game checklist
