package com.fertilegrounds;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

/**
 * Checks the mod's JSON resources against its Java source without starting Minecraft. A missing
 * lang key, model, or texture never crashes the game: it shows up as a raw key or a purple-black
 * placeholder, and an outdated recipe is silently skipped on load. These tests catch all of that at
 * build time instead.
 */
class AssetConsistencyTest {

  private static final String NS = "fertilegrounds";
  private static final Path JAVA = Path.of("src/main/java/com/fertilegrounds");
  private static final Path CLIENT_JAVA = Path.of("src/client/java/com/fertilegrounds");
  private static final Path ASSETS = Path.of("src/main/resources/assets", NS);
  private static final Path DATA = Path.of("src/main/resources/data", NS);

  // ---- Lang keys ----

  @Test
  void everyRegisteredThingHasALangEntry() throws IOException {
    final Set<String> lang = readJson(ASSETS.resolve("lang/en_us.json")).keySet();
    final List<String> missing = new ArrayList<>();
    for (final String block : blocks()) {
      requireKey(lang, "block." + NS + "." + block, missing);
    }
    for (final String item : plainItems()) {
      requireKey(lang, "item." + NS + "." + item, missing);
    }
    for (final String entity : entities()) {
      requireKey(lang, "entity." + NS + "." + entity, missing);
    }
    // Recipe viewers (EMI, JEI, REI) show item tags by name, using Fabric's tag.item.* convention.
    for (final String tag : baseNames(DATA.resolve("tags/item"))) {
      requireKey(lang, "tag.item." + NS + "." + tag.replace('/', '.'), missing);
    }
    // The Edit Game Rules screen shows each rule's name and its ".description" line.
    for (final String rule : gameRules()) {
      requireKey(lang, "gamerule." + NS + "." + rule, missing);
      requireKey(lang, "gamerule." + NS + "." + rule + ".description", missing);
    }
    assertNone("Missing lang entries", missing);
  }

  @Test
  void everyKeyUsedInCodeOrDataHasALangEntry() throws IOException {
    final Set<String> lang = readJson(ASSETS.resolve("lang/en_us.json")).keySet();
    final Set<String> used = new TreeSet<>();
    // Keys in Java show up as string literals, e.g. Component.translatable("...") or a helper
    // that takes the key as a string.
    final Pattern literal = Pattern.compile("\"((?:[a-zA-Z]+)\\." + NS + "\\.[a-z0-9_.]+)\"");
    for (final Path file : files(JAVA, ".java")) {
      collect(literal, Files.readString(file), used);
    }
    for (final Path file : files(CLIENT_JAVA, ".java")) {
      collect(literal, Files.readString(file), used);
    }
    // Advancement titles and sound subtitles name their keys in JSON.
    final Pattern jsonKey =
        Pattern.compile("\"(?:translate|subtitle)\"\\s*:\\s*\"([^\"]*" + NS + "[^\"]*)\"");
    for (final Path file : files(DATA.resolve("advancement"), ".json")) {
      collect(jsonKey, Files.readString(file), used);
    }
    final Path sounds = ASSETS.resolve("sounds.json");
    if (Files.exists(sounds)) {
      collect(jsonKey, Files.readString(sounds), used);
    }
    final List<String> missing = new ArrayList<>();
    for (final String key : used) {
      requireKey(lang, key, missing);
    }
    assertNone("Lang keys used but not defined", missing);
  }

  /** Regression for 0a7576e: block items showed "item.fertilegrounds.*" raw keys. */
  @Test
  void blockItemsReuseTheBlockName() throws IOException {
    final String modBlocks = Files.readString(JAVA.resolve("block/ModBlocks.java"));
    assertTrue(
        modBlocks.contains(".useBlockDescriptionPrefix()"),
        "ModBlocks must build block items with useBlockDescriptionPrefix(), or their names"
            + " resolve to item.fertilegrounds.* keys the lang file doesn't define");
  }

  // ---- Client assets ----

