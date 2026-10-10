# Design

## Context

`add-fairy-jar` builds hotels as blocks of one `FairyHotelBlock` class with no block entity, and `add-jar-garden-effects` registers occupied hotels as a POI type and adds a `PETTED` state. The Chime Hotel joins both as a third block.

## Goals / Non-Goals

**Goals:** the ready rule (count, share, threshold, re-arm) in a pure `ChimeRules` class with JUnit coverage.

**Non-Goals:** an animated spyglass; the block model is static.

## Decisions

- **Third block, a subclass.** `ChimeHotelBlock extends FairyHotelBlock` adds a `READY` boolean state on top of `PETTED`, so the other hotels don't carry a state they never use. `CHIME_FAIRY_HOTEL` joins the occupied-hotel POI type and drops itself.
- **Repeating scheduled tick every 100 ticks**, started in `onPlace`. Scheduled ticks save with the chunk, so the check resumes after a reload. Random ticks are too irregular for "the moment the field is ready." Vanilla keeps one pending scheduled tick per block and position, so petting can't schedule its own dawn tick here; instead the first check after dawn (`FairyHotelRules.isJustAfterDawn`) ends the petted mood.
- **Fully grown** means the block is in `fairy_crops` and `CropGrowthUtil.canGrow` returns false. Counting reuses the same box the hotel's crop boost uses, widened to 8 blocks.
- **Re-arm:** `READY` flips true when the chime plays and false when the share drops below the threshold. `ChimeRules.next(ready, grown, total, threshold)` returns the new state and whether to chime.
- **Sound event** `fertilegrounds:block.chime_fairy_hotel.chime` in `sounds.json`, pointing at vanilla's note block chime file (`note/icechime`), so no new audio files ship. `HotelChimes` plays it 3 times at rising pitches, 4 ticks apart, from a small queue drained on each server tick, since the block's one scheduled tick is taken by the check. Volume 2 makes it audible within 32 blocks.
- **Recipe** is shapeless with the Occupied Fairy Hotel item, Note Block, and Spyglass. Hotels carry no item data, so nothing needs copying.
- **Gamerule** `chime_ready_percent` (default 80, range 1 to 100).

## Look and sound

| Moment | Look | Sound | Particle |
|---|---|---|---|
| Placing | Occupied hotel with a spyglass on top | Lantern place, then the 3-note chime | `note`, 3 |
| Crops ready | A burst of `glow` particles | 3-note rising chime from `note/icechime` | `note`, 6, rising |
| Breaking | Normal | Lantern break | None |

## Risks / Trade-offs

- [Checks on many Chime Hotels every 5 seconds] → each reads a 17x5x17 box, 1,445 blocks; 20 hotels cost about 5,800 block reads per second, which is small next to a chunk's random ticks.
- [Crops that never report "fully grown" in a modded pack] → those crops count as growing forever and can keep the hotel from chiming; document that the tag decides.
