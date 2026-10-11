# Proposal

## Why

Every feature so far helps a farm grow. Blight adds the opposite: a rare curse that creeps across soil, gives players a reason to wall off and tend their fields, and on servers lets rivals sabotage each other's farms. Every cure uses something the mod already has (Fertilizer, Bean Fairies, and later the Fairy Grove), so blight also gives those features a second job.

## What Changes

- **Blighted Dirt:** one new block. It drops itself when mined with any tool, like dirt.
- **Spreading:** on random ticks it turns one touching block from the `fertilegrounds:blightable` tag (dirt, grass, farmland, sand, red sand, and the mod's enriched soils) into Blighted Dirt. Enriched soil resists at half the rate and Super Enriched at a quarter. Any block outside the tag, such as a path, stone, or water, stops it.
- **Withering:** when blight takes a block, the plant on top withers into a dead bush and drops nothing.
- **Witches:** a witch killed by a player has a 1% chance to blight the soil under it (a 3x3 patch). Nearby players get a chat message saying blight has started there.
- **Crafting:** Dirt, Nether Wart, a Blaze Rod, and a Fermented Spider Eye (shapeless) make 1 Blighted Dirt, for deliberate sabotage.
- **Cures:** using Fertilizer on Blighted Dirt turns it back into plain dirt, and a Bean Fairy visit cures the blight under its 5x5 patch. `add-fairy-grove` adds a cure for a whole area.
- **Server controls:** gamerules `blight_spread_chance` (0 stops all spreading) and `witch_blight_chance`.
- A guide book page and the gameplay docs explain blight and its cures.

Out of scope: a blighted farmland block, blight on other blocks (stone, mud, podzol, mycelium) unless a pack adds them to the tag, blight harming mobs or players, Jade tooltips, and advancements.

New art: Blighted Dirt block (a sickly, ashen dirt, traced from vanilla dirt; no emissive layer).

## Capabilities

### New Capabilities

- `blight`: Blighted Dirt, how it spreads, how it starts, and how players cure it.

### Modified Capabilities

None. The Fertilizer and fairy-visit cures are new behavior, so they live in the `blight` spec.

## Impact

A new block, block item, loot table, block tag, recipe and unlock advancement, two gamerules, a death event listener, lang keys, a guide page, and hooks in `FertilizerItem` and the fairy visit's growth step. `add-fairy-grove` depends on this change.
