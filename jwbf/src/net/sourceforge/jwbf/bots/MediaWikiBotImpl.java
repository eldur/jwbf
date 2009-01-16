package net.sourceforge.jwbf.bots;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import net.sourceforge.jwbf.actions.mw.MultiAction;
import net.sourceforge.jwbf.actions.mw.MediaWiki.Version;
import net.sourceforge.jwbf.actions.mw.editing.GetRevision;
import net.sourceforge.jwbf.actions.mw.editing.PostModifyContent;
import net.sourceforge.jwbf.actions.mw.login.PostLogin;
import net.sourceforge.jwbf.actions.mw.login.PostLoginOld;
import net.sourceforge.jwbf.actions.mw.meta.GetSiteinfo;
import net.sourceforge.jwbf.actions.mw.meta.GetUserinfo;
import net.sourceforge.jwbf.actions.mw.meta.GetVersion;
import net.sourceforge.jwbf.actions.mw.util.ActionException;
import net.sourceforge.jwbf.actions.mw.util.MWAction;
import net.sourceforge.jwbf.actions.mw.util.ProcessException;
import net.sourceforge.jwbf.bots.util.LoginData;
import net.sourceforge.jwbf.contentRep.mw.Article;
import net.sourceforge.jwbf.contentRep.mw.ContentAccessable;
import net.sourceforge.jwbf.contentRep.mw.Siteinfo;
import net.sourceforge.jwbf.contentRep.mw.Userinfo;

import org.apache.log4j.Logger;

public class MediaWikiBotImpl extends HttpBot {


	private static Logger log = Logger.getLogger(MediaWikiBotImpl.class);
	private LoginData login = null;


	private Version version = null;
	private Userinfo ui = null;
	
	protected MediaWikiBotImpl() {
		// design for extension
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
	
			GetRevision ac = new GetRevision(name, properties);

			performAction(ac);
			return new Article(ac.getArticle(), this);
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
		return readContent(name, GetRevision.CONTENT
				| GetRevision.COMMENT | GetRevision.USER);

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
