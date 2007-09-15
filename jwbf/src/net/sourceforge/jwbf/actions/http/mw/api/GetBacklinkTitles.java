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
package net.sourceforge.jwbf.actions.http.mw.api;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.jwbf.actions.http.ProcessException;
import net.sourceforge.jwbf.actions.http.mw.MWAction;
import net.sourceforge.jwbf.actions.http.mw.api.MultiAction;
import net.sourceforge.jwbf.bots.MediaWikiBot;

import org.apache.commons.httpclient.methods.GetMethod;


/**
 * action class using the MediaWiki-api's "list=backlinks" 
 *
 * @author Thomas Stock
 * @author Tobias Knerr
 * @since MediaWiki 1.9.0
 */
public class GetBacklinkTitles extends MWAction implements MultiAction<String> {

	/** constant value for the bllimit-parameter. **/
	private static final int LIMIT = 50;
	
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
	 * The public constructor. It will have an MediaWiki-request generated,
	 * which is then added to msgs. When it is answered,
	 * the method processAllReturningText will be called
	 * (from outside this class).
	 * For the parameters, see {@link GetBacklinkTitles#generateRequest(String, String, String)}
	 */
	public GetBacklinkTitles(String articleName, String namespace){
		generateRequest(articleName,namespace,null);
	}
	
	/**
	 * The private constructor, which is used to create follow-up actions.
	 */
	private GetBacklinkTitles(String nextPageInfo) {
		generateRequest(null,null,nextPageInfo);
	}
	
	/**
	 * generates the next MediaWiki-request (GetMethod) and adds it to msgs.
	 *
	 * @param articleName   the title of the article,
	 *                      may only be null if blcontinue is not null
	 * @param namespace     the namespace(s) that will be searched for links,
	 *                      as a string of numbers separated by '|';
	 *                      if null, this parameter is omitted
	 * @param blcontinue    the value for the blcontinue parameter,
	 *                      null for the generation of the initial request
	 */
	protected void generateRequest(String articleName, String namespace, String blcontinue){
	 
	 	String uS = "";
		
		try {
		
			if (blcontinue == null) {
		
				uS = "/api.php?action=query&list=backlinks"
						+ "&titles=" + URLEncoder.encode(articleName, MediaWikiBot.CHARSET) 
						+ ((namespace!=null)?("&blnamespace="+namespace):"")
						+ "&bllimit=" + LIMIT + "&format=xml";
			
			} else {
				
				uS = "/api.php?action=query&list=backlinks"
						+ "&blcontinue=" + URLEncoder.encode(blcontinue, MediaWikiBot.CHARSET)
						+ "&bllimit=" + LIMIT + "&format=xml";
				
			}
			
			msgs.add(new GetMethod(uS));
		
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
		String t = encodeUtf8(s);
		parseArticleTitles(t);
		parseHasMore(t);
		return "";
	}

	/**
	 * gets the information about a follow-up page from a provided api response.
	 * If there is one, a new request is added to msgs by calling generateRequest.
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
	public void parseArticleTitles(String s) {
		
		// get the backlink titles and add them all to the titleCollection
			
		Pattern p = Pattern.compile(
			"<bl pageid=\".*?\" ns=\".*?\" title=\"(.*?)\" />");
			
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
			return new GetBacklinkTitles(nextPageInfo);
		}
	}
	
	

}
