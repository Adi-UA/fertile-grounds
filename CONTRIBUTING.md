# Contributing

Bug reports and ideas go in [GitHub issues](https://github.com/Adi-UA/fertile-grounds/issues/new/choose). For code changes, `AGENTS.md` covers the layout, commands, and tests.

## Translations

All of the mod's text lives in `src/main/resources/assets/fertilegrounds/lang/`. `en_us.json` is the English source, and each other language is a file named after Minecraft's language code, such as `de_de.json` or `pt_br.json` (the [Minecraft Wiki](https://minecraft.wiki/w/Language) lists every code).

To add or update a language:

1. Copy `en_us.json` to `<code>.json`, or open the existing file.
2. Translate the values and keep the keys as they are. Keep each `%s`, which the game fills in with a number or name, and each `%%`, which shows as a percent sign.
3. In the guide book pages (`book.fertilegrounds.guide.page.*`), `\n` starts a new line. A book page fits about 14 short lines, so keep each page close to the English length.
4. Leave out any key you haven't translated. The game shows English for it, never a raw key.
5. Run `./gradlew test`. It fails if your file has a key that `en_us.json` doesn't, which usually means a typo or a key the mod no longer uses.
6. Open a pull request.

Translations come from people who speak the language. Please don't submit machine translations.
