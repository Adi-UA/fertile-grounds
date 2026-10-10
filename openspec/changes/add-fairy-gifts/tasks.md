# Tasks

## 1. Visit owner

- [x] 1.1 Add `source` and `owner` to `FairyVisit` with optional codec fields; verify a JUnit codec test decodes a 1.2.0-shaped entry as UNOWNED
- [x] 1.2 Record the owner in natural rolls and `FairyDustItem` (none for dispensers); verify JUnit shows a visit with no player is unowned, `FairyDustTests` sees a mock player's dust visit saved with them as owner, and `FairyGiftTests` sees the owner get the gift

## 2. Gift rules

- [x] 2.1 Add `FairyGifts` (chance, cooldown, tier with Harvest Moon step) with JUnit tests for every spec scenario
- [x] 2.2 Add gamerules `fairy_gift_chance_natural`, `fairy_gift_chance_dust`, `fairy_gift_cooldown_days`; verify defaults in the smoke test server
- [x] 2.3 Add the cooldown level attachment; verify a JUnit test round-trips the saved cooldown map through its codec (the attachment uses the same persistent pattern as `FairyVisits`), `FairyGiftTests` sees a day-0 gift block day 1's and allow day 3's, and the user's in-game checklist keeps: get a gift, restart the world, and get no gift the next night (a restart needs a real save)

## 3. Delivery

- [x] 3.1 Add the three loot tables from the spec's default gift tables; verify a JUnit asset test reads each JSON and matches every item, count range, and weight to the spec, and `/loot spawn ~ ~ ~ loot fertilegrounds:gameplay/fairy_gift/rare` drops an item in the smoke test
- [x] 3.2 Drop gifts from `FairyVisitEvents.onVisitGrown` with particles and owner message; verify the full smoke run still passes with the new visit plumbing, and `FairyGiftTests` sees an owned visit at 100% drop a gift with unlimited lifetime, the gift message, and no gift for an unowned visit
- [x] 3.3 Add the first gift and Harvest Moon advancements; verify the smoke test boots with them and no advancement error, and `FairyGiftTests` and `VisitFeedbackTests` earn both through real actions with a mock player
- [x] 3.4 Document gifts and their gamerules in `README.md`; verify numbers match the defaults
- [x] 3.5 Add every row of the design's Look and sound table to the user's in-game checklist; the user plays through the checklist at the end of the 2.0 run
