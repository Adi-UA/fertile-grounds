# Spec Delta

## Purpose

Lets modpack makers and other mods decide which blocks and items Fertile Grounds treats as crops and flowers, without code.

## ADDED Requirements

### Requirement: Fairy crops tag
The mod SHALL treat a block as a fairy crop only when it is in the `fertilegrounds:fairy_crops` block tag and is not fully grown. The tag SHALL contain `#minecraft:crops` by default.

#### Scenario: Vanilla crop counts
- **WHEN** a player uses Fairy Dust on a half-grown wheat crop with default tags
- **THEN** a fairy visit is scheduled for that crop

#### Scenario: Modded crop added by datapack
- **WHEN** a datapack adds a modded crop block to `fertilegrounds:fairy_crops` and a player uses Fairy Dust on it
- **THEN** a fairy visit is scheduled for that crop

#### Scenario: Block removed from the tag
- **WHEN** a datapack replaces `fertilegrounds:fairy_crops` with a list that leaves out carrots
- **THEN** Fairy Dust used on carrots does nothing and natural visits never target carrots

### Requirement: Fairy flowers tag
The mod SHALL define `fertilegrounds:fairy_flowers` as both a block tag and an item tag, each containing `#minecraft:small_flowers` by default. Every flower-based feature SHALL read these tags.

#### Scenario: Modded flower added by datapack
- **WHEN** a datapack adds a modded flower to both `fertilegrounds:fairy_flowers` tags
- **THEN** every flower-based feature treats it the same as a vanilla poppy
