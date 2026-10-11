# tuning-gamerules Specification

## Purpose
Lets server owners and modpack makers tune every Fertile Grounds number in game with `/gamerule`, per world.

## Requirements

### Requirement: Namespaced integer gamerules
Each tunable number SHALL be an integer gamerule with the `fertilegrounds:` namespace, stored per world, changeable with `/gamerule`, and read at the moment the mod uses it. Chances SHALL be whole percentages from 0 to 100.

#### Scenario: Change takes effect without restart
- **WHEN** an operator runs `/gamerule fertilegrounds:super_enriched_boost_chance 0`
- **THEN** Super Enriched soil stops boosting crops on the next random tick

#### Scenario: Out of range value
- **WHEN** an operator sets a chance gamerule to 150
- **THEN** the command fails and the old value stays

### Requirement: Natural visit chance gamerule
`fertilegrounds:fairy_visit_chance` SHALL set the base chance, per player per night, of a natural fairy visit. Default 1.

#### Scenario: Visits disabled
- **WHEN** the gamerule is 0
- **THEN** no natural visits are scheduled, and Fairy Dust still works

#### Scenario: Visits guaranteed
- **WHEN** the gamerule is 100 and a player stands within 16 blocks of a growing fairy crop as night starts
- **THEN** a natural visit is scheduled for that player that night

### Requirement: Soil boost chance gamerules
`fertilegrounds:enriched_boost_chance` (default 15) SHALL set the per-random-tick boost chance of Enriched Farmland and Enriched Sand. `fertilegrounds:super_enriched_boost_chance` (default 50) SHALL set it for Super Enriched Farmland. The dirt tiers don't boost, since crops only grow on farmland.

#### Scenario: Defaults match 1.2.0
- **WHEN** a 1.2.0 world loads in 2.0.0 for the first time
- **THEN** the gamerules read 15 and 50, and soil behaves exactly as in 1.2.0
