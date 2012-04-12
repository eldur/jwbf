package net.sourceforge.jwbf.mediawiki.bots;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jwbf.core.actions.ContentProcessable;
import net.sourceforge.jwbf.core.actions.HttpActionClient;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.actions.util.ProcessException;
import net.sourceforge.jwbf.core.bots.HttpBot;
import net.sourceforge.jwbf.core.bots.WikiBot;
import net.sourceforge.jwbf.core.bots.util.JwbfException;
import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.core.contentRep.ContentAccessable;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.core.contentRep.Userinfo;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.editing.GetRevision;
import net.sourceforge.jwbf.mediawiki.actions.editing.PostDelete;
import net.sourceforge.jwbf.mediawiki.actions.editing.PostModifyContent;
import net.sourceforge.jwbf.mediawiki.actions.login.PostLogin;
import net.sourceforge.jwbf.mediawiki.actions.login.PostLoginOld;
import net.sourceforge.jwbf.mediawiki.actions.meta.GetUserinfo;
import net.sourceforge.jwbf.mediawiki.actions.meta.GetVersion;
import net.sourceforge.jwbf.mediawiki.actions.meta.Siteinfo;
import net.sourceforge.jwbf.mediawiki.actions.util.VersionException;
import net.sourceforge.jwbf.mediawiki.contentRep.LoginData;

/**
 * This class helps you to interact with each <a href="http://www.mediawiki.org"
 * target="_blank">MediaWiki</a>. This class offers a <b>basic set</b> of
 * methods which are defined in the package net.sourceforge.jwbf.actions.mw.*
 * 
 * 
 * How to use:
 * 
 * <pre>
 * MediaWikiBot b = new MediaWikiBot(&quot;http://yourwiki.org&quot;);
 * b.login(&quot;Username&quot;, &quot;Password&quot;);
 * System.out.println(b.readContent(&quot;Main Page&quot;).getText());
 * </pre>
 * 
 * <b>How to find the correct wikiurl</b>
 * <p>
 * The correct wikiurl is sometimes not easy to find, because some wikiadmis
 * uses url rewriting rules. In this cases the correct url is the one, which
 * gives you access to <code>api.php</code>. E.g. Compare
 * 
 * <pre>
 * http://www.mediawiki.org/wiki/api.php
 * http://www.mediawiki.org/w/api.php
 * </pre>
 * 
 * Thus the correct wikiurl is: <code>http://www.mediawiki.org/w/</code>
 * </p>
 * 
 * @author Thomas Stock
 * @author Tobias Knerr
 * @author Justus Bisser
 * 
 * @see MediaWikiAdapterBot
 * 
 */
@Slf4j
public class MediaWikiBot implements WikiBot {

  private LoginData login = null;

  private Version version = null;
  private Userinfo ui = null;

  private boolean loginChangeUserInfo = false;
  private boolean loginChangeVersion = false;
  private boolean useEditApi = true;

  @Inject
  private HttpBot bot;

  /**
   * These chars are not allowed in article names.
   */
  public static final char[] INVALID_LABEL_CHARS = "[]{}<>|".toCharArray();
  private static final int DEFAULT_READ_PROPERTIES = GetRevision.CONTENT
      | GetRevision.COMMENT | GetRevision.USER | GetRevision.TIMESTAMP
      | GetRevision.IDS | GetRevision.FLAGS;

  private static final Set<String> emptySet = Collections
      .unmodifiableSet(new HashSet<String>());

  /**
   * @param u
   *          wikihosturl like "http://www.mediawiki.org/w/"
   */
  public MediaWikiBot(final URL u) {
    bot = new HttpBot(u);
  }

  /**
   * 
   * @param client
   *          a
   */
  public MediaWikiBot(final HttpActionClient client) {
    bot = new HttpBot(client);
  }

  /**
   * @param url
   *          wikihosturl like "http://www.mediawiki.org/w/"
   * @throws MalformedURLException
   *           if param url does not represent a well-formed url
   */

