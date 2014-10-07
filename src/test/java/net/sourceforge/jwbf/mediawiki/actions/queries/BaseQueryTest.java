package net.sourceforge.jwbf.mediawiki.actions.queries;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BaseQueryTest {

  @Spy
  BaseQuery<Object> testee = new BaseQuery<Object>(Mockito.mock(MediaWikiBot.class)) {
    @Override
    protected Iterator<Object> copy() {
      return ImmutableList.of().iterator();
    }

    @Override
    protected HttpAction prepareNextRequest() {
      return null;
    }

    @Override
    protected ImmutableList<Object> parseElements(String s) {
      return ImmutableList.of();
    }

    @Override
    protected Optional<String> parseHasMore(String s) {
      return Optional.absent();
    }
  };

  @Test(expected = UnsupportedOperationException.class)
  public void testRemove() {
    testee.remove();
  }

  @Test(expected = CloneNotSupportedException.class)
  public void testClone() throws CloneNotSupportedException {
    testee.clone();
  }

  @Test
  public void testGetCopyOf_empty() {

    // GIVEN / WHEN
    ImmutableList<Object> of = testee.getCopyOf(0);

    // THEN
    assertTrue(of.isEmpty());
  }

  @Test
  public void testGetCopyOf_too_much() {

    // GIVEN / WHEN
    ImmutableList<Object> of = testee.getCopyOf(10);

    // THEN
    assertTrue(of.isEmpty());
  }

  @Test
  public void testGetCopyOf_negative() {
    try {
      // GIVEN / WHEN
      testee.getCopyOf(-1);
      fail();
    } catch (IllegalArgumentException e) {
      // THEN
      assertEquals("limit is negative", e.getMessage());
    }
  }

  public static String emptyXml() {
    return "<empty />";
  }

}
