package com.fertilegrounds.gametest;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.xml.parsers.ParserConfigurationException;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GlobalTestReporter;
import net.minecraft.gametest.framework.JUnitLikeTestReporter;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;

/**
 * Every in-world test, in a form all three loaders run the same way: one vanilla {@link
 * TestFunction} per {@link FertileTest} method, from {@link #tests()}. Each loader's gametest code
 * registers this class and {@link TestEnvironments} with vanilla's {@code GameTestRegistry}. Test
 * ids match the ones Fabric API's {@code @GameTest} gave: the class and method name in snake case.
 */
public final class TestCatalog {

  public static final String NAMESPACE = "fertilegrounds-gametest";

  /** An empty 8 by 8 by 8 space, the same default Fabric API's tests use. */
  public static final String EMPTY_STRUCTURE = NAMESPACE + ":empty";

  /**
   * Set by {@code -PgametestFilter}: only tests whose ids match this pattern run ({@code *} matches
   * anything but a colon).
   */
  private static final String FILTER = System.getProperty("fertilegrounds.gametest.filter");

  /** Set by the build: where the JUnit-style report goes. */
  private static final String REPORT = System.getProperty("fertilegrounds.gametest.report");

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
   * Jade publishes no Forge build for Minecraft 1.21.1, so the Forge build leaves the Jade
   * integration and these tests out; the other loaders compile and run them.
   */
  private static final String JADE_TESTS = "com.fertilegrounds.gametest.JadeDataTests";

  /**
   * What vanilla calls to collect the tests. It also points the test server's report at the file
   * the build named, since only Fabric API has its own option for that in this Minecraft version.
   */
  @GameTestGenerator
  public List<TestFunction> tests() throws ParserConfigurationException {
    if (REPORT != null) {
      GlobalTestReporter.replaceWith(new JUnitLikeTestReporter(new File(REPORT)));
    }
    return functions();
  }

  static List<Class<?>> classes() {
    final List<Class<?>> classes = new ArrayList<>(CLASSES);
    try {
      classes.add(Class.forName(JADE_TESTS, false, TestCatalog.class.getClassLoader()));
    } catch (final ClassNotFoundException e) {
      // The Forge build, without Jade.
    }
    return classes;
  }

  /** Every test as a vanilla test function. */
  static List<TestFunction> functions() {
    final List<TestFunction> functions = new ArrayList<>();
    for (final Class<?> type : classes()) {
      final Object tests = newInstance(type);
      for (final Method method : testMethods(type)) {
        final String id = id(type, method).toString();
        if (FILTER != null && !matches(FILTER, id)) {
          continue;
        }
        final FertileTest test = method.getAnnotation(FertileTest.class);
        functions.add(
            new TestFunction(
                test.environment(),
                id,
                test.structure(),
                Rotation.NONE,
                test.maxTicks(),
                0,
                true,
                false,
                1,
                1,
                test.skyAccess(),
                helper -> invoke(method, tests, helper)));
      }
    }
    return functions;
  }

  /** Every environment a test names, so a check can confirm each has its batch setup. */
  static Set<String> environments() {
    final Set<String> environments = new TreeSet<>();
    for (final Class<?> type : classes()) {
      for (final Method method : testMethods(type)) {
        environments.add(method.getAnnotation(FertileTest.class).environment());
      }
    }
    return environments;
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

  static boolean matches(final String pattern, final String id) {
    final String regex =
        Arrays.stream(pattern.split("\\*", -1))
            .map(Pattern::quote)
            .collect(Collectors.joining("[^:]*"));
    return id.matches(regex);
  }

  static List<Method> testMethods(final Class<?> type) {
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
