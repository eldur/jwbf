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
package net.sourceforge.jwbf.mediawiki.actions.queries;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_11;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_12;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_13;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_14;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_15;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.core.actions.util.ProcessException;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.actions.util.SupportedBy;
import net.sourceforge.jwbf.mediawiki.actions.util.VersionException;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import org.apache.log4j.Logger;


/**
 * A abstract action class using the MediaWiki-api's "list=categorymembers ".
 * For further information see 
 * <a href="http://www.mediawiki.org/wiki/API:Query_-_Lists#categorymembers_.2F_cm">API documentation</a>.
 * 
 * TODO change visibilty to package, refactor test
 * @author Thomas Stock
 */

@SupportedBy({ MW1_11, MW1_12, MW1_13, MW1_14, MW1_15 })
abstract class CategoryMembers extends MWAction {

	

	/** constant value for the bllimit-parameter. **/
	private static final int LIMIT = 50;
	
	
	
	protected final MediaWikiBot bot;
	/**
	 * information necessary to get the next api page.
	 */
	protected String nextPageInfo = null;
	protected boolean hasMoreResults = false;
	
	protected boolean init = true;
	/**
	 * Name of the category.
	 */
	protected final String categoryName;
	
	
	private RequestBuilder r = null;
	
	protected final int [] namespace;
	private String namespaceStr = "";
	
	private Logger log = Logger.getLogger(getClass());

	
	/**
	 * The private constructor, which is used to create follow-up actions.
	 * @throws VersionException on version problems
	 */
	protected CategoryMembers(MediaWikiBot bot, String categoryName, int [] namespace) throws VersionException {
		super(bot.getVersion());
		this.namespace = namespace;
		namespaceStr = createNsString(namespace);
		this.categoryName = categoryName.replace(" ", "_");
		this.bot = bot;
		createRequestor();
		
	}
	
	private void createRequestor() throws VersionException {

		switch (bot.getVersion()) {
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
	 * @return a
	 */
	protected final Get generateFirstRequest() {

		return new Get(r.first(categoryName));
	}
	
	/**
	 * generates the next MediaWiki-request (GetMethod) and adds it to msgs.
	 *
	 * 
	 * @param cmcontinue    the value for the blcontinue parameter,
	 *                      null for the generation of the initial request
	 * @return a
	 */
	protected final Get generateContinueRequest(String cmcontinue) {

		try {

			return new Get(r.continiue(cmcontinue));

		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		return null;

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
			hasMoreResults = true;
		} else {
			hasMoreResults = false;
		}
		if (log.isDebugEnabled())
			log.debug("has more = " + hasMoreResults);

	}

	/**
	 * picks the article name from a MediaWiki api response.
	 *	
	 * @param s   text for parsing
	 */
	private final void parseArticleTitles(String s) {
		
		// get the backlink titles and add them all to the titleCollection
			
		Pattern p = Pattern.compile(
			"<cm pageid=\"(.*?)\" ns=\"(.*?)\" title=\"(.*?)\" />");
			
		Matcher m = p.matcher(s);
		
		while (m.find()) {
			
			addCatItem(m.group(3), Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));

		}
		finalizeParse();
	}
	protected abstract void finalizeParse();
	
	protected abstract void addCatItem(String title, int pageid, int ns);
	

	private class RequestBuilder_1_11 extends RequestBuilder {
		
		RequestBuilder_1_11() {
			super();
		}
		String continiue(String cmcontinue)  {
			String uS = "";	
			String nsinj = "";
			if (namespaceStr.length() > 0) {
				nsinj = "&cmnamespace=" + namespaceStr;
			}
				uS = "/api.php?action=query&list=categorymembers"
						+ "&cmcategory=" + MediaWiki.encode(categoryName) 
						+ nsinj
						+ "&cmcontinue=" + MediaWiki.encode(cmcontinue)
						+ "&cmlimit=" + LIMIT + "&format=xml";
				return uS;
		}
		
		String first(String categoryName) {
			String uS = "";
			String nsinj = "";
			if (namespaceStr.length() > 0) {
				nsinj = "&cmnamespace=" + namespaceStr;
			}
			
				uS = "/api.php?action=query&list=categorymembers"
						+ "&cmcategory=" + MediaWiki.encode(categoryName) 
						+ nsinj
						+ "&cmlimit=" + LIMIT + "&format=xml";
				return uS;
		}

		
		
	}
	
	private class RequestBuilder {
		
		RequestBuilder() {
			
		}
		
		String continiue(String cmcontinue) {
			String uS = "";	
			String nsinj = "";
			if (namespaceStr.length() > 0) {
				nsinj = "&cmnamespace=" + namespaceStr;
			}
			
			//TODO: do not add Category: - instead, change other methods' descs (e.g. in MediaWikiBot)
			
				uS = "/api.php?action=query&list=categorymembers"
						+ "&cmtitle=Category:" + MediaWiki.encode(categoryName) 
						+ nsinj
						+ "&cmcontinue=" + MediaWiki.encode(cmcontinue)
						+ "&cmlimit=" + LIMIT + "&format=xml";
				return uS;
		}
		
		String first(String categoryName) {
			String uS = "";
			String nsinj = "";
			if (namespaceStr.length() > 0) {
				nsinj = "&cmnamespace=" + namespaceStr;
			}
		
				//TODO: do not add Category: - instead, change other methods' descs (e.g. in MediaWikiBot)
			
				uS = "/api.php?action=query&list=categorymembers"
						+ "&cmtitle=Category:" + MediaWiki.encode(categoryName) 
						+ nsinj
						+ "&cmlimit=" + LIMIT + "&format=xml";
				return uS;
		}
		
	}

	

}