  public MediaWikiBot(final String url) {
    bot = new HttpBot(url);
    if (!(url.endsWith(".php") || url.endsWith("/"))) {
      throw new IllegalArgumentException("(" + url
          + ") url must end with slash or .php");
    }
    bot.setConnection(url);
  }

  /**
   * 
   * @param url
   *          wikihosturl like "http://www.mediawiki.org/w/"
   * @param testHostReachable
   *          if true, test if host reachable
   * @throws IOException
   *           a
   */
  public MediaWikiBot(URL url, boolean testHostReachable) throws IOException {
    bot = new HttpBot(url);
    if (testHostReachable) {
      bot.getPage(url.toExternalForm());
    }
    bot.setConnection(url);
  }

  /**
   * Performs a Login.
   * 
   * @param username
   *          the username
   * @param passwd
   *          the password
   * @param domain
   *          login domain (Special for LDAPAuth extention to authenticate
   *          against LDAP users)
   * @throws ActionException
   *           on problems with http, cookies and io
   * @see PostLogin
   * @see PostLoginOld
   */
  public void login(final String username, final String passwd,
      final String domain) throws ActionException {
    try {
      LoginData login = new LoginData();
      switch (getVersion()) {
        case MW1_09:
        case MW1_10:
        case MW1_11:
        case MW1_12:
          performAction(new PostLoginOld(username, passwd, domain, login));
          break;

        default:
          performAction(new PostLogin(username, passwd, domain, login));
          break;
      }

      this.login = login;
      loginChangeUserInfo = true;
      if (getVersion() == Version.UNKNOWN) {
        loginChangeVersion = true;
      }
    } catch (ProcessException e) {
      throw new ActionException(e.getLocalizedMessage());
    } catch (RuntimeException e) {
      throw new ActionException(e);
    }

  }

  /**
   * Performs a Login. Actual old cookie login works right, because is pending
   * on {@link #writeContent(ContentAccessable)}
   * 
   * @param username
   *          the username
   * @param passwd
   *          the password
   * @throws ActionException
   *           on problems with http, cookies and io
   * @see PostLogin
   * @see PostLoginOld
   */
  public void login(final String username, final String passwd)
      throws ActionException {

    login(username, passwd, null);
  }

  /**
   * 
   * @param name
   *          of article in a mediawiki like "Main Page"
   * @param properties
   *          {@link GetRevision}
   * @return a content representation of requested article, never null
   * @throws ActionException
   *           on problems with http, cookies and io
   * @throws ProcessException
   *           on access problems
   * @see GetRevision
   */
  public synchronized Article getArticle(final String name, final int properties)
      throws ActionException, ProcessException {
    return new Article(this, readData(name, properties));
  }

  /**
   * {@inheritDoc}
   */
  public synchronized SimpleArticle readData(final String name,
      final int properties) throws ActionException, ProcessException {

    GetRevision ac = new GetRevision(getVersion(), name, properties);

    performAction(ac);

    return ac.getArticle();

  }

  /**
   * {@inheritDoc}
   */
  public SimpleArticle readData(String name) throws ActionException,
      ProcessException {

    return readData(name, DEFAULT_READ_PROPERTIES);
  }

  /**
   * 
   * @param name
   *          of article in a mediawiki like "Main Page"
   * @return a content representation of requested article, never null
   * @throws ActionException
   *           on problems with http, cookies and io
   * @throws ProcessException
   *           on access problems
   * @see GetRevision
   */
  public synchronized Article getArticle(final String name)
      throws ActionException, ProcessException {
    return getArticle(name, DEFAULT_READ_PROPERTIES);

  }

