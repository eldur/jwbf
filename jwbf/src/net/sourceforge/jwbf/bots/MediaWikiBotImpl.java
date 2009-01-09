package net.sourceforge.jwbf.bots;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import net.sourceforge.jwbf.actions.mw.MultiAction;
import net.sourceforge.jwbf.actions.mw.editing.GetRevision;
import net.sourceforge.jwbf.actions.mw.editing.PostModifyContent;
import net.sourceforge.jwbf.actions.mw.login.PostLogin;
import net.sourceforge.jwbf.actions.mw.meta.GetSiteinfo;
import net.sourceforge.jwbf.actions.mw.meta.GetUserinfo;
import net.sourceforge.jwbf.actions.mw.meta.GetVersion;
import net.sourceforge.jwbf.actions.mw.util.ActionException;
import net.sourceforge.jwbf.actions.mw.util.MWAction;
import net.sourceforge.jwbf.actions.mw.util.ProcessException;
import net.sourceforge.jwbf.bots.util.LoginData;
import net.sourceforge.jwbf.contentRep.mw.ContentAccessable;
import net.sourceforge.jwbf.contentRep.mw.SimpleArticle;
import net.sourceforge.jwbf.contentRep.mw.Siteinfo;
import net.sourceforge.jwbf.contentRep.mw.Userinfo;
import net.sourceforge.jwbf.contentRep.mw.Version;

import org.apache.log4j.Logger;

public class MediaWikiBotImpl extends HttpBot {

	public static final int ARTICLE = 1 << 1;
	public static final int MEDIA = 1 << 2;
	public static final int SUBCATEGORY = 1 << 3;

	public static final String CHARSET = "utf-8";


	public static final int NS_MAIN = 0;
	public static final int NS_MAIN_TALK = 1;
	public static final int NS_USER = 2;
	public static final int NS_USER_TALK = 3;
	public static final int NS_META = 4;
	public static final int NS_META_TALK = 5;
	public static final int NS_IMAGES = 6;
	public static final int NS_IMAGES_TALK = 7;
	public static final int NS_MEDIAWIKI = 8;
	public static final int NS_MEDIAWIKI_TALK = 9;
	public static final int NS_TEMPLATE = 10;
	public static final int NS_TEMPLATE_TALK = 11;
	public static final int NS_HELP = 12;
	public static final int NS_HELP_TALK = 13;
	public static final int NS_CATEGORY = 14;
	public static final int NS_CATEGORY_TALK = 15;

	private static Logger log = Logger.getLogger(MediaWikiBotImpl.class);
	private LoginData login = null;


	private Version version = null;
	private Userinfo ui = null;
	
	public MediaWikiBotImpl() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param u
	 *            wikihosturl like "http://www.mediawiki.org/wiki/"
	 */
	public MediaWikiBotImpl(final URL u) {
		super();
		setConnection(u);

	}
	
