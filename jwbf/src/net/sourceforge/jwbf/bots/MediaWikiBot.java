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
import java.util.Iterator;

import net.sourceforge.jwbf.actions.mw.editing.FileUpload;
import net.sourceforge.jwbf.actions.mw.editing.PostDelete;
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
import net.sourceforge.jwbf.actions.mw.util.ProcessException;
import net.sourceforge.jwbf.contentRep.mw.CategoryItem;
import net.sourceforge.jwbf.contentRep.mw.ContentAccessable;
import net.sourceforge.jwbf.contentRep.mw.LogItem;
import net.sourceforge.jwbf.contentRep.mw.SimpleFile;

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
 * 
 */
public class MediaWikiBot extends MediaWikiBotImpl {

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

//	private static Logger log = Logger.getLogger(MediaWikiBot.class);
//	private LoginData login;
//
//	private Version version = null;
//	private Userinfo ui = null;

	/**
	 * Design only for extension.
	 */
	protected MediaWikiBot() {
		// do nothing, design only for extension
	}
	
	/**
	 * @param u
	 *            wikihosturl like "http://www.mediawiki.org/wiki/"
	 */
	public MediaWikiBot(final URL u) {
		super(u);

	}

	/**
	 * @param url
	 *            wikihosturl like "http://www.mediawiki.org/wiki/"
	 * @throws MalformedURLException
	 *            if param url does not represent a well-formed url
	 */
	public MediaWikiBot(final String url) throws MalformedURLException {
		super(url);

	}


	/**
	 * @see GetAllPageTitles
	 */
	public Iterable<String> getAllPageTitles(String from, String prefix,
			boolean redirects, boolean nonredirects, int... namespaces)
			throws ActionException {
		GetAllPageTitles a = new GetAllPageTitles(from, prefix, redirects,
				nonredirects, namespaces);
		return performMultiAction(a);
	}

	/**
	 * @see GetAllPageTitles
	 */
	public Iterable<String> getAllPageTitles(int... namespaces)
			throws ActionException {
		return getAllPageTitles(null, null, false, true, namespaces);


	}

	/**
	 * @see GetAllPageTitles
	 */
	public Iterable<String> getAllPageTitles(String from, String prefix,
			boolean redirects, boolean nonredirects) throws ActionException {

		return getAllPageTitles(from, prefix, redirects, nonredirects, null);

	}

	/**
	 * @see GetBacklinkTitles
	 */
	public Iterable<String> getBacklinkTitles(String article,
			RedirectFilter redirectFilter, int... namespaces)
			throws ActionException, ProcessException {

		GetBacklinkTitles a = new GetBacklinkTitles(article,
				redirectFilter, getVersion(), namespaces);

		return performMultiAction(a);
	}

	/**
	 * @see GetBacklinkTitles
	 */
	public Iterable<String> getBacklinkTitles(String article, int... namespaces)
			throws ActionException, ProcessException {

		return getBacklinkTitles(article, RedirectFilter.all, namespaces);
	}
	/**
	 * @see GetBacklinkTitle
	 */
	public Iterable<String> getBacklinkTitles(String article, RedirectFilter redirectFilter)
			throws ActionException, ProcessException {

		return getBacklinkTitles(article, redirectFilter, null);
	}


	/**
	 *  @see GetBacklinkTitle
	 */
	public Iterable<String> getBacklinkTitles(String article)
			throws ActionException, ProcessException {

		return getBacklinkTitles( article, RedirectFilter.all, null);
	}


	/**
	 * @see PostDelete
	 */
	public void postDelete(String title) throws ActionException, ProcessException {
		
		performAction(new PostDelete(title, getSiteinfo(), getUserinfo()));
	}

	/**
	 *
	 * @param category like "Buildings" or "Chemical elements" without prefix "Category:"
	 * @return of article labels
	 * @throws ActionException on any kind of http or version problems
	 * @throws ProcessException on inner problems like version mismatch
	 * @supportedBy MediaWikiAPI 1.11 categorymembers / cm  TODO Test Required
	 */
	public Iterable<String> getCategoryMembers(String category) throws ActionException, ProcessException {
		return getCategoryMembers(category, NS_MAIN);
	}

