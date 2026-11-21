## MODIFIED Requirements

### Requirement: Silent load failures fail the smoke test
The smoke test SHALL fail when the server log contains a recipe, advancement, loot table, or tag parse error, in addition to registry and tick errors, on the Fabric, NeoForge, and Forge test servers.

#### Scenario: Broken recipe
- **WHEN** a recipe JSON fails to parse on load
- **THEN** the smoke test exits 1 and prints the log line

#### Scenario: Broken recipe on Forge
- **WHEN** the same broken recipe loads on the Forge test server
- **THEN** the Forge smoke test also exits 1 and prints the log line

### Requirement: Client log check
`python3 scripts/client_log_check.py` SHALL start the dev client, wait for the mod's init line and the title screen, stop the client, and exit 1 when the log names a missing model, texture, or sound for `fertilegrounds`. It SHALL take `--loader fabric` (the default), `--loader neoforge`, or `--loader forge`.

#### Scenario: Missing texture
- **WHEN** a block model points at a texture file that was never added
- **THEN** the script exits 1 and prints the warning

#### Scenario: Forge client
- **WHEN** a developer runs the script with `--loader forge`
- **THEN** it boots the Forge dev client and applies the same checks

## ADDED Requirements

### Requirement: Loader metadata matches
`./gradlew test` SHALL fail when `fabric.mod.json`, `neoforge.mods.toml`, and Forge's `mods.toml` disagree on the mod id, name, description, license, or icon, or when any of them names a Minecraft version other than the branch's.

#### Scenario: Description drift
- **WHEN** a developer edits the description in `fabric.mod.json` only
- **THEN** the test fails and names the files that disagree
