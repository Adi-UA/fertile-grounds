# fairy-visit-chance Specification

## Purpose
Defines how likely a natural Bean Fairy visit is each night, so players can raise their odds by building a welcoming farm.

## Requirements

### Requirement: Chance is rolled for a chosen crop
At nightfall, for each Overworld player, the mod SHALL first pick a random growing fairy crop within 16 blocks horizontally and 4 vertically, then roll the visit chance worked out for that crop. A player with no crop nearby SHALL get no roll.

#### Scenario: No crops nearby
- **WHEN** night starts and a player has no growing fairy crop within range
- **THEN** no visit is scheduled for that player

### Requirement: Flower bonus
Each distinct block type from `#fertilegrounds:fairy_flowers` within 8 blocks horizontally and 4 vertically of the chosen crop SHALL add `fertilegrounds:fairy_flower_bonus` percent of the base chance (default 10), counting at most 10 types.

#### Scenario: Three flower types
- **WHEN** the base chance is 1, and poppies, dandelions, and cornflowers grow within 8 blocks of the chosen crop
- **THEN** the chance before other bonuses is 1.3%

#### Scenario: Repeated flowers count once
- **WHEN** 20 poppies and nothing else grow near the chosen crop
- **THEN** the flower bonus counts one type

#### Scenario: Cap
- **WHEN** 14 distinct flower types grow near the chosen crop
- **THEN** the flower bonus counts 10 types

### Requirement: Beehive bonus
If any block from `#minecraft:beehives` lies within 8 blocks horizontally and 4 vertically of the chosen crop, the chance SHALL gain `fertilegrounds:fairy_hive_bonus` percent of the base chance (default 50). Several hives SHALL count once.

#### Scenario: Bee nest near the farm
- **WHEN** the base chance is 1 and one bee nest sits 5 blocks from the chosen crop with no flowers nearby
- **THEN** the chance is 1.5%

### Requirement: Harvest Moon
On a night with a full moon, the chance after flower and hive bonuses SHALL be multiplied by `fertilegrounds:harvest_moon_multiplier` (default 3), capped at 100%. At nightfall on those nights, every Overworld player SHALL see a "Harvest Moon" message.

#### Scenario: Full moon with a full farm
- **WHEN** the base chance is 1, the crop has 10 flower types and a hive nearby, and the moon is full
- **THEN** the chance is 7.5%

#### Scenario: Message
- **WHEN** a full-moon night starts
- **THEN** each Overworld player sees a translated Harvest Moon message once

#### Scenario: Not a full moon
- **WHEN** a night starts in any other moon phase
- **THEN** no multiplier applies and no message appears
