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
 * 
 */
package net.sourceforge.jwbf.actions.http.mw;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.jwbf.bots.MediaWikiBot;

import org.apache.commons.httpclient.methods.GetMethod;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasChildFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;

/**
 * @author Thomas Stock
 * @author Tobias Knerr
 */
public class GetBacklinkTitles extends GetMultipageNames {

	/** constant value for the bllimit-parameter **/
	private static final int LIMIT = 50;
	
	
	/**
	 * @param articleName	 name of an article;
	 *                      the class will search for everything linking here.
	 */
	public GetBacklinkTitles(final String articleName,
		Collection<String> c) {
		super(articleName, c);
	}

	/**
	 * @param articleName	 name of an article;
	 *                      the class will search for everything linking here.
	 * @param from   "from-value" (the value for the )
	 */
	public GetBacklinkTitles(final String articleName,
			final String from, Collection<String> c) {
		super(articleName, from, c);
	}

	/**
	 * creates the GET request for the action.
	 * 
	 * @param pagename
	 *            name of a next
	 * @param from
	 *            start bye article
	 */
	protected void addNextPage(final String pagename, final String from) {
		
		String uS = "";
		
		if( from.equals("") ){
	
			try {
				uS = "/api.php?action=query&list=backlinks&titles="
				    + URLEncoder.encode(pagename, MediaWikiBot.CHARSET)
				    + "&format=xml";
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		
		} else {
			
			uS = "/api.php?action=query&list=backlinks&&blcontinue="
			    + from + "&format=xml";
			
		}
		
		/*++ debug ++*/ System.out.println(uS);
		
		msgs.add(new GetMethod(uS));

	}

	/**
	 * @param node
	 *            of html text
	 */
	protected void parseHasMore(final Node node) {

		// the content of the current page
		String content = node.toHtml();
			
		// get the blcontinue-value
		
		Pattern p = Pattern.compile(
			"<query-continue>.*?" +
			"<backlinks *blcontinue=\"([^\"]*)\" */>" +
			".*?</query-continue>",
			Pattern.DOTALL | Pattern.MULTILINE);
			
		Matcher m = p.matcher(content);

		if (m.find()) {			
			setHasMore(true);
			setNextPage(m.replaceFirst("$1"));			
		}

	}

	/**
	 * 
	 * @param node
	 *            with content of bodyContent div
	 * @return a of articlenames
	 */
	public Collection<String> getArticles(final Node node) {

		// the return value,
		// a collection that will contain all backlink titles from this api page 
		Collection<String> titleCollection = new Vector<String>();
		
		// the content of the current page
		String content = node.toHtml();
		
		/*++ debug ++*/ System.out.println("getArticles, node is \n"+content);
		
		// get the backlink titles
			
		Pattern p = Pattern.compile(
			"<bl pageid=\".*?\" ns=\".*?\" title=\"(.*?)\" />");
			
		Matcher m = p.matcher(content);

		while (m.find()) {			
			titleCollection.add(m.replaceFirst("$1"));			
		}
		
		return titleCollection;
		
	}

	/**
	 * @param s
	 *            html string
	 * @return an empty string
	 */
	@Override
	protected String processHtml(final String s) {		
		parseHasMore(getHtmlBody(s));
		content.addAll(getArticles(getDivBodyContent(s)));
		return "";
	}

}
