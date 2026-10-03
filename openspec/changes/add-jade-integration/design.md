# Design

## Context

Pending visits and jar state live on the server; on a multiplayer server the client doesn't have them. Jade solves this with server data providers that send extra data for the block a player looks at.

## Goals / Non-Goals

**Goals:** all Jade code in a `compat/jade` package loaded only through Jade's entrypoint, so no Jade class loads when Jade is absent.

**Non-Goals:** Jade config options.

## Decisions

- **`compileOnly` (or `modCompileOnly` where needed) dependency on the Jade API**, pinned per branch from Jade's Modrinth versions. Runtime use only through the `jade` entrypoint in `fabric.mod.json`, which Fabric ignores when Jade is missing.
- **Server data providers** for pending visits (from `FairyVisits`) and jar state (from the block entity), **client component providers** for the text.
- **Soil chance sent from the server**, because gamerules aren't synced to clients.

## Risks / Trade-offs

- [Jade's plugin API changes between versions] → each backport checks the Jade API jar and records differences in the porting divergence table.
- [Dev runs need Jade at runtime to test] → add Jade as a `localRuntime` dependency for `runClient` only, never bundled.
