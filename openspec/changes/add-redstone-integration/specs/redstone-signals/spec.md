# Spec Delta

## Purpose

Lets redstone builds read and act on Fertile Grounds state using only vanilla redstone components.

## ADDED Requirements

### Requirement: Chime Hotel comparator pulse
A comparator reading a Chime Hotel SHALL output 15 for 20 ticks (1 second) starting when the hotel chimes, and 0 otherwise. Empty and occupied hotels SHALL output 0.

#### Scenario: Harvest alarm
- **WHEN** a Chime Hotel chimes with a comparator behind it wired to a redstone lamp
- **THEN** the lamp lights for 1 second

#### Scenario: Quiet field
- **WHEN** the field is still growing
- **THEN** the comparator outputs 0

### Requirement: Soil comparator output
A comparator reading Enriched Dirt, Enriched Farmland, or Enriched Sand SHALL output 7, and reading Super Enriched Dirt or Farmland SHALL output 15.

#### Scenario: Sorting soil
- **WHEN** a comparator reads Super Enriched Farmland
- **THEN** it outputs 15
