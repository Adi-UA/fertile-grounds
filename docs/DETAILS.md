# Fertile Grounds in detail

The exact numbers, recipes, and tags behind each feature. The [README](../README.md) has the short version, and the [guide](GUIDE.md) has the friendly one plus every setting.

## Gameplay

Each enriched soil works like its vanilla block: you till it, plant on it, and it dries out or gets trampled the same way. On each random tick, it also has a chance to grow the plant on top by one stage, as bone meal does:

| Blocks | Crafted from | Chance per random tick |
|---|---|---|
| Enriched Dirt and Enriched Farmland | Dirt + Bone Meal | 15% |
| Enriched Sand | Sand + Bone Meal | 15% |
| Super Enriched Dirt and Super Enriched Farmland | Dirt + Bone Meal + Glowstone Dust | 50% |

Each recipe is shapeless, with one of each ingredient, and unlocks in the recipe book the first time you hold its ingredients.

**Enriched Dirt and Super Enriched Dirt** work like vanilla dirt: a hoe tills them into farmland, and only the farmland takes crops. When their farmland dries out or gets trampled, it turns back into its *own* dirt, not vanilla dirt, and it looks darker when wet, as vanilla farmland does.

**Enriched Sand** works like vanilla sand, so sugar cane still grows on it next to water. It boosts any plant on top that takes bone meal, such as bamboo. Sugar cane and cactus don't take bone meal in vanilla, so they grow at their normal speed on it.

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

### Fairy Grove

Build a shrine: a **Chime Hotel** standing on **Super Enriched Dirt**, an occupied Fairy Hotel (or Chime Hotel) exactly 3 blocks north, south, east, and west of it at the same height, and at least 4 small flowers within 3 blocks. Use a **Diamond** on the Chime Hotel, and its fairies rebuild the 35x35 area around the shrine over about 30 seconds:

- 8 plots of 9x9 Enriched Farmland, already wet, each around one water block, with nothing planted
- grass borders with flowers between the plots, a beehive with a lantern at each of the 4 crossings next to the shrine, and a flowering azalea hedge with a lantern at the other 12
- a 2-block meadow rim of grass, flowers, and azalea bushes, some lit by lanterns

The land is leveled to the shrine's soil: everything up to 24 blocks above it is cleared, and dips up to 8 blocks deep are filled with dirt. The shrine's own 9x9 plot is never touched. It can't be undone.

Before using the diamond, the grove checks that the area holds only natural terrain (the `fertilegrounds:grove_replaceable` tag: dirt, stone, sand, gravel, ores, logs, leaves, plants, crops, farmland, water, and similar). If anything else is there, such as a chest or a wall, you keep the diamond and the action bar names the block and where it is. An incomplete shrine also keeps the diamond and says what's missing. A block placed mid-build stays, along with the rest of its column. A grove interrupted by a restart finishes afterward.

### Wandering Trader

A Wandering Trader can roll Fairy Dust or Fertilizer among its 2 rare trades: 2 Fairy Dust for 4 emeralds (4 times) and 1 Fertilizer for 6 emeralds (3 times). Its vanilla trades stay as they are.

### Guide book

Every player gets a **Fertile Grounds Guide** the first time they join a world with the mod, including worlds from older versions; it covers every feature in 13 short pages (also [on GitHub](GUIDE.md)), in the player's own language. Lost it? Craft a new one from a Book and Bone Meal. Packs with their own quest book can turn the gift off with `fertilegrounds:give_guide_book false`.

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
| `fertilegrounds:grove_replaceable` | Block | Natural terrain: dirt, stone, sand, gravel, ores, logs, leaves, plants, crops, farmland, water, and lava | Blocks a Fairy Grove may clear or fill; anything else stops it |
| `fertilegrounds:fairy_flowers` | Block and item | `#minecraft:small_flowers`, plus `#c:flowers/small` when another mod defines it | Flower-based features, the Fairy Hotel recipe, and the flower a Jarred Fairy leaves |

Gift loot lives in the loot tables `fertilegrounds:gameplay/fairy_gift/common`, `uncommon`, and `rare`; a datapack can replace any of them.

Gamerules tune the numbers per world. The [guide's settings section](GUIDE.md#settings-and-commands) lists every one with its default and a ready-to-paste command.

## Minecraft version

This branch targets Minecraft 1.21.11. Version 2.0 supports 26.1, 26.2, 26.3, 1.21.11, 1.21.10, and 1.21.1, each on its own branch; changes land on `main` first and are backported one version at a time. Versions from 1.21.1 up to 26.1 get 2.0 only; 1.20.1 stays on 1.2.0:

| Branch | Minecraft version | Mod version |
|---|---|---|
| `26.3` / `main` | 26.3 (latest) | 2.0 |
| `26.2` | 26.2 | 2.0 |
| `26.1` | 26.1 | 2.0 |
| `1.21.11` | 1.21.11 (this branch) | 2.0 (final) |
| `1.21.10` | 1.21.10 | 2.0 (final) |
| `1.21.1` | 1.21.1 | 2.0 (final) |
| `1.20.1` | 1.20.1 | 1.2.0 (frozen) |

## Requirements

- Minecraft 1.21.11, Java 21+
- One loader:
  - [Fabric Loader](https://fabricmc.net/) 0.20.1+ with [Fabric API](https://modrinth.com/mod/fabric-api) 0.141.6+1.21.11
  - [NeoForge](https://neoforged.net/) 21.11.48+
  - [Forge](https://files.minecraftforge.net/) 61.2.4+
- Optional: [Jade](https://modrinth.com/mod/jade) for look-at tooltips, on Fabric and NeoForge (Jade has no Forge build for 1.21.11)

## Building from source

```
./gradlew build
```

Each loader's jar lands in its own `build/libs/` folder: `fabric/` builds `fertilegrounds-2.0.1+1.21.11.jar`, and `neoforge/` and `forge/` build `fertilegrounds-neoforge-2.0.1+1.21.11.jar` and `fertilegrounds-forge-2.0.1+1.21.11.jar`. The `+<mc version>` suffix matches Fabric's own convention (see Fabric API's own release names) and keeps jars from different branches from colliding if you're collecting builds from more than one version in the same place. Drop the jar for your loader in your `mods/` folder, alongside Fabric API on Fabric.

## Development

[AGENTS.md](../AGENTS.md) lists every command, including the dev client, formatting, and the GameTests.
