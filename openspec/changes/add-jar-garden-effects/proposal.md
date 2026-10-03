# Proposal

## Why

A hotel fairy needs a reason to live on the farm beyond light. Small, vanilla-flavored help (crops grow a little faster, bees make honey sooner, creepers keep away like they do from cats, crops don't get trampled) makes the hotel a farm guardian, without replacing the soil tiers or sprinkler mods. A daily brush gives players a small ritual with their fairy, and a gentle speed lift makes working the farm feel lighter.

## What Changes

- An Occupied Fairy Hotel (including a Chime Hotel) gently boosts fairy crops within 4 blocks. Clustered hotels don't multiply the boost.
- Bees carrying nectar near a hotel give it an extra boost attempt.
- **Honey:** a hotel sometimes adds one honey level to a beehive or bee nest within 4 blocks.
- Creepers avoid occupied hotels within 6 blocks, the same way they avoid cats.
- Farmland within 4 blocks of an occupied hotel can't be trampled, by players or mobs.
- **Petting:** using a Brush on an occupied hotel once per in-game day makes the fairy happy until the next dawn, raising its crop boost chance. Breaking the hotel forgets that it was petted.
- **Fairy Speed:** players within 8 blocks of an occupied hotel move 10% faster, shown with vanilla's Speed icon, like a beacon's effect. Several hotels don't stack.
- Empty Fairy Hotels do none of this.

Out of scope: protecting crops from other mobs, keeping out non-creeper monsters, petting effects beyond the crop boost, and hunger effects (vanilla has no hunger-drain attribute, so it would need a mixin).

New art: none.

## Capabilities

### New Capabilities

- `fairy-hotel-effects`: What an occupied hotel does to the farm around it, and petting.

### Modified Capabilities

None.

## Impact

Occupied hotel blocks (random ticks, a `PETTED` block state, a scheduled tick at dawn), a point-of-interest type for occupied hotels, a custom mob effect reusing vanilla's Speed icon, a per-player check every second, a creeper AI mixin, a farmland trample mixin, gamerules. Depends on `add-fairy-jar`.
