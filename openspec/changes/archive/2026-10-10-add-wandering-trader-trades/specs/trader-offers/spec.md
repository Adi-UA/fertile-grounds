# Spec Delta

## Purpose

Introduces Fertile Grounds items to players through the Wandering Trader, a vanilla source of rare goods.

## ADDED Requirements

### Requirement: Fairy Dust trade
The Wandering Trader's rare trade pool SHALL include 2 Fairy Dust for 4 emeralds, with 4 uses before it locks.

#### Scenario: Trader offers dust
- **WHEN** a Wandering Trader rolls the Fairy Dust trade
- **THEN** a player can buy 2 Fairy Dust for 4 emeralds up to 4 times

### Requirement: Fertilizer trade
The Wandering Trader's rare trade pool SHALL include 1 Fertilizer for 6 emeralds, with 3 uses before it locks.

#### Scenario: Trader offers Fertilizer
- **WHEN** a Wandering Trader rolls the Fertilizer trade
- **THEN** a player can buy 1 Fertilizer for 6 emeralds up to 3 times

### Requirement: Vanilla trades unchanged
Adding these trades SHALL NOT remove or alter any vanilla Wandering Trader trade.

#### Scenario: Normal trader
- **WHEN** a Wandering Trader spawns without rolling either mod trade
- **THEN** its trades match vanilla's possible trades