	/**
	 *
	 * @param category like "Buildings" or "Chemical elements" without prefix "Category:"
	 * @return of article labels
	 * @throws ActionException on any kind of http or version problems
	 * @throws ProcessException on inner problems like mw version
	 * @supportedBy MediaWikiAPI 1.11 categorymembers / cm  TODO Test Required
	 */
	public Iterable<String> getCategoryMembers(String category, int... namespaces) throws ActionException, ProcessException {
		
		
		return GetSimpleCategoryMembers.get(this, category, namespaces);
	}
	/**
	 *
	 * @param category like "Buildings" or "Chemical elements" without prefix Category
	 * @return of category items with more details as simple labels
	 * @throws ActionException on any kind of http or version problems
	 * @throws ProcessException on inner problems like a version mismatch
	 * @supportedBy MediaWikiAPI 1.11 categorymembers / cm TODO Test Required
	 */
	public Iterable<CategoryItem> getFullCategoryMembers(String category) throws ActionException, ProcessException {
		return getFullCategoryMembers(category, NS_MAIN);
	}
	/**
	 *
	 * @param category like "Buildings" or "Chemical elements" without prefix Category
	 * @return of category items with more details as simple labels
	 * @throws ActionException on any kind of http or version problems
	 * @throws ProcessException on inner problems like a version mismatch
	 * @supportedBy MediaWikiAPI 1.11 categorymembers / cm TODO Test Required
	 */
	public Iterable<CategoryItem> getFullCategoryMembers(String category, int... namespaces) throws ActionException, ProcessException {
		return GetFullCategoryMembers.get(this, category, namespaces);
		
	}

	/**
	 * @deprecated  use {@link FileUpload} instead 
	 * 
	 */
	public final void uploadFile(final String fileName) throws ActionException,
			ProcessException {

		File f = new File(fileName);

		SimpleFile a = new SimpleFile(f.getName(), fileName);
		performAction(new FileUpload(a, this));
		
		

	}

	/**
	 * 
	 * @deprecated  use {@link FileUpload} instead 
	 */
	public void uploadFile(SimpleFile file) throws ActionException,
			ProcessException {
		performAction(new FileUpload(file, this));
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
	
		return GetImagelinkTitles.get(this, image, namespaces);

	}

	/**
	 * variation of the getImagelinkTitles-method which does not set a namespace
	 * restriction.
	 *
	 * @param image name of
	 * @return an of labels
	 * @see #getImagelinkTitles(String, int[])
	 * @throws ActionException
	 *             on problems with http, cookies and io
	 * @see #getImagelinkTitles(String, int...)
	 */
	public Iterable<String> getImagelinkTitles(String image)
			throws ActionException {

		return getImagelinkTitles(image, null);

	}
	/**
	 * @deprecated  use {@link GetImageInfo} instead 
	 */
	public String getImageInfo(String imagename) throws ActionException, ProcessException {
		
		return GetImageInfo.get(this, imagename);
	}

	/**
	 *
	 * @param type event type like: upload, delete, ...
	 * @return the last ten log events
	 * @throws ActionException on problems with http, cookies and io
	 * @throws ProcessException 
	 * @supportedBy MediaWikiAPI 1.11 logevents / le
	 * @see #getLogEvents(int, String...)
	 */
	public Iterator<LogItem> getLogEvents(String ... type) throws ActionException, ProcessException {

		return getLogEvents(10, type);
	}

	/**
	 *
	 * @param type event type like: upload, delete, ...
	 * @param limit number of events
	 * @return the last ten log events
	 * @throws ActionException on problems with http, cookies and io
	 * @throws ProcessException 
	 * @supportedBy MediaWikiAPI 1.11 logevents / le TODO Test Required
	 * TODO API state is (semi-complete), see
	 * http://www.mediawiki.org/wiki/API:Query_-_Lists#logevents_.2F_le_.28semi-complete.29
	 */
	public Iterator<LogItem> getLogEvents(int limit, String ... type) throws ActionException, ProcessException {
		
		return GetLogEvents.get(this, limit, type);
		
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
		
		return GetTemplateUserTitles.get(this, template, namespaces);

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
	 * @see #getTemplateUserTitles(String, int...)
	 */
	public Iterable<String> getTemplateUserTitles(String template)
			throws ActionException {
		return getTemplateUserTitles(template, null);

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
		return GetRecentchanges.get(this, count, namespaces);
		
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
	 * TODO TEST
	 */
	public Iterable<String> getRecentchangesTitles(final int count)
			throws ActionException {

		return getRecentchangesTitles(count, NS_MAIN);
	}



	/**
	 * Writes an iteration of ContentAccessables to the mediawiki.
	 *
	 * @param cav
	 *            a
	 * @throws ActionException
	 *             on problems with http, cookies and io
	 * @throws ProcessException on acces problems
	 * @supportedBy MediaWiki 1.9.x, 1.10.x, 1.11.x, 1.12.x, 1.13.x, 1.14.x
	 */
	public final void writeMultContent(final Iterator<ContentAccessable> cav)
			throws ActionException, ProcessException {
		while (cav.hasNext()) {
			writeContent(cav.next());

		}
	}

}
