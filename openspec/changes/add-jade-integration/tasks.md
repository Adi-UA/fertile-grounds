# Tasks

## 1. Setup

- [x] 1.1 Add the Jade API `compileOnly` dependency and a dev-only runtime dependency, pinned from Jade's Modrinth versions for 26.3; verify `./gradlew build` passes and the built jar has no Jade classes inside
- [x] 1.2 Add the `jade` entrypoint and an empty plugin; verify `client_log_check.py` passes with Jade present, and the GameTests pass without Jade

## 2. Tooltips

- [x] 2.1 Add the soil provider with server-sent chance; verify a `JadeDataTests` GameTest checks the data each provider sends; add "look at soil with Jade" to the in-game checklist
- [x] 2.2 Add the pending visit provider; add "use dust, look at the crop" to the in-game checklist
- [x] 2.3 Add the hotel provider; add "look at a hotel before and after brushing, and at a Chime Hotel" to the in-game checklist
- [x] 2.4 Add tooltip lang keys (asset test covers them) and a Jade note in `README.md`
- [x] 2.5 Add "tooltip lines use Jade's default styling and colors" to the in-game checklist; no extra sound or particles, since a tooltip is passive
