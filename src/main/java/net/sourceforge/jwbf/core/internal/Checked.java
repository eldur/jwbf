package net.sourceforge.jwbf.core.internal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Preconditions;

public class Checked {

  private Checked() {
    // do nothing
  }

  @Nonnull
  public static <T> T nonNull(@Nullable T t, String msg) {
    return Preconditions.checkNotNull(t, String.valueOf(msg) + " must not be null");
  }
}
