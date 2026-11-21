# Proposal

## Why

Jade appears in most large modpacks (69.6M Modrinth downloads, supports every 2.0.0 target). Showing Fertile Grounds state in its tooltip lets pack players see soil tiers, pending visits, and hotel status at a glance, which fits the "complement big mods" goal.

## What Changes

- With Jade installed, looking at soil shows its boost chance.
- Looking at a crop with a pending visit shows "A Bean Fairy is coming tonight."
- Looking at an occupied Fairy Hotel shows whether its fairy was petted today; a Chime Hotel also shows how many nearby crops are grown (for example "Crops ready: 41/50").
- Without Jade, nothing changes and the mod doesn't require it.

Out of scope: WTHIT support (4.2M downloads, no 26.3 version yet).

New art: none.

## Capabilities

### New Capabilities

- `jade-tooltips`: What Fertile Grounds shows in Jade tooltips.

### Modified Capabilities

None.

## Impact

A `compileOnly` Jade dependency per branch, a Jade plugin entrypoint in `fabric.mod.json`, server data providers. Depends on `add-chime-hotel`.
