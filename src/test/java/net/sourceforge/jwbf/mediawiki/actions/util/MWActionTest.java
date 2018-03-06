package net.sourceforge.jwbf.mediawiki.actions.util;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

import net.sourceforge.jwbf.GAssert;

public class MWActionTest {

  @Test
  public void testNullSafeCopyOf() {

    ImmutableList<Integer> ints = MWAction.nullSafeCopyOf(new int[] {1});
    GAssert.assertEquals(ImmutableList.of(1), ints);

    ImmutableList<Integer> empty = MWAction.nullSafeCopyOf((int[]) null);
    GAssert.assertEquals(ImmutableList.<Integer>of(), empty);

    ImmutableList<String> strings = MWAction.nullSafeCopyOf(new String[] {""});
    GAssert.assertEquals(ImmutableList.of(""), strings);

    ImmutableList<String> emptyStrings = MWAction.nullSafeCopyOf((String[]) null);
    GAssert.assertEquals(ImmutableList.<Integer>of(), emptyStrings);
  }
}
