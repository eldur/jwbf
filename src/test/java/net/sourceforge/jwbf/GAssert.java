package net.sourceforge.jwbf;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.startsWith;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import net.sourceforge.jwbf.core.internal.NonnullFunction;
import org.junit.Assert;
import org.junit.ComparisonFailure;

public class GAssert {

  private static ImmutableList<?> sortedCopy(Set<?> actual) {
    return FluentIterable.from(actual).toSortedList(Ordering.usingToString());
  }

  public static ImmutableList<String> toList(String bs) {
    return ImmutableList.copyOf(Splitter.on("\n").split(bs));
  }

  public static void assertEquals(ImmutableMultimap<?, ?> expected,
      ImmutableMultimap<?, ?> actual) {
    assertEquals(expected.asMap(), actual.asMap());
  }

  public static void assertEquals(ImmutableMap<?, ?> expected, ImmutableMap<?, ?> actual) {
    ImmutableList<?> expectedTuples = sortedCopy(expected.entrySet());
    ImmutableList<?> actualTuples = sortedCopy(actual.entrySet());
    assertEquals(expectedTuples, actualTuples);
  }

  public static <T> void assertEquals(ImmutableList<T> expected, Set<T> actual) {
    assertEquals(expected, sortedCopy(actual));
  }

  public static <T> void assertEquals(ImmutableSet<T> expected, Set<T> actual) {
    assertEquals(sortedCopy(expected), sortedCopy(actual));
  }

  public static void assertEquals(ImmutableList<?> expected, ImmutableList<?> actual) {
    try {
      Assert.assertEquals(expected, actual);
    } catch (AssertionError e) {

      String actualTypes = joinToString(actual, TO_TYPE_STRING);
      String expectedTypes = joinToString(expected, TO_TYPE_STRING);

      final String actual1;
      final String expected1;

      if (actualTypes != null && actualTypes.equals(expectedTypes)) {
        actual1 = joinToString(actual, Functions.toStringFunction());
        expected1 = joinToString(expected, Functions.toStringFunction());
      } else {
        actual1 = joinToString(actual, TO_DETAIL_STRING);
        expected1 = joinToString(expected, TO_DETAIL_STRING);
      }

      Assert.assertEquals(expected1, actual1);
      throw e;
    }
  }

  private static String joinToString(ImmutableList<?> list, Function<Object, String> toString) {
    if (list == null) {
      return null;
    }
    ImmutableList<String> parts = FluentIterable.from(list).transform(toString).toList();
    return Joiner.on("\n").join(parts);
  }

  public static void assertStartsWith(final String expected, final String actual) {
    try {
      Assert.assertTrue("expected value: \"\" is too short", expected.length() > 0);
      Assert.assertThat(actual, startsWith(expected));
    } catch (AssertionError e) {
      throw new ComparisonFailure(e.getMessage().trim(), expected, actual);
    }
  }

  public static void assertEndsWith(final String expected, final String actual) {
    try {
      Assert.assertTrue("expected value: \"\" is too short", expected.length() > 0);
      Assert.assertThat(actual, endsWith(expected));
    } catch (AssertionError e) {
      throw new ComparisonFailure(e.getMessage().trim(), expected, actual);
    }
  }

  static Function<Object, String> TO_DETAIL_STRING = new NonnullFunction<Object, String>() {
    @Nonnull
    @Override
    public String applyNonnull(@Nonnull Object input) {
      if (input instanceof Map.Entry) {
        Map.Entry<?, ?> entry = (Map.Entry<?, ?>) input;
        Object key = entry.getKey();
        Object value = entry.getValue();
        if (value instanceof ImmutableList) {
          ImmutableList<?> list = (ImmutableList<?>) value;
          return toClassString(key) + "=" + Joiner.on("\n  ") //
              .join(Iterables.transform(list, TO_CLASS_STRING)) + "\n";
        } else {
          return toClassString(key) + "=" + toClassString(value);
        }
      }
      return input.toString();
    }

  };

  static String toClassString(Object o) {
    return "{" + o.toString() + " [" + o.getClass().getCanonicalName() + "]}";
  }

  static Function<Object, String> TO_TYPE_STRING = new NonnullFunction<Object, String>() {
    @Nonnull
    @Override
    protected String applyNonnull(@Nonnull Object input) {
      if (input instanceof Map.Entry) {
        Map.Entry<?, ?> entry = (Map.Entry<?, ?>) input;
        Object key = entry.getKey();
        Object value = entry.getValue();
        if (value instanceof ImmutableList) {
          ImmutableList<?> list = (ImmutableList<?>) value;
          return key.getClass().getCanonicalName() + "=" + Joiner.on(",") //
              .join(Iterables.transform(list, TO_CLASSES));
        } else {
          return key.getClass().getCanonicalName() + "=" + value.getClass().getCanonicalName();
        }
      }
      return input.toString();
    }
  };

  static Function<Object, String> TO_CLASS_STRING = new Function<Object, String>() {
    @Nullable
    @Override
    public String apply(@Nullable Object input) {
      return toClassString(input);
    }
  };

  static Function<Object, String> TO_CLASSES = new NonnullFunction<Object, String>() {
    @Nonnull
    @Override
    public String applyNonnull(@Nonnull Object input) {
      return input.getClass().getCanonicalName();
    }
  };

  public static void assertNotEndsWith(final String expected, String actual) {
    try {
      Assert.assertThat(actual, not(endsWith(expected)));
    } catch (AssertionError e) {
      throw new ComparisonFailure(e.getMessage().trim(), expected, actual);
    }
  }
}
