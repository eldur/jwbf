package net.sourceforge.jwbf.mediawiki.bots;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.sourceforge.jwbf.core.actions.ContentProcessable;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.actions.util.ProcessException;
import net.sourceforge.jwbf.core.bots.HttpBot;
import net.sourceforge.jwbf.core.bots.WikiBot;
import net.sourceforge.jwbf.core.bots.util.CacheHandler;
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

import org.apache.log4j.Logger;

/**
 * This class helps you to interact with each
 * <a href="http://www.mediawiki.org" target="_blank">MediaWiki</a>. This class offers
 * a <b>basic set</b> of methods which are defined in the package net.sourceforge.jwbf.actions.mw.*
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
 * <b>How to find the correct wikiurl</b><p>
 * The correct wikiurl is sometimes not easy to find, because some wikiadmis uses
 * url rewriting rules. In this cases the correct url is the one, which gives you
 * access to <code>api.php</code>. E.g. Compare
 * <pre>
 * http://www.mediawiki.org/wiki/api.php
 * http://www.mediawiki.org/w/api.php
 * </pre>
 * Thus the correct wikiurl is: <code>http://www.mediawiki.org/w/</code>
 * </p>
 * @author Thomas Stock
 * @author Tobias Knerr
 * @author Justus Bisser
 *
 * @see MediaWikiAdapterBot
 *
 */
public class MediaWikiBot extends HttpBot implements WikiBot {

  private static Logger LOGGER = Logger.getLogger(MediaWikiBot.class);
  private LoginData login = null;

  private CacheHandler store = null;

  private Version version = null;
  private Userinfo ui = null;

  private boolean loginChangeUserInfo = false;
  private boolean loginChangeVersion = false;
  private boolean useEditApi = true;

  /**
   * These chars are not allowed in article names.
   */
  public static final char [] INVALID_LABEL_CHARS = "[]{}<>|".toCharArray();
  private static final int READVAL = GetRevision.CONTENT
  | GetRevision.COMMENT | GetRevision.USER | GetRevision.TIMESTAMP | GetRevision.IDS | GetRevision.FLAGS;

  private static final Set<String> emptySet = Collections.unmodifiableSet(new HashSet<String>());

  /**
   * @param u
   *            wikihosturl like "http://www.mediawiki.org/w/"
   */
  public MediaWikiBot(final URL u) {
    super(u);

  }

  /**
   * @param url
   *            wikihosturl like "http://www.mediawiki.org/w/"
   * @throws MalformedURLException
   *            if param url does not represent a well-formed url
   */

  public MediaWikiBot(final String url) throws MalformedURLException {
    super(url);
    if (!(url.endsWith(".php") || url.endsWith("/"))) {
      throw new MalformedURLException("(" + url + ") url must end with slash or .php");
    }
    setConnection(url);

  }

  /**
   *
   * @param url wikihosturl like "http://www.mediawiki.org/w/"
   * @param testHostReachable if true, test if host reachable
   * @throws IOException a
   */
  public MediaWikiBot(URL url, boolean testHostReachable) throws IOException {
    super(url);
    if (testHostReachable) {
      try {
        getPage(url.toExternalForm());
      } catch (ActionException e) {
        throw new UnknownHostException(url.toExternalForm());
      }
    }
    setConnection(url);
  }