  /** Regression for e5356de: items showed the missing-model placeholder. */
  @Test
  void itemModelDefinitionsMatchItems() throws IOException {
    final Set<String> items = new TreeSet<>(blockItems());
    items.addAll(plainItems());
    final Set<String> definitions = baseNames(ASSETS.resolve("items"));
    final List<String> problems = new ArrayList<>();
    for (final String item : items) {
      if (!definitions.contains(item)) {
        problems.add("no items/" + item + ".json for item " + item);
      }
    }
    for (final String definition : definitions) {
      if (!items.contains(definition)) {
        problems.add("items/" + definition + ".json matches no registered item");
      }
    }
    assertNone("Item model definition mismatches", problems);
  }

  @Test
  void everyBlockHasABlockState() throws IOException {
    final Set<String> states = baseNames(ASSETS.resolve("blockstates"));
    final List<String> missing = new ArrayList<>();
    for (final String block : blocks()) {
      if (!states.contains(block)) {
        missing.add("blockstates/" + block + ".json");
      }
    }
    assertNone("Blocks without a block state", missing);
  }

  @Test
  void everyReferencedModelAndTextureExists() throws IOException {
    final List<String> missing = new ArrayList<>();
    // Item definitions and block states point at models under a "model" key, at any depth.
    for (final String dir : List.of("items", "blockstates")) {
      for (final Path file : files(ASSETS.resolve(dir), ".json")) {
        walk(
            JsonParser.parseString(Files.readString(file)),
            (key, value) -> {
              if (key.equals("model")) {
                requireAsset(value, "models", ".json", file, missing);
              }
            });
      }
    }
    for (final Path file : files(ASSETS.resolve("models"), ".json")) {
      final JsonObject model = readJson(file);
      if (model.has("parent")) {
        requireAsset(model.get("parent").getAsString(), "models", ".json", file, missing);
      }
      if (model.has("textures")) {
        for (final var entry : model.getAsJsonObject("textures").entrySet()) {
          // "#name" points at another texture variable in the same model, not a file.
          final String texture = entry.getValue().getAsString();
          if (!texture.startsWith("#")) {
            requireAsset(texture, "textures", ".png", file, missing);
          }
        }
      }
    }
    // Entity renderers name their texture in Java, e.g. id("textures/entity/bean_fairy.png").
    final Pattern javaTexture = Pattern.compile("id\\(\"(textures/[a-z0-9_/]+\\.png)\"\\)");
    for (final Path file : files(CLIENT_JAVA, ".java")) {
      final Matcher matcher = javaTexture.matcher(Files.readString(file));
      while (matcher.find()) {
        if (!Files.exists(ASSETS.resolve(matcher.group(1)))) {
          missing.add(matcher.group(1) + " (used in " + file.getFileName() + ")");
        }
      }
    }
    assertNone("Missing models or textures", missing);
  }

  // ---- Data file shape ----

  /**
   * Regression for b95a82e: since 1.21.2, ingredients are plain "minecraft:x" strings and results
   * use "id". The old {"item": ...} shape makes the recipe fail to parse, and the game just skips
   * it with a log line.
   */
  @Test
  void recipesUseTheCurrentShape() throws IOException {
    final List<String> problems = new ArrayList<>();
    for (final Path file : files(DATA.resolve("recipe"), ".json")) {
      walk(
          JsonParser.parseString(Files.readString(file)),
          (key, value) -> {
            if (key.equals("item")) {
              problems.add(file.getFileName() + " uses \"item\": " + value);
            }
          });
    }
    assertNone("Recipes in the old format", problems);
  }

  /**
   * Recipe advancements use "recipes" for recipe_unlocked, not the old "recipe" key, which fails to
   * parse on 26.3. Item predicates use a plain string or tag for "items" to match vanilla's style
   * (b95a82e); a list still parses, but one criterion per item reads like vanilla's advancements.
   */
  @Test
  void advancementsUseTheCurrentShape() throws IOException {
    final List<String> problems = new ArrayList<>();
    for (final Path file : files(DATA.resolve("advancement"), ".json")) {
      final JsonObject criteria = readJson(file).getAsJsonObject("criteria");
      for (final var criterion : criteria.entrySet()) {
        final JsonObject conditions =
            criterion.getValue().getAsJsonObject().getAsJsonObject("conditions");
        if (conditions == null) {
          continue;
        }
        if (conditions.has("recipe")) {
          problems.add(file.getFileName() + ": " + criterion.getKey() + " uses \"recipe\"");
        }
        if (conditions.has("items") && conditions.get("items").isJsonArray()) {
          for (final JsonElement predicate : conditions.getAsJsonArray("items")) {
            final JsonElement items = predicate.getAsJsonObject().get("items");
            if (items != null && items.isJsonArray()) {
              problems.add(file.getFileName() + ": " + criterion.getKey() + " has an items array");
            }
          }
        }
      }
    }
    assertNone("Advancements in the old format", problems);
  }

