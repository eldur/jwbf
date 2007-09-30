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
 * 
 */
package net.sourceforge.jwbf.actions.http.mw.api;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.jwbf.actions.http.ProcessException;
import net.sourceforge.jwbf.actions.http.mw.MWAction;
import net.sourceforge.jwbf.bots.MediaWikiBot;

import org.apache.commons.httpclient.methods.GetMethod;


/**
 * action class using the MediaWiki-api's "list=categorymembers " .
 *
 * @author Thomas Stock
 */
public abstract class GetCategoryMembers extends MWAction {

	/** constant value for the bllimit-parameter. **/
	private static final int LIMIT = 50;
	
	

	/**
	 * information necessary to get the next api page.
	 */
	protected String nextPageInfo = null;
	
	
	/**
	 * Name of the category.
	 */
	protected String categoryName = "";
		
		
	/**
	 * The public constructor. It will have an MediaWiki-request generated,
	 * which is then added to msgs. When it is answered,
	 * the method processAllReturningText will be called
	 * (from outside this class).
	 * For the parameters, see {@link GetCategoryMembers#generateRequest(String, String, String)}
	 */
	protected GetCategoryMembers(String nextPageInfo, String categoryName){
		this.categoryName = categoryName;
		generateContinueRequest(nextPageInfo);
	}
	
	/**
	 * The private constructor, which is used to create follow-up actions.
	 */
	public GetCategoryMembers(String articleName) {
		generateFirstRequest(articleName);
	}
	
	/**
	 * generates the next MediaWiki-request (GetMethod) and adds it to msgs.
	 *
	 * @param categoryName   the title of the article,
	 *                      may only be null if blcontinue is not null
	 * 
	 * @param cmcontinue    the value for the blcontinue parameter,
	 *                      null for the generation of the initial request
	 */
	protected void generateFirstRequest(String categoryName) {
		this.categoryName = categoryName;
	 	String uS = "";
		
		try {
		

		
				uS = "/api.php?action=query&list=categorymembers"
						+ "&cmcategory=" + URLEncoder.encode(categoryName, MediaWikiBot.CHARSET) 
						+ "&cmlimit=" + LIMIT + "&format=xml";
			
			
			msgs.add(new GetMethod(uS));
		
		} catch (UnsupportedEncodingException e) {
    	e.printStackTrace();
		}		
		
	}
	
	/**
	 * generates the next MediaWiki-request (GetMethod) and adds it to msgs.
	 *
	 * 
	 * @param cmcontinue    the value for the blcontinue parameter,
	 *                      null for the generation of the initial request
	 */
	protected void generateContinueRequest(String cmcontinue) {
	 
	 	String uS = "";
		
		try {
		

				uS = "/api.php?action=query&list=categorymembers"
						+ "&cmcategory=" + URLEncoder.encode(categoryName, MediaWikiBot.CHARSET) 
						+ "&cmcontinue=" + URLEncoder.encode(cmcontinue, MediaWikiBot.CHARSET)
						+ "&cmlimit=" + LIMIT + "&format=xml";
				

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
			+ "<categorymembers *cmcontinue=\"([^\"]*)\" */>"
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
			"<cm pageid=\"(.*?)\" ns=\"(.*?)\" title=\"(.*?)\" />");
			
		Matcher m = p.matcher(s);
		
		while (m.find()) {
			
			addCatItem(m.group(3), Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));

		}
		
	}
	
	protected abstract void addCatItem(String title, int pageid, int ns);
	

	

	
	

}
