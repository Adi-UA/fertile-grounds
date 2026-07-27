# Fertile Grounds

Tired of standing over your crops with a bone meal stack in hand? Fertile Grounds is a Fabric mod for Minecraft 1.20.1 that adds tiered soil-enrichment blocks: till or place them once, and they keep quietly bone-mealing whatever grows on top for as long as they're there. No more babysitting a farm block by block, just a small, honest convenience upgrade, not a "click once and every crop in the world is instant" mod. It still plays by vanilla's rules: till it, water it, watch it flood, dry out, or get trampled like normal soil.

![Fertile Grounds demo: vanilla farmland next to Enriched Sand, Enriched Dirt, and Super Enriched Dirt, showing the growth speed difference over time](docs/hero.gif)

## What it adds

Three tiers, each a drop-in soil upgrade that behaves like its vanilla counterpart (till it, plant on it, it still floods/dries/tramples) but with a chance per random tick to instantly advance whatever's growing on top of it, as if bone-mealed:

| Tier | Block(s) | Crafted from | Boost chance/tick |
|---|---|---|---|
| 1 | Enriched Dirt / Enriched Farmland | Dirt + Bone Meal | 15% |
| 2 | Enriched Sand | Sand + Bone Meal | 15% |
| 3 | Super Enriched Dirt / Super Enriched Farmland | Dirt + Bone Meal + Glowstone Dust | 50% |

All three are craftable (shapeless, 1:1:1, no crafting table shape required) and unlock in the recipe book the first time you hold the right ingredients.

**Enriched Dirt / Super Enriched Dirt** work exactly like vanilla dirt: till them into farmland with a hoe, only the farmland form accepts crops. Their farmland reverts back to their *own* dirt tier (not vanilla dirt) on drought or trampling, and shows a darker, moist texture at full hydration, same as vanilla farmland.

**Enriched Sand** behaves like vanilla sand, including letting sugar cane be planted on it near water. No tilling involved, it boosts whatever's growing directly on top of it.

All three items live in their own creative-inventory tab, "Fertile Grounds."

## Requirements

- Minecraft 1.20.1
- [Fabric Loader](https://fabricmc.net/) 0.19.3+
- [Fabric API](https://modrinth.com/mod/fabric-api) 0.92.11+1.20.1
- Java 17+

## Building from source

```
./gradlew build
```

The mod jar is output to `build/libs/fertilegrounds-<version>.jar`. Drop it in your `mods/` folder alongside Fabric API.

## Development

```
./gradlew runClient      # launch a dev client with the mod loaded
./gradlew spotlessApply  # auto-format code
```

## License

CC0-1.0. See `LICENSE`.
