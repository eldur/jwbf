package net.sourceforge.jwbf.mediawiki.bots;

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
import net.sourceforge.jwbf.mediawiki.actions.meta.GetUserinfo;
import net.sourceforge.jwbf.mediawiki.actions.meta.GetVersion;
import net.sourceforge.jwbf.mediawiki.actions.meta.Siteinfo;
import net.sourceforge.jwbf.mediawiki.actions.util.VersionException;
import net.sourceforge.jwbf.mediawiki.contentRep.LoginData;

/**
 * This class helps you to interact with each <a href="http://www.mediawiki.org"
 * target="_blank">MediaWiki</a>. This class offers a <b>basic set</b> of methods which are defined
 * in the package net.sourceforge.jwbf.actions.mw.*
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
 * The correct wikiurl is sometimes not easy to find, because some wikiadmis uses url rewriting
 * rules. In this cases the correct url is the one, which gives you access to <code>api.php</code>.
 * E.g. Compare
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
  private static final int DEFAULT_READ_PROPERTIES = GetRevision.CONTENT | GetRevision.COMMENT
      | GetRevision.USER | GetRevision.TIMESTAMP | GetRevision.IDS | GetRevision.FLAGS;

  private static final Set<String> emptySet = Collections.unmodifiableSet(new HashSet<String>());

  /**
   * use this constructor, if you want to work with IoC.
   * 
   */
  public MediaWikiBot() {

  }

  /**
   * @param u
   *          wikihosturl like "http://www.mediawiki.org/w/"
   */
  public MediaWikiBot(final URL u) {
    bot = new HttpBot(u);
  }

  public MediaWikiBot(final HttpActionClient client) {
    bot = new HttpBot(client);
  }

  /**
   * @param url
   *          wikihosturl like "http://www.mediawiki.org/w/"
   * @throws IllegalArgumentException
   *           if param url does not represent a well-formed url
   */

  public MediaWikiBot(final String url) {
    bot = new HttpBot(url);
    if (!(url.endsWith(".php") || url.endsWith("/"))) {
      throw new IllegalArgumentException("(" + url + ") url must end with slash or .php");
    }
    getBot().setClient(url);
  }

  /**
   * 
   * @param url
   *          wikihosturl like "http://www.mediawiki.org/w/"
   * @param testHostReachable
   *          if true, test if host reachable
   */
  public MediaWikiBot(URL url, boolean testHostReachable) {
    bot = new HttpBot(url);
    if (testHostReachable) {
      getBot().getPage(url.toExternalForm());
    }
    getBot().setClient(url);
  }

  /**
   * Performs a Login.
   * 
   * @param username
   *          the username
   * @param passwd
   *          the password
   * @param domain
   *          login domain (Special for LDAPAuth extention to authenticate against LDAP users)
   * @see PostLogin
   * @see PostLoginOld
   */
  public void login(final String username, final String passwd, final String domain) {
    LoginData login = new LoginData();
    performAction(new PostLogin(username, passwd, domain, login));

    this.login = login;
    loginChangeUserInfo = true;
    if (getVersion() == Version.UNKNOWN) {
      loginChangeVersion = true;
    }

  }

  /**
   * TODO mv doc
   * 
   * Performs a Login. Actual old cookie login works right, because is pending on
   * {@link #writeContent(ContentAccessable)}
   * 
   * @param username
   *          the username
   * @param passwd
   *          the password
   * @see PostLogin
   * @see PostLoginOld
   */
  public void login(final String username, final String passwd) {

    login(username, passwd, null);
  }

  /**
   * 
   * @param name
   *          of article in a mediawiki like "Main Page"
   * @param properties
   *          {@link GetRevision}
   * @return a content representation of requested article, never null
   * @see GetRevision
   */
  public synchronized Article getArticle(final String name, final int properties) {
    return new Article(this, readData(name, properties));
  }

  /**
   * {@inheritDoc}
   */
  public synchronized SimpleArticle readData(final String name, final int properties) {

    GetRevision ac = new GetRevision(getVersion(), name, properties);

    performAction(ac);

    return ac.getArticle();

  }

  /**
   * {@inheritDoc}
   */
  public SimpleArticle readData(String name) {

    return readData(name, DEFAULT_READ_PROPERTIES);
  }

  /**
   * 
   * @param name
   *          of article in a mediawiki like "Main Page"
   * @return a content representation of requested article, never null
   * @see GetRevision
   */
  public synchronized Article getArticle(final String name) {
    return getArticle(name, DEFAULT_READ_PROPERTIES);

  }

  /**
   * {@inheritDoc}
   */
  public synchronized void writeContent(final SimpleArticle simpleArticle) {
    if (!isLoggedIn()) {
      throw new ActionException("Please login first");
    }

    for (char invChar : INVALID_LABEL_CHARS) { // FIXME Replace with a REGEX
      if (simpleArticle.getTitle().contains(invChar + "")) {
        throw new ActionException("Invalid character in label\"" + simpleArticle.getTitle()
            + "\" : \"" + invChar + "\"");
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
   * {@inheritDoc}
   */
  public Userinfo getUserinfo() {
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
   * {@inheritDoc}
   */
  public void delete(String title) {
    performAction(new PostDelete(this, title));
  }

  /**
   * deletes an article with a reason
   */
  public void delete(String title, String reason) {
    performAction(new PostDelete(this, title, reason));
  }

  public synchronized String performAction(ContentProcessable a) {
    if (a.isSelfExecuter()) {
      throw new ActionException("this is a selfexcecuting action, "
          + "please do not perform this action manually");
    }
    return getBot().performAction(a);
  }

  private HttpBot getBot() {
    if (bot == null) {
      throw new IllegalStateException("please use another constructor or inject "
          + HttpBot.class.getCanonicalName());
    }
    return bot;
  }

  /**
   * 
   * @return the
   * @throws IllegalStateException
   *           if no version was found.
   * @see #getSiteinfo()
   */
  @Nonnull
  public Version getVersion() throws IllegalStateException {
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
   * 
   *         on problems with http, cookies and io
   * @see Siteinfo
   */
  @Nonnull
  public Siteinfo getSiteinfo() {

    Siteinfo gs = null;
    try {
      gs = new Siteinfo();
      performAction(gs);
    } catch (ProcessException e) {
      log.error("{}", e);
    }

    return gs;

  }

  /**
   * 
   * @return the
   */
  public boolean isEditApi() {
    return useEditApi;
  }

  /**
   * @param useEditApi
   *          Set to false, to force editing without the API.
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
    return getBot().getHostUrl();
  }

}
