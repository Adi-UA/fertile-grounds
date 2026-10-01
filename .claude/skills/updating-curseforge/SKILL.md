---
name: updating-curseforge
description: Publish a Fertile Grounds release to CurseForge (author console https://authors.curseforge.com/#/projects/1627313) with Claude in Chrome, mirroring the GitHub release and Modrinth page. Use when asked to update CurseForge, upload jars or files to CurseForge, or sync CurseForge with GitHub or Modrinth.
---

# Updating CurseForge

Mirror a GitHub release onto the CurseForge project: one file per jar, plus the summary, categories and description. Do Modrinth first (`.claude/skills/updating-modrinth/SKILL.md`), since CurseForge copies its summary and description. The user is signed in to CurseForge in Chrome; Claude drives the browser.

## Guardrails (read first)

- **Never archive, delete or edit an existing file.** Old files stay exactly as they are. Only add new ones.
- **Only touch** the summary, categories, description and new files. Never change the name, slug, logo, license, members, visibility ("Unlisted project"), default relations or donation settings without asking.
- **Upload only jars from the GitHub release** (`gh release download`, see the Modrinth skill step 1), never a local `build/libs` jar.
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
const ta = [...document.querySelectorAll('textarea')].find(t => t.value.includes('Crops Growing Demo'));
Object.getOwnPropertyDescriptor(HTMLTextAreaElement.prototype, 'value').set.call(ta, body);
ta.dispatchEvent(new Event('input', {bubbles: true}));
```

Use the Modrinth body, but point the Fabric API link at `https://www.curseforge.com/minecraft/mc-mods/fabric-api`. Click **Save**.

## 4. Add one file per jar (Files tab)

1. Click **Add File**. Use `find` for "file input for jar upload", then `file_upload` the jar. Don't click Choose File; that opens a native picker.
2. **Tags:** the form copies Environment, Modloader, Java and Minecraft from an earlier upload, and not always the one you expect. Wait 2 seconds, then read the real values:
   ```js
   const t = document.querySelector('main').innerText; t.slice(t.indexOf('Environment'), t.indexOf('Release Type'))
   ```
   Fix them to match the older file for the same Minecraft version:

   | Jar | Minecraft | Java |
   |---|---|---|
   | 26.x | its own version only (pick `26.3`, never `26.3-snapshot`) | Java 25 |
   | 1.21.x, 1.20.1 | its own version only | Java 17, 18, 19, 20, 21, 22, 25 |

   Environment is always Client and Server; Modloader is Fabric. Remove a chip with its x (chips shift left, so screenshot before each click). Add a version by clicking the Minecraft field, typing it, and ticking the checkbox.
3. **Changelog:** the editor switches between Markdown and WYSIWYG on its own; plain text works in both. Click it and type one or two sentences.
4. **Dependencies:** leave "Include default project relations" on. It adds Fabric API as a Required Dependency. CurseForge doesn't pin versions, so there's nothing else to set. Release Type stays Release.
5. Click **Add File**. The new row shows Uploading, then Processing, then Under Review. CurseForge moderators approve it, which can take hours, so "Under Review" counts as done.

## 5. Verify

Reload the page (`location.reload()`), then re-read the General summary and categories, the Description `textarea`, and the Files rows with the snippet from step 1. Each new row needs the right file name, Release type, Minecraft version, Java tags, Client, Server and Fabric.

## 6. Report

Tell the user what changed, link `https://www.curseforge.com/minecraft/mc-mods/fertile-grounds`, list files still under review, and close the tab.
