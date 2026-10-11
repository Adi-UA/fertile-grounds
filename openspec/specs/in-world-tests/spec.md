# in-world-tests Specification

## Purpose
Defines the automated in-world checks every change must pass, including checks that need a player, so features are verified on every build instead of on a manual checklist.

## Requirements

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

### Requirement: Checks that need a player run automatically
In-world checks SHALL be able to act as a player: use an item on a block, stand near a visit, receive messages, and earn advancements. Every behavior that previously needed the in-game checklist only because the test server had no player SHALL be checked this way.

#### Scenario: Fairy Dust by hand
- **WHEN** a check has a player use Fairy Dust on a half-grown crop at night
- **THEN** a visit is pending for that crop, owned by that player, and one Fairy Dust is used

#### Scenario: Gift for the owner
- **WHEN** the dust gift chance is 100 and a player's Fairy Dust visit grows crops while they stand nearby
- **THEN** gift items lie at the crop and never despawn, the player sees the gift message, and the player earns the first gift advancement

#### Scenario: Cooldown
- **WHEN** that player owns another visit the next night
- **THEN** no gift drops

### Requirement: Existing checks keep working
Every check the smoke test performs at the start of this change SHALL still run, with the same pass and fail behavior, including each check labeled with the bug commit it guards.

#### Scenario: Old regression
- **WHEN** Enriched Farmland's revert target is changed to vanilla dirt
- **THEN** the trample and dry-out checks fail, as they did in the smoke test

### Requirement: What still needs a person
The in-game checklist SHALL contain only what an automated check can't judge: how things look and sound, and sleeping through a night with real players. Each change's tasks SHALL say which of its checks are automated and which are on the checklist.

#### Scenario: Look and sound
- **WHEN** a change adds a particle or sound
- **THEN** its tasks put "see and hear it" on the checklist and automate everything else about the moment

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