  /**
   * Performs a Login.
   *
   * @param username
   *            the username
   * @param passwd
   *            the password
   * @param domain
   *            login domain (Special for LDAPAuth extention to authenticate against LDAP users)
   * @throws ActionException
   *             on problems with http, cookies and io
   * @see PostLogin
   * @see PostLoginOld
   */
  public void login(final String username, final String passwd, final String domain)
  throws ActionException {
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
   *            the username
   * @param passwd
   *            the password
   * @throws ActionException
   *             on problems with http, cookies and io
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
   *            of article in a mediawiki like "Main Page"
   * @param properties {@link GetRevision}
   * @return a content representation of requested article, never null
   * @throws ActionException
   *             on problems with http, cookies and io
   * @throws ProcessException on access problems
   * @see GetRevision
   */
  public synchronized Article readContent(final String name, final int properties)
  throws ActionException, ProcessException {
    return new Article(this, readData(name, properties));
  }
  /**
   * {@inheritDoc}
   */
  public synchronized SimpleArticle readData(final String name, final int properties)
  throws ActionException, ProcessException {

    GetRevision ac;
    if (store != null) {
      if (store.containsKey(name)) {
        ac = new GetRevision(getVersion(), name, GetRevision.TIMESTAMP);
        performAction(ac);

        SimpleArticle storeSa = store.get(name);
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("stored article (" + storeSa.getTitle() + ") revid: " + storeSa.getRevisionId());
        }
        SimpleArticle liveSa = ac.getArticle();
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("live article revid: " + liveSa.getRevisionId());
        }
        if (liveSa.getRevisionId().equals(storeSa.getRevisionId())) {

          return store.get(name);
        }
      }

      ac = new GetRevision(getVersion(), name, properties);

      performAction(ac);
      LOGGER.debug("update cache (put)");
      store.put(ac.getArticle());

    } else {
      ac = new GetRevision(getVersion(), name, properties);

      performAction(ac);
    }
    return ac.getArticle();

  }

  /**
   * {@inheritDoc}
   */
  public SimpleArticle readData(String name) throws ActionException,
  ProcessException {

    return readData(name, READVAL);
  }
  /**
   *
   * {@inheritDoc}
   */
  public synchronized void setCacheHandler(CacheHandler ch) {
    store = ch;
  }
  /**
   *
   * {@inheritDoc}
   */
  public synchronized boolean hasCacheHandler() {
    if (store != null) {
      return true;
    }
    return false;
  }
  /**
   *
   * @param name
   *            of article in a mediawiki like "Main Page"
   * @return a content representation of requested article, never null
   * @throws ActionException
   *             on problems with http, cookies and io
   * @throws ProcessException on access problems
   * @see GetRevision
   */
  public synchronized Article readContent(final String name)
  throws ActionException, ProcessException {
    return readContent(name, READVAL);

  }

  /**
   *
   * @param a a
   * @throws ActionException
   *             on problems with http, cookies and io
   * @throws ProcessException on access problems
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
    if (store != null) {
      String label = simpleArticle.getTitle();
      SimpleArticle sa;
      if (store.containsKey(label)) {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("contains article: " + label); // TODO RM
        sa = store.get(label);
      } else {
        sa = new SimpleArticle(label);
      }
      sa.setText(simpleArticle.getText());
      sa.setEditor(getUserinfo().getUsername());
      sa.setEditSummary(simpleArticle.getEditSummary());
      sa.setMinorEdit(simpleArticle.isMinorEdit());
      //			sa.setRevisionId((Integer.parseInt(sa.getRevisionId()) + 1) + "");
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("update cache (write)");
      store.put(sa);
    }
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
   *             on problems with http, cookies and io
   * @throws ProcessException on access problems
   */
  public Userinfo getUserinfo() throws ActionException, ProcessException {
    LOGGER.debug("get userinfo");
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
   * @param title to delete
   * @throws ActionException if
   * @throws ProcessException if
   */
  public void postDelete(String title) throws ActionException, ProcessException {

    performAction(new PostDelete(this, title));
  }
  /**
   * {@inheritDoc}
   */
  @Override
  public synchronized String performAction(ContentProcessable a)
  throws ActionException, ProcessException {
    if (a.isSelfExecuter()) {
      throw new ActionException("this is a selfexcecuting action, "
          + "please do not perform this action manually");
    }
    return super.performAction(a);
  }

  /**
   *
   * @return the
   * @throws RuntimeException if no version was found.
   * @see #getSiteinfo()
   */
  public final Version getVersion() throws RuntimeException {
    if (version == null || loginChangeVersion) {

      try {
        GetVersion gs = new GetVersion();
        performAction(gs);

        version = gs.getVersion();
        loginChangeVersion = false;
      } catch (JwbfException e) {
        LOGGER.error(e.getClass().getName() + e.getLocalizedMessage());
        throw new RuntimeException(e.getLocalizedMessage());
      }
      LOGGER.debug("Version is: " + version.name());

    }
    return version;
  }

  /**
   *
   * @return a
   * @throws ActionException
   *             on problems with http, cookies and io
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
   * @param useEditApi if
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



}
