package net.sourceforge.jwbf.core;

import com.google.common.base.Optional;
import com.google.common.base.Strings;

public final class Optionals {

  private Optionals() {
    // do nothing
  }

  public static Optional<String> absentIfEmpty(String nullableReference) {
    Optional<String> fromNullable = Optional.fromNullable(nullableReference);
    if (fromNullable.isPresent()) {
      if (Strings.isNullOrEmpty(fromNullable.get().trim())) {
        return Optional.absent();
      } else {
        return fromNullable;
      }
    } else {
      return Optional.absent();
    }
  }

  public static <T> T getOrThrow(Optional<T> token, String exceptionMessage) {
    if (token.isPresent()) {
      return token.get();
    } else {
      throw new IllegalArgumentException(exceptionMessage);
    }
  }
}
