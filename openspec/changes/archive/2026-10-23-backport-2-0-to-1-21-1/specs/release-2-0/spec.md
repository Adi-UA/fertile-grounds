## MODIFIED Requirements

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
