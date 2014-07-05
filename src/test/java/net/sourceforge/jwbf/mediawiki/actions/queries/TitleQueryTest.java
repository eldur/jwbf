package net.sourceforge.jwbf.mediawiki.actions.queries;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TitleQueryTest {

  @Mock(answer = Answers.CALLS_REAL_METHODS)
  TitleQuery<Object> testee;

  @Test(expected = UnsupportedOperationException.class)
  public void testRemove() {
    testee.remove();
  }
}
