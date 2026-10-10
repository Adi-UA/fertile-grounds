# Design

## Context

The rule classes `FairyTiming`, `ChimeRules`, and `FairyHotelRules` hold no Minecraft types and have JUnit tests in `src/test`. Nothing in the repo uses Lean yet, and neither Lean nor elan is installed.

## Goals / Non-Goals

**Goals:** the fewest theorems that cover the rules most likely to hide a bug players would notice, each stated in plain terms the user can check.

**Non-Goals:** extracting Lean from Java or Java from Lean; proofs about Minecraft; making `./gradlew build` need Lean.

## Decisions

- **Install with Homebrew's `elan-init`**, the official Lean version manager; mise has no Lean plugin. `proofs/lean-toolchain` pins the latest stable Lean release.
- **No Mathlib.** Every model uses whole numbers, so Lean's built-in `omega`, `decide`, and `simp` suffice, and the project skips Mathlib's 5 GB download. The visit chance works in hundredths of a percent: Java computes `base * (1 + flowers * fp / 100 + hp / 100)`, and multiplying by 100 gives `base * (100 + flowers * fp + hp)`, a whole number. Floating-point rounding in Java's `double` stays outside the proofs.
- **Mirror, don't translate.** Each Lean definition names the Java method it mirrors and keeps its argument order and constants. A few JUnit cases appear as `example`s, so a changed constant breaks the build.
- **Committed, with the build folder ignored.** `proofs/` is in git; `proofs/.lake/` is in `.gitignore`.
- **Layout.** `proofs/FertileGrounds/{VisitChance,ChimeRules,ClusterLeader,Dawn}.lean`, imported by `proofs/FertileGrounds.lean`.
- **Assumption for cluster leaders:** distinct hotels have distinct packed positions (`BlockPos.asLong` is one-to-one for in-world coordinates), and "in range" is symmetric (the POI lookup uses straight-line distance).
- **No porting difference.** The rule classes are identical on every branch.

## Look and sound

None: the change adds no player-facing moment.

## Risks / Trade-offs

- [The Lean model drifts from the Java] → each file names its Java method, and the mirrored examples catch changed constants.
- [Theorem statements that prove the wrong thing] → the user approves each statement before the proofs are written.
