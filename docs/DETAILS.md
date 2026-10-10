# Fertile Grounds in detail

The exact numbers, recipes, and settings behind each feature. The [README](../README.md) has the short version.

## Gameplay

Three tiers, each a drop-in soil upgrade that behaves like its vanilla counterpart (till it, plant on it, it still floods/dries/tramples) but with a chance per random tick to instantly advance whatever's growing on top of it, as if bone-mealed:

| Tier | Block(s) | Crafted from | Boost chance/tick |
|---|---|---|---|
| 1 | Enriched Dirt / Enriched Farmland | Dirt + Bone Meal | 15% |
| 2 | Enriched Sand | Sand + Bone Meal | 15% |
| 3 | Super Enriched Dirt / Super Enriched Farmland | Dirt + Bone Meal + Glowstone Dust | 50% |

All three are craftable (shapeless, 1:1:1, no crafting table shape required) and unlock in the recipe book the first time you hold the right ingredients.

**Enriched Dirt / Super Enriched Dirt** work exactly like vanilla dirt: till them into farmland with a hoe, only the farmland form accepts crops. Their farmland reverts back to their *own* dirt tier (not vanilla dirt) on drought or trampling, and shows a darker, moist texture at full hydration, same as vanilla farmland.

**Enriched Sand** behaves like vanilla sand (sugar cane can still be planted on it near water), no tilling involved. It boosts whatever bonemealable plant is directly on top of it, bamboo is the clearest example since it can grow on sand; sugar cane and cactus don't accept bone meal in vanilla, so they grow at normal speed on it either way.

**Fertilizer** upgrades soil you already farm on, so you don't have to dig up a farm and replant it. Use it on vanilla or Enriched dirt or farmland to turn it into the Super Enriched version, or on vanilla sand to turn it into Enriched Sand. The crop on top keeps its growth stage and farmland keeps its moisture. Dispensers apply it to the block they face. Craft 4 (shapeless) from 1 Bone Meal, 2 Glowstone Dust, and 6 small flowers in any mix; the recipe unlocks when you pick up Glowstone Dust.

All of the mod's items live in their own creative-inventory tab, "Fertile Grounds."

### Bean Fairy

Like Stardew Valley's Crop Fairy, a Bean Fairy (a tiny winged edamame pod) sometimes visits a farm at night and fully grows every crop in a 5x5 patch. Each night, every player in the Overworld has a 1% chance of a visit to a random crop within 16 blocks. If everyone sleeps through it, the crops are grown at dawn anyway.

A welcoming farm raises the odds. Each different kind of small flower within 8 blocks of the crop adds a tenth of the base chance (up to 10 kinds), and a beehive or bee nest nearby adds half. On full-moon nights a **Harvest Moon** rises: everyone in the Overworld gets a message, and the chance triples, so a farm with 10 kinds of flowers and a hive gets 7.5% instead of 1%.

To call one on purpose, craft **Fairy Dust** (shapeless: Glowstone Dust + Amethyst Shard + Bone Meal) and use it on a growing crop. Fairies still only come at night: within about 30 seconds if it's already dark, otherwise at nightfall. Only crops in the `fertilegrounds:fairy_crops` tag count, which by default is vanilla's `#minecraft:crops` (wheat, carrots, potatoes, beetroots, melon and pumpkin stems, torchflowers, pitcher plants) plus any modded crop that joins it.

The creative tab also has a Bean Fairy Spawn Egg, which sends a fairy to the nearest crop.

A visit is a short scene. Fireflies drift over the patch in the minute before the fairy arrives. It comes down from the sky, spins above the crop, then grows the patch in rings from the center out, with a rising chime for each ring. Afterward it lingers for a minute, drifting between the crops and humming, before it flies away. Bean Fairies are shy: run at one and it darts away, but sneak up and it lets you come close.

Sometimes a fairy leaves a gift at the crop for the player it visited: a 30% chance after a natural visit, 10% after Fairy Dust, plus 1% for each kind of small flower nearby (up to 10%). Dust from a dispenser never brings a gift, and each player can get one gift every 3 in-game days. Bigger farms get better gifts: under 25 crops nearby rolls the common table (seeds, bone meal, flowers), 25 to 99 the uncommon one (berries, cocoa, Fairy Dust), and 100 or more the rare one (Fertilizer, golden carrots, torchflower seeds, pitcher pods). On a Harvest Moon, gifts roll one table higher. Gifts never despawn, so they wait for you.

