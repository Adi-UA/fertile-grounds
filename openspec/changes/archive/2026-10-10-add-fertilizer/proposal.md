# Proposal

## Why

Upgrading an existing farm to Fertile Grounds soil means digging up every block and replanting, which stops players with big farms (including Farmer's Delight farms) from adopting the mod. An item that upgrades soil in place removes that cost.

## What Changes

- Add the **Fertilizer** item. Recipe (shapeless, full grid): Bone Meal + 2 Glowstone Dust + 6 items from `#fertilegrounds:fairy_flowers`, making 4 Fertilizer.
- Using Fertilizer on vanilla or Enriched dirt or farmland turns it into the Super Enriched version in place, keeping the crop on top and the farmland moisture.
- Using it on vanilla sand turns it into Enriched Sand.
- Dispensers apply Fertilizer to the block in front of them.
- A recipe advancement unlocks Fertilizer when the player holds Glowstone Dust.

Out of scope: in-place upgrades for blocks other than dirt, farmland, and sand, and a cheaper tier 1 only Fertilizer.

New art: Fertilizer item texture.

## Capabilities

### New Capabilities

- `soil-fertilizer`: Upgrading soil in place with Fertilizer, by hand or dispenser.

### Modified Capabilities

None.

## Impact

New item, dispenser behavior, recipe, recipe advancement, and lang keys. Depends on `add-fairy-tags-and-gamerules` for the flower tag.
