# Tasks

All work happens on the local `1.21.11` branch in the worktree `../fertile-grounds-1.21.11`. Nothing gets pushed or published until group 5.

## 1. Branch and build

- [x] 1.1 Replace the `1.21.11` tree with `origin/26.1`. Verify: `git status` shows only the tree swap on top of `origin/1.21.11`.
- [x] 1.2 Set the pins from the proposal, Java 21, `data_pack_format` 94 (1.21.11's `version.json`), and the metadata ranges (`~1.21.11`, Forge `[61,)`). Verify: `LoaderMetadataTest` passes.
- [x] 1.3 Move Fabric to Loom with Mojang mappings and `common` to NeoForm 1.21.11, as on 1.21.1. Verify: `:fabric:genSources` runs.

## 2. Code and data

- [x] 2.1 Fix `common` against the 1.21.11 sources (renames, crop mixins, trader offers, data formats from the design). Verify: `:common:compileJava`, `compileClientJava`, `compileGametestJava`, and `:common:test` pass.
- [x] 2.2 Fix each loader (Fabric API 0.141 names, trade hooks, Forge saved data, Fabric growth mixin, Forge LWJGL repository). Verify: every loader's `compileJava` and `compileGametestJava` pass.

## 3. Verification

- [x] 3.1 `python3 scripts/run_gametests.py --loader all` passes with the same test count per loader as 26.1 (Forge without Jade), twice in a row. Verify: both runs green.
- [x] 3.2 `./gradlew build` passes. Verify: green.
- [x] 3.3 `client_log_check.py` passes for Fabric, NeoForge, and Forge. Verify: no mod warnings.
- [x] 3.4 Compare each jar's file list with its 26.1 counterpart. Verify: only the data changes from the design differ.

## 4. Docs and the gate

- [x] 4.1 Add the new rows to the porting table (on `1.21.11` and `main`) and update the `1.21.11` README to say it carries 2.0.0 only. Verify: each row names code checked in this port.
- [x] 4.2 Commit the port on `1.21.11` as `feat: Backport 2.0.0 to Minecraft 1.21.11`. Verify: `git log` shows it on top of `origin/1.21.11`.
- [x] 4.3 Give the maintainer an in-game checklist for 1.21.11 on all three loaders. Verify: the maintainer marks every item. (Skipped by the maintainer's decision; 1.21.11 shipped without the in-game playtest.)

## 5. Release (only after the gate passes)

- [x] 5.1 Update the README, `docs/DETAILS.md`, `docs/ROADMAP.md`, `openspec/config.yaml`, and the porting and release skills on `main` to list 1.21.11, and the `1.21.10` README. Verify: the `release-2-0` scenarios read true.
- [x] 5.2 The maintainer pushes; Claude publishes the jars to Modrinth and CurseForge with the store skills, leaving the 1.2.0 files. Verify: the store skills' read-back steps.
