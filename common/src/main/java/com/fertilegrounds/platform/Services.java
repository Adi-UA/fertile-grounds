package com.fertilegrounds.platform;

import java.util.ServiceLoader;

/**
 * Finds the current loader's implementation of a shared interface, the {@code Services} pattern
 * from jaredlll08/MultiLoader-Template. Each loader lists its class in {@code
 * META-INF/services/<interface name>}, and Java's {@link ServiceLoader} reads that file.
 */
final class Services {

  private Services() {}

  static <T> T load(final Class<T> type) {
    return ServiceLoader.load(type, Services.class.getClassLoader())
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("No loader implements " + type.getName()));
  }
}
