## MODIFIED Requirements

### Requirement: Supported versions
Fertile Grounds 2.0.0 SHALL ship for Minecraft 26.1, 26.2, and 26.3 only, as a Fabric, a NeoForge, and a Forge jar for each version. Players on 1.21.11, 1.21.10, 1.21.1, or 1.20.1 SHALL keep 1.2.0, which stays downloadable and unchanged.

#### Scenario: Player on 1.21.11
- **WHEN** a 1.21.11 player opens the Modrinth or CurseForge page
- **THEN** they read that 2.0 needs 26.1 or newer and that 1.2.0 remains the version for 1.21.11 and older, and the 1.2.0 files are still listed

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
The README, `docs/DETAILS.md`, `docs/ROADMAP.md`, `openspec/config.yaml`, and the porting and release skills SHALL name the same supported versions and loaders, and the README on each frozen branch SHALL say that branch stays on 1.2.0 and point to `main` for 2.0.

#### Scenario: Reading an old branch
- **WHEN** someone opens the 1.21.11 branch on GitHub
- **THEN** its README says the branch stays on 1.2.0 and links to `main`

#### Scenario: Loader requirements
- **WHEN** someone reads the README's Versions section
- **THEN** it says the Fabric jar needs Fabric API, the NeoForge and Forge jars need nothing else, and Jade tooltips work on Fabric and NeoForge
