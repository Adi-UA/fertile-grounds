# fairy-hotel-effects Specification

## Purpose
Defines how an occupied Fairy Hotel guards and gently helps the farm around it, so a hotel fairy is a useful farm companion.

## Requirements

### Requirement: Gentle crop boost
On each random tick, an occupied hotel SHALL pick one random growing `#fertilegrounds:fairy_crops` block within 4 blocks horizontally and 2 vertically and advance it one bone-meal step with `fertilegrounds:fairy_hotel_boost_chance` percent (default 50). An Empty Fairy Hotel SHALL NOT boost.

#### Scenario: Crop near a hotel
- **WHEN** an occupied hotel sits 3 blocks from growing wheat with random ticks on
- **THEN** the wheat grows faster than identical wheat 10 blocks away

#### Scenario: Empty hotel
- **WHEN** a hotel has no fairy
- **THEN** nearby crops grow at their normal rate

### Requirement: Clustered hotels don't multiply
When occupied hotels sit within 8 blocks of each other, only one hotel in that cluster SHALL boost crops or add honey, chosen the same way every time.

#### Scenario: Two hotels side by side
- **WHEN** a player places two occupied hotels next to each other in a wheat field
- **THEN** the wheat grows at the same rate as with one hotel

### Requirement: Bee pollination
On a random tick, if a bee carrying nectar is within 6 blocks of an occupied hotel, the hotel SHALL make one extra boost attempt.

#### Scenario: Bees nearby
- **WHEN** a beehive's bees carry nectar past an occupied hotel in a wheat field
- **THEN** the wheat near the hotel grows faster than with the hotel alone

### Requirement: Honey boost
On each random tick, with `fertilegrounds:fairy_hotel_honey_chance` percent (default 10), an occupied hotel SHALL add one honey level to one random block from `#minecraft:beehives` within 4 blocks horizontally and 2 vertically that is not already full.

#### Scenario: Hive next to a hotel
- **WHEN** an occupied hotel sits 2 blocks from a beehive with random ticks on
- **THEN** the hive fills with honey faster than an identical hive 10 blocks away

#### Scenario: Full hive
- **WHEN** the only nearby hive already has honey level 5
- **THEN** nothing changes

### Requirement: Petting
Using a Brush on an occupied hotel that hasn't been petted since the last dawn SHALL mark it petted, use 1 Brush durability, and show hearts. Until the next dawn, a petted hotel SHALL use `fertilegrounds:fairy_hotel_petted_boost_chance` (default 65) instead of the normal boost chance. Using a Brush on a hotel already petted today SHALL do nothing and use no durability. Breaking a hotel SHALL clear its petted state.

#### Scenario: Morning ritual
- **WHEN** a player brushes an occupied hotel in the morning
- **THEN** hearts appear and nearby crops get the 65% boost chance until the next dawn

#### Scenario: Second brush the same day
- **WHEN** the player brushes the same hotel again that afternoon
- **THEN** nothing happens and the brush keeps its durability

#### Scenario: Next day
- **WHEN** dawn passes after a hotel was petted
- **THEN** the hotel returns to the 50% chance and can be petted again

### Requirement: Fairy Speed near hotels
While `fertilegrounds:fairy_hotel_speed` is true (default true), every player within 8 blocks of an occupied hotel SHALL have the Fairy Speed effect, which raises movement speed by 10%, shows vanilla's Speed icon with a translated "Fairy Speed" name, and ends within 3 seconds of leaving the area. Several hotels SHALL NOT stack.

#### Scenario: Working the field
- **WHEN** a player walks within 6 blocks of an occupied hotel
- **THEN** the Fairy Speed icon appears and the player moves 10% faster

#### Scenario: Leaving the farm
- **WHEN** the player walks 20 blocks away
- **THEN** the effect ends within 3 seconds

#### Scenario: Three hotels
- **WHEN** a player stands between three occupied hotels
- **THEN** the player moves 10% faster, not 30%

#### Scenario: Turned off
- **WHEN** `fertilegrounds:fairy_hotel_speed` is false
- **THEN** no player gets Fairy Speed

### Requirement: Creepers avoid hotels
A creeper within 6 blocks of an occupied hotel SHALL move away from it, with the same priority vanilla gives fleeing a cat. Empty hotels SHALL NOT scare creepers.

#### Scenario: Creeper approaches the farm
- **WHEN** a creeper chasing a player comes within 6 blocks of an occupied hotel
- **THEN** the creeper turns and moves away from the hotel

### Requirement: No trampling near hotels
Any farmland block within 4 blocks horizontally and 2 vertically of an occupied hotel SHALL NOT turn to dirt when an entity falls on it. Drying out SHALL still work.

#### Scenario: Jump on protected farmland
- **WHEN** a player jumps onto farmland 3 blocks from an occupied hotel
- **THEN** the farmland stays farmland

#### Scenario: Unprotected farmland
- **WHEN** a mob falls 10 blocks onto farmland 6 blocks from the nearest occupied hotel
- **THEN** the farmland turns to its dirt block as usual
