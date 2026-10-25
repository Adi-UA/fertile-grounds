package com.fertilegrounds.gametest;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;

/**
 * Every in-world test, in a form all three loaders run the same way: each loader registers a {@code
 * test_function} from {@link #functions()}, and {@link #main} writes the matching test-instance
 * files at build time.
 */
public final class TestCatalog {

  public static final String NAMESPACE = "fertilegrounds-gametest";

  /** An empty 8 by 8 by 8 space, the same default Fabric API's tests use. */
  public static final String EMPTY_STRUCTURE = NAMESPACE + ":empty";

  private static final List<Class<?>> CLASSES =
      List.of(
          SetupTests.class,
          FairyVisitTests.class,
          FairyLingerTests.class,
          HarvestMoonTests.class,
          SoilRegressionTests.class,
          FairyContentTagTests.class,
          TuningGameruleTests.class,
          FertilizerTests.class,
          AdvancementTests.class,
          GiftTableTests.class,
          FairyDustTests.class,
          NaturalVisitTests.class,
          VisitFeedbackTests.class,
          FairyGiftTests.class,
          ShyFlightTests.class,
          FairyHotelTests.class,
          FairyCatchTests.class,
          JarredFairyExpiryTests.class,
          HotelEffectTests.class,
          FairySpeedTests.class,
          ChimeHotelTests.class,
          RedstoneTests.class,
          TraderTests.class,
          GuideBookTests.class,
          FairyGroveTests.class,
          LoaderHookTests.class);

  /**
   * Jade has no Forge build for this Minecraft version, so the Forge build leaves these tests out.
   */
  private static final String JADE_TESTS = "com.fertilegrounds.gametest.JadeDataTests";

  private TestCatalog() {}

  static List<Class<?>> classes() {
    final List<Class<?>> classes = new ArrayList<>(CLASSES);
    try {
      classes.add(Class.forName(JADE_TESTS, false, TestCatalog.class.getClassLoader()));
    } catch (final ClassNotFoundException e) {
      // The Forge build, without Jade.
    }
    return classes;
  }

  /** Each test's id and the code it runs, for the {@code test_function} registry. */
  public static Map<ResourceLocation, Consumer<GameTestHelper>> functions() {
    final Map<ResourceLocation, Consumer<GameTestHelper>> functions = new LinkedHashMap<>();
    for (final Class<?> type : classes()) {
      final Object tests = newInstance(type);
      for (final Method method : testMethods(type)) {
        functions.put(id(type, method), helper -> invoke(method, tests, helper));
      }
    }
    return functions;
  }

  /** Each test's id and its test-instance file, as written into the gametest data pack. */
  static Map<ResourceLocation, JsonObject> instances() {
    final Map<ResourceLocation, JsonObject> instances = new LinkedHashMap<>();
    for (final Class<?> type : classes()) {
      for (final Method method : testMethods(type)) {
        final FertileTest test = method.getAnnotation(FertileTest.class);
        final ResourceLocation id = id(type, method);
        final JsonObject json = new JsonObject();
        json.addProperty("type", "minecraft:function");
        json.addProperty("function", id.toString());
        json.addProperty("environment", test.environment());
        json.addProperty("structure", test.structure());
        json.addProperty("max_ticks", test.maxTicks());
        json.addProperty("sky_access", test.skyAccess());
        if (instances.put(id, json) != null) {
          throw new IllegalStateException("Two tests share the id " + id);
        }
      }
    }
    return instances;
  }

  /**
   * Same naming as Fabric API: {@code FairyDustTests.callsAFairy} is {@code
   * fairy_dust_tests_calls_afairy}.
   */
  static ResourceLocation id(final Class<?> type, final Method method) {
    final String name = type.getSimpleName() + "_" + method.getName();
    return ResourceLocation.fromNamespaceAndPath(
        NAMESPACE, name.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.ROOT));
  }

  /** Writes every test-instance file under the given folder; run by the build. */
  public static void main(final String[] args) throws IOException {
    final Path root = Path.of(args[0], "data", NAMESPACE, "test_instance");
    final var gson = new GsonBuilder().setPrettyPrinting().create();
    for (final var entry : instances().entrySet()) {
      final Path file = root.resolve(entry.getKey().getPath() + ".json");
      Files.createDirectories(file.getParent());
      Files.writeString(file, gson.toJson(entry.getValue()) + "\n");
    }
  }

  private static List<Method> testMethods(final Class<?> type) {
    return Arrays.stream(type.getDeclaredMethods())
        .filter(method -> method.isAnnotationPresent(FertileTest.class))
        .sorted(Comparator.comparing(Method::getName))
        .toList();
  }

  private static Object newInstance(final Class<?> type) {
    try {
      return type.getDeclaredConstructor().newInstance();
    } catch (final ReflectiveOperationException e) {
      throw new IllegalStateException("Can't create " + type.getName(), e);
    }
  }

  /** Runs a test method, passing its own failure (such as a failed assertion) through as is. */
  private static void invoke(final Method method, final Object tests, final GameTestHelper helper) {
    try {
      method.invoke(tests, helper);
    } catch (final InvocationTargetException e) {
      if (e.getCause() instanceof RuntimeException runtime) {
        throw runtime;
      }
      if (e.getCause() instanceof Error error) {
        throw error;
      }
      throw new IllegalStateException(e.getCause());
    } catch (final IllegalAccessException e) {
      throw new IllegalStateException("Test method isn't public: " + method, e);
    }
  }
}
