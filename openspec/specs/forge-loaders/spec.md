# forge-loaders Specification

## Purpose
Defines how Fertile Grounds behaves on NeoForge and Forge: the same game content as the Fabric build, each loader's own jar and metadata, and the loader-specific hooks that must still work.

## Requirements

### Requirement: Same content on every loader
The NeoForge and Forge builds SHALL register the same blocks, items, entity, sounds, mob effect, data components, advancement criteria, and gamerules as the Fabric build, with the same ids and default values. They SHALL load the same recipes, tags, loot tables, advancements, trades, and lang files.

#### Scenario: Gamerule defaults match
- **WHEN** a player creates a new world on NeoForge 26.3 or Forge 26.3 and runs `/gamerule fertilegrounds:enriched_boost_chance`
- **THEN** it reports the same default as on Fabric 26.3

#### Scenario: Shared tags
- **WHEN** a modpack adds a block to `#fertilegrounds:fairy_crops` with a datapack on Forge
- **THEN** Bean Fairies grow that crop, as they do on Fabric

### Requirement: Loader jars and metadata
Each version branch SHALL build `fertilegrounds-neoforge-<mod_version>+<mc>.jar` and `fertilegrounds-forge-<mod_version>+<mc>.jar`. Each SHALL use mod id `fertilegrounds` and version `<mod_version>+<mc>`, and limit its Minecraft range to that branch's version. Neither SHALL require anything besides its loader and Minecraft. The NeoForge jar SHALL list Jade as optional.

#### Scenario: Wrong Minecraft version
- **WHEN** a player puts the 26.2 Forge jar into a 26.3 Forge instance
- **THEN** Forge refuses to load it and names the Minecraft version it needs

#### Scenario: No extra downloads
- **WHEN** a player installs only NeoForge and the Fertile Grounds NeoForge jar
- **THEN** the game starts and every feature works

### Requirement: Loader hooks work everywhere
On NeoForge and Forge, these SHALL behave as on Fabric:
- tilling Enriched and Super Enriched Dirt with a hoe
- the first-join guide book
- night fairy visits
- Chime Hotel ringing
- Fairy Grove building
- the creative tab
- Bean Fairy attributes

#### Scenario: Tilling
- **WHEN** a player uses a hoe on Enriched Dirt on Forge
- **THEN** it becomes Enriched Farmland, as on Fabric

#### Scenario: Guide book once
- **WHEN** a player joins a NeoForge world for the first time, leaves, and joins again
- **THEN** they get the guide book once

### Requirement: Saved data survives restarts
On NeoForge and Forge, pending fairy visits, gift cooldowns, Fairy Grove build jobs, and each player's guide book flag SHALL persist across a world save and reload. A world moved between loaders SHALL keep every block, item, and entity. It SHALL also load without errors, and the mod's saved per-world and per-player data SHALL start fresh.

#### Scenario: Grove build across a restart
- **WHEN** a Fairy Grove is half built and the Forge server restarts
- **THEN** the builder fairies finish the same grove after the restart

### Requirement: Jade where Jade exists
With Jade installed on NeoForge, the soil, visit, hotel, and chime tooltips SHALL show the same lines as on Fabric. Jade publishes no Forge build for 26.x, so the Forge jar SHALL load with no Jade code and no Jade warnings. On any loader without Jade, the mod SHALL load with no errors about Jade.

#### Scenario: Without Jade
- **WHEN** the NeoForge test server runs with Jade absent
- **THEN** every GameTest that doesn't need Jade passes and the log has no Jade error

#### Scenario: Forge
- **WHEN** the Forge test server runs
- **THEN** the Jade data tests are skipped for Forge, with the reason next to them, and every other GameTest passes

### Requirement: Dedicated server safety
The NeoForge and Forge jars SHALL run on a dedicated server, where no client classes exist, without loading any client-only class.

#### Scenario: Server start
- **WHEN** the Forge jar runs on a dedicated 26.3 server
- **THEN** the server starts and logs no class-loading error for `fertilegrounds`
