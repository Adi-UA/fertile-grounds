# release-2-0 Specification

## Purpose
Defines what Fertile Grounds 2.0.0 supports, how players learn that older versions stay behind, and the demo world used to show the mod off.

## Requirements

### Requirement: Supported versions
Fertile Grounds 2.0.0 SHALL ship for Minecraft 26.1, 26.2, 26.3, and 1.21.1, as a Fabric, a NeoForge, and a Forge jar for each version. Players on 1.20.1 SHALL keep 1.2.0, and players on 1.21.11 and 1.21.10 SHALL keep 1.2.0 until their own 2.0.0 backports ship; every 1.2.0 file stays downloadable and unchanged. The 1.21.1 jars SHALL offer the same features as the 26.1 jars, except where Minecraft 1.21.1 itself lacks something, and the design names each such gap. Versions from 1.21.1 up to 26.1 SHALL get 2.0.0 only; later features ship for 26.1 and newer.

#### Scenario: Player on 1.21.11
- **WHEN** a 1.21.11 player opens the Modrinth or CurseForge page
- **THEN** they read that 2.0 runs on 26.1 or newer and on 1.21.1, that 2.0.0 is coming to 1.21.11 and 1.21.10, that 1.20.1 stays on 1.2.0, and the 1.2.0 files are still listed

#### Scenario: Player on 1.21.1
- **WHEN** a 1.21.1 player filters by their version and loader on Modrinth or CurseForge
- **THEN** they find a 2.0.0 file built for 1.21.1 for Fabric, NeoForge, and Forge, and the page says 1.21.1 gets 2.0.0 only

#### Scenario: Older 1.21.1 file stays
- **WHEN** a 1.21.1 player looks at the version list after 2.0.0 for 1.21.1 is published
- **THEN** the 1.2.0 file for 1.21.1 is still listed and downloadable

#### Scenario: Player on 26.2
- **WHEN** a 26.2 player filters by their version
- **THEN** they find a 2.0.0 file built for 26.2

#### Scenario: NeoForge player on 26.2
- **WHEN** a 26.2 player filters by their version and NeoForge on Modrinth or CurseForge
- **THEN** they find the 2.0.0 NeoForge file built for 26.2, with no required dependency listed

#### Scenario: Loader still in beta
- **WHEN** NeoForge or Forge has no stable or recommended build for a supported Minecraft version at upload time
- **THEN** that loader's file is published with the Beta release type, and its changelog names the loader build it was tested on

### Requirement: Consistent docs
The README, `docs/DETAILS.md`, `docs/ROADMAP.md`, `openspec/config.yaml`, and the porting and release skills SHALL name the same supported versions and loaders. The README on the 1.20.1 branch SHALL say it stays on 1.2.0 and point to `main` for 2.0. The READMEs on the 1.21.11 and 1.21.10 branches SHALL say 2.0.0 is coming more slowly, name one or two 1.21.1 compromises, and say versions from 1.21.1 up to 26.1 get 2.0.0 only. The README on the `1.21.1` branch SHALL say it carries 2.0.0 only.

#### Scenario: Reading an old branch
- **WHEN** someone opens the 1.21.11 branch on GitHub
- **THEN** its README says 2.0.0 is coming more slowly, gives a 1.21.1 compromise as an example, and links to `main`

#### Scenario: Reading the 1.21.1 branch
- **WHEN** someone opens the 1.21.1 branch on GitHub
- **THEN** its README says it carries 2.0.0 for 1.21.1 and gets no later features

#### Scenario: Loader requirements
- **WHEN** someone reads the README's Versions section
- **THEN** it says the Fabric jar needs Fabric API, the NeoForge and Forge jars need nothing else, and Jade tooltips work on Fabric and NeoForge

### Requirement: Demo world
Before publishing, a 26.3 save named "Fertile Grounds Demo" SHALL exist in `~/Library/Application Support/minecraft/saves/`, with cheats on, laid out so each core feature has its own spot along a short path: the soil comparison rows, a fairy farm with ten kinds of flowers and a hive, a Fairy Hotel garden, a Chime Hotel wired to a lamp, a Fairy Grove shrine with a clear area around it, a finished hero grove with Bean Fairies that hover in place for photos, the guide book on a lectern, and chests with every mod item. It SHALL load without errors in `runClient` and in a real 26.3 client with the 2.0.0 jar and Fabric API.

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
- `/function fgdemo:mode/demo` restores the still recording settings, and `/function fgdemo:mode/default` restores every mod gamerule to its default
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