  /**
   * 
   * @param a
   *          a
   * @throws ActionException
   *           on problems with http, cookies and io
   * @throws ProcessException
   *           on access problems
   * @see PostModifyContent
   * 
   */
  public synchronized void writeContent(final SimpleArticle simpleArticle)
      throws ActionException, ProcessException {
    if (!isLoggedIn()) {
      throw new ActionException("Please login first");
    }

    for (char invChar : INVALID_LABEL_CHARS) { // FIXME Replace with a REGEX
      if (simpleArticle.getTitle().contains(invChar + "")) {
        throw new ActionException("Invalid character in label\""
            + simpleArticle.getTitle() + "\" : \"" + invChar + "\"");
      }
    }

    performAction(new PostModifyContent(this, simpleArticle));
    if (simpleArticle.getText().trim().length() < 1)
      throw new RuntimeException("Content is empty, still written");
  }

  /**
   * 
   * @return true if
   */
  public final boolean isLoggedIn() {

    if (login != null) {
      return login.isLoggedIn();
    }
    return false;

  }

  /**
   * 
   * @return a
   * @throws ActionException
   *           on problems with http, cookies and io
   * @throws ProcessException
   *           on access problems
   */
  public Userinfo getUserinfo() throws ActionException, ProcessException {
    log.debug("get userinfo");
    if (ui == null || loginChangeUserInfo) {
      GetUserinfo a;
      try {
        a = new GetUserinfo(getVersion());

        performAction(a);
        ui = a;
        loginChangeUserInfo = false;
      } catch (VersionException e) {
        if (login != null && login.getUserName().length() > 0) {
          ui = new Userinfo() {

            public String getUsername() {
              return login.getUserName();
            }

            public Set<String> getRights() {
              return emptySet;
            }

            public Set<String> getGroups() {
              return emptySet;
            }
          };
        } else {
          ui = new Userinfo() {

            public String getUsername() {
              return "unknown";
            }

            public Set<String> getRights() {
              return emptySet;
            }

            public Set<String> getGroups() {
              return emptySet;
            }
          };
        }
      }

    }
    return ui;
  }

  /**
   * @param title
   *          to delete
   * @throws ActionException
   *           if
   * @throws ProcessException
   *           if
   */
  public void delete(String title) throws ActionException, ProcessException {

    performAction(new PostDelete(this, title));
  }

  public synchronized String performAction(ContentProcessable a) {
    if (a.isSelfExecuter()) {
      throw new ActionException("this is a selfexcecuting action, "
          + "please do not perform this action manually");
    }
    return bot.performAction(a);
  }

  /**
   * 
   * @return the
   * @throws IllegalStateException
   *           if no version was found.
   * @see #getSiteinfo()
   */
  @Nonnull
  public final Version getVersion() throws RuntimeException {
    if (version == null || loginChangeVersion) {
      try {
        GetVersion gs = new GetVersion();
        performAction(gs);

        version = gs.getVersion();
        loginChangeVersion = false;
      } catch (JwbfException e) {
        log.error(e.getClass().getName() + e.getLocalizedMessage());
        throw new IllegalStateException(e.getLocalizedMessage());
      }
      log.debug("Version is: " + version.name());

    }
    return version;
  }

  /**
   * 
   * @return a
   * @throws ActionException
   *           on problems with http, cookies and io
   * @see Siteinfo
   */
  public Siteinfo getSiteinfo() throws ActionException {

    Siteinfo gs = null;
    try {
      gs = new Siteinfo();
      performAction(gs);
    } catch (ProcessException e) {
      e.printStackTrace();
    }

    return gs;

  }

  /**
   * 
   * @return the
   */
  public final boolean isEditApi() {
    return useEditApi;
  }

  /**
   * Set to false, to force editing without the API.
   * 
   * @param useEditApi
   *          if
   * 
   */
  public final void useEditApi(boolean useEditApi) {
    this.useEditApi = useEditApi;
  }

  /**
   * {@inheritDoc}
   */
  public final String getWikiType() {
    return MediaWiki.class.getName() + " " + getVersion();
  }

  public String getHostUrl() {
    return bot.getHostUrl();
  }

}
