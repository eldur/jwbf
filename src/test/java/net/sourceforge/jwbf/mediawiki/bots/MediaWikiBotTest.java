package net.sourceforge.jwbf.mediawiki.bots;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import net.sourceforge.jwbf.core.actions.HttpActionClient;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.editing.PostModifyContent;
import net.sourceforge.jwbf.mediawiki.actions.login.PostLogin;
import net.sourceforge.jwbf.mediawiki.actions.meta.GetVersion;
import net.sourceforge.jwbf.mediawiki.actions.meta.Siteinfo;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class MediaWikiBotTest {

  private MediaWikiBot testee;

  private HttpActionClient client;

  @Before
  public void before() {
    client = mock(HttpActionClient.class);
  }


  @Test
  public void testInitWithBuilder() {
    // GIVEN
    String url = "http://localhost/";

    // WHEN
    testee = new MediaWikiBot(HttpActionClient.of(url));

    // THEN
    assertNotNull(testee);
  }

  @Test
  public void testGetVersion_fail() {
    // GIVEN
    when(client.performAction(Mockito.any(GetVersion.class))) //
        .thenThrow(new IllegalStateException("fail"));
    testee = new MediaWikiBot(client);

    try {
      // WHEN
      testee.getVersion();
      fail();
    } catch (IllegalStateException e) {
      // THEN
      assertEquals("fail", e.getMessage());
    }
  }

  @Test
  public void testGetVersion() {
    // GIVEN
    when(client.performAction(Mockito.any(GetVersion.class))).thenReturn("");
    testee = new MediaWikiBot(client);

    // WHEN
    Version version = testee.getVersion();

    // THEN
    assertEquals(Version.UNKNOWN, version);
  }

  @Test
  public void testWriteContent_not_logged_in() {
    // GIVEN
    testee = new MediaWikiBot(client);

    try {
      // WHEN
      testee.writeContent(null);
    } catch (ActionException e) {
      // THEN
      assertEquals("Please login first", e.getMessage());
    }
  }

  @Test
  public void testWriteContent_null() {
    // GIVEN
    mockValidLogin("username", client);
    testee = new MediaWikiBot(client);
    testee.login("username", "pw");

    try {
      // WHEN
      testee.writeContent(null);
      fail();
    } catch (NullPointerException npe) {
      // THEN
      assertEquals("content must not be null", npe.getMessage());
    }
  }

  @Test
  public void testWriteContent_noTitle() {
    // GIVEN
    mockValidLogin("username", client);
    testee = new MediaWikiBot(client);
    testee.login("username", "pw");

    try {
      // WHEN
      testee.writeContent(new SimpleArticle());
      fail();
    } catch (ActionException npe) {
      // THEN
      assertEquals("imposible request, no title", npe.getMessage());
    }
  }

  @Test
  @Deprecated
  public void testWriteContent_strageRuntimeException() {
    // GIVEN
    mockValidLogin("username", client);
    testee = new MediaWikiBot(client);
    testee.login("username", "pw");

    // WHEN
    SimpleArticle simpleArticle = new SimpleArticle();
    simpleArticle.setTitle("Test");
    try {
      testee.writeContent(simpleArticle);
      fail();
    } catch (RuntimeException e) {
      // THEN
      assertEquals("Content is empty, still written", e.getMessage());
      verify(client).performAction(isA(PostModifyContent.class));
    }
  }

  @Test
  public void testWriteContent() {
    // GIVEN
    mockValidLogin("username", client);
    testee = new MediaWikiBot(client);
    testee.login("username", "pw");

    // WHEN
    SimpleArticle simpleArticle = new SimpleArticle();
    simpleArticle.setTitle("Test");
    simpleArticle.setText("Test");
    testee.writeContent(simpleArticle);

    // THEN
    verify(client).performAction(isA(PostModifyContent.class));
  }

  @Test
  public void testIsLoggedIn() {
    // GIVEN
    String username = "username";
    mockValidLogin(username, client);
    testee = new MediaWikiBot(client);
    testee.login(username, "password");

    // WHEN / THEN
    assertTrue(testee.isLoggedIn());
  }

  @Test
  public void testIsNotLoggedIn() {
    // GIVEN
    testee = new MediaWikiBot(client);

    // WHEN / THEN
    assertFalse(testee.isLoggedIn());
  }

  @Test
  public void testCheckTitle_A() {
    try {
      MediaWikiBot.checkTitle("A[B");
    } catch (ActionException e) {
      assertEquals("Invalid character \"[\" in label \"A[B\"", e.getMessage());
    }

  }

  @Test
  public void testCheckTitle_B() {
    try {
      MediaWikiBot.checkTitle("AA|B");
    } catch (ActionException e) {
      assertEquals("Invalid character \"|\" in label \"AA|B\"", e.getMessage());
    }
  }

  @Test
  public void testGetSiteInfo() {
    // GIVEN
    testee = new MediaWikiBot(client);

    // WHEN
    Siteinfo siteinfo = testee.getSiteinfo();

    // THEN
    assertEquals("", siteinfo.getMainpage());
  }


  @Test
  public void testGetWikiType() {
    // GIVEN
    testee = new MediaWikiBot(client);

    // WHEN / THEN
    assertEquals("MediaWiki UNKNOWN", testee.getWikiType());
  }


  private void mockValidLogin(final String username, HttpActionClient mockClient) {
    doAnswer(new Answer<Void>() {
      public Void answer(InvocationOnMock invocation)
          throws Throwable {
        Object[] args = invocation.getArguments();
        if (args[0] instanceof PostLogin) {
          PostLogin out = (PostLogin) args[0];
          out.getLoginData().setup(username, true);
          // XXX ^ do not mutate this value
        }
        return null;
      }
    }).when(mockClient).performAction(isA(PostLogin.class));
  }
}
