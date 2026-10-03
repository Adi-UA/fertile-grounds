# Tasks

## 1. Visit hook

- [ ] 1.1 Add `FairyVisitEvents.onVisitGrown` and call it from `VisitCropGoal` and the dawn path; verify the smoke test logs the hook for a dust visit and a dawn visit

## 2. Sound and messages

- [ ] 2.1 Register the arrival sound with `sounds.json` and subtitle lang key; verify the `runClient` log has no missing sound warning, and add "hear the chime" to the in-game checklist
- [ ] 2.2 Send the 32-block overlay message on spawn and the once-per-dawn chat message; verify the smoke test sees the dawn message once for two overnight visits

## 3. Advancements

- [ ] 3.1 Add the `fertilegrounds:fairy_visit` trigger; verify a test datapack advancement on it is earned in the smoke test
- [ ] 3.2 Add the tab root, first visit, and Fertilizer advancements with lang keys; verify the smoke test grants each through its real action and `/advancement test` reports it done
- [ ] 3.3 Add the advancement list to `README.md`; verify names match the lang file
