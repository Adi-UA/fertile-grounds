# Spec Delta

## Purpose

Defines the automatic checks that prove a branch works beyond compiling, so past bugs can't silently return during features or ports.

## ADDED Requirements

### Requirement: Lang keys for everything
`./gradlew test` SHALL fail when any block, item, entity, advancement, sound subtitle, or `Component.translatable` key used in the mod's source or data lacks an entry in `en_us.json`.

#### Scenario: New item without a name
- **WHEN** a developer registers a new item and forgets its lang entry
- **THEN** `./gradlew test` fails and names the missing key

#### Scenario: Block item key
- **WHEN** a block item's name would resolve to an `item.fertilegrounds.*` key instead of the block's `block.fertilegrounds.*` key
- **THEN** the test fails (regression for 0a7576e)

### Requirement: Client assets present
`./gradlew test` SHALL fail when an item lacks an item model definition in `assets/fertilegrounds/items/`, a block lacks a block state file, or any model references a texture that doesn't exist.

#### Scenario: Missing item model definition
- **WHEN** an item has no `items/<name>.json`
- **THEN** the test fails and names the item (regression for e5356de)

### Requirement: Data file shape
`./gradlew test` SHALL fail when a recipe uses the `{"item": ...}` ingredient wrapper or an `"item"` result key, or a recipe advancement uses the old `"recipe"` criterion key or an array `items` predicate.

#### Scenario: Old recipe format
- **WHEN** a recipe uses `{"item": "minecraft:bone_meal"}` as an ingredient
- **THEN** the test fails (regression for b95a82e and 02ba2bb)

### Requirement: Silent load failures fail the smoke test
The smoke test SHALL fail when the server log contains a recipe, advancement, loot table, or tag parse error, in addition to registry and tick errors.

#### Scenario: Broken recipe
- **WHEN** a recipe JSON fails to parse on load
- **THEN** the smoke test exits 1 and prints the log line

### Requirement: Runtime regressions
The smoke test SHALL check, in a real world, that trampled and dried Enriched and Super Enriched Farmland revert to their own dirt tier, that Enriched Sand is in `#minecraft:sand`, that a fairy reaches a roofed crop, and that a fairy with no crop leaves.

#### Scenario: Trampling
- **WHEN** a mob falls 10 blocks onto Enriched Farmland
- **THEN** the block becomes Enriched Dirt, not vanilla dirt (regression for 83fd1fb)

#### Scenario: Sugar cane soil
- **WHEN** the smoke test checks Enriched Sand against `#minecraft:sand`
- **THEN** the check passes (regression for 1e5d483)

### Requirement: Client log check
`python3 scripts/client_log_check.py` SHALL start `runClient`, wait for the mod's init line and the title screen, stop the client, and exit 1 when the log names a missing model, texture, or sound for `fertilegrounds`.

#### Scenario: Missing texture
- **WHEN** a block model points at a texture file that was never added
- **THEN** the script exits 1 and prints the warning

### Requirement: Every fix adds a regression
Each bug fix commit SHALL add a check to the JUnit tests or the smoke test, with a comment naming the commit or bug it guards.

#### Scenario: Future fix
- **WHEN** a developer fixes a bug found in playtesting
- **THEN** the same commit adds a check that fails without the fix
