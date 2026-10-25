---
name: updating-modrinth
description: Publish a Fertile Grounds release to Modrinth (https://modrinth.com/mod/fertile-grounds) with Claude in Chrome, from jars built at each branch's release commit. Use when asked to update Modrinth, upload new versions or jars to Modrinth, or sync the Modrinth page with GitHub.
---

# Updating Modrinth

Publish a release on the Modrinth project: one Modrinth version per jar (Fabric, NeoForge and Forge each get their own), plus the summary, tags and description. The user is signed in to Modrinth in Chrome; Claude drives the browser and checks every change through the public API.

## Guardrails (read first)

- **Never delete, hide or edit an existing version.** Old versions stay exactly as they are. Only create new ones. The one exception: once a Beta NeoForge or Forge file's loader gets a final build and its GameTests pass on that build (`ORG_GRADLE_PROJECT_neoforge_version=<build> python3 scripts/run_gametests.py --loader neoforge`, or `forge_version`), change only its type to Release (Edit → Edit details) and its changelog's "Tested on" sentence; the file stays.
- **Only touch** the summary, tags, description and new versions. Never change visibility, license, members, links, monetization, the icon, the gallery or anything in the danger zone without asking.
- **Upload only jars built at each branch's release commit** (step 1), in a clean worktree so nothing local slips in. GitHub releases hold notes that link to Modrinth and CurseForge, never jars.
- **Verify every save through the public API** (step 5) before moving on. A screenshot alone isn't proof; Save buttons sometimes miss the first click.
- **Stop and ask the user** if Modrinth shows you logged out, asks for a password or 2FA, shows a confirm dialog you didn't expect, or a detected Minecraft version looks wrong. Never type credentials.
- Don't trigger browser `confirm` dialogs. A "Leave site?" block on navigation means unsaved changes: save first.

## 1. Gather inputs

```bash
D=build/modrinth-upload && rm -rf $D && mkdir -p $D
for b in 26.1 26.2 26.3 1.21.11 1.21.10 1.21.1; do   # each branch being released, checked out at its release commit
  V=$(git show "${b}:gradle.properties" | rg -o '^mod_version=(.*)' -r '$1')   # each branch has its own mod version
  git worktree add -q ../fg-$b $b && (cd ../fg-$b && ./gradlew build -q) \
    && cp ../fg-$b/{fabric,neoforge,forge}/build/libs/fertilegrounds*-$V+$b.jar $D/ && git worktree remove ../fg-$b; done
for b in 26.1 26.2 26.3 1.21.11 1.21.10 1.21.1; do   # versions before 26.1 get 2.0.x only
  echo "$b $(git show "${b}:gradle.properties" | rg -o 'fabric_api_version=.*')"; done
```

The jars are `fertilegrounds-<mod>+<mc>.jar` (Fabric), `fertilegrounds-neoforge-<mod>+<mc>.jar` and `fertilegrounds-forge-<mod>+<mc>.jar`. Each Fabric version pins Fabric API (project ID `P7dR8mSH`) to the exact build that branch compiles against; NeoForge and Forge versions have no required dependency. Confirm Modrinth has each pin:

```bash
curl -s 'https://api.modrinth.com/v2/project/P7dR8mSH/version?loaders=%5B%22fabric%22%5D' \
  | python3 -c "import json,sys; [print(v['version_number'], v['id']) for v in json.load(sys.stdin)]" | rg '<pin>'
```

Read the current project state so you know what's already there:

```bash
curl -s https://api.modrinth.com/v2/project/fertile-grounds | python3 -m json.tool | head -40
curl -s https://api.modrinth.com/v2/project/fertile-grounds/version \
  | python3 -c "import json,sys; [print(v['version_number'], v['game_versions']) for v in json.load(sys.stdin)]"
```

Skip any jar whose `version_number` already exists. A NeoForge or Forge file goes out as **Beta** while its loader has no final build for that Minecraft version (NeoForge: no stable `26.x.y.z` without `-beta` on `https://maven.neoforged.net/releases/net/neoforged/neoforge/maven-metadata.xml`; Forge: no recommended build in `https://files.minecraftforge.net/net/minecraftforge/forge/promotions_slim.json`). Check both and note the loader version each file was tested on (`git show <branch>:gradle.properties | rg 'neoforge_version|forge_version'`).

| Jar | Loader | Version number | Dependency | Type |
|---|---|---|---|---|
| `fertilegrounds-` | Fabric | `<mod>+<mc>` | Fabric API, Required, pinned | Release |
| `fertilegrounds-neoforge-` | NeoForge | `<mod>+<mc>-neoforge` | none | Release, or Beta without a final loader build |
| `fertilegrounds-forge-` | Forge | `<mod>+<mc>-forge` | none | Release, or Beta without a final loader build |

## 2. Summary and tags

- **Summary:** `https://modrinth.com/mod/fertile-grounds/settings` → click the Summary field, `cmd+a`, type the new text, click **Save** in the bottom bar.
- **Tags:** `.../settings/tags` → tick categories; Featured tags allow at most 3. Click **Save**. Current tags: featured Food, Magic, Utility; extra Mobs.

## 3. Description

`.../settings/description` holds a CodeMirror editor, not a textarea, so typing is slow and setting `.value` does nothing. A synthetic paste after `cmd+a` can replace only part of the text and save both copies, so change the document through CodeMirror itself:

1. Read the current text from the API (`(await fetch('https://api.modrinth.com/v2/project/fertile-grounds?t=' + Date.now()).then(r => r.json())).body`), not the editor: CodeMirror only renders the lines on screen. Make edits as exact string replacements on that text, each matching once.
2. Run in `javascript_tool` and check it prints `[true, <length>]`:

```js
const body = `...full markdown...`;
let tile = document.querySelector('.cm-content').cmTile, view = tile.view;
while (!view && tile.parent) { tile = tile.parent; view = tile.view; }
view.dispatch({changes: {from: 0, to: view.state.doc.length, insert: body}});
[view.state.doc.toString() === body, view.state.doc.length]
```

3. Click **Save**, then check the API `body` equals the new text exactly.

Keep the description consistent with the GitHub README: the hero image, one section per feature with its gif, the texture table, the loader list (Fabric needs Fabric API; NeoForge and Forge need nothing else), the README and guide links, and the Art section copied word for word. Embed media by raw URL (`https://raw.githubusercontent.com/Adi-UA/fertile-grounds/refs/heads/main/docs/media/<file>`), since gallery uploads cap file size.

## 4. Create one version per jar

From `https://modrinth.com/mod/fertile-grounds/versions`, upload the jars oldest Minecraft version first (26.1, then 26.2, then 26.3; within a version: Fabric, NeoForge, Forge), so the newest lands on top of the version list. For each jar:

1. Click **Create version** (right after a page load the first click often does nothing; check `document.querySelectorAll('input[type=file]').length` and click again). Use `find` for "file input in upload dialog", then `file_upload` the jar. Don't click the file input; that opens a native picker.
2. **Environment step** (shown on the first upload of a session): pick **Client and server**, then **Required on both**, which the older versions store as `client_and_server`.
3. **Metadata step:** the loader must match the jar (table in step 1; Modrinth reads it from `fabric.mod.json`, `neoforge.mods.toml` or `mods.toml`), and detected versions must match the jar's Minecraft version. Modrinth reads the jar's `minecraft` range, which can detect more versions than the jar was built for. Click **Edit** beside Detected versions, **Clear all**, and tick only the jar's own version, matching the older uploads. Keep extra patch versions only when the jar was verified on them (the 26.1 Fabric jar covers 26.1, 26.1.1 and 26.1.2, while the 26.1 NeoForge and Forge jars list only 26.1.2, the only version their loaders have a stable build for). The editor reflows after each click, so take a screenshot before the next one. Environment: client and server.
4. **Dependencies:** NeoForge and Forge jars get none; remove any suggested row. For a Fabric jar, ignore the "Suggested" Fabric API row, because it pins a version for the wrong Minecraft release. Click **Add dependency** → paste `P7dR8mSH` → pick Fabric API → type the branch's pin in Version and pick it → relation **Required** → **Add dependency**.
5. **Add details:** pick the type from the table in step 1. Version number and subtitle autofill as `<mod>+<mc>` and `Fertile Grounds <mod>+<mc>`; keep them for Fabric, and add `-neoforge` or `-forge` to both for the other loaders. Type a one or two sentence changelog in the editor; a NeoForge or Forge changelog names the loader version it was tested on.
6. Click **Create version**, then verify it (step 5) before starting the next jar.

The dialog layout stays the same between jars, so after the first one you can batch the clicks with `browser_batch`. Take a screenshot before each Create click.

## 5. Verify

The API can lag a few seconds, so add a cache-busting query:

```bash
curl -s "https://api.modrinth.com/v3/project/fertile-grounds/version?t=$(date +%s)" | python3 -c "
import json,sys
for v in json.load(sys.stdin): print(v['version_number'], v['loaders'], v['game_versions'], v['version_type'], v['environment'],
  [f['filename'] for f in v['files']], [d['version_id'] for d in v['dependencies']], bool(v['changelog']))"
```

Check each new row against the table in step 1: the right jar, loader, Minecraft version, type, `client_and_server`, a changelog, and for Fabric exactly one Fabric API `version_id` matching its pin (none for NeoForge and Forge). Also check the project's `description`, `categories`, `additional_categories` and `body` the same way.

## 6. Report

Tell the user what changed, link `https://modrinth.com/mod/fertile-grounds`, and list anything skipped or that needs their review. Close the tab and the Claude tab group, then mirror the release to CurseForge with `.claude/skills/updating-curseforge/SKILL.md`.
