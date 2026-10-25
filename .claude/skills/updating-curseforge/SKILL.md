---
name: updating-curseforge
description: Publish a Fertile Grounds release to CurseForge (author console https://authors.curseforge.com/#/projects/1627313) with Claude in Chrome, mirroring the Modrinth page. Use when asked to update CurseForge, upload jars or files to CurseForge, or sync CurseForge with GitHub or Modrinth.
---

# Updating CurseForge

Publish a release on the CurseForge project: one file per jar (Fabric, NeoForge and Forge), plus the summary, categories and description. Do Modrinth first (`.claude/skills/updating-modrinth/SKILL.md`), since CurseForge copies its summary and description. The user is signed in to CurseForge in Chrome; Claude drives the browser.

## Guardrails (read first)

- **Never archive, delete or edit an existing file.** Old files stay exactly as they are. Only add new ones. The one exception is promoting a Beta file to Release, under the rule in the Modrinth skill's guardrails: click the file's **Edit file** button, change only Release Type and the changelog, and click **Submit**; the file stays approved. CurseForge can't replace a file's jar, so when the maintainer asks to fix a released jar without a version bump, upload the rebuilt jar as a new file with the same display name and tags, then archive the old file.
- **Only touch** the summary, categories, description and new files. Never change the name, slug, logo, license, members, visibility ("Unlisted project"), default relations or donation settings without asking.
- **Upload only the jars the Modrinth skill's step 1 built**, the same files Modrinth got.
- **Verify every change** by reading it back after a reload (step 5). CurseForge has no keyless public API for project details, so the author console is the source of truth.
- **Stop and ask the user** if CurseForge shows you logged out, asks for a password or 2FA, or shows a dialog you didn't expect. Never type credentials.
- **Close the tab and the Claude tab group when done.**

## 1. Gather inputs

Reuse the jars and the Modrinth project as the source of text:

```bash
curl -s https://api.modrinth.com/v2/project/fertile-grounds | python3 -c "import json,sys; d=json.load(sys.stdin); print(d['description']); print(d['body'])"
```

On the Files tab, list what's already there by reading each row's tags (the visible cell truncates them):

```js
[...document.querySelectorAll('tr')].map(r => [...r.querySelectorAll('[title],[aria-label]')]
  .map(e => e.getAttribute('title') || e.getAttribute('aria-label')).join(' | ')).join('\n')
```

Skip any jar whose file name is already listed.

## 2. Summary and categories (General tab)

- **Summary:** click the field, `cmd+a`, type the Modrinth summary (limit 256 characters).
- **Categories:** main category stays Farming. In Additional categories, type a name to filter and tick it; current set is Automation, Utility & QoL, Magic, Mobs.
- Click **Save** in the bottom bar and wait for "Changes saved successfully".

## 3. Description (Description tab)

The editor is a Markdown `textarea`. Replace its text with React's native setter so the form notices the change:

```js
const body = `...Modrinth body...`;
const ta = [...document.querySelectorAll('textarea')].find(t => t.value.length > 200);
Object.getOwnPropertyDescriptor(HTMLTextAreaElement.prototype, 'value').set.call(ta, body);
ta.dispatchEvent(new Event('input', {bubbles: true}));
```

CurseForge keeps its own formatting (its own bullets, and Jade and Fabric API linked to CurseForge pages), so make the same edits as on Modrinth as exact string replacements on the textarea's current text, each matching once. Click **Save**. A reload right after saving can still show the old text; wait a few seconds and reload again before deciding the save failed, and keep the new text in a file, since a reload clears page variables.

## 4. Add one file per jar (Files tab)

Upload the jars oldest Minecraft version first (26.1, then 26.2, then 26.3; within a version: Fabric, NeoForge, Forge), so the newest lands on top of the file list. Use the loader and release type from the table in the Modrinth skill's step 1.

1. Click **Add File**. Use `find` for "file input for jar upload", then `file_upload` the jar. Don't click Choose File; that opens a native picker.
2. **Tags:** the form copies Environment, Modloader, Java and Minecraft from an earlier upload, and not always the one you expect. Wait 2 seconds, then read the real values:
   ```js
   const t = document.querySelector('main').innerText; t.slice(t.indexOf('Environment'), t.indexOf('Release Type'))
   ```
   Fix them to match the older file for the same Minecraft version:

   | Jar | Minecraft | Java |
   |---|---|---|
   | 26.x | its own version only (pick `26.3`, never `26.3-snapshot`); a Fabric jar ticks the same patch releases as on Modrinth (26.1: 26.1, 26.1.1, 26.1.2), NeoForge and Forge only 26.1.2 | Java 25 |
   | 1.21.x (2.0.x) | its own version only | Java 21, 22, 25 |
   | 1.21.x, 1.20.1 (1.2.0) | its own version only | Java 17, 18, 19, 20, 21, 22, 25 |

   Environment is always Client and Server; Modloader is the jar's loader (Fabric, NeoForge or Forge, only one). Remove a chip with its x (chips shift left, so screenshot before each click). Add a version by clicking the Minecraft field, typing it, and ticking the checkbox.
3. **Changelog:** the editor switches between Markdown and WYSIWYG on its own; plain text works in both. Scroll it into view, click inside it, type one or two sentences, and check its word count is no longer 0 (a click on the toolbar types nowhere).
4. **Dependencies and type:** for a Fabric jar, leave "Include default project relations" on; it adds Fabric API as a Required Dependency, and CurseForge doesn't pin versions. For a NeoForge or Forge jar, turn it off, then click **Remove relation** in the Fabric API row, which stays listed after the toggle, until it reads "Project removed from relations"; those jars need no Fabric API. Release Type is Release, or Beta for a NeoForge or Forge file whose loader has no final build for that Minecraft version.
5. Click **Add File** (use `find` for it; a coordinate click sometimes misses). If the form stays open, check the Files tab in a second tab before clicking again, so a retry can't upload twice. The new row shows Uploading, then Processing, then Under Review. CurseForge moderators approve it, which can take hours, so "Under Review" counts as done.

## 5. Verify

Reload the page (`location.reload()`), then re-read the General summary and categories, the Description `textarea`, and the Files rows with the snippet from step 1. Each new row needs the right file name, release type, Minecraft version, Java tags, Client, Server and its one loader, and only Fabric files list Fabric API as a relation.

## 6. Report

Tell the user what changed, link `https://www.curseforge.com/minecraft/mc-mods/fertile-grounds`, list files still under review, and close the tab.
