## MODIFIED Requirements

### Requirement: Supported versions
Fertile Grounds 2.0 SHALL ship for Minecraft 26.1, 26.2, 26.3, 1.21.11, 1.21.10, and 1.21.1, as a Fabric, a NeoForge, and a Forge jar for each version. It ships as 2.0.0, with 2.0.1 patch jars only for the versions whose jar changed: 26.1 and 26.2 (recipe advancements now unlock their recipes) and 1.21.1 (stems stand on mod farmland); 26.3, 1.21.11, and 1.21.10 stay on 2.0.0. Players on 1.20.1 SHALL keep 1.2.0; every 1.2.0 file stays downloadable and unchanged. The 1.21.11, 1.21.10, and 1.21.1 jars SHALL offer the same features as the 26.1 jars, except where that Minecraft version itself lacks something, and the design names each such gap. Versions from 1.21.1 up to 26.1 SHALL get 2.0.0 only; later features ship for 26.1 and newer.

#### Scenario: Player on 1.21.10
- **WHEN** a 1.21.10 player filters by their version and loader on Modrinth or CurseForge
- **THEN** they find a 2.0.0 file built for 1.21.10 for Fabric, NeoForge, and Forge, and the page says 1.21.10 gets 2.0.0 only

#### Scenario: Older 1.21.10 file stays
- **WHEN** a 1.21.10 player looks at the version list after 2.0.0 for 1.21.10 is published
- **THEN** the 1.2.0 file for 1.21.10 is still listed and downloadable

#### Scenario: Player on 1.21.11
- **WHEN** a 1.21.11 player filters by their version and loader on Modrinth or CurseForge
- **THEN** they find a 2.0.0 file built for 1.21.11 for Fabric, NeoForge, and Forge, and the page says 1.21.11 gets 2.0.0 only

#### Scenario: Older 1.21.11 file stays
- **WHEN** a 1.21.11 player looks at the version list after 2.0.0 for 1.21.11 is published
- **THEN** the 1.2.0 file for 1.21.11 is still listed and downloadable

#### Scenario: Player on 1.21.1
- **WHEN** a 1.21.1 player filters by their version and loader on Modrinth or CurseForge
- **THEN** they find a 2.0.1 file built for 1.21.1 for Fabric, NeoForge, and Forge, and the page says 1.21.1 gets 2.0 only

#### Scenario: Older 1.21.1 file stays
- **WHEN** a 1.21.1 player looks at the version list after 2.0.1 for 1.21.1 is published
- **THEN** the 2.0.0 and 1.2.0 files for 1.21.1 are still listed and downloadable

#### Scenario: Player on 26.2
- **WHEN** a 26.2 player filters by their version
- **THEN** they find a 2.0.1 file built for 26.2

#### Scenario: NeoForge player on 26.2
- **WHEN** a 26.2 player filters by their version and NeoForge on Modrinth or CurseForge
- **THEN** they find the 2.0.1 NeoForge file built for 26.2, with no required dependency listed

#### Scenario: Loader still in beta
- **WHEN** NeoForge or Forge has no stable or recommended build for a supported Minecraft version at upload time
- **THEN** that loader's file is published with the Beta release type, and its changelog names the loader build it was tested on

### Requirement: Consistent docs
The README, `docs/DETAILS.md`, `docs/ROADMAP.md`, `openspec/config.yaml`, and the porting and release skills SHALL name the same supported versions and loaders. The README on the 1.20.1 branch SHALL say it stays on 1.2.0 and point to `main` for 2.0. The READMEs on the `1.21.11`, `1.21.10`, and `1.21.1` branches SHALL say they carry 2.0.0 only.

#### Scenario: Reading an old branch
- **WHEN** someone opens the 1.20.1 branch on GitHub
- **THEN** its README says it stays on 1.2.0 and links to `main` for 2.0

#### Scenario: Reading the 1.21.10 branch
- **WHEN** someone opens the 1.21.10 branch on GitHub
- **THEN** its README says it carries 2.0.0 for 1.21.10 and gets no later features

#### Scenario: Reading the 1.21.1 branch
- **WHEN** someone opens the 1.21.1 branch on GitHub
- **THEN** its README says it carries 2.0.0 for 1.21.1 and gets no later features

#### Scenario: Reading the 1.21.11 branch
- **WHEN** someone opens the 1.21.11 branch on GitHub
- **THEN** its README says it carries 2.0.0 for 1.21.11 and gets no later features

#### Scenario: Loader requirements
- **WHEN** someone reads the README's Versions section
- **THEN** it says the Fabric jar needs Fabric API, the NeoForge and Forge jars need nothing else, and Jade tooltips work on Fabric and NeoForge
