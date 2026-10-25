# release-2-0 Specification

## Purpose
Defines which Minecraft versions and loaders Fertile Grounds 2.0 supports, how players on each version find the right file, and the demo world that shows the mod off.

## Requirements

### Requirement: Supported versions
Fertile Grounds 2.0 SHALL ship for Minecraft 26.1, 26.2, 26.3, 1.21.11, 1.21.10, and 1.21.1, as a Fabric, a NeoForge, and a Forge jar for each version. The newest file SHALL be 2.0.1 for 26.1, 26.2, 1.21.11, and 1.21.1, where the 2.0.1 patch makes recipe advancements unlock their recipes (26.1 and 26.2), lets stems stand on mod farmland (1.21.1), and draws the Fairy Hotel glass instead of black (1.21.11), and 2.0.0 for 26.3 and 1.21.10. Players on 1.20.1 SHALL keep 1.2.0, and every 1.2.0 file SHALL stay downloadable and unchanged. The 1.21.11, 1.21.10, and 1.21.1 jars SHALL offer the same features as the 26.1 jars, except where that Minecraft version lacks something, and the port's design names each such gap. Versions older than 26.1 SHALL get no features after 2.0; later features ship for 26.1 and newer.

#### Scenario: Player on 1.21.10
- **WHEN** a 1.21.10 player filters by their version and loader on Modrinth or CurseForge
- **THEN** they find a 2.0.0 file built for 1.21.10 for Fabric, NeoForge, and Forge, and the page says 1.21.10 gets 2.0.0 only

#### Scenario: Older 1.21.10 file stays
- **WHEN** a 1.21.10 player looks at the version list
- **THEN** the 1.2.0 file for 1.21.10 is still listed and downloadable

#### Scenario: Player on 1.21.11
- **WHEN** a 1.21.11 player filters by their version and loader on Modrinth or CurseForge
- **THEN** they find a 2.0.1 file built for 1.21.11 for Fabric, NeoForge, and Forge, and the page says 1.21.11 gets 2.0 only

#### Scenario: Older 1.21.11 file stays
- **WHEN** a 1.21.11 player looks at the version list
- **THEN** the 1.2.0 file for 1.21.11 is still listed and downloadable

#### Scenario: Player on 1.21.1
- **WHEN** a 1.21.1 player filters by their version and loader on Modrinth or CurseForge
- **THEN** they find a 2.0.1 file built for 1.21.1 for Fabric, NeoForge, and Forge, and the page says 1.21.1 gets 2.0 only

#### Scenario: Older 1.21.1 file stays
- **WHEN** a 1.21.1 player looks at the version list
- **THEN** the 2.0.0 and 1.2.0 files for 1.21.1 are still listed and downloadable

#### Scenario: Player on 26.2
- **WHEN** a 26.2 player filters by their version
- **THEN** they find a 2.0.1 file built for 26.2

#### Scenario: NeoForge player on 26.2
- **WHEN** a 26.2 player filters by their version and NeoForge on Modrinth or CurseForge
- **THEN** they find the 2.0.1 NeoForge file built for 26.2, with no required dependency listed

#### Scenario: Loader still in beta
- **WHEN** NeoForge or Forge has no stable or recommended build for a supported Minecraft version when its file is uploaded
- **THEN** that loader's file is published with the Beta release type, and its changelog names the loader build it was tested on

### Requirement: Consistent docs
The README, `docs/DETAILS.md`, `docs/ROADMAP.md`, `openspec/config.yaml`, and the porting and release skills SHALL name the same supported versions and loaders. The README on the 1.20.1 branch SHALL say it stays on 1.2.0 and point to `main` for 2.0. The READMEs on the `1.21.11`, `1.21.10`, and `1.21.1` branches SHALL say they carry 2.0 and get no later features.

#### Scenario: Reading an old branch
- **WHEN** someone opens the 1.20.1 branch on GitHub
- **THEN** its README says it stays on 1.2.0 and links to `main` for 2.0

