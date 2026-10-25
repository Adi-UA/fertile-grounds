# Spec Delta

## Purpose

Lets players upgrade soil they already farm on into Fertile Grounds soil without breaking blocks or replanting.

## ADDED Requirements

### Requirement: Fertilizer recipe
The game SHALL craft 4 Fertilizer from a shapeless recipe of 1 Bone Meal, 2 Glowstone Dust, and 6 items from `#fertilegrounds:fairy_flowers`, in any mix. The recipe SHALL NOT match any other Fertile Grounds recipe.

#### Scenario: Mixed flowers
- **WHEN** a player fills a crafting table with 1 Bone Meal, 2 Glowstone Dust, 3 poppies, and 3 dandelions
- **THEN** the output slot shows 4 Fertilizer

#### Scenario: Recipe unlock
- **WHEN** a player first picks up Glowstone Dust
- **THEN** the Fertilizer recipe appears in their recipe book

### Requirement: Upgrade dirt and farmland to Super Enriched
Using Fertilizer on vanilla Dirt or Enriched Dirt SHALL replace it with Super Enriched Dirt. Using it on vanilla Farmland or Enriched Farmland SHALL replace it with Super Enriched Farmland, keeping the moisture level. The block above SHALL be unchanged. One Fertilizer SHALL be used per block outside creative mode.

#### Scenario: Farmland with a crop
- **WHEN** a player uses Fertilizer on moist vanilla farmland under half-grown wheat
- **THEN** the farmland becomes moist Super Enriched Farmland, the wheat keeps its growth stage, and one Fertilizer is used

#### Scenario: Already Super Enriched
- **WHEN** a player uses Fertilizer on Super Enriched Farmland
- **THEN** nothing changes and no Fertilizer is used

### Requirement: Upgrade sand
Using Fertilizer on vanilla Sand SHALL replace it with Enriched Sand and use one Fertilizer.

#### Scenario: Sand under bamboo
- **WHEN** a player uses Fertilizer on sand under a bamboo shoot
- **THEN** the sand becomes Enriched Sand and the bamboo stays

### Requirement: Feedback on use
A successful upgrade SHALL play a sound and show particles at the block.

#### Scenario: Visible upgrade
- **WHEN** a Fertilizer upgrade succeeds
- **THEN** nearby players see particles and hear a sound at the upgraded block

### Requirement: Dispenser use
A dispenser holding Fertilizer SHALL apply it to the block in front of it under the same rules as a player.

#### Scenario: Automated upgrade
- **WHEN** a powered dispenser with Fertilizer faces vanilla farmland
- **THEN** the farmland becomes Super Enriched Farmland and the dispenser loses one Fertilizer

#### Scenario: Nothing to upgrade
- **WHEN** a powered dispenser with Fertilizer faces stone
- **THEN** the dispenser keeps its Fertilizer and plays the vanilla failed-dispense click
