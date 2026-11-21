# redstone-signals Specification

## Purpose
Lets redstone builds read and act on Fertile Grounds state using only vanilla redstone components.

## Requirements

### Requirement: Chime Hotel comparator pulse
A comparator reading a Chime Hotel SHALL output 15 for 20 ticks (1 second) starting when the hotel chimes for a ready field, and 0 otherwise. The chime on placement SHALL NOT pulse, so placing a hotel next to an auto-harvester never starts it. Empty and occupied hotels SHALL output 0.

#### Scenario: Harvest alarm
- **WHEN** a Chime Hotel chimes with a comparator behind it wired to a redstone lamp
- **THEN** the lamp lights for 1 second

#### Scenario: Placing the hotel
- **WHEN** a player places a Chime Hotel with a comparator behind it
- **THEN** the hotel chimes and the comparator stays at 0

#### Scenario: Quiet field
- **WHEN** the field is still growing
- **THEN** the comparator outputs 0

### Requirement: Soil comparator output
A comparator reading Enriched Dirt, Enriched Farmland, or Enriched Sand SHALL output 7, and reading Super Enriched Dirt or Farmland SHALL output 15.

#### Scenario: Sorting soil
- **WHEN** a comparator reads Super Enriched Farmland
- **THEN** it outputs 15
