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

import java.io.UnsupportedEncodingException;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.jwbf.actions.Get;
import net.sourceforge.jwbf.actions.mediawiki.MediaWiki;
import net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.actions.mediawiki.util.VersionException;
import net.sourceforge.jwbf.actions.util.ActionException;
import net.sourceforge.jwbf.actions.util.HttpAction;
import net.sourceforge.jwbf.actions.util.ProcessException;
import net.sourceforge.jwbf.bots.MediaWikiBot;

import org.apache.log4j.Logger;


/**
 * action class using the MediaWiki-api's "list=backlinks" 
 *
 * @author Thomas Stock
 * @author Tobias Knerr
 * @since JWBF 1.1
 * @supportedBy MediaWikiAPI 1.9, 1.10, 1.11, 1.12, 1.13, 1.14
 */
public class BacklinkTitles extends TitleQuery {

	/**
	 * enum that defines the three posibilities of dealing with
	 * article lists including both redirects and non-redirects.
	 * <ul>
	 * <li>all: List all pages regardless of their redirect flag</li>
	 * <li>redirects: Only list redirects</li>
	 * <li>nonredirects: Don't list redirects</li>
	 * </ul>
	 */
	private Logger log = Logger.getLogger(getClass());
	public static enum RedirectFilter {all, redirects, nonredirects};
	
	private Get msg;
	/** constant value for the bllimit-parameter. **/
	private static final int LIMIT = 50;
	
	/** object creating the requests that are sent to the api */
	private RequestBuilder requestBuilder = null;
	private boolean init = true;
	private boolean hasMoreResults = true;
	private final String articleName; 

	/**
	 * Collection that will contain the result
	 * (titles of articles linking to the target) 
	 * after performing the action has finished.
	 */
	private Vector<String> titleCollection = new Vector<String>();
	
	/**
	 * information necessary to get the next api page.
	 */
	private String nextPageInfo = null;
		
	
	private MediaWikiBot bot;
	private final RedirectFilter rf;
	private final int [] namespaces;
	/**
	 * The public constructor. It will have a MediaWiki-request generated,
	 * which is then added to msgs. When it is answered,
	 * the method processAllReturningText will be called
	 * (from outside this class).
	 * 
	 * @param articleName    the title of the article, != null
	 * @param namespace      the namespace(s) that will be searched for links,
	 *                       as a string of numbers separated by '|';
	 *                       if null, this parameter is omitted. 
	 *                       See for e.g. {@link MediaWiki#NS_ALL}.
	 * @param redirectFilter filter that determines how to handle redirects,
	 *                       must be all for MW versions before 1.11; != null
	 * @param bot	         a
	 *                       
	 * @throws VersionException  if general functionality or parameter values 
	 *                           are not compatible with apiVersion value 
	 */
	 public BacklinkTitles( MediaWikiBot bot, String articleName, RedirectFilter redirectFilter,
			 int ... namespace) throws VersionException {
		
		assert bot != null;
		assert articleName != null && redirectFilter != null;
		this.rf = redirectFilter;
		namespaces = namespace;
		
		
		this.articleName = articleName;
		this.bot = bot;
		requestBuilder = createRequestBuilder(bot.getVersion());
		
		
	}
	/**
	 * 
	 * @param articleName a
	 * @param bot a
	 * @throws VersionException if action is not supported
	 */
	 public BacklinkTitles( MediaWikiBot bot, String articleName) 
			throws VersionException {
		 this(bot, articleName, RedirectFilter.all, null);
		 
	 }
	 
