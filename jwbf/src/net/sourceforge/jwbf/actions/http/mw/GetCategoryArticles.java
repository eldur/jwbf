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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.jwbf.bots.MediaWikiBot;

import org.apache.commons.httpclient.methods.GetMethod;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.filters.LinkRegexFilter;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;

/**
 * TODO eldurloki: working on type media and/or article.
 * 
 * @author Thomas Stock
 * 
 * 
 */
public class GetCategoryArticles extends GetMultipageNames {

	private String prevFrom = "";

	private final boolean first;

	/**
	 * 
	 * @param categoryname
	 *            the
	 * @param c
	 *            a
	 */
	public GetCategoryArticles(final String categoryname, Collection<String> c) {
		super(categoryname, c);
		first = true;
	}

	/**
	 * 
	 * @param categoryname
	 *            the
	 * @param from
	 *            where category colection begins, like "from=D"
	 * @param c
	 *            a
	 */
	public GetCategoryArticles(final String categoryname, final String from,
			Collection<String> c) {

		super(categoryname, from, c);
		first = false;
		this.prevFrom = from;
	}

	// /**
	// * if line is betwene lines <!-- start content --> and <!-- end content
	// -->
	// * returns, set inner variable on true.
	// *
	// * @param s
	// * line of html file
	// */
	// protected void checkIsContent(final String s) {
	//
	// if (s.indexOf("<!-- start content -->") > 1) {
	// subContent = true;
	//
	// } else if (s.indexOf("printfooter") > 1) {
	// isContent = false;
	// }
	// // no subcategories
	// if (s.indexOf("<!-- Saved in parser") >= 0 && subContent) {
	// isContent = true;
	// }
	//
	// }

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
		String fromEl = "";

		try {
			if (from.length() > 0) {
				fromEl = "&from=" + from;
			}
			uS = "/index.php?title="
					+ URLEncoder.encode(pagename, MediaWikiBot.CHARSET)
					+ fromEl + "&dontcountme=s";
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		msgs.add(new GetMethod(uS));
	}

	/**
	 * 
	 * @param node
	 *            of html text
	 */
	protected void parseHasMore(final Node node) {

		try {
			NodeList linkList = new NodeList();

			NodeFilter linkFilter = new LinkRegexFilter("from");

			for (NodeIterator e = node.getChildren().elements(); e
					.hasMoreNodes();) {
				e.nextNode().collectInto(linkList, linkFilter);
			}

			NodeIterator bodyEl = linkList.elements();
			Node last = null;
			while (bodyEl.hasMoreNodes()) {
				last = bodyEl.nextNode();

			}
			String link = encode(last.toHtml());
			int fromStart = link.indexOf("from");
			String t = link.substring(fromStart + 5);
			int end = t.indexOf("\"");
			String x = t.substring(0, end);
			if (x.length() > 0 && !prevFrom.equalsIgnoreCase(x)) {
				setHasMore(true);
				setNextPage(x);
				log.debug("has more: " + nextElement());
			}

		} catch (Exception e) {
			setHasMore(false);
			log.debug("No Html elements found", e);
		}

	}

	/**
	 * 
	 * @param line
	 *            of html
	 */
	private void parsePageLinks(final String line) {
		String ms = "<li><a href=\"(.*)\" title=\"(.*)\">(.*)</a></li>";
		StringBuffer myStringBuffer = new StringBuffer();
		String tempLine = line.replace("</li><li>", "</li>\n<li>");
		Matcher myMatcher = Pattern.compile(ms).matcher(tempLine);
		while (myMatcher.find()) {
			String temp = myMatcher.group(2);
			if (temp.length() > 0) {
				content.add(temp);
				log.debug("add: " + temp);
			}
		}
		myMatcher.appendTail(myStringBuffer);
	}

	/**
	 * @param s html text
	 * @return an empty string
	 */
	@Override
	protected String processHtml(final String s) {

		Node mYcontent = getDivBodyContent(s);

		parseHasMore(mYcontent);

		if (hasMoreElements() || first) {
			String[] lines = mYcontent.toHtml().split("\n");

			for (int i = 0; i < lines.length; i++) {
				parsePageLinks(lines[i]);
			}
		}

		log.debug("Pagecount: " + content.size());

		return "";
	}

}
