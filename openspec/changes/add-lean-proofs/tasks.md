# Tasks

## 1. Setup

- [ ] 1.1 Show the user every definition and theorem statement and get approval before writing proofs
- [ ] 1.2 Install `elan-init` with Homebrew, create the `proofs/` Lake project with no dependencies, and add `proofs/.lake/` to `.gitignore`; verify `lake build` succeeds on an empty project
- [ ] 1.3 Add `proofs/check_proofs.py`; verify it exits 0 on the empty project and 1 after adding a file with `sorry`, then remove that file

## 2. Proofs

- [ ] 2.1 `VisitChance.lean`: prove the Visit chance requirement; verify `check_proofs.py`
- [ ] 2.2 `ChimeRules.lean`: prove the Chime Hotel rings requirement; verify `check_proofs.py`
- [ ] 2.3 `ClusterLeader.lean`: prove the Hotel cluster leaders requirement; verify `check_proofs.py`
- [ ] 2.4 `Dawn.lean`: prove the Dawn check requirement; verify `check_proofs.py`

## 3. Finish

- [ ] 3.1 Mutation check: change the Chime minimum from 4 to 5 in the model and confirm `check_proofs.py` fails, then revert
- [ ] 3.2 Add the `proofs/` row and `check_proofs.py` command to `AGENTS.md`; verify the paths exist
