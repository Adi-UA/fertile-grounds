# Proposal

## Why

A visit today only grows crops, so after the first few there's nothing new to look forward to. A small, occasional gift gives players a reason to check the farm each morning, and scaling it with farm size rewards players who build and tend large farms.

## What Changes

- After a visit grows crops, the fairy has a chance to leave a gift at the patch. The gift item never despawns.
- Balance rules, each behind a gamerule:

  | Rule | Default |
  |---|---|
  | Natural visit chance | 30% |
  | Fairy Dust visit chance | 10% |
  | Dispenser Fairy Dust visit | Never gives a gift |
  | Flower bonus | +1% per distinct flower type, capped at +10% |
  | Cooldown | 1 gift per player every 3 in-game days |

- Farm size picks the gift tier: under 25 fairy crops nearby is common, 25 to 99 is uncommon, 100+ is rare. On a Harvest Moon, the gift rolls one tier higher.
- Each tier is a data-driven loot table that packs can override.
- Adds a "first gift" advancement.

Out of scope: gifts from jarred fairies, and gifts when a fairy flies away from a jar (the user rejected that).

New art: none.

## Capabilities

### New Capabilities

- `fairy-gifts`: When a visit leaves a gift, which tier it uses, and how often a player can get one.

### Modified Capabilities

None. The new advancement is added to `mod-advancements` as an ADDED requirement.

## Impact

`FairyVisit` gains a source and an owner (saved worlds from 1.2.0 still load), `FairyDustItem`, `FairyVisitEvents`, a world attachment for cooldowns, loot tables, gamerules. Depends on `add-visit-attraction` (Harvest Moon) and `add-visit-feedback` (visit hook, advancements).
