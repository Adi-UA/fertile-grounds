# Spec Delta

## Purpose

Teaches players every Fertile Grounds mechanic in game through a vanilla written book, with no extra mods required.

## ADDED Requirements

### Requirement: Book on first join
A player joining a world for the first time with 1.3.0 or later SHALL receive one Fertile Grounds Guide, unless `fertilegrounds:give_guide_book` is false (default true). A player SHALL receive it only once per world.

#### Scenario: Existing world after update
- **WHEN** a player who played in 1.2.0 joins the same world on 1.3.0
- **THEN** they receive the guide once

#### Scenario: Rejoin
- **WHEN** the player leaves and rejoins
- **THEN** they don't receive a second guide

#### Scenario: Pack turns it off
- **WHEN** the gamerule is false and a new player joins
- **THEN** no guide is given

#### Scenario: Full inventory
- **WHEN** a new player joins with a full inventory
- **THEN** the guide drops at their feet

### Requirement: Craftable copy
The game SHALL craft a Fertile Grounds Guide from a shapeless recipe of 1 Book and 1 Bone Meal.

#### Scenario: Lost the book
- **WHEN** a player crafts Book + Bone Meal
- **THEN** they get a guide matching the installed mod version

### Requirement: Translated content
Every page and the title SHALL use translation keys, so the book shows in each player's own language.

#### Scenario: Other language
- **WHEN** a player with a translated language selected opens the guide
- **THEN** the pages show in that language

### Requirement: Coverage
The guide SHALL have pages for soil tiers, Fertilizer, fairy visits, what attracts fairies, Harvest Moon, gifts, Fairy Jars and friendship, jar effects, and redstone.

#### Scenario: Reader looks up jars
- **WHEN** a player opens the guide to the jar pages
- **THEN** they learn how to catch, feed, and befriend a fairy, and what happens when a jar runs out
