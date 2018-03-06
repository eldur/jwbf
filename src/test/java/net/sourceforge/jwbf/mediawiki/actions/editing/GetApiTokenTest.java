package net.sourceforge.jwbf.mediawiki.actions.editing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import net.sourceforge.jwbf.TestHelper;
import net.sourceforge.jwbf.core.actions.ParamTuple;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.mediawiki.actions.editing.GetApiToken.Intoken;

public class GetApiTokenTest {

  private GetApiToken testee;

  @Test
  public void testProcessReturningText() {
    // GIVEN
    testee = new GetApiToken(Intoken.EDIT, "test");
    String xml = TestHelper.anyWikiResponse("intoken.xml");

    // WHEN
    testee.processReturningText(xml, testee.popAction());

    // THEN
    ParamTuple<String> encodedToken =
        new ParamTuple("token", "e0691d5329779f0c01b1b286cd44a278%2B%5C");
    assertEquals(encodedToken, testee.get().urlEncodedToken());
    ParamTuple<String> token = new ParamTuple("token", "e0691d5329779f0c01b1b286cd44a278+\\");
    assertEquals(token, testee.get().token());
  }

  @Test
  public void testProcessReturningText_requestMissmatch() {
    // GIVEN
    testee = new GetApiToken(Intoken.DELETE, "test");
    String xml = TestHelper.anyWikiResponse("intoken.xml");

    // WHEN
    try {
      testee.processReturningText(xml, testee.popAction());
      fail();
    } catch (NullPointerException e) {
      // THEN
      assertEquals("attribute value for key: deletetoken must not be null", e.getMessage());
    }
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
    assertEquals(
        "/api.php?action=query&format=xml&intoken=move&prop=info&titles=" + title,
        first.getRequest());
  }
}
