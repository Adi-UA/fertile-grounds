# fairy-gifts Specification

## Purpose
Defines the gift a Bean Fairy can leave after a visit, which stays rare and grows with the size of the farm.

## Requirements

### Requirement: Visit owner
Each visit SHALL record its owner: the player whose nightly roll scheduled it, or the player who used the Fairy Dust. A visit from a dispenser or spawn egg SHALL have no owner. Visits saved by 1.2.0, which stored no owner, SHALL load with no owner.

#### Scenario: Old save
- **WHEN** a world saved by 1.2.0 with a pending dust visit loads
- **THEN** the visit still happens and gives no gift

### Requirement: Gift chance
When a visit with an owner grows crops, the mod SHALL roll a gift with `fertilegrounds:fairy_gift_chance_natural` (default 30) for natural visits or `fertilegrounds:fairy_gift_chance_dust` (default 10) for Fairy Dust visits, plus 1 percentage point per distinct `#fertilegrounds:fairy_flowers` block type within 8 blocks horizontally and 4 vertically, capped at 10 points. A gamerule set to 0 SHALL give no gifts, whatever flowers grow nearby.

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
The gift tier SHALL depend on the number of fairy crop blocks within 16 blocks horizontally and 4 vertically of the visit: under 25 uses common, 25 to 99 uses uncommon, 100 or more uses rare. On a Harvest Moon night, the tier SHALL move up one step, and rare SHALL stay rare.

#### Scenario: Large farm
- **WHEN** a gift is rolled at a visit with 140 fairy crops nearby on a normal night
- **THEN** the gift comes from the rare table

#### Scenario: Harvest Moon boost
- **WHEN** a gift is rolled at a visit with 10 fairy crops nearby on a Harvest Moon
- **THEN** the gift comes from the uncommon table

### Requirement: Gift delivery
A gift SHALL drop as items from the loot table `fertilegrounds:gameplay/fairy_gift/<tier>` at the visit's crop, with sparkle particles, and the items SHALL never despawn. The owner SHALL see a translated action bar message if within 32 blocks, and SHALL earn the first gift advancement while online.

#### Scenario: Plain item drops
- **WHEN** a gift drops
- **THEN** it appears as ordinary item entities on the ground that players pick up by walking over them, with no container or menu

#### Scenario: Pack overrides a tier
- **WHEN** a datapack replaces the common gift table with Farmer's Delight rice
- **THEN** common gifts drop rice

#### Scenario: Gift waits for the player
- **WHEN** a gift drops at dawn and the owner arrives 20 real minutes later with the chunk loaded the whole time
- **THEN** the gift items are still there

### Requirement: Default gift tables
Each gift SHALL roll once on its tier's loot table, picking one entry by weight with a random count in the listed range. The default tables SHALL be:

| Tier | Item | Count | Weight |
|---|---|---|---|
| Common | Bone Meal | 3 to 6 | 25 |
| Common | Wheat Seeds | 4 to 8 | 20 |
| Common | Random item from `#fertilegrounds:fairy_flowers` | 1 to 3 | 20 |
| Common | Beetroot Seeds | 4 to 8 | 15 |
| Common | Pumpkin Seeds | 2 to 4 | 10 |
| Common | Melon Seeds | 2 to 4 | 10 |
| Uncommon | Sweet Berries | 4 to 8 | 30 |
| Uncommon | Glow Berries | 3 to 6 | 25 |
| Uncommon | Cocoa Beans | 3 to 6 | 25 |
| Uncommon | Fairy Dust | 1 | 20 |
| Rare | Fertilizer | 2 to 4 | 30 |
| Rare | Golden Carrot | 2 to 4 | 30 |
| Rare | Torchflower Seeds | 1 to 2 | 20 |
| Rare | Pitcher Pod | 1 | 20 |

#### Scenario: Common gift
- **WHEN** a common gift rolls the flower entry
- **THEN** 1 to 3 of one random fairy flower drop at the crop

#### Scenario: Rare gift odds
- **WHEN** 1,000 rare gifts are rolled
- **THEN** about 300 are Fertilizer, 300 Golden Carrots, 200 Torchflower Seeds, and 200 Pitcher Pods