When a fairy arrives, you hear a soft chime and see "A Bean Fairy is visiting nearby" if you're within 32 blocks. If you slept through a visit, you get a message at dawn instead.

### Fairy Hotels

A fairy that has finished growing its patch can be caught: sneak up while it lingers (it darts away from a running player) and use a Glass Bottle on it to get a **Jarred Fairy**. A fairy still flying in or growing crops is too busy to catch. A Jarred Fairy does nothing on its own, and it never stays trapped: after 3 in-game days, whether carried, stored in a chest, or dropped, it slips away and leaves a small flower behind.

To keep it, build it a home. Craft an **Empty Fairy Hotel** (shaped: a Firefly Bush in the top middle, a Lantern in the center, and 7 small flowers in any mix around it) and use the Jarred Fairy on it. The fairy moves in for good, the hotel glows green (light level 12, against the empty hotel's 5), and you get the bottle back. Hotels hang and sit like lanterns, and breaking an occupied hotel drops it with its fairy still inside, so you can move it anywhere.

A hotel fairy looks after the farm around it:

- **Crops:** now and then it grows a crop within 4 blocks one stage, like a pinch of bone meal (50% per random tick). Bees carrying nectar nearby give it an extra try.
- **Honey:** sometimes it adds a level of honey to a beehive or bee nest within 4 blocks (10% per random tick).
- **Petting:** brush the hotel once a day and the fairy is happy until the next dawn, growing crops a little more often (65%). Brushing again the same day does nothing and costs no durability.
- **Fairy Speed:** within 8 blocks you walk 10% faster, shown with the Speed icon, like a beacon's effect. More hotels don't make it stronger.
- **Guarding:** creepers keep 6 blocks away, the way they avoid cats, and farmland within 4 blocks can't be trampled (it still dries out without water). Modded farmland that doesn't build on vanilla's isn't covered.

Hotels within 8 blocks of each other share their crop and honey help, so a cluster helps as much as one hotel. Empty hotels do none of this.

A **Chime Hotel** (shapeless: an Occupied Fairy Hotel, a Note Block, and a Spyglass) does everything an occupied hotel does, and its fairy watches the field for you. Every 5 seconds it counts the crops within 8 blocks (2 up or down), and once at least 4 are there and 80% are fully grown, it rings a soft 3-note chime that players hear up to 32 blocks away. It rings once per harvest: after you harvest and replant, it rings again when the field is ready. It also chimes once when placed, so you learn the sound. Crops from other mods that never report being fully grown keep it from ringing; the `fertilegrounds:fairy_crops` tag decides what counts.

### Blight

**Blighted Dirt** is cursed, ashen soil. On each random tick it picks one touching block, and if that block is surface soil (dirt, grass, farmland, sand, or red sand, with nothing solid on top), it turns into Blighted Dirt 20% of the time. Enriched soil holds out twice as long and Super Enriched four times as long. Any plant on a block blight takes withers into a dead bush and drops nothing. Blight never crosses stone, a dirt path, water, or anything else outside the `fertilegrounds:blightable` tag, so a path around a farm protects it. Nothing grows on Blighted Dirt except dead bushes, and a hoe can't till it.

Blight starts two ways. A witch killed by a player has a 1% chance to blight the soil in a 3x3 patch under it, and every player within 32 blocks reads a chat message about it. Or craft it (shapeless: Dirt + Nether Wart + Blaze Rod + Fermented Spider Eye), which is handy for sabotage on servers.

To cure it, use **Fertilizer** on a blighted block (by hand or from a Dispenser) to turn it back into dirt. A Bean Fairy visit also cures the blight under its 5x5 patch.

### Fairy Grove

Build a shrine: a **Chime Hotel** standing on **Super Enriched Dirt**, an occupied Fairy Hotel (or Chime Hotel) exactly 3 blocks north, south, east, and west of it at the same height, and at least 4 small flowers within 3 blocks. Use a **Diamond** on the Chime Hotel, and its fairies rebuild the 35x35 area around the shrine over about 30 seconds:

- 8 plots of 9x9 Enriched Farmland, already wet, each around one water block, with nothing planted
- grass borders with flowers between the plots, a beehive with a lantern at each of the 4 crossings next to the shrine, and a flowering azalea hedge with a lantern at the other 12
- a 2-block meadow rim of grass, flowers, and azalea bushes, some lit by lanterns

The land is leveled to the shrine's soil: everything up to 24 blocks above it is cleared, and dips up to 8 blocks deep are filled with dirt. Blighted Dirt in the area turns back into dirt. The shrine's own 9x9 plot is never touched. It can't be undone.

Before using the diamond, the grove checks that the area holds only natural terrain (the `fertilegrounds:grove_replaceable` tag: dirt, stone, sand, gravel, ores, logs, leaves, plants, crops, farmland, water, and similar). If anything else is there, such as a chest or a wall, you keep the diamond and the action bar names the block and where it is. An incomplete shrine also keeps the diamond and says what's missing. A block placed mid-build stays, along with the rest of its column. A grove interrupted by a restart finishes afterward.

### Wandering Trader

A Wandering Trader can roll Fairy Dust or Fertilizer among its rare trades: 2 Fairy Dust for 4 emeralds (4 times) and 1 Fertilizer for 6 emeralds (3 times). Its vanilla trades stay as they are.

### Guide book

Every player gets a **Fertile Grounds Guide** the first time they join a world with the mod, including worlds from older versions; it covers every feature in 12 short pages, in the player's own language. Lost it? Craft a new one from a Book and Bone Meal. Packs with their own quest book can turn the gift off with `fertilegrounds:give_guide_book false`.

### Redstone

A comparator reading a Chime Hotel gives a 1-second pulse of 15 when the hotel rings for a ready field, enough to light a lamp or start an auto-harvester; the chime when you place the hotel gives no pulse. Empty and occupied hotels read 0. A comparator reading soil gives its tier: 7 for Enriched Dirt, Farmland, and Sand, and 15 for Super Enriched Dirt and Farmland.

### Jade

With [Jade](https://modrinth.com/mod/jade) installed, looking at blocks shows extra lines: the boost chance of enriched farmland and sand, "A Bean Fairy is coming tonight" on a crop with a pending visit, whether a hotel's fairy was petted today, and a Chime Hotel's count such as "Crops ready: 41/50". Each line can be turned off in Jade's settings. Jade is optional; without it nothing changes.

### Advancements

Fertile Grounds has its own advancement tab:

| Advancement | How to earn it |
|---|---|
| Fertile Grounds | Get any enriched soil block (opens the tab) |
| A Little Help | Be within 32 blocks when a Bean Fairy grows your crops |
| Grassroots Upgrade | Upgrade soil in place with Fertilizer |
| Fairy Favor | Receive a gift from a Bean Fairy |
| By the Light of the Harvest Moon | Be near a Bean Fairy visit on a full-moon night |
| Fairy in a Bottle | Catch a lingering Bean Fairy in a Glass Bottle |
| Home Sweet Hotel | Give a Bean Fairy a home in a Fairy Hotel |

Datapacks and quest mods can use the `fertilegrounds:fairy_visit` and `fertilegrounds:harvest_moon_visit` advancement triggers, which fire for every player within 32 blocks of a visit, `fertilegrounds:fairy_gift`, which fires for the player who receives a gift, and `fertilegrounds:fairy_caught` and `fertilegrounds:fairy_moved_in` for the player who catches a fairy or moves one into a hotel.

## For modpack makers

Tags decide which blocks count as crops and flowers. Add to them with a datapack, and modded content works with no code.

| Tag | Type | Default | Used for |
|---|---|---|---|
| `fertilegrounds:fairy_crops` | Block | `#minecraft:crops` | Crops a Bean Fairy, Fairy Dust, or a Fairy Hotel may grow, and the crops a Chime Hotel watches |
| `fertilegrounds:blightable` | Block | Dirt, grass block, farmland, sand, red sand, and the mod's enriched soils | Soil that Blighted Dirt and the witch curse can take |
| `fertilegrounds:grove_replaceable` | Block | Natural terrain: dirt, stone, sand, gravel, ores, logs, leaves, plants, crops, farmland, water, and lava | Blocks a Fairy Grove may clear or fill; anything else stops it |
| `fertilegrounds:fairy_flowers` | Block and item | `#minecraft:small_flowers`, plus `#c:flowers/small` when another mod defines it | Flower-based features, the Fairy Hotel recipe, and the flower a Jarred Fairy leaves |

Gift loot lives in the loot tables `fertilegrounds:gameplay/fairy_gift/common`, `uncommon`, and `rare`; a datapack can replace any of them.

Gamerules tune the numbers per world with `/gamerule <name> <value>`. Chances are whole percentages from 0 to 100, and a change applies right away.

| Gamerule | Default | What it sets |
|---|---|---|
| `fertilegrounds:fairy_visit_chance` | 1 | Chance per player, per night, of a natural Bean Fairy visit (0 turns natural visits off; Fairy Dust still works) |
| `fertilegrounds:fairy_flower_bonus` | 10 | Percent of the base visit chance each different small flower near the crop adds (up to 10 kinds) |
| `fertilegrounds:fairy_hive_bonus` | 50 | Percent of the base visit chance a nearby beehive or bee nest adds |
| `fertilegrounds:harvest_moon_multiplier` | 3 | What the visit chance is multiplied by on full-moon nights (1 to 10, not a percentage) |
| `fertilegrounds:fairy_linger_seconds` | 60 | Seconds a fairy stays over the crops after growing them (0 to 600, not a percentage) |
| `fertilegrounds:fairy_gift_chance_natural` | 30 | Chance a natural visit leaves its player a gift |
| `fertilegrounds:fairy_gift_chance_dust` | 10 | Chance a Fairy Dust visit leaves its player a gift |
| `fertilegrounds:fairy_gift_cooldown_days` | 3 | In-game days between gifts for one player (0 to 30, not a percentage) |
| `fertilegrounds:jarred_fairy_days` | 3 | In-game days a Jarred Fairy waits before it slips away (1 to 30, not a percentage) |
| `fertilegrounds:fairy_hotel_boost_chance` | 50 | Chance per random tick that an occupied hotel grows a crop within 4 blocks |
| `fertilegrounds:fairy_hotel_petted_boost_chance` | 65 | The same chance for a hotel brushed since the last dawn |
| `fertilegrounds:fairy_hotel_honey_chance` | 10 | Chance per random tick that an occupied hotel adds honey to a hive within 4 blocks |
| `fertilegrounds:chime_ready_percent` | 80 | Percent of the crops near a Chime Hotel that must be fully grown for it to ring (1 to 100) |
| `fertilegrounds:blight_spread_chance` | 20 | Chance per random tick that Blighted Dirt takes the plain soil it picked; enriched soil resists (0 stops all spreading) |
| `fertilegrounds:witch_blight_chance` | 1 | Chance that a witch killed by a player blights the soil under it |
| `fertilegrounds:fairy_grove` | true | Whether a diamond on a Chime Hotel shrine builds a Fairy Grove (true or false) |
| `fertilegrounds:fairy_hotel_speed` | true | Whether players near an occupied hotel get Fairy Speed (true or false) |
| `fertilegrounds:give_guide_book` | true | Whether each player gets the guide book on their first join (true or false) |
| `fertilegrounds:enriched_boost_chance` | 15 | Boost chance per random tick for Enriched Farmland and Enriched Sand |
| `fertilegrounds:super_enriched_boost_chance` | 50 | Boost chance per random tick for Super Enriched Farmland |

## Minecraft version

This branch targets Minecraft 26.3. Other supported versions live on their own branches, each an independently maintained port (changes aren't shared automatically between them):

| Branch | Minecraft version |
|---|---|
| `1.20.1` | 1.20.1 |
| `1.21.1` | 1.21.1 |
| `1.21.10` | 1.21.10 |
| `1.21.11` | 1.21.11 |
| `26.1` | 26.1 |
| `26.2` | 26.2 |
| `26.3` / `main` | 26.3 (this branch, latest supported version) |

## Requirements

- Minecraft 26.3
- [Fabric Loader](https://fabricmc.net/) 0.19.5+
- [Fabric API](https://modrinth.com/mod/fabric-api) 0.161.0+26.3
- Java 25+
- Optional: [Jade](https://modrinth.com/mod/jade) for look-at tooltips

## Building from source

```
./gradlew build
```

The mod jar is output to `build/libs/fertilegrounds-<mod version>+<minecraft version>.jar` (e.g. `fertilegrounds-1.2.0+26.3.jar`). The `+<mc version>` suffix matches Fabric's own convention (see Fabric API's own release names) and keeps jars from different branches from colliding if you're collecting builds from more than one version in the same place. Drop it in your `mods/` folder alongside Fabric API.

## Development

[AGENTS.md](../AGENTS.md) lists every command, including the dev client, formatting, and the GameTests.
