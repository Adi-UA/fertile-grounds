# verification-suite Specification

## Purpose
Defines the automatic checks that prove a branch works beyond compiling, so past bugs can't silently return during features or ports.

## Requirements

### Requirement: Lang keys for everything
`./gradlew test` SHALL fail when any block, item, entity, advancement, sound subtitle, or `Component.translatable` key used in the mod's source or data lacks an entry in `en_us.json`.

#### Scenario: New item without a name
- **WHEN** a developer registers a new item and forgets its lang entry
- **THEN** `./gradlew test` fails and names the missing key

#### Scenario: Block item key
- **WHEN** a block item's name would resolve to an `item.fertilegrounds.*` key instead of the block's `block.fertilegrounds.*` key
- **THEN** the test fails

### Requirement: Client assets present
`./gradlew test` SHALL fail when an item lacks an item model definition in `assets/fertilegrounds/items/`, a block lacks a block state file, or any model references a texture that doesn't exist.

#### Scenario: Missing item model definition
- **WHEN** an item has no `items/<name>.json`
- **THEN** the test fails and names the item

### Requirement: Data file shape
`./gradlew test` SHALL fail when a recipe uses the `{"item": ...}` ingredient wrapper or an `"item"` result key, or a recipe advancement uses the old `"recipe"` criterion key or an array `items` predicate.

#### Scenario: Old recipe format
- **WHEN** a recipe uses `{"item": "minecraft:bone_meal"}` as an ingredient
- **THEN** the test fails

### Requirement: Silent load failures fail the GameTest run
`python3 scripts/run_gametests.py` SHALL fail when the server log contains a recipe, advancement, loot table, or tag parse error, in addition to registry and tick errors, on the Fabric, NeoForge, and Forge test servers.

#### Scenario: Broken recipe
- **WHEN** a recipe JSON fails to parse on load
- **THEN** the run exits 1 and prints the log line

#### Scenario: Broken recipe on Forge
- **WHEN** the same broken recipe loads on the Forge test server
- **THEN** the Forge run also exits 1 and prints the log line

### Requirement: Runtime regressions
The GameTests SHALL check, in a real world, that trampled and dried Enriched and Super Enriched Farmland revert to their own dirt tier, that Enriched Sand is in `#minecraft:sand`, that a fairy reaches a roofed crop, and that a fairy with no crop leaves.

#### Scenario: Trampling
- **WHEN** a mob falls 10 blocks onto Enriched Farmland
- **THEN** the block becomes Enriched Dirt, not vanilla dirt

#### Scenario: Sugar cane soil
- **WHEN** a GameTest checks Enriched Sand against `#minecraft:sand`
- **THEN** the check passes

### Requirement: Client log check
`python3 scripts/client_log_check.py` SHALL start the dev client, wait for the mod's init line and the title screen, stop the client, and exit 1 when the log names a missing model, texture, or sound for `fertilegrounds`. It SHALL take `--loader fabric` (the default), `--loader neoforge`, or `--loader forge`.

#### Scenario: Missing texture
- **WHEN** a block model points at a texture file that was never added
- **THEN** the script exits 1 and prints the warning

#### Scenario: Forge client
- **WHEN** a developer runs the script with `--loader forge`
- **THEN** it boots the Forge dev client and applies the same checks

### Requirement: Every fix adds a regression
Each bug fix SHALL add a JUnit test or a GameTest that fails without the fix.

#### Scenario: Future fix
- **WHEN** a developer fixes a bug found in playtesting
- **THEN** the same commit adds a check that fails without the fix

### Requirement: Loader metadata matches
`./gradlew test` SHALL fail when `fabric.mod.json`, `neoforge.mods.toml`, and Forge's `mods.toml` disagree on the mod id, name, description, license, or icon, or when any of them names a Minecraft version other than the branch's.

#### Scenario: Description drift
- **WHEN** a developer edits the description in `fabric.mod.json` only
- **THEN** the test fails and names the files that disagree
