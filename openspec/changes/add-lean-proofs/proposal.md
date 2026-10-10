# Proposal

## Why

A few rules decide things players can't easily check by playing: whether flowers and a full moon can ever lower the visit chance, whether a Chime Hotel can ring twice for one field, whether two hotels side by side can both boost. JUnit tests chosen inputs; a Lean proof covers every input. Proving these before the manual test means the user's play session only has to judge looks, feel, and timing.

## What Changes

- Install Lean 4 through `elan` (Homebrew `elan-init`; mise has no Lean plugin).
- Add a small `proofs/` Lake project with no dependencies beyond Lean itself, committed to the repo (its `.lake/` build folder is git-ignored).
- Prove a minimal set of theorems about four rules: the visit chance, the Chime Hotel ring, hotel cluster leaders, and the dawn check. The user approves the exact theorem statements before any proof is written.
- Add `proofs/check_proofs.py`, which runs `lake build` and fails on any `sorry`.

Out of scope: proofs for the other rule classes (soil upgrades, gifts, Jarred Fairies, growth rings, shy flight), proving the Java code itself (the Lean model mirrors it by hand), floating-point rounding, and running the proofs in `./gradlew build`.

New art: none.

## Capabilities

### New Capabilities

- `rule-proofs`: Which rule properties have machine-checked proofs.

### Modified Capabilities

None.

## Impact

A new `proofs/` directory (4 Lean files, a pinned `lean-toolchain`, a check script) and a `.gitignore` line for `proofs/.lake`. No mod code or gameplay changes.
