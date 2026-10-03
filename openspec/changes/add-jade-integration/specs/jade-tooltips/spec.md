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
With Jade, looking at a Fertile Grounds soil block SHALL show its current boost chance from the gamerules.

#### Scenario: Super Enriched
- **WHEN** a player with Jade looks at Super Enriched Farmland with default gamerules
- **THEN** the tooltip shows a 50% boost chance

### Requirement: Pending visit tooltip
With Jade, looking at a fairy crop with a pending visit SHALL show a translated "A Bean Fairy is coming tonight" line, including on multiplayer servers.

#### Scenario: After Fairy Dust
- **WHEN** a player uses Fairy Dust on wheat during the day and looks at it
- **THEN** the tooltip shows the coming-tonight line

### Requirement: Jar tooltip
With Jade, looking at a filled Fairy Jar SHALL show its days left and friendship progress out of 8, or "Permanent" for a permanent jar.

#### Scenario: Jar in progress
- **WHEN** a player looks at a jar with 4.2 days left and 5 counted flowers
- **THEN** the tooltip shows 4 days left and Friendship 5/8
