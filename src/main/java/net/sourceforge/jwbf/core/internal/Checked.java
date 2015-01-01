package net.sourceforge.jwbf.core.internal;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.sourceforge.jwbf.core.contentRep.WatchResponse;

import com.google.common.base.Preconditions;

public class Checked {

  private Checked() {
    // do nothing
  }

  @Nonnull
  public static <T> T nonNull(@Nullable T t, String msg) {
    return Preconditions.checkNotNull(t, String.valueOf(msg) + " must not be null");
  }

  @Nonnull
  public static String nonBlank(@Nullable String string, String msg) {
    if (string == null || string.length() == 0) {
      throw new IllegalArgumentException("The argument '" + msg + "' must not be null or empty");
    }
    return string;

  }
}
