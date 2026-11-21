# Spec Delta

## Purpose

Shows Fertile Grounds state in Jade's look-at tooltip for players who have Jade, with no effect on players who don't.

## ADDED Requirements

### Requirement: Optional
The mod SHALL load and work the same without Jade installed, and SHALL NOT list Jade as a required dependency.

#### Scenario: No Jade
- **WHEN** the game starts with Fertile Grounds and without Jade
- **THEN** the game loads with no Jade-related errors in the log

### Requirement: Soil tooltip
With Jade, looking at Enriched or Super Enriched Farmland, or Enriched Sand, SHALL show its current boost chance from the gamerules. Enriched dirt shows no chance, since it only boosts crops once tilled.

#### Scenario: Super Enriched
- **WHEN** a player with Jade looks at Super Enriched Farmland with default gamerules
- **THEN** the tooltip shows a 50% boost chance

### Requirement: Pending visit tooltip
With Jade, looking at a fairy crop with a pending visit SHALL show a translated "A Bean Fairy is coming tonight" line, including on multiplayer servers.

#### Scenario: After Fairy Dust
- **WHEN** a player uses Fairy Dust on wheat during the day and looks at it
- **THEN** the tooltip shows the coming-tonight line

### Requirement: Hotel tooltip
With Jade, looking at an occupied Fairy Hotel or Chime Hotel SHALL show a translated "Petted today" or "Ready for a brush" line. Looking at a Chime Hotel SHALL also show the grown and total fairy crops it counts, for example "Crops ready: 41/50".

#### Scenario: Morning check
- **WHEN** a player who hasn't brushed their hotel today looks at it
- **THEN** the tooltip shows "Ready for a brush"

#### Scenario: Chime Hotel mid-season
- **WHEN** a player looks at a Chime Hotel with 30 of 50 nearby crops grown
- **THEN** the tooltip shows "Crops ready: 30/50"
