@AGENTS.md
@.claude/rules/concise-answers.md

## Doc hygiene

Keep every instruction file under 150 lines. `AGENTS.md` holds repo facts any agent can use; put Claude-specific guidance here and repeatable workflows in `.claude/skills/`. Before finishing an edit to any doc, skill, or instruction file, self-review it: if new text restates something stated elsewhere, link the existing source instead, and confirm every path, skill name, and section pointer you touched still resolves. Writing style and general Fabric modding practice come from the global rules; don't copy them here.

## Claude notes

- When a port or feature turns up a new API or data-format change, add it to the divergence table in `.claude/skills/porting-minecraft-version/SKILL.md` in the same commit, so the next backport doesn't rediscover it.
- Gradle's `runClient`, `runServer`, and the smoke test need the sandbox disabled (they bind ports and write to `run/`).
