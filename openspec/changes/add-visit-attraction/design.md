# Design

## Context

`rollNaturalVisits` rolls the chance first and only then searches for a crop. The bonuses depend on the crop's surroundings, so the order must flip.

## Goals / Non-Goals

**Goals:** chance math in `FairyTiming` as a pure function of (base, flower types, hive present, full moon, gamerule values), covered by JUnit.

**Non-Goals:** checking flowers every tick; the scan runs once per player per nightfall.

## Decisions

- **Flip the order:** find the crop, scan its area once (17 x 9 x 17 = 2,601 blocks), then roll. That cost runs once per player per night, so no caching is needed.
- **Count distinct `Block`s, not states**, so a tall flower's two halves count once.
- **Full moon from the level's moon phase.** Read the 26.3 and 1.21.11 sources for the moon phase call first, because 1.21.11 moved some sky values into environment attributes; record any difference in the divergence table.
- **Translated chat message** for Harvest Moon over an overlay message, because nightfall can happen while the player is reading another overlay.

## Look and sound

| Moment | Look | Sound | Particle |
|---|---|---|---|
| Flowers or hives near crops | Passive, nothing shown | None | None |
| Harvest Moon nightfall | Chat message | `block.bell.resonate`, quiet, at each Overworld player | None |

## Risks / Trade-offs

- [Players stack flowers in a tiny space] → the cap and "distinct types only" rule limit it to +100% of base.
- [Harvest Moon message annoys players] → it appears once per full-moon night (every 8 in-game days); add a gamerule toggle if players ask.
