# Tasks

All work happens on the local `1.21.10` branch in the worktree `../fertile-grounds-1.21.10`. Nothing gets pushed or published until group 5.

## 1. Branch and build

- [ ] 1.1 Cherry-pick `10928c9`, `a0c05af`, and `6590736` from `1.21.11`, resolving conflicts toward the 2.0 tree. Verify: `git status` is clean and the tree matches `1.21.11` apart from the conflict files.
- [ ] 1.2 Set the pins from the proposal, `data_pack_format` 88, and the metadata ranges (`~1.21.10`, Forge `[60,)`). Verify: `LoaderMetadataTest` passes.
- [ ] 1.3 Point `common` at NeoForm 1.21.10 and Fabric at Loom 1.21.10. Verify: `:fabric:genSources` runs.

## 2. Code and data

- [ ] 2.1 Fix `common` against the 1.21.10 sources (`ResourceLocation`, `GameRules.Key` gamerules, Jade 20, anything the compiler finds). Verify: `:common:compileJava`, `compileClientJava`, `compileGametestJava`, and `:common:test` pass.
- [ ] 2.2 Fix each loader (Fabric API 0.138 names, gamerule registration, Forge 60). Verify: every loader's `compileJava` and `compileGametestJava` pass.

## 3. Verification

- [ ] 3.1 `python3 scripts/run_gametests.py --loader all` passes with the same test count per loader as 1.21.11, twice in a row. Verify: both runs green.
- [ ] 3.2 `./gradlew build` passes. Verify: green.
- [ ] 3.3 `client_log_check.py` passes for Fabric, NeoForge, and Forge. Verify: no mod warnings.
- [ ] 3.4 Compare each jar's file list with its 1.21.11 counterpart. Verify: only the differences from the design remain.

## 4. Docs and the gate

- [ ] 4.1 Add the new rows to the porting table (on `1.21.10` and `main`) and update the `1.21.10` README to say it carries 2.0.0 only. Verify: each row names code checked in this port.
- [ ] 4.2 Commit the port on `1.21.10` as `feat: Backport 2.0.0 to Minecraft 1.21.10`. Verify: `git log` shows it on top of `origin/1.21.10`.
- [ ] 4.3 Give the maintainer an in-game checklist for 1.21.10 on all three loaders. Verify: the maintainer marks every item or skips it.

## 5. Release (only after the gate passes)

- [ ] 5.1 Update the README, `docs/DETAILS.md`, `docs/ROADMAP.md`, `openspec/config.yaml`, `SECURITY.md`, and the porting and release skills on `main` and every 2.0 branch to list 1.21.10. Verify: the `release-2-0` scenarios read true.
- [ ] 5.2 The maintainer pushes; Claude publishes the jars to Modrinth and CurseForge with the store skills, leaving the 1.2.0 files. Verify: the store skills' read-back steps.
