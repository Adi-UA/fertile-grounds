# mod-advancements Specification

## Purpose
Gives players a visible path of goals through the mod in its own advancement tab, which quest-focused modpacks can build on.

## Requirements

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

### Requirement: First gift advancement
The Fertile Grounds tab SHALL contain an advancement earned the first time a player receives a fairy gift.

#### Scenario: First gift
- **WHEN** a player receives their first fairy gift
- **THEN** the player earns the first gift advancement

### Requirement: Harvest Moon visit advancement
The tab SHALL contain an advancement earned when a fairy visit grows crops near a player on a Harvest Moon night.

#### Scenario: Harvest Moon visit
- **WHEN** a visit grows crops within 32 blocks of a player on a full-moon night
- **THEN** the player earns the Harvest Moon advancement

### Requirement: First catch advancement
The Fertile Grounds tab SHALL contain an advancement earned the first time a player catches a fairy in a bottle.

#### Scenario: First catch
- **WHEN** a player catches their first fairy
- **THEN** the player earns the first catch advancement

### Requirement: First hotel advancement
The tab SHALL contain a goal advancement earned the first time a player moves a fairy into a Fairy Hotel.

#### Scenario: Moving in
- **WHEN** a player uses a Jarred Fairy on an Empty Fairy Hotel for the first time
- **THEN** that player earns the first hotel advancement
