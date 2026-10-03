# Proposal

## Why

Players see a fairy for a minute and then it's gone. A Fairy Hotel lets them give one a permanent home on their farm, the feature the README teases. Fairies must never be trapped, so a bottled fairy is only a short trip to its new home: if it isn't moved into a hotel within 3 days, it slips away and leaves a flower.

## What Changes

- **Catch:** use a Glass Bottle on a fairy that has finished growing its patch (the linger window from `add-fairy-visit-polish`) to get a **Jarred Fairy**. Catching never interrupts a visit. A Jarred Fairy does nothing on its own.
- **Slipping away:** a Jarred Fairy lasts `fertilegrounds:jarred_fairy_days` in-game days (default 3), counting down everywhere. When time runs out it becomes a random `#fertilegrounds:fairy_flowers` flower, and the holder sees "Your Bean Fairy slipped away and left a Poppy behind." (or the chest version when found in a container).
- **Fairy Hotel:** crafted from 7 fairy flowers, 1 Firefly Bush, and 1 Lantern. An **Empty Fairy Hotel** is a dim, wood-accented lantern.
- **Moving in:** use a Jarred Fairy on an Empty Fairy Hotel to get an **Occupied Fairy Hotel**, a glowing green lantern that keeps its fairy forever. The bottle comes back.
- **Portable:** breaking an Occupied Fairy Hotel drops it with the fairy inside.
- Advancements for the first catch and the first hotel move-in.

Removed from the earlier plan: the 7-day Fairy Jar block, flower feeding, the 8-flower friendship, the Empty Fairy Jar, and jar expiry on blocks.

Out of scope: what an occupied hotel does to the farm, and petting (`add-jar-garden-effects`); the Chime Hotel (`add-chime-hotel`); comparator output (`add-redstone-integration`).

New art: Jarred Fairy item (bottle with a glowing fairy, with an emissive layer), Empty Fairy Hotel block and item (dim wood-accented lantern, faint emissive wick), Occupied Fairy Hotel block and item (green lantern with a fairy, emissive layer).

## Capabilities

### New Capabilities

- `fairy-hotel`: Catching a fairy, the Jarred Fairy's 3-day limit, building a Fairy Hotel, moving a fairy in, and carrying a hotel.

### Modified Capabilities

None. Two advancements are ADDED to `mod-advancements`.

## Impact

New Jarred Fairy item with a data component for its expiry, two hotel blocks and items, a recipe, `BeanFairy.mobInteract`, a container-open hook and an item-entity hook for expiry, a gamerule, loot tables, lang keys. Depends on `add-fairy-tags-and-gamerules`, `add-visit-feedback`, and `add-fairy-visit-polish`.