	/**
	 * The private constructor, which is used to create follow-up actions.
	 * 
	 * @param nextPageInfo   value for the blcontinue parameter, != null
	 * @param requestBuilder object to use for building requests, != null
	 */
	private void prepareContinueReq() {
		
		
		
		try {
			String request = requestBuilder.buildContinueRequest(nextPageInfo);
			msg = new Get(request);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
		
	
	
	/**
	 * deals with the MediaWiki api's response by parsing the provided text.
	 *
	 * @param s   the answer to the most recently generated MediaWiki-request
	 *
	 * @return empty string
	 */
	public String processAllReturningText(final String s) throws ProcessException {
		String t = s;
		parseArticleTitles(t);
		parseHasMore(t);
		log.debug("reading data");
		return "";
	}

	/**
	 * gets the information about a follow-up page from a provided api response.
	 * If there is one, the information for the next page parameter is 
	 * added to the nextPageInfo field.
	 *	
	 * @param s   text for parsing
	 */
	private void parseHasMore(final String s) {
		
		// get the blcontinue-value
		hasMoreResults = false;
		Pattern p = Pattern.compile(
			"<query-continue>.*?"
			+ "<backlinks *blcontinue=\"([^\"]*)\" */>"
			+ ".*?</query-continue>",
			Pattern.DOTALL | Pattern.MULTILINE);
			
		Matcher m = p.matcher(s);

		if (m.find()) {			
			nextPageInfo = m.group(1);
			hasMoreResults = true;
			prepareContinueReq();
		} else {
			hasMoreResults = false;
		}

	}

	/**
	 * picks the article name from a MediaWiki api response.
	 *	
	 * @param s   text for parsing
	 */
	private void parseArticleTitles(String s) {
		
		// get the other backlink titles and add them all to the titleCollection
		
		Pattern p = Pattern.compile(
			"<bl pageid=\".*?\" ns=\".*?\" title=\"([^\"]*)\" (redirect=\"\" )?/>");
			
		Matcher m = p.matcher(s);
		
		while (m.find()) {
			titleCollection.add(m.group(1));
			
		}

		titleIterator = titleCollection.iterator();
		
	}
	
	

	
	/**
	 * creates a request builder for the given api version
	 * 
	 * @throws VersionException  if no request builder class for the apiVersion
	 *                           is known
	 */
	private RequestBuilder createRequestBuilder(Version apiVersion)
		throws VersionException {
		
		switch (apiVersion) {

		case MW1_09:
		case MW1_10:
			return new RequestBuilder_1_09();

		default: //MW1_11 and up
			return new RequestBuilder_1_11();

		}
		
	}
	
	/** interface for classes that create a request strings */	
	private static interface RequestBuilder {
		
		/**
		 * generates an initial MediaWiki-request.
		 * For params, see {@link BacklinkTitles#GetBacklinkTitles(String, net.sourceforge.jwbf.actions.mw.BacklinkTitles.GetBacklinkTitles.RedirectFilter, String, Version)}y
		 *                       
		 * @throws VersionException if a param is not compatible with the
		 *                          associated MediaWiki version 
		 */
		String buildInitialRequest(String articleName, 
                RedirectFilter redirectFilter,
                int [] namespace) throws UnsupportedEncodingException, VersionException;

		/**
		 * generates a follow-up MediaWiki-request.
		 */
		String buildContinueRequest(String blcontinue) 
			throws UnsupportedEncodingException;
		
	}

	/** request builder for MW versions 1_11 to (at least) 1_13 */
	private static class RequestBuilder_1_11 implements RequestBuilder {

		public String buildInitialRequest(String articleName, 
				RedirectFilter redirectFilter, int [] namespace)  {
			
			return "/api.php?action=query&list=backlinks"
			       + "&bltitle=" + MediaWiki.encode(articleName) 
			       + ((namespace!=null && createNsString(namespace).length() != 0)?("&blnamespace="+MediaWiki.encode(createNsString(namespace))):"")
			       + "&blfilterredir=" + MediaWiki.encode(redirectFilter.toString())
			       + "&bllimit=" + LIMIT + "&format=xml";			
		}
		
		public String buildContinueRequest(String blcontinue) {
			
			return "/api.php?action=query&list=backlinks"
			       + "&blcontinue=" + MediaWiki.encode(blcontinue)
			       + "&bllimit=" + LIMIT + "&format=xml";			
		}
		
	}
	
	/** request builder for MW versions 1_09 and 1_10 */
	private static class RequestBuilder_1_09 implements RequestBuilder {

		/**
		 * @throws UnsupportedEncodingException  if redirectFilter != all
		 */
		public String buildInitialRequest(String articleName, 
				RedirectFilter redirectFilter, int [] namespace) 
				throws VersionException {
			
			if (redirectFilter != RedirectFilter.all) {
				throw new VersionException("redirect filtering is not available in this MediaWiki version");
			}
			
			return "/api.php?action=query&list=backlinks"
			       + "&titles=" + MediaWiki.encode(articleName) 
			       + ((namespace!=null && createNsString(namespace).length() != 0)?("&blnamespace="+MediaWiki.encode(createNsString(namespace))):"")
			       + "&blfilterredir=" + MediaWiki.encode(redirectFilter.toString())
			       + "&bllimit=" + LIMIT + "&format=xml";			
		}
		
		public String buildContinueRequest(String blcontinue) {
			
			return "/api.php?action=query&list=backlinks"
			       + "&blcontinue=" + MediaWiki.encode(blcontinue)
			       + "&bllimit=" + LIMIT + "&format=xml";			
		}
		
	}
	
	protected void prepareCollection() {
		try {
			if (init || (!titleIterator.hasNext() && hasMoreResults)) {
				if (init) {

					

					try {
						String request = requestBuilder.buildInitialRequest(
								articleName, rf, namespaces);
						msg = new Get(request);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
				init = false;
				try {

					bot.performAction(this);
					setHasMoreMessages(true);
					if (log.isDebugEnabled())
						log.debug("preparing success");
				} catch (ActionException e) {
					e.printStackTrace();
					setHasMoreMessages(false);
				} catch (ProcessException e) {
					e.printStackTrace();
					setHasMoreMessages(false);
				}

			}
		} catch (VersionException e) {
			e.printStackTrace();
		}
	}


	public HttpAction getNextMessage() {
		return msg;
	}


	@Override
	protected Object clone() throws CloneNotSupportedException {
		try {
			return new BacklinkTitles(bot, articleName,rf, namespaces);
		} catch (VersionException e) {
			throw new CloneNotSupportedException(e.getLocalizedMessage());
		}
	}
	

	
}
