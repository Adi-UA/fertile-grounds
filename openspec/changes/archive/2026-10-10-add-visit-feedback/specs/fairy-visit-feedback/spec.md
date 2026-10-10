# Spec Delta

## Purpose

Makes every Bean Fairy visit noticeable, so players know when the mod has helped them, even when they slept through it.

## ADDED Requirements

### Requirement: Arrival chime
When a fairy spawns for a visit, the mod SHALL play a fairy chime sound at the fairy with a translated subtitle, and players within 32 blocks SHALL see a translated "A Bean Fairy is visiting nearby" overlay message.

#### Scenario: Awake player nearby
- **WHEN** a fairy spawns 20 blocks from a player
- **THEN** the player hears the chime, sees the subtitle if subtitles are on, and sees the overlay message

#### Scenario: Player far away
- **WHEN** a fairy spawns 50 blocks from every player
- **THEN** no player sees the overlay message

### Requirement: Dawn message for overnight visits
When a visit grows crops at dawn because no fairy flew during the night, players within 32 blocks of that visit SHALL see a translated "A Bean Fairy visited your farm" chat message once per dawn, even if several visits grew nearby.

#### Scenario: Slept through two visits
- **WHEN** a player sleeps through a night with two pending visits within 32 blocks
- **THEN** the player sees the dawn message once

### Requirement: Fairy visit trigger
The mod SHALL fire the `fertilegrounds:fairy_visit` advancement trigger for every player within 32 blocks when a visit grows crops, whether the fairy flew or the visit grew at dawn.

#### Scenario: Datapack advancement
- **WHEN** a datapack defines an advancement on `fertilegrounds:fairy_visit` and a visit grows crops 10 blocks from a player
- **THEN** the player earns that advancement
