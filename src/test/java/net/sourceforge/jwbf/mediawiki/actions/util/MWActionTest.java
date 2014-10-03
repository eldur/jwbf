package net.sourceforge.jwbf.mediawiki.actions.util;

import com.google.common.collect.ImmutableList;
import net.sourceforge.jwbf.GAssert;
import org.junit.Test;

public class MWActionTest {

  @Test
  public void testNullSafeCopyOf() {

    ImmutableList<Integer> ints = MWAction.nullSafeCopyOf(new int[] { 1 });
    GAssert.assertEquals(ImmutableList.of(1), ints);

    ImmutableList<Integer> empty = MWAction.nullSafeCopyOf(null);
    GAssert.assertEquals(ImmutableList.<Integer>of(), empty);
  }
}
