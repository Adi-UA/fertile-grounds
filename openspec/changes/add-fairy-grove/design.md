# Design

## Context

The Chime Hotel (`ChimeHotelBlock`) already handles item use for brushing. Hotels are `LanternBlock` subclasses with a `HANGING` state. Fairy visits save their state in a level attachment (`FairyVisits`), and `HotelChimes` drains a queue on `END_LEVEL_TICK`; the grove builder reuses both patterns.

## Goals / Non-Goals

**Goals:** the layout (which block goes where) and the shrine check in pure classes with JUnit coverage; a builder that never touches a block the safety check wouldn't allow.

**Non-Goals:** multiplayer claim plugins; the tag and the per-column re-check are the protection.

## Decisions

- **Trigger in `ChimeHotelBlock.useItemOn`:** when the stack is a diamond, call `FairyGrove.tryStart(level, pos, player)`. It checks the gamerule, then `GroveShrine.check`, then the safety scan, and only then consumes the diamond and queues a job. Every refusal returns `InteractionResult.SUCCESS` with an action bar message, so the diamond doesn't fall through to placing or using.
- **Shrine check** reads the block below (must be Super Enriched Dirt), the 4 spots at distance 3 (each a `FairyHotelBlock` with `isOccupied()`), and counts `fairy_flowers` in the 7x7x3 box. `GroveShrine.Result` is an enum (`OK`, `NO_SOIL`, `MISSING_HOTEL`, `TOO_FEW_FLOWERS`), each mapping to a lang key.
- **Layout as a pure function.** `GroveLayout.cell(dx, dz)` returns one of `CENTER`, `FARMLAND`, `WATER`, `BORDER`, `HIVE_POST`, `HEDGE_POST`, `MEADOW`, or `OUTSIDE` for offsets from -17 to 17. Three 9x9 plots plus four 1-block borders make 31; the 2-block meadow brings it to 35. A crossing is a spot where both offsets sit on a border line (±5 or ±15); the 4 with both offsets at ±5 are inner. JUnit checks the counts (8 water blocks, 4 hives, 12 hedges, 8 × 80 farmland blocks) and that every farmland block sits within 4 blocks of a water block, the distance vanilla farmland checks for water.
- **Allowlist tag, not a denylist.** `fertilegrounds:grove_replaceable` lists natural terrain, and anything else stops the build, including any block entity. Player builds can't be told apart from terrain in general, so refusing everything unknown keeps the grove from ever eating a base. The scan covers 35 × 35 × 33 blocks, about 40,000 reads, once per diamond.
- **Builder:** `GroveJobs` holds jobs (center, soil Y, next column index) in a level attachment so building resumes after a reload. Each server tick it rebuilds 41 columns (1,225 columns / 30 seconds / 20 ticks per second, rounded up). Per column it re-checks the tag, clears above, fills below, then places the layout block. Leaves are placed with `PERSISTENT` true so they never decay; farmland with `MOISTURE` 7. Flowers come from `minecraft:small_flowers`, chosen by a seeded random from the center position so the same grove looks the same if rebuilt.
- **Fairies:** at start, 4 Bean Fairies spawn from the 4 hotels with no visit target, follow the build front, and despawn when the job ends. They reuse the linger drifting and are flagged so they never count as a visit or get caught.
- **Blight:** Blighted Dirt is in the tag, so the normal clear and fill replaces it; the fill uses dirt.
- **Gamerule** `fairy_grove` (boolean, default true, MISC).
- **No version divergence:** `useItemOn`, level attachments, and the tags used exist unchanged on 26.1 to 26.3.

## Look and sound

| Moment | Look | Sound | Particle |
|---|---|---|---|
| Refused | Diamond stays, action bar message | Villager no | `smoke`, 4 at the Chime Hotel |
| Starting | 4 fairies burst out of the hotels | Beacon activate, then the Chime Hotel's chime | `glow`, 30 |
| Each column | The column settles into place | Rooted dirt place, quiet, every 4th column | `happy_villager`, 2 |
| Finished | Lanterns light up across the garden | Player level up | `firework`, 40 above the shrine |

## Risks / Trade-offs

- [Griefing on servers] → the allowlist refuses any player block, and `fairy_grove false` turns it off.
- [Lag while building] → 41 columns per tick, each up to 33 block sets, is at most about 1,350 block changes per tick for 30 seconds; light updates dominate. If `runClient` shows a stutter, halve the rate and double the time.
- [A tall mountain] → only the 24 blocks above the soil level get cleared; anything higher stays as an overhang. Document it in the guide.
