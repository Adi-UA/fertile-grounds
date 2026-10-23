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
import java.util.Locale;
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
  private static final Path JAVA = Path.of("common/src/main/java/com/fertilegrounds");
  private static final Path CLIENT_JAVA = Path.of("common/src/client/java/com/fertilegrounds");
  private static final Path ASSETS = Path.of("common/src/main/resources/assets", NS);
  private static final Path DATA = Path.of("common/src/main/resources/data", NS);

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

  // ---- Public docs ----

  /**
   * docs/GUIDE.md is the GitHub copy of the guide book: it has a section for every book page,
   * titled like the page, and a ready-to-paste command for every gamerule.
   */
  @Test
  void theGitHubGuideMatchesTheBookAndGamerules() throws IOException {
    final String guide = Files.readString(Path.of("docs/GUIDE.md"));
    final JsonObject lang = readJson(ASSETS.resolve("lang/en_us.json"));
    final List<String> missing = new ArrayList<>();
    for (final String key : lang.keySet()) {
      if (key.startsWith("book." + NS + ".guide.page.")) {
        final String title = lang.get(key).getAsString().split("\n")[0];
        if (key.endsWith(".page.1")
            ? !guide.contains("# " + title)
            : !guide.contains("## " + title)) {
          missing.add("page \"" + title + "\"");
        }
      }
    }
    for (final String rule : gameRules()) {
      if (!guide.contains("/gamerule " + NS + ":" + rule + " ")) {
        missing.add("gamerule " + rule);
      }
    }
    assertNone("Missing from docs/GUIDE.md", missing);
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
    // A literal ending in "." is a prefix that code completes, like the guide's page keys.
    used.removeIf(key -> key.endsWith("."));
    // Advancement titles, sound subtitles, and the guide book recipe's pages name their keys in
    // JSON; the recipe's text sits inside JSON strings, so its quotes are escaped.
    final Pattern jsonKey =
        Pattern.compile(
            "\\\\?\"(?:translate|subtitle)\\\\?\"\\s*:\\s*\\\\?\"([^\"\\\\]*" + NS + "[^\"\\\\]*)");
    for (final Path file : files(DATA.resolve("advancement"), ".json")) {
      collect(jsonKey, Files.readString(file), used);
    }
    for (final Path file : files(DATA.resolve("recipe"), ".json")) {
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

  /**
   * Player-facing text goes through translation keys, so code never builds text from a plain
   * string, which would show in English for every player.
   */
  @Test
  void noTextIsHardcoded() throws IOException {
    final List<String> found = new ArrayList<>();
    for (final Path dir : List.of(JAVA, CLIENT_JAVA)) {
      for (final Path file : files(dir, ".java")) {
        if (Files.readString(file).contains("Component.literal(")) {
          found.add(file.toString());
        }
      }
    }
    assertNone("Component.literal in mod code (use a translation key)", found);
  }

  /**
   * Other languages only translate keys English defines. A key English dropped is dead text, and
   * Minecraft already shows English for any key a language is missing.
   */
  @Test
  void otherLanguagesOnlyUseEnglishKeys() throws IOException {
    final Set<String> english = readJson(ASSETS.resolve("lang/en_us.json")).keySet();
    final List<String> stale = new ArrayList<>();
    for (final Path file : files(ASSETS.resolve("lang"), ".json")) {
      for (final String key : readJson(file).keySet()) {
        if (!english.contains(key)) {
          stale.add(file.getFileName() + ": " + key);
        }
      }
    }
    assertNone("Keys missing from en_us.json", stale);
  }

  /**
   * Regression for 0a7576e: block items showed "item.fertilegrounds.*" raw keys. On 1.21.1 a
   * BlockItem always takes its block's name, so block items must be plain BlockItems.
   */
  @Test
  void blockItemsReuseTheBlockName() throws IOException {
    final String modItems = Files.readString(JAVA.resolve("item/ModItems.java"));
    assertTrue(
        modItems.contains("new BlockItem(block,"),
        "ModItems must build block items as BlockItems, or their names resolve to"
            + " item.fertilegrounds.* keys the lang file doesn't define");
  }

  // ---- Client assets ----

  /** Regression for e5356de: items showed the missing-model placeholder. */
  @Test
  void itemModelsMatchItems() throws IOException {
    final Set<String> items = new TreeSet<>(blockItems());
    items.addAll(plainItems());
    final Set<String> models = baseNames(ASSETS.resolve("models/item"));
    final List<String> problems = new ArrayList<>();
    for (final String item : items) {
      if (!models.contains(item)) {
        problems.add("no models/item/" + item + ".json for item " + item);
      }
    }
    for (final String model : models) {
      if (!items.contains(model)) {
        problems.add("models/item/" + model + ".json matches no registered item");
      }
    }
    assertNone("Item model mismatches", problems);
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
    // Block states point at models under a "model" key, at any depth.
    for (final Path file : files(ASSETS.resolve("blockstates"), ".json")) {
      walk(
          JsonParser.parseString(Files.readString(file)),
          (key, value) -> {
            if (key.equals("model")) {
              requireAsset(value, "models", ".json", file, missing);
            }
          });
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
   * Regression for b95a82e: on 1.21.1, ingredients are {"item": ...} or {"tag": ...} objects and
   * results use "id". The newer plain-string ingredients fail to parse here, and the game just
   * skips the recipe with a log line.
   */
  @Test
  void recipesUseTheCurrentShape() throws IOException {
    final List<String> problems = new ArrayList<>();
    for (final Path file : files(DATA.resolve("recipe"), ".json")) {
      final JsonObject recipe = readJson(file);
      final List<JsonElement> ingredients = new ArrayList<>();
      if (recipe.has("key")) {
        recipe.getAsJsonObject("key").entrySet().forEach(e -> ingredients.add(e.getValue()));
      }
      if (recipe.has("ingredients")) {
        recipe.getAsJsonArray("ingredients").forEach(ingredients::add);
      }
      for (final JsonElement ingredient : ingredients) {
        if (!ingredient.isJsonObject()) {
          problems.add(file.getFileName() + " has a plain ingredient: " + ingredient);
        }
      }
      if (recipe.has("result") && !recipe.getAsJsonObject("result").has("id")) {
        problems.add(file.getFileName() + "'s result has no \"id\"");
      }
    }
    assertNone("Recipes in the wrong format", problems);
  }

  /**
   * Recipe advancements use "recipe" for recipe_unlocked on 26.2 and older; 26.3 renamed it
   * "recipes", which fails to parse here. Item predicates use a plain string or tag for "items" to
   * match vanilla's style (b95a82e); a list still parses, but one criterion per item reads like
   * vanilla's advancements.
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
        if (conditions.has("recipes")) {
          problems.add(file.getFileName() + ": " + criterion.getKey() + " uses \"recipes\"");
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

  // ---- GameTests ----

  /** The loaders only run test classes listed in TestCatalog. */
  @Test
  void everyGameTestClassIsRegistered() throws IOException {
    final Path gametest = Path.of("common/src/gametest/java/com/fertilegrounds/gametest");
    final String catalog = Files.readString(gametest.resolve("TestCatalog.java"));
    final List<String> missing = new ArrayList<>();
    for (final Path file : files(gametest, ".java")) {
      if (Files.readString(file).contains("@FertileTest(")
          || Files.readString(file).contains("@FertileTest\n")) {
        final String className = file.getFileName().toString().replace(".java", "");
        if (!catalog.contains(" " + className + ".class")
            && !catalog.contains(".gametest." + className + "\"")) {
          missing.add(className);
        }
      }
    }
    assertNone("GameTest classes missing from TestCatalog", missing);
  }

  // ---- Registered names, read from the registration classes ----

  private static Set<String> blocks() throws IOException {
    return names(JAVA.resolve("block/ModBlocks.java"), "register\\(\\s*\"(\\w+)\"");
  }

  /** Block items share their block's id, and each block's field name is its id in capitals. */
  private static Set<String> blockItems() throws IOException {
    final Set<String> found = new TreeSet<>();
    for (final String field :
        names(JAVA.resolve("item/ModItems.java"), "registerBlock\\(ModBlocks\\.(\\w+)\\)")) {
      found.add(field.toLowerCase(Locale.ROOT));
    }
    return found;
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
        JAVA.resolve("util/ModGameRules.java"), "(?:percent|integer|bool)\\(\\s*\"(\\w+)\"");
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
