# Proposal

## Why

1.3.0 adds Fertilizer, flower attraction, gifts, Harvest Moon, and jars with friendship, which is too much to discover by accident. An in-game book explains it where players are, without a wiki or a dependency.

## What Changes

- Every player gets a **Fertile Grounds Guide** (vanilla written book) on their first join after installing 1.3.0, including players in existing worlds.
- Players can craft a fresh copy from Book + Bone Meal.
- Pages cover soils, Fertilizer, fairy visits and what attracts them, Harvest Moon, gifts, jars and friendship, and redstone.
- A gamerule turns off the join gift for packs with their own quest books.

Out of scope: Patchouli or Modonomicon (Patchouli doesn't support 1.21.11, 26.2, or 26.3, and a required dependency makes the mod heavier), and pictures in the book.

New art: none.

## Capabilities

### New Capabilities

- `guide-book`: The in-game guide, how players get it, and what it covers.

### Modified Capabilities

None.

## Impact

A player attachment for "received guide", a join event handler, a recipe whose result is a written book with translatable pages, lang keys, a gamerule. Written last among gameplay changes so its pages describe final behavior.
