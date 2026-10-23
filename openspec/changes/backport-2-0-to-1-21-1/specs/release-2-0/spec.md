## MODIFIED Requirements

### Requirement: Supported versions
Fertile Grounds 2.0.0 SHALL ship for Minecraft 26.1, 26.2, 26.3, and 1.21.1, as a Fabric, a NeoForge, and a Forge jar for each version. Players on 1.21.11, 1.21.10, or 1.20.1 SHALL keep 1.2.0, which stays downloadable and unchanged. The 1.21.1 jars SHALL offer the same features as the 26.1 jars, except where Minecraft 1.21.1 itself lacks something, and the design names each such gap. Updates SHALL reach 26.x first and 1.21.1 later.

#### Scenario: Player on 1.21.11
- **WHEN** a 1.21.11 player opens the Modrinth or CurseForge page
- **THEN** they read that 2.0 runs on 26.1 or newer and on 1.21.1, that 1.2.0 remains the version for 1.21.11, 1.21.10, and 1.20.1, and the 1.2.0 files are still listed

#### Scenario: Player on 1.21.1
- **WHEN** a 1.21.1 player filters by their version and loader on Modrinth or CurseForge
- **THEN** they find a 2.0.0 file built for 1.21.1 for Fabric, NeoForge, and Forge, and the page says 1.21.1 gets updates later than 26.x

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
The README, `docs/DETAILS.md`, `docs/ROADMAP.md`, `openspec/config.yaml`, and the porting and release skills SHALL name the same supported versions and loaders. The README on each frozen branch (1.21.11, 1.21.10, 1.20.1) SHALL say that branch stays on 1.2.0 and point to `main` for 2.0, and the README on the `1.21.1` branch SHALL say it carries 2.0 and gets updates after 26.x.

#### Scenario: Reading an old branch
- **WHEN** someone opens the 1.21.11 branch on GitHub
- **THEN** its README says the branch stays on 1.2.0 and links to `main`

#### Scenario: Reading the 1.21.1 branch
- **WHEN** someone opens the 1.21.1 branch on GitHub
- **THEN** its README says it carries 2.0 for 1.21.1, that updates arrive after 26.x, and links to `main`

#### Scenario: Loader requirements
- **WHEN** someone reads the README's Versions section
- **THEN** it says the Fabric jar needs Fabric API, the NeoForge and Forge jars need nothing else, and Jade tooltips work on Fabric and NeoForge
