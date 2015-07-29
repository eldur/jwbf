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

import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import net.sourceforge.jwbf.GAssert;
import net.sourceforge.jwbf.JWBF;
import net.sourceforge.jwbf.JettyServer;
import net.sourceforge.jwbf.core.actions.ContentProcessable;
import net.sourceforge.jwbf.core.actions.HttpActionClient;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.bots.HttpBot;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.editing.GetRevision;
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
    testee = new MediaWikiBot(client);
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
  public void testInitWithBuilder_and_rechable_check_off() {
    // GIVEN
    String url = "http://localhost/";

    // WHEN
    testee = new MediaWikiBot(JWBF.newURL(url), false);

    // THEN
    assertNotNull(testee);
  }

  @Test
  public void testInitWithBuilder_and_reachable_check() {
    // GIVEN
    try (JettyServer server = new JettyServer().started(JettyServer.echoHandler())) {
      String url = server.getTestUrl();

      // WHEN
      testee = new MediaWikiBot(JWBF.newURL(url), true);

      // THEN
      assertNotNull(testee);
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  @Test
  public void testInitWithBuilder_and_reachable_check_fail() {
    // GIVEN
    String url = "https://notExistingHost/";

    try {
      // WHEN
      testee = new MediaWikiBot(JWBF.newURL(url), true);
      fail();
    } catch (IllegalStateException e) {
      // THEN
      GAssert.assertStartsWith("java.net.UnknownHostException", e.getMessage());
    }
  }

  @Test
  public void testGetVersion_fail() {
    // GIVEN
    when(client.performAction(Mockito.any(GetVersion.class))) //
        .thenThrow(new IllegalStateException("fail"));

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

    // WHEN
    Version version = testee.getVersion();

    // THEN
    assertEquals(Version.UNKNOWN, version);
  }

  @Test
  public void testWriteContent_not_logged_in() {
    // GIVEN

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
    testee.login(username, "password");

    // WHEN / THEN
    assertTrue(testee.isLoggedIn());
  }

  @Test
  public void testIsNotLoggedIn() {
    // GIVEN

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

    // WHEN
    Siteinfo siteinfo = testee.getSiteinfo();

    // THEN
    assertEquals("", siteinfo.getMainpage());
  }

  @Test
  public void testGetWikiType() {
    // GIVEN

    // WHEN / THEN
    assertEquals("MediaWiki UNKNOWN", testee.getWikiType());
  }

  @Test
  public void testReadDataOneTitle() {
    // GIVEN
    final String title = "Test";
    testee = new MediaWikiBot(client) {
      @Override
      public <T extends ContentProcessable> T getPerformedAction(T answer) {
        if (answer instanceof GetRevision) {
          GetRevision mockAnswer = mock(GetRevision.class);
          ImmutableList<SimpleArticle> articles = ImmutableList.of(new SimpleArticle(title));
          when(mockAnswer.asList()).thenReturn(articles);
          when(mockAnswer.getArticle()).thenCallRealMethod();
          return (T) mockAnswer;
        } else if (answer instanceof GetVersion) {
          return answer;
        } else {
          fail(answer.getClass().getCanonicalName());
          return null;
        }
      }
    };

    // WHEN
    SimpleArticle result = testee.readData(title);

    // THEN
    assertEquals(title, result.getTitle());
  }

  @Test
  public void testReadDataTwoTitles() {
    // GIVEN
    String[] titles = { "Test A", "Test B" };
    final ImmutableList<SimpleArticle> articles =
        ImmutableList.of(new SimpleArticle("Test A"), new SimpleArticle("Test B"));
    testee = new MediaWikiBot(client) {
      @Override
      public <T extends ContentProcessable> T getPerformedAction(T answer) {
        if (answer instanceof GetRevision) {
          GetRevision mockAnswer = mock(GetRevision.class);

          when(mockAnswer.asList()).thenReturn(articles);
          return (T) mockAnswer;
        } else if (answer instanceof GetVersion) {
          return answer;
        } else {
          fail(answer.getClass().getCanonicalName());
          return null;
        }
      }
    };

    // WHEN
    ImmutableList<SimpleArticle> result = testee.readData(titles);

    // THEN
    GAssert.assertEquals(articles, result);
  }

  @Test
  public void testReadDataThreeTitles() {
    // GIVEN
    final ImmutableList<SimpleArticle> articles = ImmutableList
        .of(new SimpleArticle("Test A"), new SimpleArticle("Test B"), new SimpleArticle("Test C"));
    ImmutableList<String> titles = ImmutableList.of("Test A", "Test B", "Test C");
    testee = new MediaWikiBot(client) {
      @Override
      public <T extends ContentProcessable> T getPerformedAction(T answer) {
        if (answer instanceof GetRevision) {
          GetRevision mockAnswer = mock(GetRevision.class);
          when(mockAnswer.asList()).thenReturn(articles);
          return (T) mockAnswer;
        } else if (answer instanceof GetVersion) {
          return answer;
        } else {
          fail(answer.getClass().getCanonicalName());
          return null;
        }
      }
    };

    // WHEN
    ImmutableList<SimpleArticle> result = testee.readData(titles);

    // THEN
    GAssert.assertEquals(articles, result);
  }

  private void mockValidLogin(final String username, HttpActionClient mockClient) {
    doAnswer(new Answer<Void>() {
      @Override
      public Void answer(InvocationOnMock invocation) throws Throwable {
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

  @Test(expected = IllegalArgumentException.class)
  public final void wikiurlMustEndWithPhpOrSlash() {

    // GIVEN
    String wikiUrl = "https://anyWikiurl.com/wiki";
    GAssert.assertNotEndsWith(".php", wikiUrl);
    GAssert.assertNotEndsWith("/", wikiUrl);

    // WHEN
    new MediaWikiBot(wikiUrl);
  }

  @Test
  public void testInitIoC() {
    // GIVEN
    Injector injector = Guice.createInjector(new AbstractModule() {
      @Override
      protected void configure() {
        bind(HttpBot.class).toInstance(new HttpBot("http://192.0.2.2/"));
      }
    });

    // WHEN
    MediaWikiBot instance = injector.getInstance(MediaWikiBot.class);

    // THEN
    instance.bot(); // no exception
  }

}
