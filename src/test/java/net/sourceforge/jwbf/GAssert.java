package net.sourceforge.jwbf;

import static org.junit.Assert.fail;

import javax.annotation.Nullable;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
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

  public static void assertEquals(ImmutableMap<?, ?> expected,
      ImmutableMap<?, ?> actual) {
    ImmutableList<?> expectedTuples = sortedCopy(expected.entrySet());
    ImmutableList<?> actualTuples = sortedCopy(actual.entrySet());
    assertEquals(expectedTuples, actualTuples);
  }

  public static void assertEquals(ImmutableList<String> expected, Set<String> actual) {
    assertEquals(expected, sortedCopy(actual));
  }

  public static void assertEquals(ImmutableList<?> expected, ImmutableList<?> actual) {
    try {
      Assert.assertEquals(expected, actual);
    } catch (AssertionError e) {
      Joiner newlineJoiner = Joiner.on("\n");
      Assert.assertEquals(newlineJoiner.join(expected), newlineJoiner.join(actual));
      throw e;
    }
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

}
