/*
 * Copyright 2007 Thomas Stock.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * Contributors:
 * Tobias Knerr
 */

package net.sourceforge.jwbf.bots;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import net.sourceforge.jwbf.actions.http.ActionException;
import net.sourceforge.jwbf.actions.http.ProcessException;
import net.sourceforge.jwbf.actions.http.VersionException;
import net.sourceforge.jwbf.actions.http.mw.GetEnvironmentVars;
import net.sourceforge.jwbf.actions.http.mw.MWAction;
import net.sourceforge.jwbf.actions.http.mw.PostLoginOld;
import net.sourceforge.jwbf.actions.http.mw.PostModifyContent;
import net.sourceforge.jwbf.actions.http.mw.api.GetAllPageTitles;
import net.sourceforge.jwbf.actions.http.mw.api.GetBacklinkTitles;
import net.sourceforge.jwbf.actions.http.mw.api.GetFullCategoryMembers;
import net.sourceforge.jwbf.actions.http.mw.api.GetImagelinkTitles;
import net.sourceforge.jwbf.actions.http.mw.api.GetLogEvents;
import net.sourceforge.jwbf.actions.http.mw.api.GetRecentchanges;
import net.sourceforge.jwbf.actions.http.mw.api.GetRevision;
import net.sourceforge.jwbf.actions.http.mw.api.GetSimpleCategoryMembers;
import net.sourceforge.jwbf.actions.http.mw.api.GetSiteinfo;
import net.sourceforge.jwbf.actions.http.mw.api.GetTemplateUserTitles;
import net.sourceforge.jwbf.actions.http.mw.api.MultiAction;
import net.sourceforge.jwbf.bots.util.LoginData;
import net.sourceforge.jwbf.contentRep.Version;
import net.sourceforge.jwbf.contentRep.mw.CategoryItem;
import net.sourceforge.jwbf.contentRep.mw.ContentAccessable;
import net.sourceforge.jwbf.contentRep.mw.LogItem;
import net.sourceforge.jwbf.contentRep.mw.Siteinfo;

/*
 * possible tag values: @supportedBy ------------------------------------------
 * MediaWiki 1.9.x MediaWiki 1.9.x API MediaWiki 1.10.x MediaWiki 1.10.x API
 *  ( current Wikipedia version ) MediaWiki 1.11.alpha MediaWiki 1.11.alpha API
 * ------------------------------------------
 */

/**
 * 
 * This class helps you to interact with each mediawiki.
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
 * 
 */
public class MediaWikiBot extends HttpBot {

	public static final int ARTICLE = 1 << 1;
	public static final int MEDIA = 1 << 2;
	public static final int SUBCATEGORY = 1 << 3;

	public static final String CHARSET = "utf-8";
	
	private Version currentVersion = null;

	private LoginData login;
	private boolean loggedIn = false;
	
	private boolean versionValidation = false;

	/**
	 * @param u
	 *            wikihosturl like "http://www.mediawiki.org/wiki/"
	 */
	public MediaWikiBot(final URL u) {
		super();
		setConnection(u);

	}
	/**
	 * Design only for extension.
	 */
	protected MediaWikiBot() {
		// do nothing, design only for extension
	}

	/**
	 * @param url
	 *            wikihosturl like "http://www.mediawiki.org/wiki/"
	 * @throws MalformedURLException  
	 *            if param url does not represent a well-formed url          
	 */
	public MediaWikiBot(final String url) throws MalformedURLException {
		super();
		setConnection(url);

	}
	
	/**
	 * If true, the bot checks if action is be allowed on the working 
	 * bot api. 
	 * @param versionValidation a
	 */
	public void setVersionValidation(boolean versionValidation) {
		this.versionValidation = versionValidation;
	}

