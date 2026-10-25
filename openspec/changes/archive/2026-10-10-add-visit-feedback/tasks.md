# Tasks

## 1. Visit hook

- [x] 1.1 Add `FairyVisitEvents.onVisitGrown` and call it from `VisitCropGoal` and the dawn path; verify the smoke test logs the hook once for a flying fairy's visit, `VisitFeedbackTests` sees pending visits grow at dawn, and the user's in-game checklist keeps: sleep through a night after using Fairy Dust and the crops are grown at dawn (sleeping needs every real player in bed)

## 2. Sound and messages

- [x] 2.1 Register the arrival sound with `sounds.json` and subtitle lang key; verify the `runClient` log has no missing sound warning, and add "hear the chime" to the in-game checklist
- [x] 2.2 Send the 32-block overlay message on spawn and the once-per-dawn chat message; verify the build passes, and `VisitFeedbackTests` sees the arrival overlay reach a mock player within 32 blocks and not one at 50, and the dawn message once for two overnight visits

## 3. Advancements

- [x] 3.1 Add the `fertilegrounds:fairy_visit` trigger; verify the smoke test boots with a test datapack advancement on it (and fails to boot with an unknown trigger), and `VisitFeedbackTests` sees a real fairy visit within 32 blocks earn a mock player the first visit advancement
- [x] 3.2 Add the tab root, first visit, and Fertilizer advancements with lang keys; verify the smoke test boots with them and no advancement load or validation error, and `AdvancementTests` and `VisitFeedbackTests` earn each one through its real action with a mock player
- [x] 3.3 Add the advancement list to `README.md`; verify names match the lang file
- [x] 3.4 Add every row of the design's Look and sound table to the user's in-game checklist; the user plays through the checklist at the end of the 2.0 run
