package net.sourceforge.jwbf.mediawiki.actions.editing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import net.sourceforge.jwbf.TestHelper;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.mediawiki.actions.util.ApiException;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import net.sourceforge.jwbf.mediawiki.contentRep.SimpleFile;

public class FileUploadTest {

  @Test
  public void testException() {
    // GIVEN
    MediaWikiBot bot = mock(MediaWikiBot.class);
    SimpleFile simpleFile = mock(SimpleFile.class);
    when(simpleFile.isFile()).thenReturn(true);
    when(simpleFile.exists()).thenReturn(true);
    when(simpleFile.canRead()).thenReturn(true);
    when(simpleFile.getPath()).thenReturn("any");
    when(simpleFile.getTitle()).thenReturn("any");
    when(bot.isLoggedIn()).thenReturn(true);
    FileUpload testee = new FileUpload(simpleFile, bot);
    HttpAction action = mock(HttpAction.class);

    String xml = TestHelper.anyWikiResponse("uploadError.xml");

    try {
      // WHEN
      testee.processReturningText(xml, action);
      fail();
    } catch (ApiException e) {
      // THEN
      assertEquals(
          "API ERROR CODE: mustposttoken VALUE: The 'token' parameter was found "
              + //
              "in the query string, but must be in the POST body",
          e.getMessage());
    }
  }
}
