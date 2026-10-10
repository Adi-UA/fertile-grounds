# Design

## Context

The rule classes (`FairyTiming`, `FairyGifts`, `GrowthRings`, `ShyFlight`, `SoilUpgrades`, `SoilTier`, `ChimeRules`, `FairyHotelRules`, `JarredFairyRules`) hold no Minecraft types and have JUnit tests in `src/test`. `FairyGiftCooldowns` stores data on the level, so its rule lives in `FairyGifts.isOffCooldown` and only that gets a proof. Nothing in the repo uses Lean yet, and neither Lean nor elan is installed.

## Goals / Non-Goals

**Goals:** one Lean file per Java rule class, each proving the spec's properties for every input, and a check that fails on unfinished proofs.

**Non-Goals:** extracting Lean from Java or Java from Lean; proofs about Minecraft; making `./gradlew build` need Lean.

## Decisions

- **Install with Homebrew's `elan-init`**, the official Lean version manager; mise has no Lean plugin. `proofs/lean-toolchain` pins the Lean version that the chosen Mathlib release names, so `lake` fetches the same compiler on every machine.
- **Mathlib as the one dependency**, pinned to a release tag in `lakefile.toml`. It supplies exact rationals and reals, `norm_num`, `omega` extensions, and finset lemmas the ring and cluster proofs need. `lake exe cache get` downloads its prebuilt files, so nobody compiles Mathlib. The alternative, core Lean alone, has no reals and would turn the shy flight and chance proofs into hand-rolled arithmetic.
- **Exact numbers in the models.** Ticks, days, and counts are `Int` or `Nat`, matching Java `long` and `int` within the ranges gamerules allow. `visitChance` divides whole numbers by 100, so the model uses `ℚ` and proves the result exactly. `ShyFlight` uses `hypot`, so its model uses `ℝ`. The design records this gap: Java's `double` rounds, the model doesn't, so the proofs say the formula is right, not that floating point is.
- **Mirror, don't translate.** Each Lean definition carries a doc comment naming the Java method (for example `/-- Mirrors ChimeRules.next. -/`) and keeps its argument order and default constants. Each JUnit case becomes an `example` proved by `decide` or `norm_num`, under a `section JUnit` in the same file. When Java changes, its JUnit test changes, and the copied examples stop matching until the model does too, the next time someone runs the proofs.
- **Layout.** `proofs/FertileGrounds/<Package>/<Class>.lean` (for example `Hotel/ChimeRules.lean`), imported by `proofs/FertileGrounds.lean`, so `lake build` builds every file.
- **Sequences for the Chime Hotel and dawn checks.** Ring counting runs `ChimeRules.next` over a list of crop counts with `List.foldl`; the dawn property works on a sorted list of check times with gaps of at most `window`.
- **Local only.** The user doesn't want Lean in the repo, so `proofs/` is git-ignored and nothing in Gradle, CI, or `AGENTS.md` refers to it. The OpenSpec change is the only committed record of what was proved.
- **`proofs/check_proofs.py`** runs `lake build` in `proofs/` (after `lake exe cache get` when `.lake` is missing), then greps the `.lean` files for `sorry` and `axiom`, and exits 1 on any. It stays out of Gradle so the mod builds without Lean.
- **No porting difference.** The rule classes are identical on every branch, so the proofs cover all four.

## Look and sound

None: the change adds no player-facing moment.

## Risks / Trade-offs

- [The Lean model drifts from the Java, and since it isn't committed, other machines don't have it] → the proofs are a one-time check before the manual test; rerunning them after later rule changes means rebuilding the models from this plan.
- [Mathlib's download is large, about 5 GB] → it sits in git-ignored `proofs/`, and only people running proofs fetch it.
- [A Mathlib update renames lemmas] → the pin keeps builds stable; bumping it is a separate change.
