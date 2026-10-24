# Contributing to Fertile Grounds

Thanks for helping! Bug reports and ideas go in [issues](https://github.com/Adi-UA/fertile-grounds/issues/new/choose), and questions go in [Discussions](https://github.com/Adi-UA/fertile-grounds/discussions). This page covers translations, then how to work on the code: setting up, how features get planned, and how testing works.

## Translations

All of the mod's text lives in `common/src/main/resources/assets/fertilegrounds/lang/`. `en_us.json` is the English source, and each other language is a file named after Minecraft's language code, such as `de_de.json` or `pt_br.json` (the [Minecraft Wiki](https://minecraft.wiki/w/Language) lists every code).

To add or update a language:

1. Copy `en_us.json` to `<code>.json`, or open the existing file.
2. Translate the values and keep the keys as they are. Keep each `%s`, which the game fills in with a number or name, and each `%%`, which shows as a percent sign.
3. In the guide book pages (`book.fertilegrounds.guide.page.*`), `\n` starts a new line. A book page fits about 14 short lines, so keep each page close to the English length.
4. Leave out any key you haven't translated. The game shows English for it, never a raw key.
5. Run `./gradlew :common:test`. It fails if your file has a key that `en_us.json` doesn't, which usually means a typo or a key the mod no longer uses.
6. Open a pull request.

Translations come from people who speak the language. Please don't submit machine translations.

## Setting up

You need Java 25 and Git. Gradle downloads everything else, including Minecraft.

```bash
git clone https://github.com/Adi-UA/fertile-grounds.git
cd fertile-grounds
./gradlew :fabric:runClient          # opens Minecraft on Fabric with the mod (and Jade) loaded
./gradlew :neoforge:runClient        # the same on NeoForge; :forge:runClient for Forge
./gradlew :fabric:runClient -PnoJade # without the optional Jade mod
./gradlew :fabric:runClient -Pshaders  # adds Sodium and Iris; put a shader pack zip in run/shaderpacks/
./gradlew build                      # compiles, checks formatting, runs the unit tests and Fabric GameTests
./gradlew spotlessApply              # formats the code the way the build expects
./gradlew :fabric:genSources         # decompiles Minecraft so you can read its code
```

The game code lives in `common/`, and `fabric/`, `neoforge/`, and `forge/` each build that loader's jar from it. The `-P` flags work on any Gradle task, so `./gradlew :neoforge:runGameTestServer -PnoJade` runs the in-world tests on NeoForge without Jade.

[AGENTS.md](AGENTS.md) maps out the code: which folder holds what, and every command. It's written for AI coding agents, but it's the quickest tour for people too.

**Branches.** Each Minecraft version has its own branch. `main` is the newest (26.3), and `26.2` and `26.1` get each change after it works on `main`. Target `main` with your pull request unless a fix only applies to an older version. The `1.21.11` and `1.21.1` branches carry 2.0 and get no later features; `1.21.10` gets 2.0.0 later, and `1.20.1` stays on 1.2.0.

## How features get planned

Every feature starts as a written plan in [`openspec/`](openspec/), using [OpenSpec](https://github.com/Fission-AI/OpenSpec). A plan says what changes and why, what players will see (as WHEN/THEN scenarios), how it's built, and a task list where every task names how it's checked.

| Folder | What's in it |
|---|---|
| `openspec/specs/` | How the mod behaves today, one folder per feature |
| `openspec/changes/<name>/` | A planned change: `proposal.md`, `design.md`, `tasks.md`, and spec changes |
| `openspec/changes/archive/` | Finished changes, kept for history |

The flow is propose, build, archive:

1. **Propose.** Write the change's plan. With [Claude Code](https://claude.com/claude-code), run `/opsx:propose <what you want>` and it drafts all four files. By hand, install the CLI (`npm install -g @fission-ai/openspec`) and run `openspec new change <name>`.
2. **Build.** Work through `tasks.md` in order, checking each task off when its check passes. In Claude Code, `/opsx:apply <name>` does this, and `/opsx:update <name>` revises the plan when the code teaches you something.
3. **Archive.** When every task is done, `/opsx:archive <name>` (or `openspec archive <name>`) merges the change's specs into `openspec/specs/`.

`openspec validate <name> --strict` checks a plan's format, and `openspec list` shows what's in progress. [docs/ROADMAP.md](docs/ROADMAP.md) lists what 2.0 has and what comes next, so check it before proposing something new.

Small fixes don't need a plan. Open an issue first for anything bigger, so we can agree on the approach.

## How testing works

A green build only proves the code compiles, so most features have tests that run the game.

| What | Command | What it checks |
|---|---|---|
| Unit tests | `./gradlew :common:test` | Rules that don't need Minecraft (fairy timing, chances, grove layout), plus every lang key, model, texture, recipe, and doc table matching up |
| GameTests | `python3 scripts/run_gametests.py --loader all` | Starts each loader's test server and runs about 100 in-world tests, with fake players that use items, get messages, and earn advancements |
| One loader | `python3 scripts/run_gametests.py --loader fabric` | The same tests on one loader; every run also fails if a data file logs a load error, and `--filter 'fairy_grove*'` runs a few |
| Without Jade | `./gradlew :fabric:runGameTest -PnoJade` | The mod works when the optional Jade mod is missing |
| Client check | `python3 scripts/client_log_check.py --loader forge` | Opens the real game on one loader and fails on missing models or textures |

GameTests live in `common/src/gametest/`, one class per feature, and each new class goes in `TestCatalog`. Tests that change the time or gamerules get their own test environment, since tests sharing one run at the same time. Every bug fix adds a test that fails without the fix.

Some things a test server can't judge: how a block looks, how a sound feels, or sleeping through the night. Check those yourself in each loader's `runClient`, and say what you checked in your pull request.

**The demo world.** `python3 scripts/demo/build_demo_world.py` builds a world with every feature laid out along a path, and copies it to your saves folder. Signs at spawn switch on fast-testing mode (quick crop growth, fairies every night) and reset any station, so it's the fastest way to try a change by hand. `./gradlew :fabric:runClient -Pshaders` adds Sodium and Iris for recording; put a shader pack in `run/shaderpacks/`.

## Pull requests

1. Run `./gradlew spotlessApply` to format the code, then `./gradlew build`.
2. Use [Conventional Commits](https://www.conventionalcommits.org/), like `fix: Let the Fairy Grove clear grass`.
3. Keep each class to one job, and write comments that explain Minecraft quirks to someone new to modding. AGENTS.md has the rest of the house style.
4. Every player-facing line goes through a translation key in `en_us.json`, written in plain, friendly English. If you change a guide book page, update [docs/GUIDE.md](docs/GUIDE.md) too; the unit tests check that they match.
5. Don't edit textures. Every texture is drawn by hand, so suggest art changes in an issue instead.

By contributing, you agree that your work is released under the project's [MIT license](LICENSE).
