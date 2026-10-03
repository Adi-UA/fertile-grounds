# Spec Delta

## Purpose

Defines how a filled Fairy Jar guards and gently helps the farm around it, so a cared-for fairy is a useful farm companion.

## ADDED Requirements

### Requirement: Gentle crop boost
On each random tick, a filled Fairy Jar SHALL pick one random growing `#fertilegrounds:fairy_crops` block within 4 blocks horizontally and 2 vertically and advance it one bone-meal step with `fertilegrounds:fairy_jar_boost_chance` percent (default 50). An Empty Fairy Jar SHALL NOT boost.

#### Scenario: Crop near a jar
- **WHEN** a filled jar sits 3 blocks from growing wheat with random ticks on
- **THEN** the wheat grows faster than identical wheat 10 blocks away

#### Scenario: Empty jar
- **WHEN** a jar expires and becomes empty
- **THEN** nearby crops grow at their normal rate

### Requirement: Clustered jars don't multiply
When filled jars sit within 8 blocks of each other, only one jar in that cluster SHALL boost crops, chosen the same way every time.

#### Scenario: Two jars side by side
- **WHEN** a player places two filled jars next to each other in a wheat field
- **THEN** the wheat grows at the same rate as with one jar

### Requirement: Bee pollination
On a random tick, if a bee carrying nectar is within 6 blocks of a filled jar, the jar SHALL make one extra boost attempt.

#### Scenario: Bees nearby
- **WHEN** a beehive's bees carry nectar past a filled jar in a wheat field
- **THEN** the wheat near the jar grows faster than with the jar alone

### Requirement: Creepers avoid jars
A creeper within 6 blocks of a filled jar SHALL move away from it, with the same priority vanilla gives fleeing a cat. Empty jars SHALL NOT scare creepers.

#### Scenario: Creeper approaches the farm
- **WHEN** a creeper chasing a player comes within 6 blocks of a filled jar
- **THEN** the creeper turns and moves away from the jar

### Requirement: No trampling near jars
Any farmland block within 4 blocks horizontally and 2 vertically of a filled jar SHALL NOT turn to dirt when an entity falls on it. Drying out SHALL still work.

#### Scenario: Jump on protected farmland
- **WHEN** a player jumps onto farmland 3 blocks from a filled jar
- **THEN** the farmland stays farmland

#### Scenario: Unprotected farmland
- **WHEN** a mob falls 10 blocks onto farmland 6 blocks from the nearest filled jar
- **THEN** the farmland turns to its dirt block as usual