  // ---- Registered names, read from the registration classes ----

  private static Set<String> blocks() throws IOException {
    return names(
        JAVA.resolve("block/ModBlocks.java"), "registerBlock(?:WithItem|Only)\\(\\s*\"(\\w+)\"");
  }

  private static Set<String> blockItems() throws IOException {
    return names(JAVA.resolve("block/ModBlocks.java"), "registerBlockWithItem\\(\\s*\"(\\w+)\"");
  }

  private static Set<String> plainItems() throws IOException {
    return names(JAVA.resolve("item/ModItems.java"), "register\\(\\s*\"(\\w+)\"");
  }

  private static Set<String> entities() throws IOException {
    return names(
        JAVA.resolve("entity/ModEntities.java"),
        "Registries\\.ENTITY_TYPE,\\s*ModIdsUtil\\.id\\(\"(\\w+)\"\\)");
  }

  private static Set<String> gameRules() throws IOException {
    return names(
        JAVA.resolve("util/ModGameRules.java"), "GameRule<\\w+> \\w+ =\\s*\\w+\\(\\s*\"(\\w+)\"");
  }

  private static Set<String> names(final Path source, final String regex) throws IOException {
    final Set<String> found = new TreeSet<>();
    collect(Pattern.compile(regex), Files.readString(source), found);
    assertFalse(found.isEmpty(), "Found no registrations in " + source + "; did the helper move?");
    return found;
  }

  // ---- Helpers ----

  /** Checks a "namespace:path" reference to a model or texture; other mods' assets are skipped. */
  private static void requireAsset(
      final String reference,
      final String folder,
      final String extension,
      final Path from,
      final List<String> missing) {
    final String[] parts =
        reference.contains(":") ? reference.split(":", 2) : new String[] {"minecraft", reference};
    if (parts[0].equals(NS)
        && !Files.exists(ASSETS.resolve(folder).resolve(parts[1] + extension))) {
      missing.add(reference + " (used in " + from.getFileName() + ")");
    }
  }

  private static void requireKey(
      final Set<String> lang, final String key, final List<String> missing) {
    if (!lang.contains(key)) {
      missing.add(key);
    }
  }

  private static void collect(final Pattern pattern, final String text, final Set<String> into) {
    final Matcher matcher = pattern.matcher(text);
    while (matcher.find()) {
      into.add(matcher.group(1));
    }
  }

  /** Visits every key whose value is a string, at any depth. */
  private static void walk(final JsonElement element, final BiConsumer<String, String> visitor) {
    if (element.isJsonObject()) {
      for (final var entry : element.getAsJsonObject().entrySet()) {
        if (entry.getValue().isJsonPrimitive()) {
          visitor.accept(entry.getKey(), entry.getValue().getAsString());
        } else {
          walk(entry.getValue(), visitor);
        }
      }
    } else if (element.isJsonArray()) {
      for (final JsonElement child : (JsonArray) element) {
        walk(child, visitor);
      }
    }
  }

  private static JsonObject readJson(final Path file) throws IOException {
    return JsonParser.parseString(Files.readString(file)).getAsJsonObject();
  }

  private static List<Path> files(final Path dir, final String extension) throws IOException {
    if (!Files.isDirectory(dir)) {
      return List.of();
    }
    try (Stream<Path> stream = Files.walk(dir)) {
      return stream.filter(p -> p.toString().endsWith(extension)).sorted().toList();
    }
  }

  private static Set<String> baseNames(final Path dir) throws IOException {
    final Set<String> names = new TreeSet<>();
    for (final Path file : files(dir, ".json")) {
      names.add(file.getFileName().toString().replace(".json", ""));
    }
    return names;
  }

  private static void assertNone(final String what, final List<String> problems) {
    assertTrue(problems.isEmpty(), what + ":\n  " + String.join("\n  ", problems));
  }
}
