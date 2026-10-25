# chime-hotel Specification

## Purpose
Lets a hotel fairy tell players when the crops around it are ready to harvest by ringing a soft chime once.

## Requirements

### Requirement: Crafting a Chime Hotel
The game SHALL craft one Chime Hotel from a shapeless recipe of 1 Occupied Fairy Hotel, 1 Note Block, and 1 Spyglass. Breaking a Chime Hotel SHALL drop a Chime Hotel. A Chime Hotel SHALL count as an occupied hotel for every hotel effect.

#### Scenario: Upgrade
- **WHEN** a player breaks their occupied hotel and crafts it with a note block and a spyglass
- **THEN** they get a Chime Hotel, and placing it gives the same crop boost, honey, creeper, and trample effects as the occupied hotel

### Requirement: Ready chime
Every 5 seconds, a Chime Hotel SHALL count the `#fertilegrounds:fairy_crops` blocks within 8 blocks horizontally and 2 vertically. When it counts at least 4 and at least `fertilegrounds:chime_ready_percent` percent of them (default 80) are fully grown, it SHALL play its three-note chime once, which players hear up to 32 blocks away. It SHALL NOT chime again until the field drops below that share and later reaches it again.

#### Scenario: Field ready
- **WHEN** 41 of 50 wheat crops near a Chime Hotel reach full growth
- **THEN** the hotel plays its chime once

#### Scenario: Stays quiet
- **WHEN** the field stays fully grown for 10 more minutes
- **THEN** the hotel doesn't chime again

#### Scenario: Next harvest
- **WHEN** the player harvests and replants the field, and it grows back to 80%
- **THEN** the hotel chimes again

#### Scenario: Not enough crops
- **WHEN** only 3 crops sit near the hotel, all fully grown
- **THEN** the hotel doesn't chime

### Requirement: Placement chime
Placing a Chime Hotel SHALL play its chime once, so players learn its sound.

#### Scenario: First placement
- **WHEN** a player places a Chime Hotel in an empty field
- **THEN** the chime plays once
