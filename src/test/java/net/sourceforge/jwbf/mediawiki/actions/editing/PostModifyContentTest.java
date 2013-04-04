package net.sourceforge.jwbf.mediawiki.actions.editing;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Sets;

public class PostModifyContentTest {

  private PostModifyContent action;

  @Before
  public void before() {
    action = Mockito.mock(PostModifyContent.class, Mockito.CALLS_REAL_METHODS);
  }

  @Test
  public void testProcessReturningText() {
    action.processAllReturningText("error");
  }

  @Test
  public void testIsIntersectionEmpty() {
    assertTrue(action.isIntersectionEmpty(null, null));
    Set<String> a = Sets.newHashSet();
    Set<String> b = Sets.newHashSet();
    assertTrue(a.containsAll(b));
    assertTrue(action.isIntersectionEmpty(a, b));
    assertTrue(action.isIntersectionEmpty(b, a));

    b.add("a");
    assertTrue(action.isIntersectionEmpty(a, b));
    b.add("c");
    assertTrue(action.isIntersectionEmpty(a, b));
    assertTrue(action.isIntersectionEmpty(b, a));
    a.add("a");
    a.add("b");

    assertFalse(action.isIntersectionEmpty(a, b));
    assertFalse(action.isIntersectionEmpty(b, a));
    assertTrue(a.size() > 1);
    assertTrue(b.size() > 1);
  }

}
