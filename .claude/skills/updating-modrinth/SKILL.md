---
name: updating-modrinth
description: Publish a Fertile Grounds release to Modrinth (https://modrinth.com/mod/fertile-grounds) with Claude in Chrome, mirroring the GitHub release. Use when asked to update Modrinth, upload new versions or jars to Modrinth, or sync the Modrinth page with GitHub.
---

# Updating Modrinth

Mirror a GitHub release onto the Modrinth project: one Modrinth version per jar, plus the summary, tags and description. The user is signed in to Modrinth in Chrome; Claude drives the browser and checks every change through the public API.

## Guardrails (read first)

- **Never delete, hide or edit an existing version.** Old versions stay exactly as they are. Only create new ones.
- **Only touch** the summary, tags, description and new versions. Never change visibility, license, members, links, monetization, the icon, the gallery or anything in the danger zone without asking.
- **Upload only jars from the GitHub release** (`gh release download`), never a local `build/libs` jar that could differ.
- **Verify every save through the public API** (step 5) before moving on. A screenshot alone isn't proof; Save buttons sometimes miss the first click.
- **Stop and ask the user** if Modrinth shows you logged out, asks for a password or 2FA, shows a confirm dialog you didn't expect, or a detected Minecraft version looks wrong. Never type credentials.
- Don't trigger browser `confirm` dialogs. A "Leave site?" block on navigation means unsaved changes: save first.

## 1. Gather inputs

```bash
V=1.2.0   # mod version being published
D=build/modrinth-upload && rm -rf $D && mkdir -p $D
gh release download v$V -R Adi-UA/fertile-grounds -D $D -p '*.jar'
for b in 26.3 26.2 26.1 1.21.11 1.21.10 1.21.1 1.20.1; do
  echo "$b $(git show "${b}:gradle.properties" | rg -o 'fabric_api_version=.*')"; done
```

Each version pins Fabric API (project ID `P7dR8mSH`) to the exact build that branch compiles against. Confirm Modrinth has each pin:

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

Skip any jar whose `version_number` already exists.

## 2. Summary and tags

- **Summary:** `https://modrinth.com/mod/fertile-grounds/settings` → click the Summary field, `cmd+a`, type the new text, click **Save** in the bottom bar.
- **Tags:** `.../settings/tags` → tick categories; Featured tags allow at most 3. Click **Save**. Current tags: featured Food, Magic, Utility; extra Mobs.

## 3. Description

`.../settings/description` holds a CodeMirror editor, not a textarea, so typing is slow and setting `.value` does nothing. Replace the whole text with a synthetic paste:

1. Read the current text: `document.querySelector('.cm-content').innerText`.
2. Click inside the editor, then press `cmd+a`.
3. Run in `javascript_tool`:

```js
const body = `...full markdown...`;
const el = document.querySelector('.cm-content');
const dt = new DataTransfer(); dt.setData('text/plain', body);
el.dispatchEvent(new ClipboardEvent('paste', {clipboardData: dt, bubbles: true, cancelable: true}));
```

4. Check the editor now contains the new text only once, then click **Save**.

Keep the description consistent with the GitHub README: the growth demo gif, the tier list, the latest feature section with its gif, the Fabric API requirement, the README link, and the Art section copied word for word. Embed gifs by raw URL (`https://raw.githubusercontent.com/Adi-UA/fertile-grounds/refs/heads/main/docs/<file>.gif`), since gallery uploads cap file size.

## 4. Create one version per jar

From `https://modrinth.com/mod/fertile-grounds/versions`, for each jar:

1. Click **Create version**. Use `find` for "file input in upload dialog", then `file_upload` the jar. Don't click the file input; that opens a native picker.
2. **Metadata step:** loader must be Fabric, and detected versions must match the jar's Minecraft version. Modrinth reads the jar's `minecraft` range, so `~1.21.1` detects 1.21.1 through 1.21.11 and `~1.20.1` detects 1.20.1 through 1.20.6. Click **Edit** beside Detected versions, **Clear all**, and tick only the jar's own version, matching the older uploads. Keep extra patch versions only when the jar was verified on them (the 26.1 jar covers 26.1, 26.1.1 and 26.1.2). The editor reflows after each click, so take a screenshot before the next one. Environment: client and server.
3. **Dependencies:** ignore the "Suggested" Fabric API row, because it pins a version for the wrong Minecraft release. Click **Add dependency** → paste `P7dR8mSH` → pick Fabric API → type the branch's pin in Version and pick it → relation **Required** → **Add dependency**.
4. **Add details:** type **Release**. Version number and subtitle autofill as `<mod>+<mc>` and `Fertile Grounds <mod>+<mc>`; keep them. Type a one or two sentence changelog in the editor.
5. Click **Create version**, then verify it (step 5) before starting the next jar.

The dialog layout stays the same between jars, so after the first one you can batch the clicks with `browser_batch`. Take a screenshot before each Create click.

## 5. Verify

The API can lag a few seconds, so add a cache-busting query:

```bash
curl -s "https://api.modrinth.com/v2/project/fertile-grounds/version?t=$(date +%s)" | python3 -c "
import json,sys
for v in json.load(sys.stdin): print(v['version_number'], v['game_versions'], v['version_type'],
  [f['filename'] for f in v['files']], [d['version_id'] for d in v['dependencies']], bool(v['changelog']))"
```

Check each new row: the right jar, Minecraft version, `release`, exactly one Fabric API `version_id` matching its pin, and a changelog. Also check the project's `description`, `categories`, `additional_categories` and `body` the same way.

## 6. Report

Tell the user what changed, link `https://modrinth.com/mod/fertile-grounds`, and list anything skipped or that needs their review. Don't publish anything to CurseForge; that's a separate step for the user.
