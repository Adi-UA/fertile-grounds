# Design

## Context

`add-fairy-jar` builds hotels as blocks of one `FairyHotelBlock` class with no block entity, and `add-jar-garden-effects` registers occupied hotels as a POI type and adds a `PETTED` state. The Chime Hotel joins both as a third block.

## Goals / Non-Goals

**Goals:** the ready rule (count, share, threshold, re-arm) in a pure `ChimeRules` class with JUnit coverage.

**Non-Goals:** an animated spyglass; the block model is static.

## Decisions

- **Third block, same class.** `CHIME_FAIRY_HOTEL` adds a `READY` boolean state on top of `PETTED`, joins the occupied-hotel POI type, and drops itself.
- **Repeating scheduled tick every 100 ticks**, started in `onPlace`. Scheduled ticks save with the chunk, so the check resumes after a reload. Random ticks are too irregular for "the moment the field is ready."
- **Fully grown** means the block is in `fairy_crops` and `CropGrowthUtil.canGrow` returns false. Counting reuses the same box the hotel's crop boost uses, widened to 8 blocks.
- **Re-arm:** `READY` flips true when the chime plays and false when the share drops below the threshold. `ChimeRules.next(ready, grown, total, threshold)` returns the new state and whether to chime.
- **Sound event** `fertilegrounds:block.fairy_hotel.chime` in `sounds.json`, pointing at vanilla's note block chime sound file at 3 pitches with short delays (three plays scheduled 4 ticks apart), so no new audio files ship.
- **Recipe** is shapeless with the Occupied Fairy Hotel item, Note Block, and Spyglass. Hotels carry no item data, so nothing needs copying.
- **Gamerule** `chime_ready_percent` (default 80, range 1 to 100).

## Look and sound

| Moment | Look | Sound | Particle |
|---|---|---|---|
| Placing | Occupied hotel with a spyglass on top | Lantern place, then the 3-note chime | `note`, 3 |
| Crops ready | Glow brightens for a moment | 3-note rising chime from `note/chime` | `note`, 5, rising |
| Breaking | Normal | Lantern break | None |

## Risks / Trade-offs

- [Checks on many Chime Hotels every 5 seconds] → each counts about 580 blocks; 20 hotels cost about 2,300 block reads per second, which is small next to a chunk's random ticks.
- [Crops that never report "fully grown" in a modded pack] → those crops count as growing forever and can keep the hotel from chiming; document that the tag decides.
