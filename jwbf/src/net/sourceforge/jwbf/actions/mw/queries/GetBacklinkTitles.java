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
package net.sourceforge.jwbf.actions.mw.queries;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.jwbf.actions.mw.MultiAction;
import net.sourceforge.jwbf.actions.mw.util.MWAction;
import net.sourceforge.jwbf.actions.mw.util.ProcessException;
import net.sourceforge.jwbf.actions.mw.util.VersionException;
import net.sourceforge.jwbf.bots.MediaWikiBot;
import net.sourceforge.jwbf.contentRep.mw.Version;

import org.apache.commons.httpclient.methods.GetMethod;


/**
 * action class using the MediaWiki-api's "list=backlinks" 
 *
 * @author Thomas Stock
 * @author Tobias Knerr
 * @since MediaWiki 1.9.0
 */
public class GetBacklinkTitles extends MWAction implements MultiAction<String> {

	/**
	 * enum that defines the three posibilities of dealing with
	 * article lists including both redirects and non-redirects.
	 * <ul>
	 * <li>all: List all pages regardless of their redirect flag</li>
	 * <li>redirects: Only list redirects</li>
	 * <li>nonredirects: Don't list redirects</li>
	 * </ul>
	 */
	public static enum RedirectFilter {all, redirects, nonredirects};
	
	
	/** constant value for the bllimit-parameter. **/
	private static final int LIMIT = 50;
	
	/** object creating the requests that are sent to the api */
	private RequestBuilder requestBuilder = null;
	
	/**
	 * Collection that will contain the result
	 * (titles of articles linking to the target) 
	 * after performing the action has finished.
	 */
	private Collection<String> titleCollection = new ArrayList<String>();

