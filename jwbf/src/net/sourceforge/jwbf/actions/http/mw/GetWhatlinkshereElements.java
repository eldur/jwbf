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

		String text = node.toHtml();
		// from-value of the current page
		int from = 1539253;
		// limit-value of the current page
		int limit = 50;
		// the content of the current page

		boolean hasNextPage = false;

		// check wheter the page contains a link to another whatlinkshere with
		// different from-value
		Pattern p = Pattern.compile("^.*?<a.*?limit=" + limit
				+ "&amp;from=([0-9]+).*?>.*$", Pattern.DOTALL
				| Pattern.MULTILINE);
		Matcher m = p.matcher(text);

		if (m.find()) {

			// isolate the from-value
			String newFromAsString = m.replaceAll("$1");

			// check whether the new from-value is greater than the current one
			if (Integer.parseInt(newFromAsString) > from) {
				hasNextPage = true;
			}

			// if not, this was the link to the previous page.
			// do (nearly) the same again, but make sure to get the _second_
			// link of that type.

			p = Pattern.compile("^.*?<a.*?limit=" + limit + "&amp;from="
					+ newFromAsString + ".*?>.*?<a.*?limit=" + limit
					+ "&amp;from=([0-9]+).*?>.*$", Pattern.DOTALL
					| Pattern.MULTILINE);
			m = p.matcher(text);

			if (m.find()) {

				newFromAsString = m.replaceAll("$1");

				if (Integer.parseInt(newFromAsString) > from) {
					hasNextPage = true;
				}

			}

		}

//		return hasNextPage;
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
		// TODO Auto-generated method stub
		return false;
	}

}
