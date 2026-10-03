# Spec Delta

## Purpose

Defines the occasional gift a Bean Fairy leaves after a visit, balanced so gifts stay special and scale with how much a player farms.

## ADDED Requirements

### Requirement: Visit owner
Each visit SHALL record its owner: the player whose nightly roll scheduled it, or the player who used the Fairy Dust. A visit from a dispenser or spawn egg SHALL have no owner. Visits saved by 1.2.0 SHALL load with no owner.

#### Scenario: Old save
- **WHEN** a 1.2.0 world with a pending dust visit loads in 1.3.0
- **THEN** the visit still happens and gives no gift

### Requirement: Gift chance
When a visit with an owner grows crops, the mod SHALL roll a gift with `fertilegrounds:fairy_gift_chance_natural` (default 30) for natural visits or `fertilegrounds:fairy_gift_chance_dust` (default 10) for Fairy Dust visits, plus 1 percentage point per distinct `#fertilegrounds:fairy_flowers` block type within 8 blocks, capped at 10 points.

#### Scenario: Dispenser dust
- **WHEN** a dispenser-applied Fairy Dust visit grows crops
- **THEN** no gift is rolled

#### Scenario: Dust visit with flowers
- **WHEN** a Fairy Dust visit with 4 flower types nearby grows crops for an owner off cooldown
- **THEN** the gift chance is 14%

### Requirement: Gift cooldown
After a player receives a gift, that player SHALL get no further gifts until `fertilegrounds:fairy_gift_cooldown_days` in-game days pass (default 3). The cooldown SHALL survive restarts and apply while the player is offline.

#### Scenario: Two lucky visits in a row
- **WHEN** a player gets a gift and a visit they own grows crops the next night
- **THEN** no gift is rolled for that visit

### Requirement: Gift tier from farm size
The gift tier SHALL depend on the number of fairy crop blocks within 16 blocks horizontally and 4 vertically of the visit: fewer than 25 uses common, 25 to 99 uses uncommon, 100 or more uses rare. On a Harvest Moon night, the tier SHALL move up one step, and rare SHALL stay rare.

#### Scenario: Large farm
- **WHEN** a gift is rolled at a visit with 140 fairy crops nearby on a normal night
- **THEN** the gift comes from the rare table

#### Scenario: Harvest Moon boost
- **WHEN** a gift is rolled at a visit with 10 fairy crops nearby on a Harvest Moon
- **THEN** the gift comes from the uncommon table

### Requirement: Gift delivery
A gift SHALL drop as items from the loot table `fertilegrounds:gameplay/fairy_gift/<tier>` at the visit's crop, with sparkle particles, and the items SHALL never despawn. The owner SHALL see a translated message if within 32 blocks, and SHALL earn the first gift advancement.

#### Scenario: Pack overrides a tier
- **WHEN** a datapack replaces the common gift table with Farmer's Delight rice
- **THEN** common gifts drop rice

#### Scenario: Gift waits for the player
- **WHEN** a gift drops at dawn and the owner arrives 20 real minutes later with the chunk loaded the whole time
- **THEN** the gift items are still there
