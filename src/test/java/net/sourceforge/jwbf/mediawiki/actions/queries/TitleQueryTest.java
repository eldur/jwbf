package net.sourceforge.jwbf.mediawiki.actions.queries;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TitleQueryTest {

  @Mock
  private TitleQuery<Object> testee;

  @Test
  public void testProcessReturningText() {
    try {
      testee.processReturningText("", null);
      fail();
    } catch (UnsupportedOperationException e) {
      assertEquals(TitleQuery.UOE_MESSAGE, e.getMessage());
    }
  }

  @Test
  public void testProcessAllReturningText() {
    try {
      testee.processAllReturningText("");
      fail();
    } catch (UnsupportedOperationException e) {
      assertEquals(TitleQuery.UOE_MESSAGE, e.getMessage());
    }
  }

}