	/**
	 * Performs a old Login via cookie.
	 * 
	 * @param username
	 *            the username
	 * @param passwd
	 *            the password
	 * @throws ActionException
	 *             on problems with http, cookies and io
	 * @supportedBy MediaWiki 1.9.x
	 */
	public final void httpLogin(final String username, final String passwd)
			throws ActionException {
		try {
			performAction(new PostLoginOld(username, passwd));
		} catch (ProcessException e) {
			e.printStackTrace();
		}
		loggedIn = true;
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
	public final void login(final String username, final String passwd)
			throws ActionException {
		// code for 1.9.x API
		// PostLogin pl = new PostLogin(username, passwd);
		// performAction(pl);
		// login = pl.getLoginData();
		httpLogin(username, passwd);
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
	 * @supportedBy MediaWikiAPI 1.9.x
	 * @supportedBy MediaWikiAPI 1.10.x
	 * @supportedBy MediaWikiAPI 1.11.x
	 */
	public final synchronized ContentAccessable readContent(final String name, final int properties)
			throws ActionException, ProcessException {
		checkApiVersion(Version.MW1_9, Version.MW1_10, Version.MW1_11);
		ContentAccessable a = null;
		GetRevision ac = new GetRevision(name, properties);

		performAction(ac);
		a = ac.getArticle();

		return a;
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
	public final synchronized ContentAccessable readContent(final String name)
			throws ActionException, ProcessException {
		return readContent(name, GetRevision.CONTENT
				| GetRevision.COMMENT | GetRevision.USER);

	}

	// /**
	// *
	// * @param title
	// * of category in a mediawiki like "Category:Small Things"
	// * @return with all article names in the requestet category
	// * @throws ActionException
	// * on problems
	// */
	// public final Collection<String> readCategory(final String title)
	// throws ActionException {
	//
	// return readCategory(title, ARTICLE);
	// }

	/**
	 * helper method generating a namespace string as required by the MW-api.
	 * 
	 * @param namespaces
	 *            namespace as
	 * @return with numbers seperated by |
	 */
	private String generateNamespaceString(int... namespaces) {

		String namespaceString = null;

		if (namespaces != null && namespaces.length != 0) {

			namespaceString = new String();

			for (int nsNumber : namespaces) {

				namespaceString += nsNumber + "|";

			}

			// remove last '|'
			if (namespaceString.endsWith("|")) {
				namespaceString = namespaceString.substring(0, namespaceString
						.length() - 1);
			}

		}

		return namespaceString;

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
	private <R> Iterable<R> performMultiAction(MultiAction<R> initialAction)
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

	/* ++ TODO: loadAll-parameter ++ */

	/**
	 * get the titles of all pages meeting certain criteria; USE WITH CAUTION -
	 * especially in big wikis!
	 * 
	 * @param from
	 *            page title to start from, may be null
	 * @param prefix
	 *            restricts search to titles that begin with this value, may be
	 *            null
	 * @param redirects
	 *            include redirects in the list
	 * @param nonredirects
	 *            include nonredirects in the list
	 * @param namespaces
	 *            numbers of the namespaces (specified using varargs) that will
	 *            be included in the search (will be ignored if redirects is
	 *            false!)
	 * 
	 * @return iterable providing access to the names of all articles which
	 *         embed the template specified by the template-parameter.
	 *         Attention: to get more article titles, the connection to the
	 *         MediaWiki must still exist.
	 * 
	 * @throws ActionException
	 *             on problems with http, cookies and io
	 * 
	 * @supportedBy MediaWikiAPI 1.9 allpages / ap
	 * @supportedBy MediaWikiAPI 1.10 allpages / ap
	 * @supportedBy MediaWikiAPI 1.11 allpages / ap
	 */
	public Iterable<String> getAllPageTitles(String from, String prefix,
			boolean redirects, boolean nonredirects, int... namespaces)
			throws ActionException {
		checkApiVersion(Version.MW1_9, Version.MW1_10, Version.MW1_11);
		GetAllPageTitles a = new GetAllPageTitles(from, prefix, redirects,
				nonredirects, generateNamespaceString(namespaces));

		return performMultiAction(a);

	}

	/**
	 * get the titles of all pages meeting certain criteria; USE WITH CAUTION -
	 * especially in big wikis!
	 * 
	 * @param namespaces
	 *            numbers of the namespaces (specified using varargs) that will
	 *            be included in the search (will be ignored if redirects is
	 *            false!)
	 * 
	 * @return iterable providing access to the names of all articles which
	 *         embed the template specified by the template-parameter.
	 *         Attention: to get more article titles, the connection to the
	 *         MediaWiki must still exist.
	 * 
	 * @throws ActionException
	 *             on problems with http, cookies and io
	 * 
	 * @supportedBy MediaWikiAPI 1.9 allpages / ap
	 * @supportedBy MediaWikiAPI 1.10 allpages / ap
	 * @supportedBy MediaWikiAPI 1.11 allpages / ap
	 */
	public Iterable<String> getAllPageTitles(int... namespaces)
			throws ActionException {

		GetAllPageTitles a = new GetAllPageTitles(null, null, false, true,
				generateNamespaceString(namespaces));

		return performMultiAction(a);

	}

	/**
	 * variation of the getAllPageTitles-method which does not set a namespace
	 * restriction.
	 * 
	 * @param from
	 *            page title to start from, may be null
	 * @param prefix
	 *            restricts search to titles that begin with this value, may be
	 *            null
	 * @param redirects
	 *            include redirects in the list
	 * @param nonredirects
	 *            include nonredirects in the list
	 * @return of titels
	 * @throws ActionException
	 *             on problems with http, cookies and io
	 * 
	 * @supportedBy MediaWikiAPI 1.9 allpages / ap
	 * @supportedBy MediaWikiAPI 1.10 allpages / ap
	 * @supportedBy MediaWikiAPI 1.11 allpages / ap
	 */
	public Iterable<String> getAllPageTitles(String from, String prefix,
			boolean redirects, boolean nonredirects) throws ActionException {

		return getAllPageTitles(from, prefix, redirects, nonredirects, null);

	}

	/**
	 * get the titles of all pages which contain a link to the given article.
	 * 
	 * @param article
	 *            title of an article
	 * 
	 * @param namespaces
	 *            numbers of the namespaces (specified using varargs) that will
	 *            be included in the search
	 * 
	 * @return iterable providing access to the names of all articles which link
	 *         to the article specified by the article-parameter. Attention: to
	 *         get more article titles, the connection to the MediaWiki must
	 *         still exist.
	 * 
	 * @throws ActionException
	 *             on problems with http, cookies and io
	 * 
	 * TODO Pending Parameter Change;
	 * http://www.mediawiki.org/wiki/API:Query_-_Lists
	 * @supportedBy MediaWikiAPI 1.9 backlinks / bl
	 * @supportedBy MediaWikiAPI 1.10 backlinks / bl
	 * @supportedBy MediaWikiAPI 1.11 backlinks / bl
	 */
	public Iterable<String> getBacklinkTitles(String article, int... namespaces)
			throws ActionException {
		checkApiVersion(Version.MW1_9, Version.MW1_10, Version.MW1_11);
		GetBacklinkTitles a = new GetBacklinkTitles(article,
				generateNamespaceString(namespaces));

		return performMultiAction(a);

	}

	/**
	 * variation of the getBacklinkTitles-method which does not set a namespace
	 * restriction.
	 * 
	 * @param article
	 *            label of article
	 * @return of article labels
	 * @throws ActionException
	 *             on problems with http, cookies and io
	 * 
	 * TODO Pending Parameter Change;
	 * http://www.mediawiki.org/wiki/API:Query_-_Lists
	 * @supportedBy MediaWikiAPI 1.9 backlinks / bl
	 * @supportedBy MediaWikiAPI 1.10 backlinks / bl
	 * @supportedBy MediaWikiAPI 1.11 backlinks / bl
	 */
	public Iterable<String> getBacklinkTitles(String article)
			throws ActionException {

		return getBacklinkTitles(article, null);

	}
	/**
	 * 
	 * @param category like "Buildings" or "Chemical elements" without prefix "Category:"
	 * @return of article labels
	 * @throws ActionException on any kind of http or version problems
	 * @supportedBy MediaWikiAPI 1.11 categorymembers / cm
	 */
	public Iterable<String> getCategoryMembers(String category) throws ActionException {
		checkApiVersion(Version.MW1_11);
		GetSimpleCategoryMembers c = new GetSimpleCategoryMembers(category);
		return performMultiAction(c);
	}
	
	/**
	 * 
	 * @param category like "Buildings" or "Chemical elements" without prefix Category
	 * @return of article labels
	 * @throws ActionException on any kind of http or version problems
	 * @supportedBy MediaWikiAPI 1.11 categorymembers / cm
	 */
	public Iterable<CategoryItem> getFullCategoryMembers(String category) throws ActionException {
		checkApiVersion(Version.MW1_11);
		GetFullCategoryMembers c = new GetFullCategoryMembers(category);
		return performMultiAction(c);
	}
	
	/**
	 * get the titles of all pages which contain a link to the given image.
	 * 
	 * @param image
	 *            title of an image, without prefix "Image:"
	 * 
	 * @param namespaces
	 *            numbers of the namespaces (specified using varargs) that will
	 *            be included in the search
	 * 
	 * @return iterable providing access to the names of all articles which link
	 *         to the image specified by the image-parameter. Attention: to get
	 *         more article titles, the connection to the MediaWiki must still
	 *         exist.
	 * 
	 * @throws ActionException
	 *             on problems with http, cookies and io
	 * 
	 * TODO Pending Parameter Change;
	 * http://www.mediawiki.org/wiki/API:Query_-_Lists TODO New API call, decide
	 * if design a switch by version or support only newest
	 * 
	 * @supportedBy MediaWikiAPI 1.9 embeddedin / ei
	 * @supportedBy MediaWikiAPI 1.10 embeddedin / ei
	 * 
	 */
	public Iterable<String> getImagelinkTitles(String image, int... namespaces)
			throws ActionException {
		checkApiVersion(Version.MW1_9, Version.MW1_10);
		GetImagelinkTitles a = new GetImagelinkTitles(image,
				generateNamespaceString(namespaces));

		return performMultiAction(a);

	}

	/**
	 * variation of the getImagelinkTitles-method which does not set a namespace
	 * restriction.
	 * 
	 * @param image
	 *            label of image like TODO what? , without prefix "Image:"
	 * @return an of labels
	 * @see #getImagelinkTitles(String, int[])
	 * @throws ActionException
	 *             on problems with http, cookies and io
	 * 
	 * TODO Pending Parameter Change;
	 * http://www.mediawiki.org/wiki/API:Query_-_Lists TODO New API call, deside
	 * if design a swich by version or support only newest
	 * 
	 * @supportedBy MediaWikiAPI 1.9 embeddedin / ei
	 * @supportedBy MediaWikiAPI 1.10 embeddedin / ei
	 */
	public Iterable<String> getImagelinkTitles(String image)
			throws ActionException {

		return getImagelinkTitles(image, null);

	}

	/**
	 * 
	 * @param type event type like: upload, delete, ...
	 * @return the last ten log events
	 * @throws ActionException on problems with http, cookies and io 
	 * @supportedBy MediaWikiAPI 1.11 logevents / le 
	 * TODO API state is (semi-complete), see 
	 * http://www.mediawiki.org/wiki/API:Query_-_Lists#logevents_.2F_le_.28semi-complete.29
	 */
	public Iterator<LogItem> getLogEvents(String ... type) throws ActionException {

		return getLogEvents(10, type);
	}
	
	/**
	 * 
	 * @param type event type like: upload, delete, ...
	 * @param limit number of events
	 * @return the last ten log events
	 * @throws ActionException on problems with http, cookies and io
	 * @supportedBy MediaWikiAPI 1.11 logevents / le 
	 * TODO API state is (semi-complete), see 
	 * http://www.mediawiki.org/wiki/API:Query_-_Lists#logevents_.2F_le_.28semi-complete.29
	 */
	public Iterator<LogItem> getLogEvents(int limit, String ... type) throws ActionException {
		checkApiVersion(Version.MW1_11);
		GetLogEvents c = new GetLogEvents(limit, type);
		try {
			performAction(c);
		} catch (ProcessException e) {
			e.printStackTrace();
		}
		return c.getResults();
	}
	/**
	 * get the titles of all pages which embed the given template.
	 * 
	 * @param template
	 *            title of a template, without prefix "Template:"
	 * 
	 * @param namespaces
	 *            numbers of the namespaces (specified using varargs) that will
	 *            be included in the search
	 * 
	 * @return iterable providing access to the names of all articles which
	 *         embed the template specified by the template-parameter.
	 *         Attention: to get more article titles, the connection to the
	 *         MediaWiki must still exist.
	 * 
	 * @throws ActionException
	 *             on problems with http, cookies and io
	 * 
	 * @supportedBy MediaWikiAPI 1.9 embeddedin / ei
	 * @supportedBy MediaWikiAPI 1.10 embeddedin / ei
	 * @supportedBy MediaWikiAPI 1.11 embeddedin / ei
	 */
	public Iterable<String> getTemplateUserTitles(String template,
			int... namespaces) throws ActionException {
		checkApiVersion(Version.MW1_9, Version.MW1_10, Version.MW1_11);
		GetTemplateUserTitles a = new GetTemplateUserTitles(template,
				generateNamespaceString(namespaces));

		return performMultiAction(a);

	}

	/**
	 * variation of the getTemplateUserTitles-method. which does not set a
	 * namespace restriction
	 * 
	 * @param template
	 *            label of template like TODO what ?, without prefix "Template:"
	 * @return an of labels
	 * @throws ActionException
	 *             on problems with http, cookies and io
	 * 
	 * @supportedBy MediaWikiAPI 1.9 embeddedin / ei
	 * @supportedBy MediaWikiAPI 1.10 embeddedin / ei
	 * @supportedBy MediaWikiAPI 1.11 embeddedin / ei
	 */
	public Iterable<String> getTemplateUserTitles(String template)
			throws ActionException {
		checkApiVersion(Version.MW1_9, Version.MW1_10, Version.MW1_11);
		return getTemplateUserTitles(template, null);

	}

	/**
	 * 
	 * @return a
	 * @throws ActionException
	 *             on problems with http, cookies and io
	 * @supportedBy MediaWikiAPI 1.9 siteinfo / si
	 * @supportedBy MediaWikiAPI 1.10 siteinfo / si
	 * @supportedBy MediaWikiAPI 1.11 siteinfo / si
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

	/**
	 * Get a number of recent changes from namespace.
	 * 
	 * @param count
	 *            of changes
	 * @param namespaces
	 *            namespacenumbers greater equals 0
	 * @return a
	 * @throws ActionException
	 *             on problems with http, cookies and io
	 * @supportedBy MediaWikiAPI 1.10 recentchanges / rc
	 * @supportedBy MediaWikiAPI 1.10 recentchanges / rc
	 */
	public Iterable<String> getRecentchangesTitles(final int count,
			int... namespaces) throws ActionException {
		checkApiVersion(Version.MW1_10, Version.MW1_11);
		GetRecentchanges a = new GetRecentchanges(count,
				generateNamespaceString(namespaces));

		return performMultiAction(a);
	}
	
	/**
	 * Get a number of recent changes from default namespace.
	 * 
	 * @param count
	 *            of changes
	 * @return a
	 * @throws ActionException
	 *             on problems with http, cookies and io
	 * @supportedBy MediaWikiAPI 1.10 recentchanges / rc
	 */
	public Iterable<String> getRecentchangesTitles(final int count)
			throws ActionException {

		return getRecentchangesTitles(count, null);
	}

	/**
	 * 
	 * @return true if
	 */
	public boolean isLoggedIn() {
		return loggedIn;
		// // code for api
		// if(login != null) {
		// return true;
		// }
		// return false;
	}

	/**
	 * 
	 * @param a
	 *            write the article (if already exists) in the mediawiki
	 * @throws ActionException
	 *             on problems with http, cookies and io
	 * @throws ProcessException on access problems
	 * @supportedBy MediaWiki 1.9.x
	 * @supportedBy MediaWiki 1.10.x
	 */
	public final synchronized void writeContent(final ContentAccessable a)
			throws ActionException, ProcessException {

		if (!isLoggedIn()) {
			throw new ActionException("Please login first");
		}
		Hashtable<String, String> tab = new Hashtable<String, String>();
		performAction(new GetEnvironmentVars(a.getLabel(), tab, login));
		performAction(new PostModifyContent(a, tab, login));

	}

	/**
	 * Writes an iteration of ContentAccessables to the mediawiki.
	 * 
	 * @param cav
	 *            a
	 * @throws ActionException
	 *             on problems with http, cookies and io
	 * @throws ProcessException on acces problems
	 * @supportedBy MediaWiki 1.9.x
	 * @supportedBy MediaWiki 1.10.x
	 */
	public final synchronized  void writeMultContent(final Iterator<ContentAccessable> cav)
			throws ActionException, ProcessException {
		while (cav.hasNext()) {
			writeContent(cav.next());

		}
	}
	/**
	 * 
	 * @param vers alowed versions
	 * @throws ActionException on problems with http, cookies and io, 
	 * 		and especially versionExceptions on version mismatch.
	 */
	private void checkApiVersion(Version... vers) throws ActionException {
		if (versionValidation) {
			if (currentVersion == null) {
				Siteinfo s = getSiteinfo();
				currentVersion = s.getVersion();
			}
			for (int i = 0; i < vers.length; i++) {
				if (vers[i] == currentVersion
						|| currentVersion == Version.MW_WIKIPEDIA) {
					return;
				}
			}
			throw new VersionException("Current Version: " + currentVersion);
		}
	}

}
