package net.sourceforge.jwbf.core.internal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

public abstract class NonnullFunction<F, T> implements Function<F, T> {
  @Nullable
  @Override
  public final T apply(@Nullable F input) {
    F nonNull = Preconditions.checkNotNull(input, "input must not be null");
    return Preconditions.checkNotNull(applyNonnull(nonNull), "result must not be null");
  }

  @Nonnull
  protected abstract T applyNonnull(@Nonnull F input);
}
