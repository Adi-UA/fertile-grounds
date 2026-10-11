# Contributing

Thanks for helping with Fertile Grounds. Bug reports and ideas go in [issues](https://github.com/Adi-UA/fertile-grounds/issues/new/choose); questions go in [Discussions](https://github.com/Adi-UA/fertile-grounds/discussions).

## Pull requests

1. Open an issue first for anything bigger than a small fix, so we can agree on the approach.
2. Each Minecraft version has its own branch (listed in `README.md`). Target the newest branch, `main`, unless the fix only applies to an older version.
3. Run `./gradlew spotlessApply` and `./gradlew build` before pushing. For gameplay or data changes, also run `python3 scripts/server_smoke_test.py`.
4. Use [Conventional Commits](https://www.conventionalcommits.org/) (`fix: stop fairy despawning in rain`).
5. Don't edit textures in `art_source/` or `src/main/resources/assets/`; suggest art changes in an issue instead.

`AGENTS.md` explains the code layout and how to verify changes.

By contributing, you agree your work is released under the project's MIT license.
