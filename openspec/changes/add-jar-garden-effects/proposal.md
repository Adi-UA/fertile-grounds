# Proposal

## Why

A jarred fairy needs a reason to sit in the farm beyond light. Small, vanilla-flavored protections (creepers keep away like they do from cats, crops don't get trampled) and a gentle growth boost make the jar a farm guardian worth caring for, without replacing the soil tiers or sprinkler mods.

## What Changes

- A filled Fairy Jar, temporary or permanent, gently boosts fairy crops within 4 blocks. Clustered jars don't multiply the boost.
- Bees carrying nectar near a jar give it an extra boost attempt.
- Creepers avoid filled jars within 6 blocks, the same way they avoid cats.
- Farmland within 4 blocks of a filled jar can't be trampled, by players or mobs.
- Empty Fairy Jars do none of this.

Out of scope: protecting crops from other mobs, and keeping out non-creeper monsters.

New art: none.

## Capabilities

### New Capabilities

- `fairy-jar-effects`: What a filled jar does to the farm around it.

### Modified Capabilities

None.

## Impact

Fairy Jar block (random ticks), a point-of-interest type for jars, a creeper AI mixin, a farmland trample mixin, gamerules. Depends on `add-fairy-jar`.
