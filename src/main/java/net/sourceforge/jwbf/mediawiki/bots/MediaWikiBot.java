package net.sourceforge.jwbf.mediawiki.bots;

import java.net.URL;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import net.sourceforge.jwbf.core.actions.ContentProcessable;
import net.sourceforge.jwbf.core.actions.HttpActionClient;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.bots.HttpBot;
import net.sourceforge.jwbf.core.bots.WikiBot;
import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.core.contentRep.Userinfo;
import net.sourceforge.jwbf.core.internal.Checked;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import net.sourceforge.jwbf.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.editing.GetRevision;
import net.sourceforge.jwbf.mediawiki.actions.editing.PostDelete;
import net.sourceforge.jwbf.mediawiki.actions.editing.PostModifyContent;
import net.sourceforge.jwbf.mediawiki.actions.login.PostLogin;
import net.sourceforge.jwbf.mediawiki.actions.meta.GetUserinfo;
import net.sourceforge.jwbf.mediawiki.actions.meta.GetVersion;
import net.sourceforge.jwbf.mediawiki.actions.meta.Siteinfo;
import net.sourceforge.jwbf.mediawiki.contentRep.LoginData;

/**
 * This class helps you to interact with each <a href="http://www.mediawiki.org"
 * target="_blank">MediaWiki</a>. This class offers a <b>basic set</b> of methods which are defined
 * in the package net.sourceforge.jwbf.actions.mw.* How to use:
 *
 * <pre>
 * MediaWikiBot b = new MediaWikiBot(&quot;http://yourwiki.org&quot;);
 * b.login(&quot;Username&quot;, &quot;Password&quot;);
 * System.out.println(b.readContent(&quot;Main Page&quot;).getText());
 * </pre>
 *
 * <b>How to find the correct wikiurl</b>
 *
 * <p>The correct wikiurl is sometimes not easy to find, because some wiki admins uses url rewriting
 * rules. In this cases the correct url is the one, which gives you access to <code>api.php</code>.
 * E.g. Compare
 *
 * <pre>
 * http://www.mediawiki.org/wiki/api.php
 * http://www.mediawiki.org/w/api.php
 * </pre>
 *
 * Thus the correct wikiurl is: <code>http://www.mediawiki.org/w/</code> Since MediaWiki 1.20, the
 * wikiurl can be found on the wiki's Special:Version page.
 *
 * @author Thomas Stock
 * @author Tobias Knerr
 * @author Justus Bisser
 */
public class MediaWikiBot implements WikiBot {

  private static final Logger log = LoggerFactory.getLogger(MediaWikiBot.class);

  private LoginData login = null;

  private Version version = null;
  private Userinfo ui = null;

  private boolean loginChangeUserInfo = false;
  private boolean loginChangeVersion = false;

  @Inject private HttpBot bot;

  private HttpActionClient client;

  /** These chars are not allowed in article names. */
  @VisibleForTesting
  public static char[] invalidLabelChars() {
    return "[]{}<>|".toCharArray();
  }

  private static final int DEFAULT_READ_PROPERTIES =
      GetRevision.CONTENT
          | GetRevision.COMMENT
          | GetRevision.USER
          | GetRevision.TIMESTAMP
          | GetRevision.IDS
          | GetRevision.FLAGS;

  /** use this constructor, if you want to work with IoC. */
  public MediaWikiBot() {}

  /** @param u wikihosturl like "http://www.mediawiki.org/w/" */
  public MediaWikiBot(final URL u) {
    this(HttpActionClient.of(u));
  }

  public MediaWikiBot(final HttpActionClient client) {
    this.client = client;
    bot = new HttpBot(client);
  }

  /**
   * @param url wikihosturl like "http://www.mediawiki.org/w/"
   * @throws IllegalArgumentException if param url does not represent a well-formed url
   */
  public MediaWikiBot(final String url) {
    if (!(url.endsWith(".php") || url.endsWith("/"))) {
      throw new IllegalArgumentException("(" + url + ") url must end with slash or .php");
    }
    this.client = HttpActionClient.of(url);
    bot = new HttpBot(client);
  }

  /**
   * @param url wikihosturl like "http://www.mediawiki.org/w/"
   * @param testHostReachable if true, test if host reachable
   */
  public MediaWikiBot(URL url, boolean testHostReachable) {
    if (testHostReachable) {
      HttpBot.getPage(url.toString());
    }
    bot = new HttpBot(url);
  }

  /**
   * Performs a Login.
   *
   * @param username the username
   * @param passwd the password
   * @param domain login domain (Special for LDAPAuth extention to authenticate against LDAP users)
   * @see PostLogin
   */
  public void login(String username, String passwd, String domain) {
    this.login = getPerformedAction(new PostLogin(username, passwd, domain)).getLoginData();
    loginChangeUserInfo = true;
    if (getVersion() == Version.UNKNOWN) {
      loginChangeVersion = true;
    }
  }

  /**
   * Performs a Login. Actual old cookie login works right, because is pending on {@link
   * #writeContent(net.sourceforge.jwbf.core.contentRep.SimpleArticle)}
   *
   * @param username the username
   * @param passwd the password
   * @see PostLogin
   */
  @Override
  public void login(String username, String passwd) {
    login(username, passwd, null);
  }

