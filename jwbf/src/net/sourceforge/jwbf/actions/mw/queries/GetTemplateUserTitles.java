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
import net.sourceforge.jwbf.bots.MediaWikiBot;

import org.apache.commons.httpclient.methods.GetMethod;


/**
 * action class using the MediaWiki-api's "list=embeddedin"
 * that is used to find all articles which use a template.
 *
 * @author Tobias Knerr
 * @since MediaWiki 1.9.0
 */
public class GetTemplateUserTitles extends MWAction implements MultiAction<String> {

	/** constant value for the eilimit-parameter. **/
	private static final int LIMIT = 50;
	
	/**
	 * Collection that will contain the result
	 * (titles of articles using the template) 
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
	 * For the parameters, see {@link GetTemplateUserTitles#generateRequest(String, String, String)}
	 */
	public GetTemplateUserTitles(String templateName, String namespace){
		generateRequest(templateName,namespace,null);
	}
	
	/**
	 * The private constructor, which is used to create follow-up actions.
	 */
	private GetTemplateUserTitles(String nextPageInfo) {
		generateRequest(null,null,nextPageInfo);
	}
	
	/**
	 * generates the next MediaWiki-request (GetMethod) and adds it to msgs.
	 *
	 * @param templateName   the name of the template,
	 *                      may only be null if eicontinue is not null
	 * @param namespace     the namespace(s) that will be searched for links,
	 *                      as a string of numbers separated by '|';
	 *                      if null, this parameter is omitted
	 * @param eicontinue    the value for the eicontinue parameter,
	 *                      null for the generation of the initial request
	 */
	protected void generateRequest(String templateName, String namespace,
		String eicontinue){
	 
	 	String uS = "";
		
		try {
		
			if (eicontinue == null) {
		
				uS = "/api.php?action=query&list=embeddedin"
						+ "&eititle=" + URLEncoder.encode(templateName, MediaWikiBot.CHARSET) 
						+ ((namespace!=null&&!namespace.isEmpty())?("&einamespace="+namespace):"")
						+ "&eilimit=" + LIMIT + "&format=xml";
			
			} else {
				
				uS = "/api.php?action=query&list=embeddedin"
						+ "&eicontinue=" + URLEncoder.encode(eicontinue, MediaWikiBot.CHARSET)
						+ "&eilimit=" + LIMIT + "&format=xml";
				
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
		String t = s;
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
			
		// get the eicontinue-value
		
		Pattern p = Pattern.compile(
			"<query-continue>.*?"
			+ "<embeddedin *eicontinue=\"([^\"]*)\" */>"
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
			"<ei pageid=\".*?\" ns=\".*?\" title=\"(.*?)\" />");
			
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
	public GetTemplateUserTitles getNextAction() {
		if( nextPageInfo == null ){ return null; }
		else{
			return new GetTemplateUserTitles(nextPageInfo);
		}
	}
	
	

}
