package net.sourceforge.jwbf;

import static org.junit.Assert.fail;

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
import org.junit.Assert;

public class GAssert {

  public static final int MINIMUM_LENGTH = 1;

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

      if (actualTypes.equals(expectedTypes)) {
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
    ImmutableList<String> parts = FluentIterable.from(list).transform(toString).toList();
    return Joiner.on("\n").join(parts);
  }

  public static void assertStartsWith(final String expected, final String actual) {

    Function<String, String> function = new Function<String, String>() {

      @Nullable
      @Override
      public String apply(@Nullable String actual) {
        return actual.substring(0, expected.length());
      }
    };
    partialAssert(expected, actual, function);

  }

  public static void assertEndsWith(final String expected, final String actual) {
    Function<String, String> function = new Function<String, String>() {

      @Nullable
      @Override
      public String apply(@Nullable String actual) {
        return actual.substring(actual.length() - expected.length(), actual.length());
      }
    };
    partialAssert(expected, actual, function);

  }

  static void partialAssert(String expected, String actual, Function<String, String> function) {
    if (actual != null) {
      if (expected.length() <= MINIMUM_LENGTH) {
        fail("expected value: \"" + expected + "\" is too short");
      } else if (actual.length() > expected.length()) {
        Assert.assertEquals(expected, function.apply(actual));
      } else {
        Assert.assertEquals(expected, actual);
      }
    } else {
      Assert.assertEquals(expected, actual);
    }
  }

  static Function<Object, String> TO_DETAIL_STRING = new Function<Object, String>() {
    @Nullable
    @Override
    public String apply(@Nullable Object input) {
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

  static Function<Object, String> TO_TYPE_STRING = new Function<Object, String>() {
    @Nullable
    @Override
    public String apply(@Nullable Object input) {
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

  static Function<Object, String> TO_CLASSES = new Function<Object, String>() {
    @Nullable
    @Override
    public String apply(@Nullable Object input) {
      return input.getClass().getCanonicalName();
    }
  };

}
