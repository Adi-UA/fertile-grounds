# fairy-grove Specification

## Purpose
Lets endgame players spend a diamond so their fairies turn the land around a shrine into a lit, flowered garden of watered farm plots.

## Requirements

### Requirement: Shrine
A shrine SHALL be complete when a Chime Hotel stands on Super Enriched Dirt, an occupied Fairy Hotel or Chime Hotel (standing or hanging) sits exactly 3 blocks north, south, east, and west of it at the same height, and at least 4 blocks in `#fertilegrounds:fairy_flowers` sit within 3 blocks of it horizontally and 1 block up or down.

#### Scenario: Missing hotel
- **WHEN** a player uses a diamond on a Chime Hotel with only 3 hotels around it
- **THEN** the diamond stays in their hand and the action bar says the shrine needs a Fairy Hotel with a fairy 3 blocks away on each side

#### Scenario: Wrong soil
- **WHEN** the Chime Hotel stands on grass instead of Super Enriched Dirt
- **THEN** nothing is built, the diamond stays, and the action bar says the hotel must stand on Super Enriched Dirt

### Requirement: Safety check
Before using the diamond, the game SHALL check every block in the 35x35 area centered on the shrine, from 8 blocks below the shrine's soil to 24 blocks above it, excluding the center 9x9 plot. If any block that is not air and not in `#fertilegrounds:grove_replaceable` sits there, building SHALL NOT start, the diamond SHALL stay, and the action bar SHALL name that block and its coordinates. The tag SHALL hold natural terrain by default: dirt and grass, stone, ores, and terracotta, sand, sandstone, and gravel, clay and mud, moss, snow and ice, logs and leaves, plants and crops, farmland, water, and lava.

#### Scenario: A chest nearby
- **WHEN** a chest sits 10 blocks from the shrine
- **THEN** the action bar says a Chest at its coordinates is in the way, and the diamond stays

#### Scenario: Wild land
- **WHEN** the area holds only hills, trees, a pond, and tall grass
- **THEN** the check passes and the diamond is used up

#### Scenario: Modpack protects a block
- **WHEN** a pack removes `#minecraft:logs` from `fertilegrounds:grove_replaceable`
- **THEN** a tree in the area stops the grove from building

### Requirement: Building the grove
After a passed check, the game SHALL use up the diamond and rebuild the area over about 30 seconds, one column at a time, while 4 Bean Fairies fly over it. Each column SHALL be cleared to air up to 24 blocks above the shrine's soil level and filled with dirt down to solid ground, at most 8 blocks deep. If a column holds a non-replaceable block by the time the fairies reach it, that column SHALL be skipped. Building SHALL continue after the world is saved and reloaded.

#### Scenario: A hill and a dip
- **WHEN** the area has a 6-block hill on one side and a 3-block dip on the other
- **THEN** after building, the whole area outside the center plot is level with the shrine's soil

#### Scenario: Player drops a chest mid-build
- **WHEN** a player places a chest in a column the fairies haven't reached yet
- **THEN** that column is left as it is and the chest is untouched

#### Scenario: Reload mid-build
- **WHEN** the server stops halfway through building and starts again
- **THEN** the fairies finish the rest of the grove

### Requirement: Garden layout
The finished grove SHALL hold, around the untouched center 9x9 plot:
- 8 farm plots of 9x9, each of moist Enriched Farmland around one water block in its center, with nothing planted
- one-block grass borders between and around the plots, with a small flower on about 3 of every 4 border blocks
- a beehive topped with a lantern at each of the 4 inner border crossings
- a flowering azalea leaf block, which never decays, topped with a lantern at each of the 12 outer border crossings
- a 2-block meadow rim of grass, flowers, and azalea leaf bushes, with a lantern on about 1 bush in 4

#### Scenario: Ready to plant
- **WHEN** the grove finishes
- **THEN** every farmland block is moist, and a player can plant seeds in all of them at once

#### Scenario: Lit at night
- **WHEN** night falls on a finished grove
- **THEN** lanterns light the borders and rim, so no mob spawns on the plots or paths

### Requirement: Server control
When the gamerule `fertilegrounds:fairy_grove` is false, using a diamond on a shrine SHALL do nothing except an action bar message saying fairy groves are turned off.

#### Scenario: Turned off
- **WHEN** an operator sets `/gamerule fertilegrounds:fairy_grove false`
- **THEN** a diamond on a complete shrine is not used up and nothing is built
