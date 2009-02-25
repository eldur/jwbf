/*
 * Copyright 2007 Tobias Knerr.
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
 *
 */
package net.sourceforge.jwbf.actions.mediawiki.queries;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.jwbf.actions.ContentProcessable;
import net.sourceforge.jwbf.actions.Get;
import net.sourceforge.jwbf.actions.mediawiki.MediaWiki;
import net.sourceforge.jwbf.actions.mediawiki.util.MWAction;
import net.sourceforge.jwbf.actions.util.ActionException;
import net.sourceforge.jwbf.actions.util.CookieException;
import net.sourceforge.jwbf.actions.util.HttpAction;
import net.sourceforge.jwbf.actions.util.ProcessException;
import net.sourceforge.jwbf.bots.MediaWikiBot;

import org.apache.commons.httpclient.Cookie;
import org.apache.log4j.Logger;

/**
 * Action class using the MediaWiki-api's "list=allpages".
 * 
 * @author Tobias Knerr
 * @author Thomas Stock
 *  
 * @supportedBy MediaWikiAPI 1.9, 1.10, 1.11, 1.12, 1.13, 1.14
 * 
 */
public class GetAllPageTitles implements Iterable<String>, Iterator<String>, ContentProcessable {
	
	private static final Logger LOG = Logger.getLogger(GetAllPageTitles.class);
	
	/** Pattern to parse returned page, @see {@link #parseHasMore(String)} */
	private static final Pattern HAS_MORE_PATTERN = Pattern
			.compile(
					"<query-continue>.*?<allpages *apfrom=\"([^\"]*)\" */>.*?</query-continue>",
					Pattern.DOTALL | Pattern.MULTILINE);
	private static final Pattern ARTICLE_TITLES_PATTERN = Pattern
			.compile("<p pageid=\".*?\" ns=\".*?\" title=\"(.*?)\" />");
	/** Pattern to parse returned page, @see {@link #parseArticleTitles(String)} */
	/** Constant value for the aplimit-parameter. **/
	private static final int LIMIT = 50;
	

	private boolean init = true;
	
	/**
	 * Information given in the constructor, necessary for creating next action.
	 */
	private String prefix;
	private String namespace;
	private boolean redirects;
	private boolean nonredirects;
	private MediaWikiBot bot;

	/** Information necessary to get the next api page. */
	private String nextPageInfo = null;
	
	private HttpAction msg;
	
	/**
	 * The public constructor. It will have an MediaWiki-request generated,
	 * which is then added to msgs. When it is answered, the method
	 * processAllReturningText will be called (from outside this class). For the
	 * parameters, see
	 * {@link GetAllPageTitles#generateRequest(String, String, boolean, boolean, String)}
	 * 
	 * @param from
	 *            page title to start from, may be null
	 * @param prefix
	 *            restricts search to titles that begin with this value, may be
	 *            null
	 * @param redirects
	 *            include redirects in the list
	 * @param bot a
	 * @param nonredirects
	 *            include nonredirects in the list (will be ignored if redirects
	 *            is false!)
	 * @param namespaces
	 *            the namespace(s) that will be searched for links, as a string
	 *            of numbers separated by '|'; if null, this parameter is
	 *            omitted
	 */
	public GetAllPageTitles(String from, String prefix, boolean redirects,
			boolean nonredirects, MediaWikiBot bot, int ... namespaces) {
		this(from, prefix, redirects, nonredirects, MWAction.createNsString(namespaces), bot);

	}
	public GetAllPageTitles(MediaWikiBot bot, int ... namespaces) {
		this(null, null, false, false, bot, namespaces);

	}

	protected GetAllPageTitles(String from, String prefix, boolean redirects,
			boolean nonredirects, String namespaces, MediaWikiBot bot) {

		this.bot = bot;

		this.prefix = prefix;
		this.namespace = namespaces;
		this.redirects = redirects;
		this.nonredirects = nonredirects;
		msg = generateRequest(from, prefix, redirects, nonredirects, namespace);
	}

