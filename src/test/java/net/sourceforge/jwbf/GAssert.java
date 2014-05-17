package net.sourceforge.jwbf;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import org.junit.Assert;

public class GAssert {

  public static void assertEquals(ImmutableList<?> expected, ImmutableList<?> actual) {
    try {
      Assert.assertEquals(expected, actual);
    } catch (AssertionError e) {
      Joiner newlineJoiner = Joiner.on("\n");
      Assert.assertEquals(newlineJoiner.join(expected), newlineJoiner.join(actual));
      throw e;
    }
  }

  public static void assertStartsWith(String expected, String actual) {
    if (actual != null) {
      if (actual.length() > expected.length()) {
        Assert.assertEquals(expected, actual.substring(0, expected.length()));
      } else {
        Assert.assertEquals(expected, actual);
      }
    } else {
      Assert.assertEquals(expected, actual);
    }

  }

}
