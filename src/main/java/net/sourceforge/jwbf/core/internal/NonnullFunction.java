package net.sourceforge.jwbf.core.internal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Function;

public abstract class NonnullFunction<F, T> implements Function<F, T> {
  @Nullable
  @Override
  public final T apply(@Nullable F in) {
    F nonNull = Checked.nonNull(in, "input");
    return Checked.nonNull(applyNonnull(nonNull), "result");
  }

  @Nonnull
  protected abstract T applyNonnull(@Nonnull F in);
}
