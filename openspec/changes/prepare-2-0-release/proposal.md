# Proposal

## Why

Changes 1 to 16 work on 26.3 and passed the user's playtest; `add-blight` and `add-fairy-grove` land before this change starts. Before release, three things remain: narrowing the supported versions to the ones that are cheap to keep in step, giving players and listing pages a clear message about that, and a demo world the user can load to record screenshots and video of the core features.

The user chose 26.x only after comparing the numbers (2026-10-10). Fabric API downloads from the last 90 days put 26.x at about 57% of Fabric players and 1.21.11 at 21%. 1.21.11 is the last version before Minecraft dropped code-name scrambling, so a 1.21.11 port needs a different build setup and four features rewritten. The user accepts that 1.21.11 players stay on 1.2.0.

## What Changes

- **2.0.0 supports Minecraft 26.1, 26.2, and 26.3 only.** The 1.21.11, 1.21.10, 1.21.1, and 1.20.1 branches stay on 1.2.0 and get a README note saying so. Their branches stay in git, frozen.
- **Docs say the same everywhere:** README, `docs/DETAILS.md`, `docs/ROADMAP.md`, `openspec/config.yaml`, the porting skill, and the two release skills.
- **Demo world:** before publishing, Claude builds a save with a laid-out showcase of the core features (soil rows, a fairy farm, a hotel garden, a Chime Hotel harvest alarm, a blighted plot, a Fairy Grove shrine ready to wake, the guide book, supply chests). Claude copies it into the macOS Minecraft saves folder.
- **Shaders in the dev client:** Sodium and Iris as dev-only runtime dependencies behind a Gradle flag, plus a shader pack the user downloads into `run/shaderpacks/`, so the user can record in `runClient`. If that fails, the user records in their real client with the exported jar, using the same save.
- **Listing pages:** after the jars are built, the Modrinth and CurseForge pages get the 2.0 description and say clearly that versions before 26.1 stay on 1.2.0.

Out of scope: the 1.21.11 port, renaming the mod (the user kept "Fertile Grounds"), recording the demo media (the user records), and committing the demo world or any shader pack.

New art: none in this change. The user asked (2026-10-10) to build everything first, so the placeholder textures (Fertilizer, Empty and Occupied Fairy Hotel, Jarred Fairy, Chime Hotel, each with glow layers, and Blighted Dirt) get replaced after the backports and before publishing; one art commit on `main` is then cherry-picked to every 26.x branch.

## Capabilities

### New Capabilities

- `release-2-0`: Which Minecraft versions 2.0.0 supports, what players see about dropped versions, and the demo world for showing the mod.

### Modified Capabilities

None.

## Impact

Docs on `main`, `26.2`, `26.1`, and a README note on the four frozen branches; `build.gradle` and `gradle.properties` (dev-only Sodium and Iris); a demo-world builder under `scripts/demo/`; the release skills; the Modrinth and CurseForge pages. No gameplay changes.
