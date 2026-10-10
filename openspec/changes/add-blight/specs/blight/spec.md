# Spec Delta

## Purpose

A rare soil curse that spreads across farmland, withers crops, and gives players and server rivals something to fight with Fertilizer and fairies.

## ADDED Requirements

### Requirement: Blighted Dirt block
The game SHALL add a Blighted Dirt block that drops itself when broken with or without a tool. Nothing SHALL grow on it except dead bushes, and a hoe SHALL NOT till it.

#### Scenario: Mining it
- **WHEN** a player breaks Blighted Dirt with a shovel or by hand
- **THEN** it drops one Blighted Dirt

#### Scenario: No farming on it
- **WHEN** a player uses a hoe on Blighted Dirt or tries to plant a sapling or flower on it
- **THEN** nothing happens

### Requirement: Spreading
On each random tick, Blighted Dirt SHALL pick one of the 6 touching blocks and, if it is in the `fertilegrounds:blightable` block tag, turn it into Blighted Dirt with a chance of `fertilegrounds:blight_spread_chance` percent (default 20). Enriched soil SHALL resist with half that chance and Super Enriched soil with a quarter. The tag SHALL hold dirt, grass blocks, farmland, sand, red sand, and every Enriched and Super Enriched soil by default. A chance of 0 SHALL stop all spreading.

#### Scenario: Creeping across a field
- **WHEN** one Blighted Dirt block sits in the middle of a wheat field on plain farmland for an in-game day
- **THEN** the farmland touching it has turned to Blighted Dirt, block by block

#### Scenario: A path stops it
- **WHEN** a dirt path or a row of stone separates the blight from a field
- **THEN** the blight never crosses it

#### Scenario: Rich soil holds out longer
- **WHEN** blight touches plain farmland and Super Enriched Farmland side by side
- **THEN** on average the Super Enriched Farmland takes four times as long to fall

#### Scenario: Server owner turns it off
- **WHEN** an operator sets `/gamerule fertilegrounds:blight_spread_chance 0`
- **THEN** existing Blighted Dirt stays but never spreads

#### Scenario: Modpack extends it
- **WHEN** a pack adds `minecraft:podzol` to `fertilegrounds:blightable`
- **THEN** blight spreads into podzol too

### Requirement: Withering
When blight takes a block, any plant growing on that block SHALL wither into a dead bush and drop nothing.

#### Scenario: Crops lost
- **WHEN** blight takes the farmland under a ripe wheat crop
- **THEN** the wheat turns into a dead bush with no wheat or seeds dropped

### Requirement: Witch curse
When a player kills a witch, with a chance of `fertilegrounds:witch_blight_chance` percent (default 1), the game SHALL turn every `fertilegrounds:blightable` block in the 3x3 patch under the witch into Blighted Dirt. If at least one block turns, every player within 32 blocks SHALL get a chat message saying the witch's curse has blighted the ground nearby. If no block under the witch is in the tag, nothing SHALL happen and no message SHALL appear. Witches killed by anything other than a player SHALL never blight.

#### Scenario: Rare curse
- **WHEN** a player kills a witch standing on grass and the 1% roll succeeds
- **THEN** the grass under it turns to Blighted Dirt and the player reads that blight has started there

#### Scenario: Stone floor
- **WHEN** the roll succeeds but the witch died on stone
- **THEN** no block changes and no message appears

#### Scenario: Not a player kill
- **WHEN** a witch dies from lava or a zombie
- **THEN** no blight starts

### Requirement: Crafting Blighted Dirt
The game SHALL craft 1 Blighted Dirt from a shapeless recipe of 1 Dirt, 1 Nether Wart, 1 Blaze Rod, and 1 Fermented Spider Eye. The recipe SHALL unlock in the recipe book once the player holds a Fermented Spider Eye.

#### Scenario: Sabotage
- **WHEN** a player crafts Blighted Dirt and places it next to a rival's farmland
- **THEN** the blight starts spreading into that farm

### Requirement: Cures
Using Fertilizer on Blighted Dirt SHALL turn it into plain dirt and use up one Fertilizer, by hand or from a dispenser. When a Bean Fairy grows its 5x5 patch, it SHALL turn every Blighted Dirt in that patch, at the crop's level or one block below, into plain dirt.

#### Scenario: Fertilizer cure
- **WHEN** a player uses Fertilizer on Blighted Dirt
- **THEN** it becomes dirt, which the player can till again, and one Fertilizer is used up

#### Scenario: Dispenser cure
- **WHEN** a powered dispenser holding Fertilizer faces Blighted Dirt
- **THEN** the block becomes dirt

#### Scenario: Fairy cure
- **WHEN** a Fairy Dust visit grows a crop whose neighbors stand on Blighted Dirt
- **THEN** the blighted blocks in its 5x5 patch turn back into dirt
