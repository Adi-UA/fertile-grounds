# Proposal

## Why

Redstone players build reminder lamps, sorters, and automatic farms around vanilla signals. Exposing jar and soil state through comparators and dispensers lets them build around Fertile Grounds without new mechanics to learn.

## What Changes

- **Comparators** read a Fairy Jar's days left (0 to 15) and a soil block's tier.
- **Dispensers** feed fairy flowers to a jar in front of them, adding days but not friendship, so permanence still needs a player's care.

Out of scope: new redstone blocks, and sculk sensors detecting fairy arrival (deferred by the user).

New art: none.

## Capabilities

### New Capabilities

- `redstone-signals`: The redstone signals and dispenser behaviors Fertile Grounds blocks provide.

### Modified Capabilities

None.

## Impact

Fairy Jar and soil blocks (`hasAnalogOutputSignal`), and a dispenser behavior for the flower tag. Depends on `add-fairy-jar`.
