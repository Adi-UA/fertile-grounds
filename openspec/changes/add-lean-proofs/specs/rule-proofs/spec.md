# Spec Delta

## Purpose

Gives the mod's Minecraft-free rules machine-checked proofs, so their key properties hold for every input, not only the inputs JUnit picks.

## ADDED Requirements

### Requirement: Proofs build cleanly
`python3 proofs/check_proofs.py` SHALL run `lake build` in `proofs/` and exit 0 only when every file builds and no proof uses `sorry` or a new `axiom`.

#### Scenario: Unfinished proof
- **WHEN** a developer leaves a `sorry` in a proof
- **THEN** the script exits 1 and names the file

### Requirement: Local only
The `proofs/` directory SHALL be git-ignored, so no Lean file is committed.

#### Scenario: Status after proving
- **WHEN** a developer builds the proofs and runs `git status`
- **THEN** nothing under `proofs/` shows as new or changed

### Requirement: Models mirror the Java
Each Lean model SHALL name the Java class and method it mirrors, use the same default numbers, and include every JUnit case for that class as a Lean `example` checked by `decide` or `norm_num`.

#### Scenario: Java rule changes
- **WHEN** a developer changes the Chime Hotel's minimum from 4 crops to 5 and updates `ChimeRulesTest`
- **THEN** the mirrored Lean examples fail until the Lean model changes the same way

### Requirement: Visit chance
For base chance, flower bonus, and hive bonus from 0 to 100, and Harvest Moon multiplier from 1 to 10, the proofs SHALL show that `visitChance` is between 0 and 100, never decreases when a flower kind, a hive, or a full moon is added, stops rising after 10 flower kinds, and equals 7.5 for base 1, 10 flower kinds, a hive, and a full moon at default bonuses. They SHALL show `transition` reports a change only when the previous state is known and differs.

#### Scenario: Flowers past the cap
- **WHEN** a farm has 14 kinds of flowers instead of 10
- **THEN** the proven visit chance is the same

### Requirement: Soil
The proofs SHALL show that Fertilizer's upgrade map sends every listed block to a Fertile Grounds block, that no upgrade result upgrades again (so a second use does nothing), and that comparator signals are 7 and 15 and rise with the tier.

#### Scenario: Fertilizing twice
- **WHEN** Fertilizer is used on Super Enriched Farmland
- **THEN** the proven map has no upgrade for it

### Requirement: Chime Hotel rings
Over any sequence of crop counts, the proofs SHALL show a Chime Hotel never rings with fewer than 4 crops, rings at most once between two checks where the field is below the threshold, and rings again after a check below the threshold followed by one at or above it.

#### Scenario: Ripe field left standing
- **WHEN** a field stays at 100% for any number of checks
- **THEN** the proven ring count for that stretch is at most 1

### Requirement: Hotel clusters and dawn
The proofs SHALL show that among any finite, nonempty set of occupied hotels at least one leads, two hotels that see each other never both lead, `ticksUntilDawn` is between 1 and 24,000, and checks spaced at most `window` ticks apart (with `window` at most 24,000) land in the window after every dawn they pass.

#### Scenario: Two hotels side by side
- **WHEN** two occupied hotels sit next to each other
- **THEN** the proofs show exactly one of them boosts crops

### Requirement: Jarred Fairies and gifts
The proofs SHALL show a Jarred Fairy slips away exactly when `days` full days have passed since it was caught, `leftFlower` returns one of the listed flowers or the fallback when the list is empty, a gift tier never drops when the farm grows or a Harvest Moon rises, the gift chance is 0 exactly when the source has no base chance, and a player is off cooldown again `days` days after a gift and never before.

#### Scenario: Harvest Moon on a rare farm
- **WHEN** a farm already rolls rare gifts and a Harvest Moon rises
- **THEN** the proven tier stays rare

### Requirement: Growth rings and shy flight
The proofs SHALL show that rings 0, 1, and 2 cover the 5x5 patch with each spot in exactly one ring, ring `n` has `8n` spots for `n ≥ 1`, and a shy fairy's target is never more than 8 blocks from its crop and keeps the fairy's height.

#### Scenario: Fairy cornered at the edge
- **WHEN** a player chases a fairy toward the far edge of its patch
- **THEN** the proven target stays within 8 blocks of the crop
