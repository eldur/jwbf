package net.sourceforge.jwbf.core;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public class Transform {

  private Transform() {
    // do nothing
  }

  public static <F, T> ImmutableList<T> a(Iterable<F> from, Function<F, T> function) {
    return ImmutableList.copyOf(Iterables.transform(from, function));
  }

  public static <F, T> ImmutableList<T> the(Iterable<F> from, Function<F, T> function) {
    return Transform.a(from, function);
  }
}
