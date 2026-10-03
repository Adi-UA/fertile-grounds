# Proposal

## Why

Players see a fairy for a few seconds and then it's gone. A jar lets them keep one as a glowing farm companion, the feature the README already teases. Jars need a cost so the mod never rewards trapping fairies forever for free. A jarred fairy leaves after 7 days unless the player cares for it, and caring for it over several days earns a permanent friend.

## What Changes

- **Capture:** use a Glass Bottle or an Empty Fairy Jar on a fairy that has finished growing its patch to get a **Fairy Jar**. Catching never interrupts a visit.
- **Fairy Jar block:** looks like a vanilla lantern tinted green with a fairy inside, and gives light.
- **Lifespan:** 7 in-game days from capture (gamerule), counting down everywhere: placed, in a chest, or in an inventory. The glow dims on the last day.
- **Feeding:** using a `#fertilegrounds:fairy_flowers` item on the jar adds 1 day, up to 7 days left.
- **Friendship:** at most 2 flowers count toward friendship per in-game day. After 8 counted flowers (at least 4 days of care), the jar becomes permanent and never expires.
- **Expiry:** at zero, the fairy flies away, with no gift, and the block becomes an **Empty Fairy Jar**, which looks like a Bottle o' Enchanting and can catch another fairy.
- Advancements for the first jarred fairy and the first permanent jar.

Out of scope: the jar's effects on crops, creepers, trampling, and bees (`add-jar-garden-effects`), and comparator output (`add-redstone-integration`).

New art: Fairy Jar block and item (green lantern with a fairy), Empty Fairy Jar block and item (Bottle o' Enchanting look). Optional animated texture for the fairy.

## Capabilities

### New Capabilities

- `fairy-jar`: Catching a fairy, the jar's lifespan, feeding, friendship, permanence, and expiry.

### Modified Capabilities

None. Two advancements are ADDED to `mod-advancements`.

## Impact

New blocks, block entity, items, a data component for jar state on items, `BeanFairy.mobInteract`, `VisitCropGoal` (expose "done growing"), gamerules, loot tables, lang keys. Depends on `add-fairy-tags-and-gamerules` and `add-visit-feedback`.
