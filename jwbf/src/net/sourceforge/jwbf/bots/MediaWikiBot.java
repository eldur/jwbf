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
 * Justus Bisser - thanks for file upload methods
 */

package net.sourceforge.jwbf.bots;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import net.sourceforge.jwbf.actions.mw.MultiAction;
import net.sourceforge.jwbf.actions.mw.editing.FileUpload;
import net.sourceforge.jwbf.actions.mw.editing.GetRevision;
import net.sourceforge.jwbf.actions.mw.login.PostLoginOld;
import net.sourceforge.jwbf.actions.mw.meta.GetSiteinfo;
import net.sourceforge.jwbf.actions.mw.meta.GetVersion;
import net.sourceforge.jwbf.actions.mw.queries.GetAllPageTitles;
import net.sourceforge.jwbf.actions.mw.queries.GetBacklinkTitles;
import net.sourceforge.jwbf.actions.mw.queries.GetFullCategoryMembers;
import net.sourceforge.jwbf.actions.mw.queries.GetImageInfo;
import net.sourceforge.jwbf.actions.mw.queries.GetImagelinkTitles;
import net.sourceforge.jwbf.actions.mw.queries.GetLogEvents;
import net.sourceforge.jwbf.actions.mw.queries.GetRecentchanges;
import net.sourceforge.jwbf.actions.mw.queries.GetSimpleCategoryMembers;
import net.sourceforge.jwbf.actions.mw.queries.GetTemplateUserTitles;
import net.sourceforge.jwbf.actions.mw.queries.GetBacklinkTitles.RedirectFilter;
import net.sourceforge.jwbf.actions.mw.util.ActionException;
import net.sourceforge.jwbf.actions.mw.util.GetEnvironmentVars;
import net.sourceforge.jwbf.actions.mw.util.MWAction;
import net.sourceforge.jwbf.actions.mw.util.PostModifyContent;
import net.sourceforge.jwbf.actions.mw.util.ProcessException;
import net.sourceforge.jwbf.bots.util.LoginData;
import net.sourceforge.jwbf.contentRep.mw.CategoryItem;
import net.sourceforge.jwbf.contentRep.mw.ContentAccessable;
import net.sourceforge.jwbf.contentRep.mw.LogItem;
import net.sourceforge.jwbf.contentRep.mw.SimpleArticle;
import net.sourceforge.jwbf.contentRep.mw.SimpleFile;
import net.sourceforge.jwbf.contentRep.mw.Siteinfo;
import net.sourceforge.jwbf.contentRep.mw.Version;

import org.apache.log4j.Logger;

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
 * @author Justus Bisser
 */
public class MediaWikiBot extends HttpBot {

	public static final int ARTICLE = 1 << 1;
	public static final int MEDIA = 1 << 2;
	public static final int SUBCATEGORY = 1 << 3;
	
	public static final String CHARSET = "utf-8";
	
	public static final int NS_IMAGES = 6;
	public static final int NS_IMAGES_DISCUSSION = 7;
	// TODO Add missing NS variables
	
	private static Logger log = Logger.getLogger(MediaWikiBot.class);
	private LoginData login;
	private boolean loggedIn = false;
	
