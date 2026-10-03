# Tasks

## 1. JUnit asset checks

- [ ] 1.1 Add `AssetConsistencyTest` for lang keys (registry names and keys used in Java); verify it fails after deleting one lang entry, then passes after restoring it
- [ ] 1.2 Add item model, block state, and texture reference checks; verify it fails after renaming one `items/*.json`, then passes after restoring it
- [ ] 1.3 Add recipe and advancement shape checks; verify it fails on a temporary recipe using `{"item": ...}`, then passes after removing it

## 2. Smoke test suites

- [ ] 2.1 Restructure the smoke test into suites with a `--suite` flag, moving the current fairy checks into a `fairy_visit` suite labeled with 88d5657; verify the full run and `--suite fairy_visit` both pass
- [ ] 2.2 Look up parse-error log strings in the 26.3 sources and add them to the error markers; verify the run fails with a deliberately broken recipe in a temporary datapack
- [ ] 2.3 Add the `soil_regressions` suite (trample, dry-out, Enriched Sand in `#minecraft:sand`) labeled with 83fd1fb and 1e5d483; verify it passes, and fails when the trample target is changed to vanilla dirt

## 3. Client log check

- [ ] 3.1 Add `scripts/client_log_check.py`; verify it passes on `main`, and fails after temporarily pointing a model at a missing texture
- [ ] 3.2 Update the `AGENTS.md` Commands and Verification sections with the new script and `--suite`; verify `AGENTS.md` stays under 150 lines
