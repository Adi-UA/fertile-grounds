# Spec Delta

## Purpose

Gives players a visible path of goals through the mod in its own advancement tab, which quest-focused modpacks can build on.

## ADDED Requirements

### Requirement: Advancement tab
The mod SHALL add a "Fertile Grounds" advancement tab whose root unlocks when a player first obtains any Fertile Grounds soil block.

#### Scenario: Root unlock
- **WHEN** a player crafts their first Enriched Dirt
- **THEN** the Fertile Grounds tab appears with its root earned

### Requirement: First visit advancement
The tab SHALL contain an advancement earned on the player's first `fertilegrounds:fairy_visit` trigger.

#### Scenario: First visit
- **WHEN** a fairy grows crops within 32 blocks of a player for the first time
- **THEN** the player earns the first visit advancement and sees a toast

### Requirement: Fertilizer advancement
The tab SHALL contain an advancement earned the first time a player upgrades a block with Fertilizer.

#### Scenario: First Fertilizer use
- **WHEN** a player upgrades farmland with Fertilizer for the first time
- **THEN** the player earns the Fertilizer advancement
