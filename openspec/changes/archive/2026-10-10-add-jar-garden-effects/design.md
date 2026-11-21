# Design

## Context

Creepers and farmland need to ask "is an occupied hotel near this spot?" often, so a block scan per check is too slow. Vanilla's point-of-interest (POI) system answers that question for villager workstations with an indexed lookup. Hotels have no block entity (`add-fairy-jar`), so petting state must live in the block state.

## Goals / Non-Goals

**Goals:** one `FairyHotelLookup.nearestOccupiedHotel(level, pos, radius)` that every effect uses; every hotel kind that holds a fairy (occupied and Chime) counts.

**Non-Goals:** changing how creepers explode once they've started swelling.

## Decisions

- **Register a POI type for occupied hotels** (both the Occupied and Chime Hotel blocks) with Fabric's `PointOfInterestHelper`, so lookups use vanilla's chunk-indexed POI storage. Check the helper's signature in the 26.3 and 1.21.11 Fabric API jars.
- **Cluster leader = the occupied hotel with the smallest `BlockPos.asLong()`** within 8 blocks. Every hotel computes the same answer, so exactly one boosts and adds honey, and no shared state is needed.
- **Honey through vanilla's `BeehiveBlock.HONEY_LEVEL` property**, capped at the property's max, so vanilla harvesting works unchanged.
- **Petting through a `PETTED` boolean block state** set in `useItemOn` when the held item is a Brush, without the brush's hold animation. Setting it schedules a block tick for the next dawn (24,000 minus the current day time), which clears it. Scheduled ticks save with the chunk, so a petted hotel in an unloaded chunk clears when it next loads.
- **Creeper avoidance by mixin** into `Creeper.registerGoals`, adding a goal modeled on vanilla's `AvoidEntityGoal` for cats but targeting the POI position. Fabric API has no hook for adding mob goals; keep the mixin small.
- **Trample protection by mixin** into `FarmlandBlock.fallOn` (or the 26.3 method that converts farmland), cancelling only the conversion. One mixin covers vanilla, Enriched, Super Enriched, and modded farmland that extends `FarmlandBlock`.
- **Bee check with an entity AABB query** of `Bee` with `hasNectar()`, run only inside the hotel's random tick.
- **Fairy Speed as a custom `MobEffect`** with a +10% `movement_speed` modifier (`ADD_MULTIPLIED_TOTAL`, the same operation vanilla Speed uses at +20%). Every 20 ticks, a server tick handler checks each player with `FairyHotelLookup` within 8 blocks and gives the effect for 60 ticks, ambient like a beacon. Refreshing one effect, instead of adding modifiers, makes stacking impossible.
- **Vanilla Speed icon without copying it:** effect icons come from the `gui` atlas (`atlases/gui.json`, same in 1.21.11 and 26.3). The mod adds a `minecraft:single` source to that atlas mapping `minecraft:mob_effect/speed` to `fertilegrounds:mob_effect/fairy_speed`. Confirm the `single` source's field names in both versions before writing it.
- **Fixed 10% with an on/off gamerule** instead of a percent gamerule: an effect's modifier amount is fixed at registration, and scaling by amplifier would show "Fairy Speed X" in the inventory.
- **Gamerules** `fairy_hotel_boost_chance` (50), `fairy_hotel_petted_boost_chance` (65), `fairy_hotel_honey_chance` (10), all 0 to 100, and `fairy_hotel_speed` (true).

## Look and sound

| Moment | Look | Sound | Particle |
|---|---|---|---|
| Crop boost | Crop grows a stage | None, like soil | `happy_villager`, 2 |
| Honey added | Hive shows more honey | `block.beehive.work`, quiet | `wax_on`, 3 at the hive |
| Petting | Hotel glows a touch brighter for a moment | `item.brush.brushing.generic`, then `block.amethyst_block.chime` | `heart`, 3 |
| Already petted | Nothing | None | None |
| Creeper turns away | Creeper walks off | None | None |
| Entering a hotel's range | Speed icon appears with a beacon-style border | None | Vanilla ambient effect swirls, faint |

## Risks / Trade-offs

- [Mixins break across versions] → both mixins target small, stable methods; the backport phase verifies each and records signatures in the divergence table.
- [Modded farmland that doesn't extend `FarmlandBlock` isn't protected] → acceptable; document it.
- [`/time set` moves dawn after a pet] → the scheduled tick clears at the originally computed time; at worst a hotel can be petted a little early or late once.
