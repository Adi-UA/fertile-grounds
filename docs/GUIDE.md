# Fertile Grounds Guide

Welcome, farmer! This is the same guide you get in game, plus every setting you can change. It's about richer soil, the shy Bean Fairies, and how to give one a home.

**Getting the book in game:** every player gets the guide the first time they join a world with Fertile Grounds. Lost it? Craft a new one from a **Book** and **Bone Meal**.

For the exact numbers behind each feature, see [DETAILS.md](DETAILS.md).

## Enriched Soil

Craft Dirt or Sand with Bone Meal to make Enriched soil. Crops on it grow faster, as if you gave them bone meal now and then.

Add Glowstone Dust for Super Enriched soil. It works even better. Till Enriched or Super Enriched Dirt with a hoe to plant on it, just like normal dirt.

## Fertilizer

Already have a farm? Use Fertilizer on its dirt or farmland to make it Super Enriched. No need to replant! On sand, it makes Enriched Sand.

A Dispenser can spread it for you. Craft 4 from Bone Meal, 2 Glowstone Dust, and 6 small flowers of any kind.

## Bean Fairies

On some nights, a tiny Bean Fairy visits a farm. It grows the crops around it all the way in one go.

Can't wait? Use Fairy Dust on a crop, and a fairy will come that night. Craft Fairy Dust from Glowstone Dust, an Amethyst Shard, and Bone Meal.

## A Fairy Welcome

Fairies love a pretty farm. Plant many kinds of flowers near your crops, and keep a beehive close by.

The more kinds of flowers you have, the more often fairies come.

## Harvest Moon

When the moon is full, a Harvest Moon rises. Fairies visit three times as often that night, and their gifts are better too.

## Gifts

Now and then, a fairy leaves a little gift by your crops. Bigger farms get nicer gifts.

You can get one every 3 days. Gifts never disappear, so take your time.

## Catching a Fairy

After its work, a fairy stays for a minute. Sneak up slowly (running scares it) and use a Glass Bottle on it.

After 3 days in the jar, it slips out and leaves a flower.

## Fairy Hotels

To keep your fairy, give it a home. Craft a hotel from a Lantern, a Firefly Bush on top, and flowers all around.

Use the jarred fairy on the hotel, and your fairy moves in for good. You can pick the hotel up and move it, fairy and all.

## Hotel Life

Your fairy helps nearby crops grow, fills beehives with honey, and scares creepers off. Its farmland can't be trampled, and you walk faster nearby.

Brush the hotel daily. Fairies love it!

## Chime Hotel

Craft your fairy's hotel with a Note Block and a Spyglass. Now your fairy watches the fields. When most nearby crops are ready, it rings a soft chime.

Time to harvest!

## Redstone

A Comparator behind a Chime Hotel sends a short signal when it chimes. Use it to light a lamp or start a farm machine.

Comparators can tell soils apart too.

## Fairy Grove

Put a Chime Hotel on Super Enriched Dirt, with a hotel fairy 3 blocks out on each side and flowers near. Give it a Diamond.

Fairies rebuild the land into a lit farm. No undo! If something you built is in the way, the shrine tells you what and where, and you keep your Diamond.

## Settings and commands

Each world can tune Fertile Grounds with gamerules. Type a command in chat (cheats must be on, or you need to be an operator on a server), and the change works right away. Chances are percentages from 0 to 100.

To check a setting, type the command without a value, like `/gamerule fertilegrounds:fairy_visit_chance`.

### Bean Fairy visits

| What you want | Command | Default |
|---|---|---|
| How often a fairy visits each player at night | `/gamerule fertilegrounds:fairy_visit_chance 1` | 1 |
| Turn off natural visits (Fairy Dust still works) | `/gamerule fertilegrounds:fairy_visit_chance 0` | |
| How much each kind of flower helps (percent of the base chance) | `/gamerule fertilegrounds:fairy_flower_bonus 10` | 10 |
| How much a nearby beehive helps (percent of the base chance) | `/gamerule fertilegrounds:fairy_hive_bonus 50` | 50 |
| How many times more often fairies visit on a Harvest Moon (1 to 10) | `/gamerule fertilegrounds:harvest_moon_multiplier 3` | 3 |
| How many seconds a fairy lingers after its work (0 to 600) | `/gamerule fertilegrounds:fairy_linger_seconds 60` | 60 |

### Gifts and jars

| What you want | Command | Default |
|---|---|---|
| Chance of a gift after a natural visit | `/gamerule fertilegrounds:fairy_gift_chance_natural 30` | 30 |
| Chance of a gift after a Fairy Dust visit | `/gamerule fertilegrounds:fairy_gift_chance_dust 10` | 10 |
| Days between gifts for each player (0 to 30) | `/gamerule fertilegrounds:fairy_gift_cooldown_days 3` | 3 |
| Days a jarred fairy waits before it slips away (1 to 30) | `/gamerule fertilegrounds:jarred_fairy_days 3` | 3 |

### Soil and hotels

| What you want | Command | Default |
|---|---|---|
| How often Enriched soil boosts a crop | `/gamerule fertilegrounds:enriched_boost_chance 15` | 15 |
| How often Super Enriched soil boosts a crop | `/gamerule fertilegrounds:super_enriched_boost_chance 50` | 50 |
| How often a hotel fairy helps a nearby crop | `/gamerule fertilegrounds:fairy_hotel_boost_chance 50` | 50 |
| The same, after you brushed the hotel today | `/gamerule fertilegrounds:fairy_hotel_petted_boost_chance 65` | 65 |
| How often a hotel fairy adds honey to a nearby hive | `/gamerule fertilegrounds:fairy_hotel_honey_chance 10` | 10 |
| Turn off the walking speed boost near hotels | `/gamerule fertilegrounds:fairy_hotel_speed false` | true |
| How much of a field must be ripe before a Chime Hotel rings (1 to 100) | `/gamerule fertilegrounds:chime_ready_percent 80` | 80 |

### World and server

| What you want | Command | Default |
|---|---|---|
| Turn off Fairy Groves (diamonds on shrines do nothing) | `/gamerule fertilegrounds:fairy_grove false` | true |
| Stop giving new players the guide book (for packs with their own quest book) | `/gamerule fertilegrounds:give_guide_book false` | true |

### Other ways to set things up

- **Jade tooltips:** with [Jade](https://modrinth.com/mod/jade) installed, each Fertile Grounds tooltip line can be turned on or off in Jade's plugin settings.
- **Modded crops and flowers:** fairies grow anything in the `fertilegrounds:fairy_crops` tag and count anything in `fertilegrounds:fairy_flowers`. A datapack can add blocks to either, so crops and flowers from other mods join in with no code.
- **What a grove may clear:** the `fertilegrounds:grove_replaceable` tag lists the natural blocks a Fairy Grove is allowed to clear. Anything else in the area stops it.
- **Gift loot:** the gift loot tables (`fertilegrounds:gameplay/fairy_gift/common`, `uncommon`, and `rare`) can be replaced with a datapack.

[DETAILS.md](DETAILS.md#for-modpack-makers) has the default contents of every tag and the advancement triggers quest mods can use.
