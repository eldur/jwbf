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

package net.sourceforge.jwbf.mediawiki.bots;


import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.NS_MAIN;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.actions.util.ProcessException;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.mediawiki.actions.editing.FileUpload;
import net.sourceforge.jwbf.mediawiki.actions.queries.AllPageTitles;
import net.sourceforge.jwbf.mediawiki.actions.queries.BacklinkTitles;
import net.sourceforge.jwbf.mediawiki.actions.queries.CategoryMembersFull;
import net.sourceforge.jwbf.mediawiki.actions.queries.CategoryMembersSimple;
import net.sourceforge.jwbf.mediawiki.actions.queries.ImageInfo;
import net.sourceforge.jwbf.mediawiki.actions.queries.ImageUsageTitles;
import net.sourceforge.jwbf.mediawiki.actions.queries.LogEvents;
import net.sourceforge.jwbf.mediawiki.actions.queries.RecentchangeTitles;
import net.sourceforge.jwbf.mediawiki.actions.queries.TemplateUserTitles;
import net.sourceforge.jwbf.mediawiki.actions.util.RedirectFilter;
import net.sourceforge.jwbf.mediawiki.actions.util.VersionException;
import net.sourceforge.jwbf.mediawiki.contentRep.CategoryItem;
import net.sourceforge.jwbf.mediawiki.contentRep.LogItem;
import net.sourceforge.jwbf.mediawiki.contentRep.SimpleFile;

/**
 * 
 * This class helps you to interact with each 
 * <a href="http://www.mediawiki.org" target="_blank">MediaWiki</a>. This class offers
 * a set of methods which are defined in the package net.sourceforge.jwbf.actions.mw.*
 * 
 * If you need more options, use these classes directly.
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
 * @deprecated
 */

public class MediaWikiAdapterBot extends MediaWikiBot {
	
	/**
	 * @param u
	 *            wikihosturl like "http://www.mediawiki.org/wiki/"
	 */
	public MediaWikiAdapterBot(final URL u) {
		super(u);

	}

	/**
	 * @param url
	 *            wikihosturl like "http://www.mediawiki.org/wiki/"
	 * @throws MalformedURLException
	 *            if param url does not represent a well-formed url
	 */
	public MediaWikiAdapterBot(final String url) throws MalformedURLException {
		super(url);

	}
	
	/**
	 * @param url
	 *            wikihosturl like "http://www.mediawiki.org/wiki/"
	 * @param testHostReachable if true, test if host reachable
	 * @throws IOException a
	 */
	public MediaWikiAdapterBot(final URL url, boolean testHostReachable) throws  IOException {
		super(url, testHostReachable);

	}


	/**
	 * @see AllPageTitles
	 * @deprecated use {@link AllPageTitles} directly
	 */
	public Iterable<String> getAllPageTitles(String from, String prefix,
			boolean redirects, boolean nonredirects, int... namespaces)
			throws ActionException, VersionException {
		AllPageTitles a = new AllPageTitles(this, from, prefix, RedirectFilter.nonredirects, namespaces );
		return a;
	}

	/**
	 * @see AllPageTitles
	 * @deprecated use {@link AllPageTitles} directly
	 */
	public Iterable<String> getAllPageTitles(int... namespaces)
			throws ActionException, VersionException {
		return getAllPageTitles(null, null, false, true, namespaces);


	}

	/**
	 * @see AllPageTitles
	 * @deprecated use {@link AllPageTitles} directly
	 */
	public Iterable<String> getAllPageTitles(String from, String prefix,
			boolean redirects, boolean nonredirects) throws ActionException, VersionException {

		return getAllPageTitles(from, prefix, redirects, nonredirects, null);

	}

	/**
	 * @see BacklinkTitles
	 */
	public Iterable<String> getBacklinkTitles(String article,
			RedirectFilter redirectFilter, int... namespaces)
			throws ActionException, ProcessException {

		BacklinkTitles a = new BacklinkTitles(this, article,
				redirectFilter, namespaces);

		return a;
	}

	/**
	 * @see BacklinkTitles
	 */
	public Iterable<String> getBacklinkTitles(String article, int... namespaces)
			throws ActionException, ProcessException {

		return getBacklinkTitles(article, RedirectFilter.all, namespaces);
	}
	/**
	 * @see BacklinkTitles
	 */
	public Iterable<String> getBacklinkTitles(String article, RedirectFilter redirectFilter)
			throws ActionException, ProcessException {

		return getBacklinkTitles(article, redirectFilter, null);
	}