#### Scenario: Reading the 1.21.10 branch
- **WHEN** someone opens the 1.21.10 branch on GitHub
- **THEN** its README says it carries 2.0.0 for 1.21.10 and gets no later features

#### Scenario: Reading the 1.21.1 branch
- **WHEN** someone opens the 1.21.1 branch on GitHub
- **THEN** its README says it carries 2.0 for 1.21.1 and gets no later features

#### Scenario: Reading the 1.21.11 branch
- **WHEN** someone opens the 1.21.11 branch on GitHub
- **THEN** its README says it carries 2.0.1 for 1.21.11 and gets no later features

#### Scenario: Loader requirements
- **WHEN** someone reads the README's Versions section
- **THEN** it says the Fabric jar needs Fabric API, the NeoForge and Forge jars need nothing else, and Jade tooltips work on Fabric and NeoForge

### Requirement: Demo world
A 26.3 save named "Fertile Grounds Demo" SHALL exist in `~/Library/Application Support/minecraft/saves/`, with cheats on, laid out so each core feature has its own spot along a short path: the soil comparison rows, a fairy farm with ten kinds of flowers and a hive, a Fairy Hotel garden, a Chime Hotel wired to a lamp, a Fairy Grove shrine with a clear area around it, a finished hero grove with Bean Fairies that hover in place for photos, the guide book on a lectern, and chests with every mod item. It SHALL load without errors in `runClient` and in a real 26.3 client with the 2.0.0 jar and Fabric API.

#### Scenario: Recording a visit
- **WHEN** the user loads the demo world, uses Fairy Dust on the fairy farm from the supply chest, and sets the time to night
- **THEN** a fairy visits within about 30 seconds with nothing else to set up

#### Scenario: Harvest alarm shot
- **WHEN** the user waits at the Chime Hotel station
- **THEN** its field ripens, the hotel chimes, and the lamp blinks

#### Scenario: Grove on camera
- **WHEN** the user takes a diamond from the supply chest and uses it on the shrine's Chime Hotel
- **THEN** the safety check passes and the fairies build the full grove while the user records

### Requirement: Demo world doubles as a test world
The demo save SHALL keep its datapack installed so the user can test features quickly. A sign board at spawn SHALL list these commands:
- `/function fgdemo:reset/<station>` and `/function fgdemo:reset/all` rebuild a station, or every station, to its starting state, including the grove area after a grove was built
- `/function fgdemo:mode/test` sets fast-testing gamerules: random tick speed 30, `fairy_visit_chance` 100, `jarred_fairy_days` 1, and night
- `/function fgdemo:mode/demo` restores the calm recording settings (no weather changes, mobs, or fairy visits), and `/function fgdemo:mode/default` restores every mod gamerule to its default
The world SHALL also hold an empty test lot of flat grass, with water nearby and room for a second grove, and a supply chest with Bean Fairy spawn eggs.

#### Scenario: Grove again
- **WHEN** the user builds a grove, then runs `/function fgdemo:reset/grove`
- **THEN** the hillside and the shrine are back as they were, ready for another diamond

#### Scenario: Quick growth check
- **WHEN** the user runs `/function fgdemo:mode/test` and watches the soil rows
- **THEN** the enriched rows visibly outgrow the vanilla row within a minute, and `/function fgdemo:reset/soil` replants them

#### Scenario: Hero shot
- **WHEN** the user walks past the grove shrine to the hero grove at any time, before or after restarting the world
- **THEN** about 30 Bean Fairies hover over its ripe plots and shrine, wings beating, and none of them fly away or despawn

#### Scenario: Back to recording
- **WHEN** the user runs `/function fgdemo:mode/demo`
- **THEN** the weather, mobs, and fairy visits are still again

### Requirement: Shaders for recording
`./gradlew runClient -Pshaders` SHALL start the dev client with Sodium and Iris, so a shader pack placed in `run/shaderpacks/` can be turned on. Neither mod nor any shader pack SHALL ship in the mod jar or be committed.

#### Scenario: Shaders off by default
- **WHEN** a developer runs `./gradlew runClient` without the flag
- **THEN** the client starts without Sodium or Iris
