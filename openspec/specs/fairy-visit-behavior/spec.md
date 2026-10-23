# fairy-visit-behavior Specification

## Purpose
Defines how a visiting Bean Fairy behaves from arrival to departure, so a visit is a short scene players can notice, watch, and take part in.

## Requirements

### Requirement: Heralds before arrival
For a visit within 32 blocks of a player, firefly particles SHALL drift over the visit's patch starting 60 seconds before the fairy arrives, or from when the visit is scheduled if that is later, with a quiet firefly bush ambient sound.

#### Scenario: Natural visit
- **WHEN** a natural visit is due in 2 minutes and a player stands 10 blocks from its crop
- **THEN** firefly particles appear over the patch 1 minute later and continue until the fairy arrives

#### Scenario: Fairy Dust after dark
- **WHEN** a player uses Fairy Dust at night and the fairy arrives 30 seconds later
- **THEN** firefly particles appear right away and stop when the fairy arrives

### Requirement: Growth dance
On reaching its crop, the fairy SHALL spin above it for 3 seconds, then grow the patch in rings: the center crop first, then each ring outward, half a second apart, each with a chime one step higher in pitch.

#### Scenario: Full patch
- **WHEN** a fairy reaches the center of a 5x5 wheat patch
- **THEN** the center grows first, then the 8 crops around it, then the outer 16, with three rising chimes

### Requirement: Linger after growing
After growing its patch, the fairy SHALL stay for `fertilegrounds:fairy_linger_seconds` (default 60), drifting slowly between crops within its patch, playing a soft ambient hum every few seconds. Then it SHALL fly up and vanish as before.

#### Scenario: Default linger
- **WHEN** a fairy finishes growing its patch with default gamerules
- **THEN** it stays near the patch for 60 seconds before flying away

#### Scenario: Linger off
- **WHEN** `fertilegrounds:fairy_linger_seconds` is 0
- **THEN** the fairy flies away right after growing the patch

### Requirement: Shy around players
While flying to its crop or lingering, if a player who is not sneaking comes within 4 blocks, the fairy SHALL dart to a spot at least 6 blocks from that player and within 8 blocks of its crop, then resume. A sneaking player SHALL NOT scare it. Shyness SHALL NOT delay the growth dance.

#### Scenario: Running up
- **WHEN** a player sprints toward a lingering fairy
- **THEN** the fairy darts away with a soft whoosh before the player gets within reach

#### Scenario: Sneaking up
- **WHEN** a player sneaks to 1 block from a lingering fairy
- **THEN** the fairy keeps drifting and stays in reach

### Requirement: No sinking into the ground
A fairy visiting a crop with open sky above SHALL never go lower than 1 block above the crop's block. A fairy visiting a covered crop SHALL still reach it through the roof.

#### Scenario: Open field
- **WHEN** a fairy flies to wheat in an open field
- **THEN** it is never inside the farmland or the ground below at any tick of the visit

#### Scenario: Greenhouse
- **WHEN** a fairy's crop sits under a glass roof
- **THEN** the fairy passes through the roof and grows the patch