	/**
	 *  @see BacklinkTitles
	 */
	public Iterable<String> getBacklinkTitles(String article)
			throws ActionException, ProcessException {

		return getBacklinkTitles(article, RedirectFilter.all, null);
	}




	/**
	 *
	 * @see CategoryMembersSimple
	 */
	public Iterable<String> getCategoryMembers(String category) throws ActionException, ProcessException {
		return getCategoryMembers(category, NS_MAIN);
	}

	/**
	 * @see CategoryMembersSimple
	 */
	public Iterable<String> getCategoryMembers(String category, int... namespaces) throws ActionException, ProcessException {
		CategoryMembersSimple c = new CategoryMembersSimple(this, category, namespaces);
		return c;

	}
	/**
	 *
	 * @see CategoryMembersFull
	 */
	public Iterable<CategoryItem> getFullCategoryMembers(String category) throws ActionException, ProcessException {
		return getFullCategoryMembers(category, NS_MAIN);
	}
	/**
	 *
	 * @see CategoryMembersFull
	 */
	public Iterable<CategoryItem> getFullCategoryMembers(String category, int... namespaces) throws ActionException, ProcessException {
		CategoryMembersFull c = new CategoryMembersFull(this, category, namespaces );
		return c;
		
	}

	/**
	 * @param fileName a
	 * @see FileUpload
	 * TODO exception missing
	 */
	public final void uploadFile(final String fileName) throws ActionException,
			ProcessException {

		File f = new File(fileName);

		SimpleFile a = new SimpleFile(f.getName(), fileName);
		performAction(new FileUpload(a, this));
		
		

	}

	/**
	 * @param file a
	 * @see FileUpload 
	 * TODO exception missing
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
	 *
	 * @see ImageUsageTitles
	 *
	 */
	public Iterable<String> getImagelinkTitles(String image, int... namespaces)
			throws ActionException , VersionException {
		ImageUsageTitles a = new ImageUsageTitles(this, image,
				namespaces);

		return a;

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
			throws ActionException, VersionException {

		return getImagelinkTitles(image, null);

	}
	/**
	 * @param imagename a
	 * @see ImageInfo
	 */
	public String getImageInfo(String imagename) throws ActionException, ProcessException {
		
		ImageInfo a = new ImageInfo(this, imagename);
		return a.getUrlAsString();
	}

	/**
	 *
	 * @param type event type like: upload, delete, ...
	 * @return the last ten log events
	 * @throws ActionException on problems with http, cookies and io
	 * @throws ProcessException 
	 */
	public Iterator<LogItem> getLogEvents(String type) throws ActionException, ProcessException {

		return getLogEvents(10, type);
	}

	/**
	 *
	 * @param type event type like: upload, delete, ...
	 * @param limit number of events
	 * @return the last ten log events
	 * @throws ActionException on problems with http, cookies and io
	 * @throws ProcessException 
	 */
	public Iterator<LogItem> getLogEvents(int limit, String type) throws ActionException, ProcessException {
		LogEvents c = new LogEvents(this, limit, type);
		
		return c;
		
		
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
	 */
	public Iterable<String> getTemplateUserTitles(String template,
			int... namespaces) throws ActionException, VersionException {
		
		TemplateUserTitles a = new TemplateUserTitles(this, template,
				namespaces);

		return a;

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
			throws ActionException, VersionException {
		return getTemplateUserTitles(template, null);

	}



	/**
	 * Get a number of recent changes from namespace.
	 *
	 * @param namespaces
	 *            namespacenumbers greater equals 0
	 * @return a
	 * @throws ActionException
	 *             on problems with http, cookies and io
	 * @see RecentchangeTitles
	 */
	public Iterable<String> getRecentchangesTitles(
			int... namespaces) throws ActionException, VersionException {
		RecentchangeTitles a = new RecentchangeTitles(this,
				namespaces);
		
		return a;
		
	}

	/**
	 * Get a number of recent changes from default namespace.
	 *
	 * @return a
	 * @throws ActionException
	 *             on problems with http, cookies and io
	 */
	public Iterable<String> getRecentchangesTitles()
			throws ActionException, VersionException {

		return getRecentchangesTitles(NS_MAIN);
	}



	/**
	 * Writes an iteration of ContentAccessables to the mediawiki.
	 *
	 * @param cav
	 *            a
	 * @throws ActionException
	 *             on problems with http, cookies and io
	 * @throws ProcessException on access problems
	 */
	public final void writeMultContent(final Iterator<SimpleArticle> cav)
			throws ActionException, ProcessException {
		while (cav.hasNext()) {
			writeContent(cav.next());

		}
	}

}
