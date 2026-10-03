# Design

## Context

`BeanFairy` is never saved and lives only for its visit. A caught fairy must outlive the entity, so the Jarred Fairy item and the hotel block store state, not an entity. `add-fairy-visit-polish` adds `BeanFairy.hasFinishedGrowing()`, true during the linger and leave phases.

## Goals / Non-Goals

**Goals:** the expiry rule and flower pick in a pure `JarredFairyRules` class with JUnit coverage; hotels with no timers at all.

**Non-Goals:** rendering a live fairy inside the hotel; the fairy is part of the block texture, optionally animated with a `.mcmeta` file.

## Decisions

- **Absolute expiry time.** The Jarred Fairy stores `expiresAtGameTime` in a custom data component, so it ages the same in a chest, an inventory, or an unloaded chunk without ticking. "Days" are 24,000 game ticks.
- **Three expiry checks, one rule.** `Item.inventoryTick` converts carried items. A hook when a player opens a container menu scans its slots and sends the "while you were gone" message once per open, however many fairies left. A small `ItemEntity.tick` mixin converts dropped items. Check whether Fabric API has a container-open event before writing a mixin for it.
- **Flower pick** from the `fairy_flowers` item tag with the level's random source; fall back to a Poppy if a pack empties the tag.
- **Two hotel blocks, no block entity.** `EMPTY_FAIRY_HOTEL` and `FAIRY_HOTEL` are separate blocks sharing one `FairyHotelBlock` class with a lantern-style `HANGING` property. Separate blocks give separate items and loot tables, so the occupied hotel drops itself with no item data. `add-chime-hotel` adds a third block of the same class.
- **Lantern shape and sound type.** Same collision shape and `SoundType.LANTERN` as vanilla's lantern, so it hangs and sits like one.
- **Capture in `BeanFairy.mobInteract`**, gated on `hasFinishedGrowing()`.
- **Recipe** is a shaped crafting recipe with the `fairy_flowers` item tag as an ingredient. Check the ingredient tag format in 26.3 and 1.21.11.
- **Gamerule** `jarred_fairy_days` (default 3, range 1 to 30).

## Look and sound

| Moment | Look | Sound | Particle |
|---|---|---|---|
| Catching | Fairy shrinks into the bottle | `item.bottle.fill`, pitch 1.5 | `end_rod`, small puff |
| Jarred Fairy in hand | Glowing fairy in a bottle (emissive layer) | None | None |
| Slipping away | Item becomes a flower | `entity.allay.item_thrown`, soft | `cherry_leaves` around the player |
| Placing or breaking a hotel | Lantern on a post or hanging | Lantern place and break | None |
| Empty hotel | Dim wood-accented lantern, faint wick (emissive) | None | None |
| Moving in | Hotel lights up green | `entity.allay.item_given`, then `block.amethyst_cluster.place` | `heart` and `end_rod` burst |
| Occupied hotel, ambient | Green glow, fairy silhouette (emissive) | `entity.allay.ambient_without_item`, pitch 1.4, rare, client-side in `animateTick` | `glow`, slow drift |

## Risks / Trade-offs

- [Custom data components register differently on 1.21.11] → check `DataComponents` registration in both versions before coding, and add a divergence row.
- [Modded storage that never opens a vanilla menu keeps an expired Jarred Fairy] → it converts the moment a player carries it again; harmless because a Jarred Fairy does nothing.
- [Players farm flowers by catching and releasing] → each catch needs a visit and a bottle, and the flower is worth less than the bottle's effort.
