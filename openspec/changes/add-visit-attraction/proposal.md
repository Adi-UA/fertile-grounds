# Proposal

## Why

Natural visits are a flat 1% per player per night, so nothing a player builds changes their odds. Letting flowers, beehives, and the full moon raise the chance rewards cozy farm decorating with vanilla blocks and gives players a monthly event to plan around.

## What Changes

- Each distinct `#fertilegrounds:fairy_flowers` block type near the chosen crop raises the natural visit chance.
- A block from `#minecraft:beehives` near the chosen crop raises it further.
- **Harvest Moon:** on full-moon nights, the final chance is multiplied, and Overworld players see a message at nightfall.
- New gamerules for each bonus.

Out of scope: Harvest Moon gift effects (in `add-fairy-gifts`), and bee behavior near jars (in `add-jar-garden-effects`).

New art: none.

## Capabilities

### New Capabilities

- `fairy-visit-chance`: How the nightly natural visit chance is worked out from the base gamerule and the farm around the crop.

### Modified Capabilities

None.

## Impact

`FairyTiming` (new chance math), `FairyVisitScheduler.rollNaturalVisits` (pick the crop before rolling), `ModGameRules`, lang keys. Depends on `add-fairy-tags-and-gamerules`.
