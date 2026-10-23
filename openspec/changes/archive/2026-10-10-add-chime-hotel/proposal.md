# Proposal

## Why

Players leave their fields and come back too early or forget them. A hotel fairy that keeps an eye on the crops (the spyglass) and rings a soft chime when they're ready (the note block) answers "is it time to harvest?" without a menu or a tooltip mod.

## What Changes

- **Chime Hotel:** crafted from an Occupied Fairy Hotel, a Note Block, and a Spyglass (shapeless). It looks like an occupied hotel with a small spyglass on top.
- When at least 80% of the fairy crops within 8 blocks are fully grown (`fertilegrounds:chime_ready_percent`), it plays a soft 3-note chime once. It stays quiet until the share drops below the threshold, which happens after harvesting.
- It keeps every occupied-hotel effect from `add-jar-garden-effects`, including petting.
- Breaking it drops a Chime Hotel with its fairy.

Out of scope: comparator output for the chime (`add-redstone-integration`), chimes for non-crop plants, and a chime volume setting.

New art: Chime Hotel block and item (occupied hotel with a spyglass on top, with an emissive layer).

## Capabilities

### New Capabilities

- `chime-hotel`: Crafting a Chime Hotel and when it rings.

### Modified Capabilities

None.

## Impact

A third `FairyHotelBlock`, a shapeless recipe, a `READY` block state, a repeating scheduled tick, a gamerule, a custom sound event, loot table, lang keys. Depends on `add-fairy-jar` and `add-jar-garden-effects`.
