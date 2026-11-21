# Proposal

## Why

Late in a world, players have diamonds, hotels full of fairies, and Super Enriched soil, but laying out a big, pretty farm still means hours of digging, leveling, and placing water. The Fairy Grove turns those endgame materials into one magical moment: the fairies rebuild the land around a shrine into a lit, flowered garden of watered plots. It uses no new items, only a structure built from blocks the mod already has.

## What Changes

- **Shrine:** a Chime Hotel standing on Super Enriched Dirt, with 4 occupied Fairy Hotels exactly 3 blocks north, south, east, and west of it at the same height, and at least 4 fairy flowers within 3 blocks.
- **Trigger:** using a diamond on the Chime Hotel of a complete shrine, at any time. The diamond is used up only when building starts.
- **Safety check:** before using the diamond, the game scans the area. If anything other than natural terrain (dirt, stone, sand, gravel, plants, trees, water, and similar blocks in the `fertilegrounds:grove_replaceable` tag) is in the way, building doesn't start, the player keeps the diamond, and an action bar message names the first blocking block and where it is. An incomplete shrine gets a message saying what's missing.
- **Building:** over about 30 seconds, fairies fly out and rebuild a 35x35 area around the shrine one column at a time. They level it to the shrine's height, clearing up to 24 blocks above and filling dips up to 8 blocks deep with dirt.
- **Layout:**
  - The shrine sits in the center 9x9 plot, which stays untouched.
  - 8 farm plots of 9x9 surround it. Each is Enriched Farmland, already watered, around a center water block, with nothing planted.
  - One-block flower borders run between the plots.
  - At the 16 border crossings: the 4 inner ones get a beehive with a lantern on top, and the 12 outer ones get a flowering azalea leaf hedge with a lantern on top.
  - A 2-block meadow rims the edge, with grass, flowers, and azalea leaf bushes lit by lanterns.
- **Server control:** the gamerule `fairy_grove` (default true) turns the shrine off.
- A guide book page and the gameplay docs explain the shrine.

Out of scope: other layouts or sizes, undoing a grove, planting seeds, spawning bees, a dedicated advancement, and Jade tooltips.

New art: none. Everything the grove places is a vanilla or existing mod block.

## Capabilities

### New Capabilities

- `fairy-grove`: The shrine, its safety check, and the garden it builds.

### Modified Capabilities

None.

## Impact

A use-on-block hook for diamonds on the Chime Hotel, a pure layout class, a scan and a column-by-column builder saved with the world, a block tag, a gamerule, lang keys, and a guide page. Depends on `add-chime-hotel`, and `add-fertilizer` (Super Enriched Dirt).
