# Spec Delta

## Purpose

Defines what Fertile Grounds 2.0.0 supports, how players learn that older versions stay behind, and the demo world used to show the mod off.

## ADDED Requirements

### Requirement: Supported versions
Fertile Grounds 2.0.0 SHALL ship for Minecraft 26.1, 26.2, and 26.3 only. Players on 1.21.11, 1.21.10, 1.21.1, or 1.20.1 SHALL keep 1.2.0, which stays downloadable and unchanged.

#### Scenario: Player on 1.21.11
- **WHEN** a 1.21.11 player opens the Modrinth or CurseForge page
- **THEN** they read that 2.0 needs 26.1 or newer and that 1.2.0 remains the version for 1.21.11 and older, and the 1.2.0 files are still listed

#### Scenario: Player on 26.2
- **WHEN** a 26.2 player filters by their version
- **THEN** they find a 2.0.0 file built for 26.2

### Requirement: Consistent docs
The README, `docs/DETAILS.md`, `docs/ROADMAP.md`, `openspec/config.yaml`, and the porting and release skills SHALL name the same supported versions, and the README on each frozen branch SHALL say that branch stays on 1.2.0 and point to `main` for 2.0.

#### Scenario: Reading an old branch
- **WHEN** someone opens the 1.21.11 branch on GitHub
- **THEN** its README says the branch stays on 1.2.0 and links to `main`

### Requirement: Demo world
Before publishing, a 26.3 save named "Fertile Grounds Demo" SHALL exist in `~/Library/Application Support/minecraft/saves/`, with cheats on, laid out so each core feature has its own spot along a short path: the soil comparison rows, a fairy farm with ten kinds of flowers and a hive, a Fairy Hotel garden, a Chime Hotel wired to a lamp, the guide book on a lectern, and chests with every mod item. It SHALL load without errors in `runClient` and in a real 26.3 client with the 2.0.0 jar and Fabric API.

#### Scenario: Recording a visit
- **WHEN** the user loads the demo world, uses Fairy Dust on the fairy farm from the supply chest, and sets the time to night
- **THEN** a fairy visits within about 30 seconds with nothing else to set up

#### Scenario: Harvest alarm shot
- **WHEN** the user waits at the Chime Hotel station
- **THEN** its field ripens, the hotel chimes, and the lamp blinks

### Requirement: Shaders for recording
`./gradlew runClient -Pshaders` SHALL start the dev client with Sodium and Iris, so a shader pack placed in `run/shaderpacks/` can be turned on. Neither mod nor any shader pack SHALL ship in the mod jar or be committed.

#### Scenario: Shaders off by default
- **WHEN** a developer runs `./gradlew runClient` without the flag
- **THEN** the client starts without Sodium or Iris
