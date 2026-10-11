# Tasks

## 1. Dive fix

- [x] 1.1 Reproduce the fairy sinking into the ground on an open field (in the smoke server, by tracking the fairy's lowest Y each tick, since only a person can watch `runClient`); record the cause in design.md
- [x] 1.2 Add the spawn height and open-sky clamp; verify a smoke test check logs the fairy's lowest Y during a visit and fails if it drops below crop Y + 1, and the existing roofed-crop check still passes

## 2. Phases

- [x] 2.1 Add `GrowthRings` with JUnit tests (ring 0 has 1 offset, ring 1 has 8, ring 2 has 16)
- [x] 2.2 Split `VisitCropGoal` into the five phases with per-phase limits and `hasFinishedGrowing()`; verify the smoke test still sees a fairy grow wheat and leave
- [x] 2.3 Add `fairy_linger_seconds`; verify the smoke test sees the fairy alive 30 seconds after growth with the default, and gone right after growth at 0

## 3. Behavior

- [x] 3.1 Add the dance and ring growth; verify the smoke test sees the center crop grown before the edge crops on consecutive checks
- [x] 3.2 Add `ShyFlight` with JUnit tests (flee direction, 6 block distance, clamp to 8 blocks from crop); `ShyFlightTests` sees a lingering fairy get 4 blocks from a walking mock player and stay near a sneaking one
- [x] 3.3 Add heralds, with JUnit for when they start; add "watch fireflies appear before a Fairy Dust visit" to the in-game checklist

## 4. Look and sound

- [x] 4.1 Add every row of the Look and sound table and "watch a full visit start to finish" to the user's in-game checklist, and verify `client_log_check.py` shows no missing sound or particle warnings
- [x] 4.2 Document the new visit behavior in `README.md` and add any sound or particle id difference to the porting divergence table
