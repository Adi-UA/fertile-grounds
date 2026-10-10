# Tasks

## 1. Visit hook

- [x] 1.1 Add `FairyVisitEvents.onVisitGrown` and call it from `VisitCropGoal` and the dawn path; verify the smoke test logs the hook once for a flying fairy's visit, and add to the user's in-game checklist: sleep through a night after using Fairy Dust and the crops are grown at dawn (pending visits need a player to create them)

## 2. Sound and messages

- [x] 2.1 Register the arrival sound with `sounds.json` and subtitle lang key; verify the `runClient` log has no missing sound warning, and add "hear the chime" to the in-game checklist
- [x] 2.2 Send the 32-block overlay message on spawn and the once-per-dawn chat message; verify the build passes, and add to the user's in-game checklist: the overlay message when a fairy arrives nearby, and the dawn message once after sleeping through two Fairy Dust visits (messages only reach online players, which the smoke server can't have)

## 3. Advancements

- [ ] 3.1 Add the `fertilegrounds:fairy_visit` trigger; verify a test datapack advancement on it is earned in the smoke test
- [ ] 3.2 Add the tab root, first visit, and Fertilizer advancements with lang keys; verify the smoke test grants each through its real action and `/advancement test` reports it done
- [ ] 3.3 Add the advancement list to `README.md`; verify names match the lang file
- [ ] 3.4 Check every row of the design's Look and sound table in `runClient` and add it to the in-game checklist