	private Version version = null;
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
		if (!(url.endsWith(".php") || url.endsWith("/"))) {
			throw new MalformedURLException("(" + url + ") url must end with slash or .php");
		}
		setConnection(url);

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
			performAction(new PostLoginOld(username, passwd, null));
		} catch (ProcessException e) {
			e.printStackTrace();
		}
		loggedIn = true;
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
	public final void httpLogin(final String username, final String passwd, final String domain)
			throws ActionException {
		try {
			performAction(new PostLoginOld(username, passwd, domain));
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
	 * @param domain
	 *            login domain
	 * @throws ActionException
	 *             on problems with http, cookies and io
	 * @supportedBy MediaWiki 1.9.x
	 */
	public final void login(final String username, final String passwd, final String domain)
			throws ActionException {
		// code for 1.9.x API
		// PostLogin pl = new PostLogin(username, passwd);
		// performAction(pl);
		// login = pl.getLoginData();
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
	public final void login(final String username, final String passwd)
			throws ActionException {
		// code for 1.9.x API
		// PostLogin pl = new PostLogin(username, passwd);
		// performAction(pl);
		// login = pl.getLoginData();
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
	public final SimpleArticle readContent(final String name, final int properties)
			throws ActionException, ProcessException {
		SimpleArticle a = null;
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
	public final SimpleArticle readContent(final String name)
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
	private String createNsString(int... namespaces) {

		String namespaceString = "";

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
	 * @supportedBy MediaWikiAPI 1.9 allpages / ap TODO Test Required
	 * @supportedBy MediaWikiAPI 1.10 allpages / ap TODO Test Required
	 * @supportedBy MediaWikiAPI 1.11 allpages / ap TODO Test Required
	 */
	public Iterable<String> getAllPageTitles(String from, String prefix,
			boolean redirects, boolean nonredirects, int... namespaces)
			throws ActionException {
		GetAllPageTitles a = new GetAllPageTitles(from, prefix, redirects,
				nonredirects, createNsString(namespaces));

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
				createNsString(namespaces));

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
	 * get the titles of all pages which contain a link to the given article;
	 * this includes redirects to the page.
	 * 
	 * @param article
	 *            title of an article
	 * 
	 * @param redirectFilter 
	 *            filter that determines how to handle redirects
	 * @param namespaces
	 *            numbers of the namespaces (specified using varargs) that will
	 *            be included in the search;
	 *            leaving this parameter out will include all namespaces
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
	 * @supportedBy MediaWikiAPI 1.9 backlinks / bl TODO Test Required
	 * @supportedBy MediaWikiAPI 1.10 backlinks / bl TODO Test Required
	 * @supportedBy MediaWikiAPI 1.11 backlinks / bl TODO Test Required
	 */
	public Iterable<String> getBacklinkTitles(String article, 
			RedirectFilter redirectFilter, int... namespaces)
			throws ActionException {
		
		GetBacklinkTitles a = new GetBacklinkTitles(article,
				redirectFilter, createNsString(namespaces), getVersion());

		return performMultiAction(a);

	}
	
	/**
	 * variation of the getBacklinkTitles-method that returns 
	 * both redirects and non-redirects linking to the page.
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
	 * @supportedBy MediaWikiAPI 1.9 backlinks / bl TODO Test Required
	 * @supportedBy MediaWikiAPI 1.10 backlinks / bl TODO Test Required
	 * @supportedBy MediaWikiAPI 1.11 backlinks / bl TODO Test Required
	 */
	public Iterable<String> getBacklinkTitles(String article, int... namespaces)
			throws ActionException {

		return getBacklinkTitles(article, RedirectFilter.all, namespaces);
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
	 * @supportedBy MediaWikiAPI 1.11 categorymembers / cm  TODO Test Required
	 */
	public Iterable<String> getCategoryMembers(String category) throws ActionException {
		GetSimpleCategoryMembers c = new GetSimpleCategoryMembers(category, "", getVersion());
		return performMultiAction(c);
	}
	
	/**
	 * 
	 * @param category like "Buildings" or "Chemical elements" without prefix "Category:"
	 * @return of article labels
	 * @throws ActionException on any kind of http or version problems
	 * @supportedBy MediaWikiAPI 1.11 categorymembers / cm  TODO Test Required
	 */
	public Iterable<String> getCategoryMembers(String category, int... namespaces) throws ActionException {
		GetSimpleCategoryMembers c = new GetSimpleCategoryMembers(category, createNsString(namespaces), getVersion());
		return performMultiAction(c);
	}
	/**
	 * 
	 * @param category like "Buildings" or "Chemical elements" without prefix Category
	 * @return of category items with more details as simple labels
	 * @throws ActionException on any kind of http or version problems
	 * @supportedBy MediaWikiAPI 1.11 categorymembers / cm TODO Test Required
	 */
	public Iterable<CategoryItem> getFullCategoryMembers(String category) throws ActionException {
		GetFullCategoryMembers c = new GetFullCategoryMembers(category, "", getVersion());
		return performMultiAction(c);
	}
	/**
	 * 
	 * @param category like "Buildings" or "Chemical elements" without prefix Category
	 * @return of category items with more details as simple labels
	 * @throws ActionException on any kind of http or version problems
	 * @supportedBy MediaWikiAPI 1.11 categorymembers / cm TODO Test Required
	 */
	public Iterable<CategoryItem> getFullCategoryMembers(String category, int... namespaces) throws ActionException {
		GetFullCategoryMembers c = new GetFullCategoryMembers(category, createNsString(namespaces), getVersion());
		return performMultiAction(c);
	}
	
	/**
	 * uploads a file
	 * 
	 * @param fileName
	 *            the name of the file as String
	 * @supportedBy MediaWiki 1.11
	 * @supportedBy MediaWiki NO API TODO Test Required
	 */
	public final void uploadFile(final String fileName) throws ActionException,
			ProcessException {

		File f = new File(fileName);

		SimpleFile a = new SimpleFile(f.getName(), fileName);
		uploadFile(a);

	}

	/**
	 * uploads a file
	 * 
	 * @param FileName
	 *            the file as SimpleFile
	 * @supportedBy MediaWiki 1.11
	 * @supportedBy MediaWiki NO API  TODO Test Required
	 */
	public final void uploadFile(SimpleFile file) throws ActionException,
			ProcessException {

		if (!isLoggedIn()) {
			throw new ActionException("Please login first");
		}

		Hashtable<String, String> tab = new Hashtable<String, String>();
		performAction(new GetEnvironmentVars(file.getLabel(), tab, login));
		performAction(new FileUpload(file, tab, login));

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
	 * @supportedBy MediaWikiAPI 1.9 embeddedin / ei TODO Test Required
	 * @supportedBy MediaWikiAPI 1.10 embeddedin / ei TODO Test Required
	 * 
	 */
	public Iterable<String> getImagelinkTitles(String image, int... namespaces)
			throws ActionException {
		GetImagelinkTitles a = new GetImagelinkTitles(image,
				createNsString(namespaces));

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
	
	public String getImageInfo(String imagename) throws ActionException, ProcessException {
		GetImageInfo a = new GetImageInfo(imagename, getVersion());
		performAction(a);
		return a.getUrlAsString();
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
	 * @supportedBy MediaWikiAPI 1.11 logevents / le TODO Test Required
	 * TODO API state is (semi-complete), see 
	 * http://www.mediawiki.org/wiki/API:Query_-_Lists#logevents_.2F_le_.28semi-complete.29
	 */
	public Iterator<LogItem> getLogEvents(int limit, String ... type) throws ActionException {
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
	 * @supportedBy MediaWikiAPI 1.9 embeddedin / ei TODO Test Required
	 * @supportedBy MediaWikiAPI 1.10 embeddedin / ei TODO Test Required
	 * @supportedBy MediaWikiAPI 1.11 embeddedin / ei TODO Test Required
	 */
	public Iterable<String> getTemplateUserTitles(String template,
			int... namespaces) throws ActionException {
		GetTemplateUserTitles a = new GetTemplateUserTitles(template,
				createNsString(namespaces));

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
	 * @supportedBy MediaWikiAPI 1.9 embeddedin / ei TODO Test Required
	 * @supportedBy MediaWikiAPI 1.10 embeddedin / ei TODO Test Required
	 * @supportedBy MediaWikiAPI 1.11 embeddedin / ei TODO Test Required
	 */
	public Iterable<String> getTemplateUserTitles(String template)
			throws ActionException {
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
	 * @supportedBy MediaWikiAPI 1.10 recentchanges / rc TODO Test Required
	 * @supportedBy MediaWikiAPI 1.10 recentchanges / rc TODO Test Required
	 */
	public Iterable<String> getRecentchangesTitles(final int count,
			int... namespaces) throws ActionException {
		GetRecentchanges a = new GetRecentchanges(count,
				createNsString(namespaces));

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
	public final void writeContent(final ContentAccessable a)
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
	public final void writeMultContent(final Iterator<ContentAccessable> cav)
			throws ActionException, ProcessException {
		while (cav.hasNext()) {
			writeContent(cav.next());

		}
	}

	
	public Version getVersion() {
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

}
