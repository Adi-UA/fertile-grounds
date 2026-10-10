# Proposal

## Why

A fairy visit lasts about 5 seconds after arrival (a 3 second hover, a 2 second exit), makes one chime, and can dip into the ground on the way in. Players rarely see it, and catching one for a Fairy Hotel (`add-fairy-jar`) needs a fairy that sticks around. This change turns the visit into a short, mysterious scene worth watching.

## What Changes

- **Heralds:** firefly motes drift over the patch in the minute before a fairy arrives.
- **Growth dance:** the fairy spins above the crop, then crops grow in rings outward from the center, one chime per ring with rising pitch.
- **Linger:** after growing the patch, the fairy drifts between crops for `fertilegrounds:fairy_linger_seconds` (default 60) before leaving, humming softly. This is the window for catching it.
- **Shy:** a player within 4 blocks who isn't sneaking makes the fairy dart away; sneaking players can get close.
- **No dive into the ground:** the fairy comes down from open sky and never sinks below its hover height over an open crop. It still passes through roofs to reach covered crops.
- Replace the 20 second hard cap with per-phase limits so a longer visit isn't cut short.

Out of scope: catching itself (`add-fairy-jar`), arrival chime and messages (`add-visit-feedback`), and any change to visits that grow at dawn without a fairy.

New art: none.

## Capabilities

### New Capabilities

- `fairy-visit-behavior`: How a visiting fairy arrives, grows the patch, lingers, reacts to players, and leaves.

### Modified Capabilities

None.

## Impact

`VisitCropGoal` (new phases), `BeanFairy` (ambient sound, flee logic, vertical clamp), `FairyVisitScheduler` (herald timing, spawn position), `CropGrowthUtil` (grow one ring at a time), a new gamerule. Depends on `add-fairy-tags-and-gamerules` and `add-visit-feedback`.