  /**
   * @param name of article in a mediawiki like "Main Page"
   * @param properties {@link GetRevision}
   * @return a content representation of requested article, never null
   * @see GetRevision
   * @deprecated {@link #getArticle(String)}
   */
  @Deprecated
  public Article getArticle(String name, final int properties) {
    return new Article(this, readData(properties, name));
  }

  /** @deprecated use {@link #readData(String)} */
  @Override
  // TODO 'data' is not very descriptive
  public SimpleArticle readData(String name, int properties) {
    return readData(properties, name);
  }

  // TODO 'data' is not very descriptive
  SimpleArticle readData(int properties, String name) {
    return getPerformedAction(new GetRevision(null, name, properties)).getArticle();
  }

  // TODO 'data' is not very descriptive
  public ImmutableList<SimpleArticle> readData(String... names) {
    return readData(ImmutableList.copyOf(names));
  }

  // TODO 'data' is not very descriptive
  public ImmutableList<SimpleArticle> readData(ImmutableList<String> names) {
    return getPerformedAction(new GetRevision(names, DEFAULT_READ_PROPERTIES)).asList();
  }

  /** {@inheritDoc} */
  @Override
  // TODO 'data' is not very descriptive
  public SimpleArticle readData(String name) {
    return readData(DEFAULT_READ_PROPERTIES, name);
  }

  // TODO 'data' is not very descriptive
  public ImmutableList<Optional<SimpleArticle>> readDataOpt(String... names) {
    return readDataOpt(ImmutableList.copyOf(names));
  }

  // TODO 'data' is not very descriptive
  public ImmutableList<Optional<SimpleArticle>> readDataOpt(ImmutableList<String> names) {
    return getPerformedAction(new GetRevision(names, DEFAULT_READ_PROPERTIES)).asListOpt();
  }

  // TODO 'data' is not very descriptive
  public Optional<SimpleArticle> readDataOpt(String name) {
    return getPerformedAction(new GetRevision(null, name, DEFAULT_READ_PROPERTIES)).getArticleOpt();
  }

  /**
   * @param name of article in a mediawiki like "Main Page"
   * @return a content representation of requested article, never null
   * @see GetRevision
   */
  public Article getArticle(final String name) {
    return getArticle(name, DEFAULT_READ_PROPERTIES);
  }

  /** {@inheritDoc} */
  @Override
  public void writeContent(final SimpleArticle simpleArticle) {
    if (!isLoggedIn()) {
      throw new ActionException("Please login first");
    }

    SimpleArticle nonNullArticle = Checked.nonNull(simpleArticle, "content");
    checkTitle(nonNullArticle.getTitle());

    getPerformedAction(new PostModifyContent(this, simpleArticle));
    if (nonNullArticle.getText().trim().length() < 1) {
      throw new RuntimeException("Content is empty, still written");
    }
  }

  static Optional<String> checkTitle(String title) {
    for (char invChar : invalidLabelChars()) {
      if (title.contains(invChar + "")) {
        throw new ActionException(
            "Invalid character \"" + invChar + "\" in label \"" + title + "\"");
      }
    }
    return Optional.of(title);
  }

  /** @return true if */
  public boolean isLoggedIn() {
    return login != null && login.isLoggedIn();
  }

  /** {@inheritDoc} */
  @Override
  public Userinfo getUserinfo() {
    if (ui == null || loginChangeUserInfo) {
      ui = getPerformedAction(GetUserinfo.class);
      loginChangeUserInfo = false;
    }
    return ui;
  }

  /** {@inheritDoc} */
  @Override
  public void delete(String title) {
    delete(title, null);
  }

  /** deletes an article with a reason */
  public void delete(String title, String reason) {
    getPerformedAction(new PostDelete(getUserinfo(), title, reason));
  }

  /** @deprecated use {@link #getPerformedAction(ContentProcessable)} instead */
  @Deprecated
  synchronized String performAction(ContentProcessable a) {
    if (a.isSelfExecuter()) {
      throw new ActionException(
          "this is a selfexcecuting action, " + "please do not perform this action manually");
    }
    return bot().performAction(a);
  }

  public <T extends ContentProcessable> T getPerformedAction(T answer) {
    // TODO transform to throttled
    performAction(answer);
    return answer;
  }

  public <T extends ContentProcessable> T getPerformedAction(Class<T> clazz) {
    T answer;
    try {
      answer = clazz.newInstance();
      return getPerformedAction(answer);
    } catch (InstantiationException | IllegalAccessException e) {
      throw new IllegalStateException(e);
    }
  }

  @VisibleForTesting
  HttpBot bot() {
    if (bot == null) {
      throw new IllegalStateException(
          "please use another constructor or inject "
              + //
              HttpBot.class.getCanonicalName());
    }
    return bot;
  }

  /** @see #getSiteinfo() */
  @Nonnull
  public Version getVersion() {
    if (version == null || loginChangeVersion) {
      GetVersion gs = getPerformedAction(GetVersion.class);
      version = gs.getVersion();
      loginChangeVersion = false;
      log.debug("Version is: {}", version.name());
    }
    return version;
  }

  /**
   * @return a on problems with http, cookies and io
   * @see Siteinfo
   */
  @Nonnull
  public Siteinfo getSiteinfo() {
    // TODO cache value see getVersion
    return getPerformedAction(Siteinfo.class);
  }

  /** {@inheritDoc} */
  @Override
  public final String getWikiType() {
    return MediaWiki.class.getSimpleName() + " " + getVersion();
  }
}
