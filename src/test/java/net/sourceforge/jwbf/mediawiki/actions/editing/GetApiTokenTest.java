package net.sourceforge.jwbf.mediawiki.actions.editing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import net.sourceforge.jwbf.TestHelper;
import net.sourceforge.jwbf.core.actions.ParamTuple;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.mediawiki.actions.editing.GetApiToken.Intoken;
import org.junit.Test;

public class GetApiTokenTest {

  private GetApiToken testee;

  @Test
  public void testProcessReturningText() {
    // GIVEN
    testee = new GetApiToken(Intoken.EDIT, "test");
    String json = TestHelper.anyWikiResponse("intoken.json");

    // WHEN
    testee.processReturningText(json, testee.popAction());

    // THEN
    ParamTuple<String> encodedToken =
        new ParamTuple<String>("token", "e0691d5329779f0c01b1b286cd44a278%2B%5C");
    assertEquals(encodedToken, testee.get().urlEncodedToken());
    ParamTuple<String> token =
        new ParamTuple<String>("token", "e0691d5329779f0c01b1b286cd44a278+\\");
    assertEquals(token, testee.get().token());
  }

  @Test(expected = NullPointerException.class)
  public void testProcessReturningText_requestMissmatch() {
    testee = new GetApiToken(Intoken.DELETE, "test");
    String json = TestHelper.anyWikiResponse("intoken.json");
    testee.processReturningText(json, testee.popAction());
  }

  @Test
  public void testGetToken() {
    // GIVEN
    testee = new GetApiToken(Intoken.BLOCK, "test");

    try {
      // WHEN
      testee.get().token();
      fail();
    } catch (IllegalArgumentException e) {
      // THEN
      assertEquals("The argument 'token' is missing", e.getMessage());
    }
  }

  @Test
  public void testGetNextMessage() {
    // GIVEN
    String title = "test";
    testee = new GetApiToken(Intoken.MOVE, title);

    // WHEN
    assertTrue(testee.hasMoreActions());
    HttpAction first = testee.popAction();

    // THEN
    assertFalse(testee.hasMoreActions());
    assertEquals("/api.php?action=query&format=json&intoken=move&prop=info&titles=" + title,
        first.getRequest());

  }

}
