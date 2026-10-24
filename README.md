<p align="center">
  <img src="docs/icon.png" width="96" alt="Fertile Grounds icon">
</p>

# Fertile Grounds for Minecraft 1.21.11

[![Modrinth downloads](https://img.shields.io/modrinth/dt/fertile-grounds?logo=modrinth&label=Modrinth)](https://modrinth.com/mod/fertile-grounds) [![CurseForge downloads](https://img.shields.io/curseforge/dt/1627313?logo=curseforge&label=CurseForge)](https://www.curseforge.com/minecraft/mc-mods/fertile-grounds)

> **Fertile Grounds 2.0 is coming to this version, slowly.** Version 2.0 adds Fertilizer, Fairy Hotels, Chime Hotels, Fairy Groves, and much more, and it's out now for Minecraft 26.1 and newer and for 1.21.1. Older Minecraft scrambles its code and lacks some newer blocks, so it needs more workarounds, and updates reach 1.21.1, 1.21.10, and 1.21.11 slowly. On 1.21.1, for example, the Empty Fairy Hotel takes Glow Berries instead of a Firefly Bush, and the glowing parts of hotels don't glow in the dark. These versions stay on 2.0.0; new features after that come to 26.1 and newer. See the [`main` branch](https://github.com/Adi-UA/fertile-grounds/tree/main) for 2.0.

I originally started this little farming mod for my partner and me to play on our private server. Version 1.2.0 is the cozy beginning: richer soils that save you from bone-mealing the same field over and over, and a shy Bean Fairy who visits at night. Get it on [Modrinth](https://modrinth.com/mod/fertile-grounds) or [CurseForge](https://www.curseforge.com/minecraft/mc-mods/fertile-grounds).

![Vanilla farmland next to Enriched Sand, Enriched Dirt, and Super Enriched Dirt, with the enriched crops growing faster](docs/hero.gif)

## Grow more, work less

**Enriched Soil** gives your crops a free pinch of bone meal now and then, so they grow about 4 times faster, or 11 times faster on **Super Enriched** soil. It still floods, dries out, and gets trampled like normal dirt or sand.

| Soil | Craft it from | Boost chance per random tick |
|---|---|---|
| Enriched Dirt, till it into Enriched Farmland | Dirt + Bone Meal | 15% |
| Enriched Sand | Sand + Bone Meal | 15% |
| Super Enriched Dirt, till it into Super Enriched Farmland | Dirt + Bone Meal + Glowstone Dust | 50% |

Every recipe is shapeless, and each one unlocks in your recipe book once you hold its ingredients. Enriched farmland turns back into its own enriched dirt when it dries out or gets trampled, never plain dirt. Enriched Sand speeds up plants that grow on sand and take bone meal, like bamboo.

*The clip is sped up; at normal speed the soils keep the same order, just slower.*

## Meet the Bean Fairy

![A Bean Fairy visiting a farm at night and growing the crops](docs/bean_fairy.gif)

Some nights, a tiny Bean Fairy floats down and fully grows every crop in a 5x5 patch. Each night, every player has a 1% chance of a visit to a crop within 16 blocks. If everyone sleeps through it, the crops are grown at dawn anyway.

To call one on purpose, craft **Fairy Dust** (Glowstone Dust + Amethyst Shard + Bone Meal) and use it on a growing crop. Fairies only come at night: within about 30 seconds if it's already dark, or at nightfall. The creative tab also has a Bean Fairy Spawn Egg, which sends a fairy to the nearest crop.

## Requirements

- Minecraft 1.21.11
- [Fabric Loader](https://fabricmc.net/) 0.19.3 or newer
- [Fabric API](https://modrinth.com/mod/fabric-api) 0.141.6+1.21.11
- Java 17 or newer

## Building from source

```
./gradlew build
```

The jar lands in `build/libs/fertilegrounds-1.2.0+1.21.11.jar`. Drop it in your `mods/` folder next to Fabric API. `./gradlew runClient` opens a test game with the mod loaded.

## Art

The art in this repo is partially AI generated. I'm using AI to help me learn pixel art: it draws guides for me, and I trace over them by hand. I also ask it for help learning Aseprite, color theory, and how Minecraft models work. If you know other ways to learn that would let me keep building Minecraft mods at the same pace, I'm open to suggestions!

I trace over vanilla textures so I don't borrow stylistic choices from other artists.

## Feedback and license

Found a bug or have an idea? [Open an issue](https://github.com/Adi-UA/fertile-grounds/issues/new/choose). New features land on Minecraft 26.1 and newer. MIT licensed, see [LICENSE](LICENSE).
