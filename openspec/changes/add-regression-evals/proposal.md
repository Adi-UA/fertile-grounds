# Proposal

## Why

Most past bugs shipped because nothing checked for them: recipes that silently failed to parse, items showing raw translation keys, missing item models, and farmland reverting to vanilla dirt. Each one returned or nearly returned during a port. 2.0.0 adds a dozen features and will be backported to 3 branches, so every past bug and every new feature needs an automatic check before work starts.

## What Changes

- A JUnit **asset consistency test** that reads `src/main/resources` and fails on missing lang keys, missing item model definitions, missing block states or textures, and recipe or advancement JSON in an outdated shape.
- **Smoke test regressions** for each past runtime bug, each check labeled with the commit that fixed it.
- More error markers in the smoke test for silent data-load failures.
- The smoke test splits into named suites so each 2.0.0 change adds its own suite instead of growing one list.
- A `runClient` log check script for client-only failures (missing models, textures, sounds).

Past bugs this change covers:

| Fixed in | Bug | Check |
|---|---|---|
| 88d5657 | Fairy stuck under a roof or with no crop nearby | Smoke (already present, now labeled) |
| 83fd1fb | Trampled or dried Enriched Farmland reverts to vanilla dirt | Smoke |
| 1e5d483 | Sugar cane can't be planted on Enriched Sand | Smoke tag check |
| e5356de | Items show the missing-model placeholder | JUnit + client log |
| 0a7576e | Items show raw translation keys | JUnit |
| b95a82e, 02ba2bb | Recipes and recipe advancements silently fail to parse | JUnit shape check + smoke error markers |
| 14418eb | Registry ids set too late, crash at boot | Smoke boot check (already present) |

Out of scope: art checks (the user reviews art by eye), and porting the suite to 1.21.11, 26.1, and 26.2 (that happens in the backport phase).

New art: none.

## Capabilities

### New Capabilities

- `verification-suite`: The automatic checks every change must pass, and the rule that fixed bugs get regression checks.

### Modified Capabilities

None.

## Impact

`scripts/server_smoke_test.py` (restructure into suites), new `scripts/client_log_check.py`, new JUnit test under `src/test/`. No mod code changes. This change goes first in 2.0.0 so every later change extends it.
