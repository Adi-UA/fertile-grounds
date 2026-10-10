# Proposal

## Why

The mod's rules decide things players can't easily check by playing: whether 10 flowers and a hive really give 7.5%, whether a Chime Hotel can ring twice for one field, whether two hotels side by side can both boost. JUnit tests a handful of chosen inputs; a Lean proof covers every input. Proving the Minecraft-free rule classes before the manual test means the user's play session only has to judge looks, feel, and timing.

## What Changes

- Install Lean 4 through `elan` (Homebrew `elan-init`; mise has no Lean plugin) and add a `proofs/` Lake project with Mathlib, outside the Gradle build and never shipped.
- Write a Lean model of each rule class that mirrors its Java method for method, and prove the properties listed in the spec: visit chance bounds and monotonicity, night transitions, soil upgrades, comparator signals, Chime Hotel rings, cluster leaders, dawn timing, Jarred Fairy expiry, gift tiers and cooldowns, growth rings, and shy flight.
- Mirror each JUnit case as a Lean `example`, so a Java change that breaks a JUnit case also breaks the Lean build until the model is updated.
- Add `scripts/check_proofs.py`, which runs `lake build` and fails on any `sorry`.

Out of scope: proving the Java code itself (the Lean model mirrors it by hand), anything that touches Minecraft (worlds, entities, random ticks), floating-point rounding (the models use exact numbers), and running the proofs in `./gradlew build`.

New art: none.

## Capabilities

### New Capabilities

- `rule-proofs`: Which rule properties have machine-checked proofs, and how the proofs stay in step with the Java.

### Modified Capabilities

None.

## Impact

A new `proofs/` directory (Lake project pinned to a Lean toolchain and Mathlib release), `scripts/check_proofs.py`, an `AGENTS.md` row and command, and a step 17 in `docs/ROADMAP.md`. No mod code or gameplay changes. Mathlib's prebuilt cache is about 5 GB on disk, in `proofs/.lake` (git-ignored).
