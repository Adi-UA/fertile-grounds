# Proposal

## Why

Fertile Grounds only ships English. Players and pack makers who play in other languages see English text in a mod about cozy reading (the guide book, messages, advancements). Opening translation to the community reaches them at no gameplay cost.

## What Changes

- Confirm every player-facing string uses a translation key (enforced by the asset consistency test from `add-regression-evals`).
- Add a translation section to `CONTRIBUTING.md` explaining how to add a language file.
- Set up Crowdin for community translation, linked from the README. The user creates the Crowdin account and project; this change adds the repo config.

Out of scope: machine-translated language files (translations come from people).

New art: none.

## Capabilities

### New Capabilities

- `translations`: How the mod's text is translated and how contributors add languages.

### Modified Capabilities

None.

## Impact

`CONTRIBUTING.md`, `README.md`, possibly a translation service config file. Runs last so every 1.3.0 string exists.
