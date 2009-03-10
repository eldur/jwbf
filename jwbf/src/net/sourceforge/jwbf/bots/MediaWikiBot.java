package net.sourceforge.jwbf.bots;

import static net.sourceforge.jwbf.contentRep.SimpleArticle.COMMENT;
import static net.sourceforge.jwbf.contentRep.SimpleArticle.CONTENT;
import static net.sourceforge.jwbf.contentRep.SimpleArticle.USER;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import net.sourceforge.jwbf.actions.ContentProcessable;
import net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.actions.mediawiki.editing.GetRevision;
import net.sourceforge.jwbf.actions.mediawiki.editing.PostDelete;
import net.sourceforge.jwbf.actions.mediawiki.editing.PostModifyContent;
import net.sourceforge.jwbf.actions.mediawiki.login.PostLogin;
import net.sourceforge.jwbf.actions.mediawiki.login.PostLoginOld;
import net.sourceforge.jwbf.actions.mediawiki.meta.GetSiteinfo;
import net.sourceforge.jwbf.actions.mediawiki.meta.GetUserinfo;
import net.sourceforge.jwbf.actions.mediawiki.meta.GetVersion;
import net.sourceforge.jwbf.actions.util.ActionException;
import net.sourceforge.jwbf.actions.util.ProcessException;
import net.sourceforge.jwbf.bots.util.CacheHandler;
import net.sourceforge.jwbf.bots.util.LoginData;
import net.sourceforge.jwbf.contentRep.Article;
import net.sourceforge.jwbf.contentRep.ContentAccessable;
import net.sourceforge.jwbf.contentRep.SimpleArticle;
import net.sourceforge.jwbf.contentRep.mediawiki.Siteinfo;
import net.sourceforge.jwbf.contentRep.mediawiki.Userinfo;

import org.apache.log4j.Logger;

/**
 * This class helps you to interact with each 
 * <a href="http://www.mediawiki.org" target="_blank">MediaWiki</a>. This class offers
 * a <b>basic set</b> of methods which are defined in the package net.sourceforge.jwbf.actions.mw.*
 * 
 * If you need more options, use these classes directly or you can also use the 
 * class {@link MediaWikiAdapterBot} which provides a bigger set of methods.
 *
 * How to use:
 *
 * <pre>
 * MediaWikiBot b = new MediaWikiBot(&quot;http://yourwiki.org&quot;);
 * b.login(&quot;Username&quot;, &quot;Password&quot;);
 * System.out.writeln(b.readContentOf(&quot;Main Page&quot;).getText());
 * </pre>
 * 
 * @author Thomas Stock
 * @author Tobias Knerr
 * @author Justus Bisser
 * 
 * @see MediaWikiAdapterBot
 *
 */
public class MediaWikiBot extends HttpBot implements WikiBot {


	private static Logger log = Logger.getLogger(MediaWikiBot.class);
	private LoginData login = null;

	private CacheHandler store = null;

	private Version version = null;
	private Userinfo ui = null;
	
	protected MediaWikiBot() {
		// design for extension
	}
	
	/**
	 * @param u
	 *            wikihosturl like "http://www.mediawiki.org/wiki/"
	 */
	public MediaWikiBot(final URL u) {
		super();
		setConnection(u);

	}
	
	/**
	 * @param url
	 *            wikihosturl like "http://www.mediawiki.org/wiki/"
	 * @throws MalformedURLException
	 *            if param url does not represent a well-formed url
	 */
	public MediaWikiBot(final String url) throws MalformedURLException {
		super();
		if (!(url.endsWith(".php") || url.endsWith("/"))) {
			throw new MalformedURLException("(" + url + ") url must end with slash or .php");
		}
		setConnection(url);

	}

	/**
	 * 
	 * @param url wikihosturl like "http://www.mediawiki.org/wiki/"
	 * @param testHostReachable if true, test if host reachable
	 * @throws UnknownHostException a
	 * @throws IOException a
	 */
	public MediaWikiBot(URL url, boolean testHostReachable) throws UnknownHostException, IOException {
		super();
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
	 * Performs a old Login via cookie.
	 *  Special for LDAPAuth extention to authenticate against LDAP users
	 * @param username
	 *            the username
	 * @param passwd
	 *            the password
	 * @param domain
	 *            the password
	 * @throws ActionException
	 *             on problems with http, cookies and io
	 * @supportedBy MediaWiki 1.9.x
	 */
	 private void httpLogin(final String username, final String passwd, final String domain)
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
				performAction(new PostLogin(username, passwd, login));
				break;
			}

			
			this.login = login;

		} catch (ProcessException e) {
			throw new ActionException(e.getLocalizedMessage());
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
	 * @param domain
	 *            login domain
	 * @throws ActionException
	 *             on problems with http, cookies and io
	 * @see PostLogin
	 * @see PostLoginOld
	 */
	public void login(final String username, final String passwd, final String domain)
			throws ActionException {
		httpLogin(username, passwd, domain);
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

		httpLogin(username, passwd, null);
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
		if (store != null && store.containsKey(name)) {
			return new Article(this, store.get(name));
		} else {
			return new Article(this, readData(name, properties));
		}
	}
	
	public synchronized SimpleArticle readData(final String name, final int properties)
			throws ActionException, ProcessException {

			GetRevision ac = new GetRevision(name, properties, this);

			performAction(ac);
			if (store != null)
				store.put(ac.getArticle());
			return ac.getArticle();
		
	}
	
	public void setCacheHandler(CacheHandler ch) {
		
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
		return readContent(name, CONTENT
				| COMMENT | USER);

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
	public synchronized void writeContent(final ContentAccessable a)
			throws ActionException, ProcessException {
		if (!isLoggedIn()) {
			throw new ActionException("Please login first");
		}
		if (a.getText().trim().length() < 1 ) 
			throw new RuntimeException("Content is empty");
		performAction(new PostModifyContent(this, a));
		
		
	}
	
	/**
	 *
	 * @return true if
	 */
	public final boolean isLoggedIn() {

		 if(login != null) {
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
		if (ui == null) {
			GetUserinfo a = new GetUserinfo(getVersion());
			performAction(a);
			ui = a.getUserinfo();
		}
		return ui;
	}
	
	
	/**
	 * @see PostDelete
	 */
	public void postDelete(String title) throws ActionException, ProcessException {
		
		performAction(new PostDelete(title, getSiteinfo(), getUserinfo()));
	}
	@Override
	public synchronized String performAction(ContentProcessable a)
			throws ActionException, ProcessException {
		return super.performAction(a);
	}

	/**
	 * 
	 * @return the
	 * @see #getSiteinfo()
	 */
	public final Version getVersion() {
		if (version == null) {
			GetVersion gs = new GetVersion();

			try {
				performAction(gs);
			} catch (ProcessException e) {
				e.printStackTrace();
			} catch (ActionException e) {
				e.printStackTrace();
			}

			version = gs.getSiteinfo().getVersion();

			log.debug("Version is: " + version.name());

		}
		return version;
	}
	
	/**
	 *
	 * @return a
	 * @throws ActionException
	 *             on problems with http, cookies and io
	 * @see GetSiteinfo
	 */
	public Siteinfo getSiteinfo() throws ActionException {
		GetSiteinfo gs = new GetSiteinfo();

		try {
			performAction(gs);
		} catch (ProcessException e) {
			e.printStackTrace();
		}

		return gs.getSiteinfo();

	}



}
