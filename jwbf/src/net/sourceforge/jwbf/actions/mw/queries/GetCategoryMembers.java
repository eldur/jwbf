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
package net.sourceforge.jwbf.actions.mw.queries;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.jwbf.actions.mw.util.MWAction;
import net.sourceforge.jwbf.actions.mw.util.ProcessException;
import net.sourceforge.jwbf.actions.mw.util.VersionException;
import net.sourceforge.jwbf.bots.MediaWikiBot;
import net.sourceforge.jwbf.contentRep.mw.Version;

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
	
	protected Version v = Version.UNKNOWN;
	private RequestBuilder r = null;
	
	protected String namespace = "";
		
	/**
	 * The public constructor. It will have an MediaWiki-request generated,
	 * which is then added to msgs. When it is answered,
	 * the method processAllReturningText will be called
	 * (from outside this class).
	 * For the parameters, see {@link GetCategoryMembers#generateRequest(String, String, String)}
	 * @throws VersionException on version problems
	 */
	protected GetCategoryMembers(String nextPageInfo, String categoryName, String namespace, Version v) throws VersionException{
		this.categoryName = categoryName;
		this.namespace = namespace;
		this.v = v;
		createRequestor();
		generateContinueRequest(nextPageInfo);
	}
	
	/**
	 * The private constructor, which is used to create follow-up actions.
	 * @throws VersionException on version problems
	 */
	public GetCategoryMembers(String categoryName, String namespace, Version v) throws VersionException {
		this.namespace = namespace;
		this.v = v;
		createRequestor();
		
		generateFirstRequest(categoryName);
	}
	
	private void createRequestor() throws VersionException {

		switch (v) {
		case MW1_09:
		case MW1_10:
			throw new VersionException("Not supportet by this version of MW");

		case MW1_11:
			r = new RequestBuilder_1_11();
			break;

		default:
			r = new RequestBuilder();
			break;
		}

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
	protected final void generateFirstRequest(String categoryName) {
		this.categoryName = categoryName.replace(" ", "_");
	 	
		try {
		
			msgs.add(new GetMethod(r.first(categoryName)));
		
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
	protected final void generateContinueRequest(String cmcontinue) {
	 
	 	
		
		try {
			
			msgs.add(new GetMethod(r.continiue(cmcontinue)));
		
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
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
		parseArticleTitles(s);
		parseHasMore(s);
		return "";
	}

	/**
	 * gets the information about a follow-up page from a provided api response.
	 * If there is one, a new request is added to msgs by calling generateRequest.
	 *	
	 * @param s   text for parsing
	 */
	private void parseHasMore(final String s) {
			
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
	public final void parseArticleTitles(String s) {
		
		// get the backlink titles and add them all to the titleCollection
			
		Pattern p = Pattern.compile(
			"<cm pageid=\"(.*?)\" ns=\"(.*?)\" title=\"(.*?)\" />");
			
		Matcher m = p.matcher(s);
		
		while (m.find()) {
			
			addCatItem(m.group(3), Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));

		}
		
	}
	
	protected abstract void addCatItem(String title, int pageid, int ns);
	

	private class RequestBuilder_1_11 extends RequestBuilder {
		
		RequestBuilder_1_11() {
			super();
		}
		String continiue(String cmcontinue) throws UnsupportedEncodingException {
			String uS = "";	
			String nsinj = "";
			if (namespace.length() > 0) {
				nsinj = "&cmnamespace=" + namespace;
			}
				uS = "/api.php?action=query&list=categorymembers"
						+ "&cmcategory=" + URLEncoder.encode(categoryName, MediaWikiBot.CHARSET) 
						+ nsinj
						+ "&cmcontinue=" + URLEncoder.encode(cmcontinue, MediaWikiBot.CHARSET)
						+ "&cmlimit=" + LIMIT + "&format=xml";
				return uS;
		}
		
		String first(String categoryName) throws UnsupportedEncodingException {
			String uS = "";
			String nsinj = "";
			if (namespace.length() > 0) {
				nsinj = "&cmnamespace=" + namespace;
			}
			
				uS = "/api.php?action=query&list=categorymembers"
						+ "&cmcategory=" + URLEncoder.encode(categoryName, MediaWikiBot.CHARSET) 
						+ nsinj
						+ "&cmlimit=" + LIMIT + "&format=xml";
				return uS;
		}

		
		
	}
	
	private class RequestBuilder {
		
		RequestBuilder() {
			
		}
		
		String continiue(String cmcontinue) throws UnsupportedEncodingException {
			String uS = "";	
			String nsinj = "";
			if (namespace.length() > 0) {
				nsinj = "&cmnamespace=" + namespace;
			}
			
			//TODO: do not add Category: - instead, change other methods' descs (e.g. in MediaWikiBot)
			
				uS = "/api.php?action=query&list=categorymembers"
						+ "&cmtitle=Category:" + URLEncoder.encode(categoryName, MediaWikiBot.CHARSET) 
						+ nsinj
						+ "&cmcontinue=" + URLEncoder.encode(cmcontinue, MediaWikiBot.CHARSET)
						+ "&cmlimit=" + LIMIT + "&format=xml";
				return uS;
		}
		
		String first(String categoryName) throws UnsupportedEncodingException {
			String uS = "";
			String nsinj = "";
			if (namespace.length() > 0) {
				nsinj = "&cmnamespace=" + namespace;
			}
		
				//TODO: do not add Category: - instead, change other methods' descs (e.g. in MediaWikiBot)
			
				uS = "/api.php?action=query&list=categorymembers"
						+ "&cmtitle=Category:" + URLEncoder.encode(categoryName, MediaWikiBot.CHARSET) 
						+ nsinj
						+ "&cmlimit=" + LIMIT + "&format=xml";
				return uS;
		}
		
	}

	
	

}