	/**
	 * information necessary to get the next api page.
	 */
	private String nextPageInfo = null;
		
			
	/**
	 * The public constructor. It will have a MediaWiki-request generated,
	 * which is then added to msgs. When it is answered,
	 * the method processAllReturningText will be called
	 * (from outside this class).
	 * 
	 * @param articleName    the title of the article, != null
	 * @param namespace      the namespace(s) that will be searched for links,
	 *                       as a string of numbers separated by '|';
	 *                       if null, this parameter is omitted
	 * @param redirectFilter filter that determines how to handle redirects,
	 *                       must be all for MW versions before 1.11; != null
	 * @param apiVersion     version of the api to adapt requests to; != null
	 *                       
	 * @throws VersionException  if general functionality or parameter values 
	 *                           are not compatible with apiVersion value 
	 */
	public GetBacklinkTitles(String articleName, RedirectFilter redirectFilter,
			                 String namespace, Version apiVersion) 
							throws VersionException {
		
		assert apiVersion != null;
		assert articleName != null && redirectFilter != null;
		
		requestBuilder = createRequestBuilder(apiVersion);
		
		try {
			String request = requestBuilder.buildInitialRequest(articleName, redirectFilter, namespace);
			sendRequest(request);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * The private constructor, which is used to create follow-up actions.
	 * 
	 * @param nextPageInfo   value for the blcontinue parameter, != null
	 * @param requestBuilder object to use for building requests, != null
	 */
	private GetBacklinkTitles(String nextPageInfo, RequestBuilder requestBuilder) {
		
		assert requestBuilder != null;
		assert nextPageInfo != null;
		
		this.requestBuilder = requestBuilder;
		
		try {
			String request = requestBuilder.buildContinueRequest(nextPageInfo);
			sendRequest(request);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
		
	/**
	 * schedules a MediaWiki request for sending by adding it to msgs.
	 *
	 * @param request  the request string; != null
	 */
	protected void sendRequest(String request) {
	 
		assert request != null;
		
	 	msgs.add(new GetMethod(request));
		
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
		return "";
	}

	/**
	 * gets the information about a follow-up page from a provided api response.
	 * If there is one, the information for the next page parameter is 
	 * added to the nextPageInfo field.
	 *	
	 * @param s   text for parsing
	 */
	protected void parseHasMore(final String s) {
		
		// get the blcontinue-value
		
		Pattern p = Pattern.compile(
			"<query-continue>.*?"
			+ "<backlinks *blcontinue=\"([^\"]*)\" */>"
			+ ".*?</query-continue>",
			Pattern.DOTALL | Pattern.MULTILINE);
			
		Matcher m = p.matcher(s);

		if (m.find()) {			
			nextPageInfo = m.group(1);			
		}

	}

	/**
	 * picks the article name from a MediaWiki api response.
	 *	
	 * @param s   text for parsing
	 */
	protected void parseArticleTitles(String s) {
		
		// get the other backlink titles and add them all to the titleCollection
		
		Pattern p = Pattern.compile(
			"<bl pageid=\".*?\" ns=\".*?\" title=\"([^\"]*)\" (redirect=\"\" )?/>");
			
		Matcher m = p.matcher(s);
		
		while (m.find()) {
			titleCollection.add(m.group(1));
		}
		
	}
	
	/**
	 * @return   the collected article names
	 */
	public Collection<String> getResults() {
		return titleCollection;	 
	}
	
	/**
	 * @return   necessary information for the next action
	 *           or null if no next api page exists
	 */
	public GetBacklinkTitles getNextAction() {
		if( nextPageInfo == null ){ return null; }
		else{
			return new GetBacklinkTitles(nextPageInfo, requestBuilder);
		}
	}
	
	/**
	 * creates a request builder for the given api version
	 * 
	 * @throws VersionException  if no request builder class for the apiVersion
	 *                           is known
	 */
	private static RequestBuilder createRequestBuilder(Version apiVersion)
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
		 * For params, see {@link GetBacklinkTitles#GetBacklinkTitles(String, net.sourceforge.jwbf.actions.mw.queries.GetBacklinkTitles.RedirectFilter, String, Version)}y
		 *                       
		 * @throws VersionException if a param is not compatible with the
		 *                          associated MediaWiki version 
		 */
		String buildInitialRequest(String articleName, 
                RedirectFilter redirectFilter,
                String namespace) throws UnsupportedEncodingException, VersionException;

		/**
		 * generates a follow-up MediaWiki-request.
		 */
		String buildContinueRequest(String blcontinue) 
			throws UnsupportedEncodingException;
		
	}

	/** request builder for MW versions 1_11 to (at least) 1_13 */
	private static class RequestBuilder_1_11 implements RequestBuilder {

		public String buildInitialRequest(String articleName, 
				RedirectFilter redirectFilter, String namespace) throws UnsupportedEncodingException {
			
			return "/api.php?action=query&list=backlinks"
			       + "&bltitle=" + URLEncoder.encode(articleName, MediaWikiBot.CHARSET) 
			       + ((namespace!=null&&!namespace.isEmpty())?("&blnamespace="+namespace):"")
			       + "&blfilterredir=" + redirectFilter.toString() 
			       + "&bllimit=" + LIMIT + "&format=xml";			
		}
		
		public String buildContinueRequest(String blcontinue) throws UnsupportedEncodingException {
			
			return "/api.php?action=query&list=backlinks"
			       + "&blcontinue=" + URLEncoder.encode(blcontinue, MediaWikiBot.CHARSET)
			       + "&bllimit=" + LIMIT + "&format=xml";			
		}
		
	}
	
	/** request builder for MW versions 1_09 and 1_10 */
	private static class RequestBuilder_1_09 implements RequestBuilder {

		/**
		 * @throws UnsupportedEncodingException  if redirectFilter != all
		 */
		public String buildInitialRequest(String articleName, 
				RedirectFilter redirectFilter, String namespace) 
				throws UnsupportedEncodingException, VersionException {
			
			if (redirectFilter != RedirectFilter.all) {
				throw new VersionException("redirect filtering is not available in this MediaWiki version");
			}
			
			return "/api.php?action=query&list=backlinks"
			       + "&titles=" + URLEncoder.encode(articleName, MediaWikiBot.CHARSET) 
			       + ((namespace!=null&&!namespace.isEmpty())?("&blnamespace="+namespace):"")
			       + "&blfilterredir=" + redirectFilter.toString() 
			       + "&bllimit=" + LIMIT + "&format=xml";			
		}
		
		public String buildContinueRequest(String blcontinue) throws UnsupportedEncodingException {
			
			return "/api.php?action=query&list=backlinks"
			       + "&blcontinue=" + URLEncoder.encode(blcontinue, MediaWikiBot.CHARSET)
			       + "&bllimit=" + LIMIT + "&format=xml";			
		}
		
	}
	
}
