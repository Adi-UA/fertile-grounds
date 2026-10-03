# Proposal

## Why

Redstone players build harvest alarms, sorters, and automatic farms around vanilla signals. Exposing the Chime Hotel's "crops are ready" moment and soil tiers through comparators lets them build around Fertile Grounds without new mechanics to learn.

## What Changes

- **Comparators** next to a Chime Hotel get a 1-second pulse of 15 when it chimes, so a harvest alarm or auto-harvester can start from it.
- **Comparators** read a soil block's tier.

Removed from the earlier plan: jar days-left output and dispenser flower feeding, since hotels have no timer and no feeding (`add-fairy-jar`).

Out of scope: new redstone blocks, and sculk sensors detecting fairy arrival (deferred by the user).

New art: none.

## Capabilities

### New Capabilities

- `redstone-signals`: The redstone signals Fertile Grounds blocks provide.

### Modified Capabilities

None.

## Impact

Chime Hotel and soil blocks (`hasAnalogOutputSignal`), a `RINGING` block state on the Chime Hotel. Depends on `add-chime-hotel`.
