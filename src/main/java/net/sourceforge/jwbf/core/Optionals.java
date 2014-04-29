package net.sourceforge.jwbf.core;

import com.google.common.base.Optional;
import com.google.common.base.Strings;

public class Optionals {

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

}
