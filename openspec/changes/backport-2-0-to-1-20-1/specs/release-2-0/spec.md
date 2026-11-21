## MODIFIED Requirements

### Requirement: Supported versions
Fertile Grounds 2.0 SHALL ship for Minecraft 26.1, 26.2, 26.3, 1.21.11, 1.21.10, and 1.21.1, as a Fabric, a NeoForge, and a Forge jar for each version, and for Minecraft 1.20.1 as a Fabric and a Forge jar, with no NeoForge listing. The newest file SHALL be 2.0.1 for 26.1, 26.2, 1.21.11, 1.21.1, and 1.20.1, where the 2.0.1 patch makes recipe advancements unlock their recipes (26.1 and 26.2), lets stems stand on mod farmland (1.21.1 and 1.20.1), and draws the Fairy Hotel glass instead of black (1.21.11), and 2.0.0 for 26.3 and 1.21.10. Every 1.2.0 file SHALL stay downloadable and unchanged. The 1.21.11, 1.21.10, 1.21.1, and 1.20.1 jars SHALL offer the same features as the 26.1 jars, except where that Minecraft version lacks something, and the port's design names each such gap. Versions older than 26.1 SHALL get no features after 2.0; later features ship for 26.1 and newer.

#### Scenario: Player on 1.20.1
- **WHEN** a 1.20.1 player filters by their version and Fabric or Forge on Modrinth or CurseForge
- **THEN** they find a 2.0.1 file built for 1.20.1 for that loader, and the page says 1.20.1 gets 2.0 only

#### Scenario: NeoForge player on 1.20.1
- **WHEN** a 1.20.1 player filters by their version and NeoForge
- **THEN** they find no 2.0 file, and the branch README says a NeoForge jar for 1.20.1 is TBD

#### Scenario: Older 1.20.1 file stays
- **WHEN** a 1.20.1 player looks at the version list
- **THEN** the 1.2.0 Fabric file for 1.20.1 is still listed and downloadable

#### Scenario: 1.20.1 world from 1.2.0
- **WHEN** a player updates a 1.20.1 Fabric world from 1.2.0 to 2.0.1
- **THEN** the world loads with its soil blocks, items, and pending fairy visits intact

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
The README, `docs/DETAILS.md`, `docs/ROADMAP.md`, `openspec/config.yaml`, and the porting and release skills SHALL name the same supported versions and loaders. The READMEs on the `1.21.11`, `1.21.10`, `1.21.1`, and `1.20.1` branches SHALL say they carry 2.0 and get no later features, and the `1.20.1` README SHALL say a NeoForge jar for 1.20.1 is TBD.

#### Scenario: Reading an old branch
- **WHEN** someone opens the 1.20.1 branch on GitHub
- **THEN** its README says it carries 2.0.1 for 1.20.1 on Fabric and Forge, gets no later features, and says a NeoForge jar is TBD

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
- **THEN** it says the Fabric jar needs Fabric API, the NeoForge and Forge jars need nothing else, Jade tooltips work on Fabric and NeoForge, and on 1.20.1 they work on Fabric and Forge without the lines that need server data
