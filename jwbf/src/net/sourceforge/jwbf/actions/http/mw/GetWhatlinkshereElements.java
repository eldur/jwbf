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
package net.sourceforge.jwbf.actions.http.mw;

import java.util.Collection;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.NamingException;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasChildFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;

/**
 * @author Thomas Stock
 * 
 */
public class GetWhatlinkshereElements extends GetMultipageNames {

	public static final int LIMIT = 2;

	/**
	 * 
	 * @param categoryname
	 *            name of category with namespace like "Category:Names"
	 * @param c
	 *            a
	 */
	public GetWhatlinkshereElements(final String categoryname,
			Collection<String> c) {
		 super(categoryname, c);

	}

	/**
	 * 
	 * @param categoryname
	 *            name of category with namespace like "Category:Names"
	 * @param from
	 *            start of
	 * @param c
	 *            a
	 */
	public GetWhatlinkshereElements(final String categoryname,
			final String from, Collection<String> c) {
		 super(categoryname, from, c);
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
		// String uS = "";
		// String fromEl = "";
		//
		// try {
		// if (from.length() > 0) {
		// fromEl = "&from=" + from;
		// }
		// uS = "/index.php?title=Special:WhatLinksHere/" +
		// URLEncoder.encode(pagename, MediaWikiBot.CHARSET)
		// + fromEl + "&dontcountme=s"
		// + "&limit=" + LIMIT;
		// } catch (UnsupportedEncodingException e) {
		// e.printStackTrace();
		// }
		// msgs.add(new GetMethod(uS));
	}

	/**
	 * TODO prepared for test; test fails.
	 * 
	 * @param node
	 *            of html text
	 * @return true if has more pages
	 */
	public void parseHasMore(final Node node) {

		// the content of the current page
		String content = node.toHtml();

		//get the from-value of the link to the next page.
		
		Pattern p = Pattern.compile("^.*?<a.*?limit=" + 2
				+ "&amp;from=([0-9]+)+&amp;back=[0-9]+&amp;namespace.*?>.*$", Pattern.DOTALL
				| Pattern.MULTILINE);
		Matcher m = p.matcher(content);
	
		//set nextPage accordingly
		
		if (m.find()) {
		
			String nextFrom = m.replaceFirst("$1");
			
			setNextPage(nextFrom);
			
		}
		
		else{
			
			setNextPage(null);
			
		}
					
	}
	/**
	 * 
	 * @param node with content of bodyContent div
	 * @return a of articlenames
	 */
	public Collection<String> getArticles(final Node node) {

		Collection<String> col = new Vector<String>();

		try {
			NodeList linkList = new NodeList();

			NodeFilter linkFilter = new AndFilter(new TagNameFilter("LI"),
					new HasChildFilter(new TagNameFilter("A")));

			for (NodeIterator e = node.getChildren().elements();
					e.hasMoreNodes();) {
				e.nextNode().collectInto(linkList, linkFilter);
			}

			NodeIterator bodyEl = linkList.elements();
			while (bodyEl.hasMoreNodes()) {
				String toAdd = bodyEl.nextNode().getChildren().elements().nextNode()
				.toPlainTextString();
				col.add(encode(toAdd));

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return col;
	}

//	/**
//	 * 
//	 * @param node
//	 *            a
//	 * @return a url with includes a "from" variable or an empty string
//	 */
//	public String getNextPageId(final Node node) {
//		// String ms = "<a href=\"(.*)\" title(.*)</a>";
//		// ms = "<a[^>]*href=\"([^(>| )]*\")?[^>]*>[^<]*</a>";
//		// String tempLine = s.replace("&amp;", "&");
//		//
//		// String[] xLine = tempLine.split("\\(");
//		// for (int j = 0; j < xLine.length; j++) {
//		//
//		// Matcher myMatcher = Pattern.compile(ms).matcher(tempLine);
//		// while (myMatcher.find()) {
//		// String temp = myMatcher.group(1);
//		// if (temp.length() > 0) {
//		// try {
//		// String t = URLDecoder
//		// .decode(stripUrlElements(temp), MediaWikiBot.CHARSET);
//		// t = stripUrlElements(temp);
//		// t = t.substring(0, t.length() - 1);
//		//
//		// if (t.indexOf("prev") > 1) {
//		// continue;
//		// } else if (t.indexOf("from") > 1) {
//		// t = t.replace(" ", "_");
//		// return t;
//		// } else {
//		// continue;
//		// }
//		//
//		// } catch (UnsupportedEncodingException e) {
//		// e.printStackTrace();
//		// }
//		// }
//		// }
//		// }
//		return 1 +
//		 "";
//	}


	@Override
	protected String processHtml(String s) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasMore() throws NamingException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasMoreElements() {		
		return nextElement() != null;
	}

}
