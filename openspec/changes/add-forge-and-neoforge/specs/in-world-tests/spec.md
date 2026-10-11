## MODIFIED Requirements

### Requirement: One command runs every in-world check
A single command SHALL start a test world, run every in-world check, and exit non-zero if any check fails or the server logs a recipe, advancement, loot table, or tag load error. A developer SHALL be able to run one feature's checks on their own, and SHALL be able to pick the loader, Fabric, NeoForge, or Forge, with Fabric as the default.

#### Scenario: All checks pass
- **WHEN** a developer runs the in-world check command on a clean branch
- **THEN** it reports every check as passed and exits 0

#### Scenario: Broken data file
- **WHEN** a tag file references a block that doesn't exist
- **THEN** the command exits 1 and prints the log line naming the tag

#### Scenario: One feature at a time
- **WHEN** a developer asks for only the Fertilizer checks
- **THEN** only those checks run

#### Scenario: Other loaders
- **WHEN** a developer runs the command with NeoForge or Forge selected
- **THEN** the same checks run on that loader's test server and report pass or fail the same way

## ADDED Requirements

### Requirement: Same checks on both loaders
Every in-world check SHALL be written once and run on Fabric, NeoForge, and Forge. A check that some loader can't run SHALL say which loader and why, next to the check.

#### Scenario: New check
- **WHEN** a developer adds a GameTest for a new feature
- **THEN** every loader runs it without a second copy of the test

#### Scenario: Count matches
- **WHEN** every loader finishes a full run on the same branch
- **THEN** they report the same number of passed checks, minus the checks marked for a missing loader feature

### Requirement: Checks run on every push
CI SHALL run the full in-world checks, including the data-load error scan, for every loader on every push and pull request to every version branch. When a run fails, CI SHALL keep the test report and server log for download.

#### Scenario: Failing GameTest on a backport
- **WHEN** a push to the 26.1 branch breaks a Forge GameTest
- **THEN** the CI run fails, names the test, and attaches the Forge test report and log