	/**
	 * Generates the next MediaWiki-request (GetMethod) and adds it to msgs.
	 * 
	 * @param from
	 *            page title to start from, may be null
	 * @param prefix
	 *            restricts search to titles that begin with this value, may be
	 *            null
	 * @param redirects
	 *            include redirects in the list
	 * @param nonredirects
	 *            include nonredirects in the list (will be ignored if redirects
	 *            is false!)
	 * @param namespace
	 *            the namespace(s) that will be searched for links, as a string
	 *            of numbers separated by '|'; if null, this parameter is
	 *            omitted
	 */
	protected Get generateRequest(String from, String prefix,
			boolean redirects, boolean nonredirects, String namespace) {
		if (LOG.isTraceEnabled()) {
			LOG
					.trace("enter GetAllPagetitles.generateRequest(String,String,boolean,boolean,String)");
		}

		String apfilterredir;
		if (redirects && nonredirects) {
			apfilterredir = "all";
		} else if (redirects && !nonredirects) {
			apfilterredir = "redirects";
		} else {
			apfilterredir = "nonredirects";
		}

		String uS = "/api.php?action=query&list=allpages&"
				+ ((from != null) ? ("&apfrom=" + MediaWiki.encode(from)) : "")
				+ ((prefix != null) ? ("&apprefix=" + MediaWiki.encode(prefix))
						: "")
				+ ((namespace != null && namespace.length() != 0) ? ("&apnamespace=" + namespace)
						: "") + "&apfilterredir=" + apfilterredir + "&aplimit="
				+ LIMIT + "&format=xml";
		return new Get(uS);

	}

	

	


		
		
		public HttpAction getNextMessage() {
			init = false;
			return msg;
		}
		/**
		 * Deals with the MediaWiki api's response by parsing the provided text.
		 * 
		 * @param s
		 *            the answer to the most recently generated MediaWiki-request
		 * 
		 * @return empty string
		 */
		public String processAllReturningText(final String s)
				throws ProcessException {
			if (LOG.isTraceEnabled()) {
				LOG.trace("enter GetAllPagetitles.processAllReturningText(String)");
			}
			parseArticleTitles(s);
			parseHasMore(s);
			return "";
		}
		/**
		 * Picks the article name from a MediaWiki api response.
		 * 
		 * @param s
		 *            text for parsing
		 */
		private void parseArticleTitles(String s) {
			if (LOG.isTraceEnabled()) {
				LOG.trace("enter GetAllPagetitles.parseArticleTitles(String)");
			}
			Matcher m = ARTICLE_TITLES_PATTERN.matcher(s);
			while (m.find()) {
				String title = MediaWiki.decode(m.group(1));
				if (LOG.isDebugEnabled()) {
					LOG.debug("Found article title: \"" + title + "\"");
				}
				knownResults.add(title);
			}
		}
		/**
		 * Gets the information about a follow-up page from a provided api response.
		 * If there is one, a new request is added to msgs by calling
		 * generateRequest.
		 * 
		 * @param s
		 *            text for parsing
		 */
		private void parseHasMore(final String s) {
			if (LOG.isTraceEnabled()) {
				LOG.trace("enter GetAllPagetitles.parseHasMore(String)");
			}
			Matcher m = HAS_MORE_PATTERN.matcher(s);
			if (m.find()) {
				nextPageInfo = m.group(1);
			} else {
				nextPageInfo = null;
			}
		}
		public Iterator<String> iterator() {
			return this;
		}
		private int index = 0;
		private ArrayList<String> knownResults = new ArrayList<String>();
		/**
		 * if a new query is needed to request more; more results are
		 * requested.
		 *
		 * @return true if has next
		 */
		public boolean hasNext() {
			
			loadMoreResults();
			
			return index < knownResults.size();
		}
		/**
		 * request more results if local interation seems to be empty.
		 */
		private synchronized void loadMoreResults() {
			
			if ((nextPageInfo != null && index >= knownResults.size()) || init) {
				
				try {
					msg = generateRequest(nextPageInfo, prefix, redirects, nonredirects, namespace);
					init = true;
					bot.performAction(this);

					

				} catch (ActionException ae) {
					ae.printStackTrace();
	
				} catch (ProcessException e) {
					e.printStackTrace();
	
				}
			}

		}
		
		public String next() {
			return knownResults.get(index++);
		}
		public void remove() {
			// TODO SD
			
		}
		
		public boolean hasMoreMessages() {
			return init;
		}
		public String processReturningText(String s, HttpAction hm)
				throws ProcessException {
			processAllReturningText(s);
			return null;
		}
		public void validateReturningCookies(Cookie[] cs, HttpAction hm)
				throws CookieException {
			// TODO Auto-generated method stub
			
		}
	


}