	/**
	 * @param url
	 *            wikihosturl like "http://www.mediawiki.org/wiki/"
	 * @throws MalformedURLException
	 *            if param url does not represent a well-formed url
	 */
	public MediaWikiBotImpl(final String url) throws MalformedURLException {
		super();
		if (!(url.endsWith(".php") || url.endsWith("/"))) {
			throw new MalformedURLException("(" + url + ") url must end with slash or .php");
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
			login = PostLogin.post(this, username, passwd, domain);

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
	 * @supportedBy MediaWiki 1.9.x
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
	 * @supportedBy MediaWiki 1.9.x
	 */
	public void login(final String username, final String passwd)
			throws ActionException {

		httpLogin(username, passwd, null);
	}
	/**
	 *
	 * @param name
	 *            of article in a mediawiki like "Main Page"
	 * @param properties {@link getRevision}
	 * @return a content representation of requested article, never null
	 * @throws ActionException
	 *             on problems with http, cookies and io
	 * @throws ProcessException on access problems
	 * @supportedBy MediaWikiAPI 1.9.x TODO Test Required
	 * @supportedBy MediaWikiAPI 1.10.x TODO Test Required
	 * @supportedBy MediaWikiAPI 1.11.x TODO Test Required
	 * @supportedBy MediaWikiAPI 1.12.x TODO Test Required
	 * @supportedBy MediaWikiAPI 1.13.x TODO Test Required
	 * @supportedBy MediaWikiAPI 1.14.x TODO Test Required
	 */
	public SimpleArticle readContent(final String name, final int properties)
			throws ActionException, ProcessException {
	
			GetRevision ac = new GetRevision(name, properties);

			performAction(ac);
			return ac.getArticle();
	}

	/**
	 *
	 * @param name
	 *            of article in a mediawiki like "Main Page"
	 * @return a content representation of requested article, never null
	 * @throws ActionException
	 *             on problems with http, cookies and io
	 * @throws ProcessException on access problems
	 * @supportedBy MediaWikiAPI 1.9.x
	 * @supportedBy MediaWikiAPI 1.10.x
	 * @supportedBy MediaWikiAPI 1.11.x
	 */
	public SimpleArticle readContent(final String name)
			throws ActionException, ProcessException {
		return readContent(name, GetRevision.CONTENT
				| GetRevision.COMMENT | GetRevision.USER);

	}
	
	/**
	 *
	 * @param a a
	 * @throws ActionException
	 *             on problems with http, cookies and io
	 * @throws ProcessException on access problems
	 * @supportedBy MediaWiki 1.9.x, 1.10.x, 1.11.x, 1.12.x, 1.13.x, 1.14.x
	 * 
	 */
	public void writeContent(final ContentAccessable a)
			throws ActionException, ProcessException {
		if (!isLoggedIn()) {
			throw new ActionException("Please login first");
		}
		performAction(new PostModifyContent(a));
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
	public Userinfo getUserinfo() throws ActionException, ProcessException {
		if (ui == null) {
			GetUserinfo a = new GetUserinfo(getVersion());
			performAction(a);
			ui = a.getUserinfo();
		}
		return ui;
	}
	/**
	 * generates an iterable with the results from a series of MultiAction when
	 * given the first of the actions. The result type can vary to match the
	 * result type of the MultiActions.
	 *
	 *
	 * @param initialAction
	 *            first action to perform, provides a next action.
	 * @param <R>
	 *            type like String
	 * @return iterable providing access to the result values from the responses
	 *         to the initial and subsequent actions. Attention: when the values
	 *         from the subsequent actions are accessed for the first time, the
	 *         connection to the MediaWiki must still exist, /*++ unless ...
	 *
	 * @throws ActionException
	 *             on problems with http, cookies and io
	 * @supportedBy MediaWiki 1.9.x API, 1.10.x API
	 */
	@SuppressWarnings("unchecked")
	public final <R> Iterable<R> performMultiAction(MultiAction<R> initialAction)
			throws ActionException {

		/**
		 * Iterable-class which will store all results which are already known
		 * and perform the next action when more titles are needed
		 */
		@SuppressWarnings("hiding")
		class MultiActionResultIterable<R> implements Iterable<R> {

			private MultiAction<R> nextAction = null;

			private ArrayList<R> knownResults = new ArrayList<R>();

			/**
			 * constructor.
			 *
			 * @param initialAction
			 *            the
			 */
			public MultiActionResultIterable(MultiAction<R> initialAction) {
				this.nextAction = initialAction;
			}

			/**
			 * request more results if local interation seems to be empty.
			 */
			private void loadMoreResults() {

				if (nextAction != null) {

					try {

						performAction((MWAction) nextAction); /*
																 * ++ remove
																 * that cast! ++
																 */
						knownResults.addAll(nextAction.getResults());

						nextAction = nextAction.getNextAction();

					} catch (ActionException ae) {
						nextAction = null;
					} catch (ProcessException e) {
						nextAction = null;
					}

				}

			}

			/**
			 * @return a
			 */
			public Iterator<R> iterator() {
				return new MultiActionResultIterator<R>(this);
			}

			/**
			 * matching Iterator, containing an index variable and a reference
			 * to a MultiActionResultIterable
			 */
			class MultiActionResultIterator<R> implements Iterator<R> {

				private int index = 0;

				private MultiActionResultIterable<R> generatingIterable;

				/**
				 * constructor, relies on generatingIterable != null
				 *
				 * @param generatingIterable
				 *            a
				 */
				MultiActionResultIterator(
						MultiActionResultIterable<R> generatingIterable) {
					this.generatingIterable = generatingIterable;
				}

				/**
				 * if a new query is needed to request more; more results are
				 * requested.
				 *
				 * @return true if has next
				 */
				public boolean hasNext() {
					while (index >= generatingIterable.knownResults.size()
							&& generatingIterable.nextAction != null) {
						generatingIterable.loadMoreResults();
					}
					return index < generatingIterable.knownResults.size();
				}

				/**
				 * if a new query is needed to request more; more results are
				 * requested.
				 *
				 * @return a element of iteration
				 */
				public R next() {
					while (index >= generatingIterable.knownResults.size()
							&& generatingIterable.nextAction != null) {
						generatingIterable.loadMoreResults();
					}
					return generatingIterable.knownResults.get(index++);
				}

				/**
				 * is not supported
				 */
				public void remove() {
					throw new UnsupportedOperationException();
				}

			}

		}

		return new MultiActionResultIterable(initialAction);

	}
	
	public final Version getVersion() {
		if (version == null) {
			GetVersion gs = new GetVersion();

			try {
				performAction(gs);
			} catch (ProcessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ActionException e) {
				// TODO Auto-generated catch block
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
	 * @supportedBy MediaWikiAPI 1.9, 1.10, 1.11, 1.12, 1.13, 1.14
	 */
	public Siteinfo getSiteinfo() throws ActionException {
		GetSiteinfo gs = new GetSiteinfo();

		try {
			performAction(gs);
		} catch (ProcessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return gs.getSiteinfo();

	}

}
