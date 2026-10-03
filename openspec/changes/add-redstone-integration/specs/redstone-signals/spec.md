# Spec Delta

## Purpose

Lets redstone builds read and act on Fertile Grounds state using only vanilla redstone components.

## ADDED Requirements

### Requirement: Jar comparator output
A comparator reading a Fairy Jar SHALL output 15 for a permanent jar, 0 for an Empty Fairy Jar, and otherwise the days left scaled to 1 to 15 against `fertilegrounds:fairy_jar_days`, rounded up.

#### Scenario: Half-full jar
- **WHEN** a comparator reads a jar with 3.5 of 7 days left
- **THEN** it outputs 8

#### Scenario: Reminder lamp
- **WHEN** a jar's days left drop below 1 day
- **THEN** the comparator output drops to 3 or less

### Requirement: Soil comparator output
A comparator reading Enriched Dirt, Enriched Farmland, or Enriched Sand SHALL output 7, and reading Super Enriched Dirt or Farmland SHALL output 15.

#### Scenario: Sorting soil
- **WHEN** a comparator reads Super Enriched Farmland
- **THEN** it outputs 15

### Requirement: Dispenser feeds jars
A dispenser holding a `#fertilegrounds:fairy_flowers` item and facing a filled, non-permanent jar SHALL feed it one flower, adding 1 day under the normal cap. Dispenser feeding SHALL NOT count toward friendship.

#### Scenario: Automatic upkeep
- **WHEN** a powered dispenser with poppies faces a jar with 2 days left
- **THEN** the jar has 3 days left and its friendship count is unchanged

#### Scenario: Jar already full
- **WHEN** the jar already has full days
- **THEN** the dispenser keeps its flower
