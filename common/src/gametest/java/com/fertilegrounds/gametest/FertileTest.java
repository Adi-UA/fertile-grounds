package com.fertilegrounds.gametest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as an in-world test. {@link TestCatalog} turns each one into a vanilla test
 * function plus a test-instance file, which every loader's test server runs the same way.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FertileTest {

  /** The test environment (time, weather, gamerules), as a {@code test_environment} id. */
  String environment() default "minecraft:default";

  /** The structure placed for the test, from {@code data/<namespace>/structure/}. */
  String structure() default TestCatalog.EMPTY_STRUCTURE;

  /** Ticks before an unfinished test fails. */
  int maxTicks() default 20;

  /** Whether the test needs open sky above it, such as for daylight. */
  boolean skyAccess() default false;
}
