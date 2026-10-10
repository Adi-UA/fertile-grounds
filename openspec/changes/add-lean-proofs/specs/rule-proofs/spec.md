# Spec Delta

## Purpose

Gives a few of the mod's Minecraft-free rules machine-checked proofs, so their key properties hold for every input, not only the inputs JUnit picks.

## ADDED Requirements

### Requirement: Proofs build cleanly
`python3 proofs/check_proofs.py` SHALL run `lake build` in `proofs/` and exit 0 only when every file builds and no proof uses `sorry`.

#### Scenario: Unfinished proof
- **WHEN** a developer leaves a `sorry` in a proof
- **THEN** the script exits 1 and names the file

### Requirement: Visit chance
The proofs SHALL show that `FairyTiming.visitChance` never decreases when a flower kind, a hive, or a full moon is added, ignores flower kinds past 10, never exceeds 100%, and gives 7.5% for base 1%, 10 flower kinds, a hive, and a full moon at default bonuses.

#### Scenario: Flowers past the cap
- **WHEN** a farm has 14 kinds of flowers instead of 10
- **THEN** the proven visit chance is the same

### Requirement: Chime Hotel rings
The proofs SHALL show a Chime Hotel never rings with fewer than 4 crops, rings at most once over any run of checks where the field stays ready, and rings when a ready check follows a not-ready one.

#### Scenario: Ripe field left standing
- **WHEN** a field stays ready for any number of checks
- **THEN** the proven ring count for that run is at most 1

### Requirement: Hotel cluster leaders
The proofs SHALL show that among any nonempty group of occupied hotels at least one leads, and two hotels in range of each other never both lead.

#### Scenario: Two hotels side by side
- **WHEN** two occupied hotels sit next to each other
- **THEN** the proofs show exactly one of them boosts crops

### Requirement: Dawn check
The proofs SHALL show that a check run at most `window` ticks after the previous one (with `window` at most 24,000) lands in the window after any dawn that passed between them, so petting always wears off.

#### Scenario: Dawn between two checks
- **WHEN** dawn falls between two Chime Hotel checks 100 ticks apart
- **THEN** the second check is proven to see it
