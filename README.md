<p align="center">
  <img src="docs/icon.png" width="96" alt="Fertile Grounds icon">
</p>

# Fertile Grounds

[![build](https://github.com/Adi-UA/fertile-grounds/actions/workflows/build.yml/badge.svg)](https://github.com/Adi-UA/fertile-grounds/actions/workflows/build.yml)

Fertile Grounds is a Fabric mod for Minecraft 1.20.1 that adds tiered soil-enrichment blocks. Place one once, and it has a chance every so often to bone-meal whatever grows on top of it on its own, so you stop manually reapplying bone meal to the same farm every few minutes. That works out to roughly 4x faster growth on Tier 1/Tier 2 soil and roughly 11x faster on Super Enriched soil, for a single well-watered crop. It's a convenience upgrade, not a balance-breaking one: the soil still floods, dries out, and gets trampled like normal dirt or sand.

![Fertile Grounds demo: vanilla farmland next to Enriched Sand, Enriched Dirt, and Super Enriched Dirt, showing the growth speed difference over time](docs/hero.gif)

*Recorded with `/gamerule randomTickSpeed 100` to make the difference visible in a short clip. Default random tick speed (3) looks the same relative to each other, just much slower in real time.*

## What it adds

Three tiers, each a drop-in soil upgrade that behaves like its vanilla counterpart (till it, plant on it, it still floods/dries/tramples) but with a chance per random tick to instantly advance whatever's growing on top of it, as if bone-mealed:

| Tier | Block(s) | Crafted from | Boost chance/tick |
|---|---|---|---|
| 1 | Enriched Dirt / Enriched Farmland | Dirt + Bone Meal | 15% |
| 2 | Enriched Sand | Sand + Bone Meal | 15% |
| 3 | Super Enriched Dirt / Super Enriched Farmland | Dirt + Bone Meal + Glowstone Dust | 50% |

All three are craftable (shapeless, 1:1:1, no crafting table shape required) and unlock in the recipe book the first time you hold the right ingredients.

**Enriched Dirt / Super Enriched Dirt** work exactly like vanilla dirt: till them into farmland with a hoe, only the farmland form accepts crops. Their farmland reverts back to their *own* dirt tier (not vanilla dirt) on drought or trampling, and shows a darker, moist texture at full hydration, same as vanilla farmland.

**Enriched Sand** behaves like vanilla sand (sugar cane can still be planted on it near water), no tilling involved. It boosts whatever bonemealable plant is directly on top of it, bamboo is the clearest example since it can grow on sand; sugar cane and cactus don't accept bone meal in vanilla, so they grow at normal speed on it either way.

All three items live in their own creative-inventory tab, "Fertile Grounds."

## Minecraft version

This branch targets Minecraft 1.20.1. Other supported versions live on their own branches, each an independently maintained port (changes aren't shared automatically between them):

| Branch | Minecraft version |
|---|---|
| `1.20.1` | 1.20.1 (this branch) |
| `1.21.1` | 1.21.1 |
| `26.1` | 26.1 |
| `main` | latest supported version |

## Requirements

- Minecraft 1.20.1
- [Fabric Loader](https://fabricmc.net/) 0.19.3+
- [Fabric API](https://modrinth.com/mod/fabric-api) 0.92.11+1.20.1
- Java 17+

## Building from source

```
./gradlew build
```

The mod jar is output to `build/libs/fertilegrounds-<mod version>+<minecraft version>.jar` (e.g. `fertilegrounds-1.1.0+1.20.1.jar`). The `+<mc version>` suffix matches Fabric's own convention (see Fabric API's own release names) and keeps jars from different branches from colliding if you're collecting builds from more than one version in the same place. Drop it in your `mods/` folder alongside Fabric API.

## Development

```
./gradlew runClient      # launch a dev client with the mod loaded
./gradlew spotlessApply  # auto-format code
```

## License

MIT. See `LICENSE`.
