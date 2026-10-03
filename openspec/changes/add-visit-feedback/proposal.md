# Proposal

## Why

Players can miss a fairy visit entirely, especially one that happens while they sleep, so the mod's signature moment goes unnoticed. Players also have no goals to work toward beyond growing crops faster.

## What Changes

- A soft chime and subtitle play when a fairy arrives, and nearby players see "A Bean Fairy is visiting nearby."
- After a visit that happened while players slept, nearby players see "A Bean Fairy visited your farm" at dawn.
- A "Fertile Grounds" advancement tab with a root and the first entries: first fairy visit, first Fertilizer use. Later changes add their own entries (gift, jar, permanent jar, Harvest Moon visit).
- A `fertilegrounds:fairy_visit` advancement trigger that datapacks and quest mods can use.

Out of scope: a visit history or journal.

New art: none (the tab icon reuses the Fairy Dust texture).

## Capabilities

### New Capabilities

- `fairy-visit-feedback`: What players see and hear when a fairy visits.
- `mod-advancements`: The Fertile Grounds advancement tab and its custom trigger.

### Modified Capabilities

None.

## Impact

`FairyVisitScheduler`, `VisitCropGoal`, a new sound event and `sounds.json`, a new criterion trigger class, advancement JSON, lang keys. Depends on `add-fertilizer` for the Fertilizer advancement.
