package net.sourceforge.jwbf.mediawiki.actions.editing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import net.sourceforge.jwbf.TestHelper;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.mediawiki.actions.editing.GetApiToken.Intoken;
import org.junit.Test;

public class GetApiTokenTest {

  private GetApiToken testee;

  @Test
  public void testProcessReturningText() {
    // GIVEN
    testee = new GetApiToken(Intoken.EDIT, "test");
    String xml = TestHelper.anyWikiResponse("intoken.xml");

    // WHEN
    testee.processReturningText(xml, testee.getNextMessage());

    // THEN
    assertEquals("e0691d5329779f0c01b1b286cd44a278+\\", testee.getToken());

  }

  @Test
  public void testProcessReturningText_requestMissmatch() {
    // GIVEN
    testee = new GetApiToken(Intoken.DELETE, "test");
    String xml = TestHelper.anyWikiResponse("intoken.xml");

    // WHEN
    try {
      testee.processReturningText(xml, testee.getNextMessage());
      fail();
    } catch (NullPointerException e) {
      // THEN
      assertEquals("no attribute found for key: deletetoken", e.getMessage());
    }

  }

  @Test
  public void testGetToken() {
    // GIVEN
    testee = new GetApiToken(Intoken.BLOCK, "test");

    try {
      // WHEN
      testee.getToken();
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
    assertTrue(testee.hasMoreMessages());
    HttpAction first = testee.getNextMessage();

    // THEN
    assertFalse(testee.hasMoreMessages());
    assertEquals("/api.php?action=query&format=xml&intoken=move&prop=info&titles=" + title,
        first.getRequest());
    assertNull(testee.getNextMessage());
  }

}
