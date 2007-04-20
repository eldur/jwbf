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
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import net.sourceforge.jwbf.bots.MediaWikiBot;

import org.apache.commons.httpclient.methods.GetMethod;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasChildFilter;
import org.htmlparser.filters.HasParentFilter;
import org.htmlparser.filters.LinkRegexFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;

/**
 * TODO eldurloki: working on type media and/or article.
 * @author Thomas Stock
 * 
 * 
 */
public class GetCategoryArticles extends GetMultipageNames {

	private String prevFrom = "";
	private boolean more = false;
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

//	/**
//	 * if line is betwene lines <!-- start content --> and <!-- end content -->
//	 * returns, set inner variable on true.
//	 * 
//	 * @param s
//	 *            line of html file
//	 */
//	protected void checkIsContent(final String s) {
//
//		if (s.indexOf("<!-- start content -->") > 1) {
//			subContent = true;
//
//		} else if (s.indexOf("printfooter") > 1) {
//			isContent = false;
//		}
//		// no subcategories
//		if (s.indexOf("<!-- Saved in parser") >= 0 && subContent) {
//			isContent = true;
//		}
//
//	}

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
			uS = "/index.php?title=" + URLEncoder.encode(pagename, MediaWikiBot.CHARSET)
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
	public void parseHasMore(final Node node) {
		
		
		
		
		
		try {
			NodeList linkList = new NodeList();

			NodeFilter linkFilter = new LinkRegexFilter("from");

			for (NodeIterator e = node.getChildren().elements();
					e.hasMoreNodes();) {
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
				more = true;
				setNextPage(x);
				log.debug("has more: " + nextElement());
			}
			

			

		} catch (Exception e) {
//			e.printStackTrace();
			more = false;
			log.debug("No Html elements found", e);
		}
		
//		for (int i = 0; i < lines.length; i++) {
//				
//		
//			String xLine = node.toPlainTextString().replace("\n", "");
//			checkIsContent(xLine);
//			if (xLine.contains("from") && xLine.contains("(")) {
//	
//				String urlEl = getNextPageId(node);
//	
//				if (urlEl.indexOf("from") > 1 && hasNoChiled) {
//	
//					hasNoChiled = false;
//					int fromStart = urlEl.indexOf("from");
//					setNextPage(urlEl.substring(fromStart + 5));
//					log.debug("has more: " + nextElement());
//				}
//				moreCount++;
//			}
//		}
	}

//	/**
//	 * 
//	 * @param s
//	 *            a
//	 * @return a url with includes a "from" variable or an empty string
//	 */
//	public String getNextPageId(final Node s) {
//		String ms = "<a href=\"(.*)\" title(.*)</a>";
//		ms = "<a[^>]*href=\"([^(>| )]*\")?[^>]*>[^<]*</a>";
//		String tempLine = s.toPlainTextString().replace("&amp;", "&");
//
//		String[] xLine = tempLine.split("\\(");
//		for (int j = 0; j < xLine.length; j++) {
//
//			Matcher myMatcher = Pattern.compile(ms).matcher(tempLine);
//			while (myMatcher.find()) {
//				String temp = myMatcher.group(1); // + " - " +
//				// myMatcher.group(2) + " -
//				// " + myMatcher.group(3);
//				if (temp.length() > 0) {
//					try {
//						String t = URLDecoder
//								.decode(stripUrlElements(temp), MediaWikiBot.CHARSET);
//						t = stripUrlElements(temp);
//						t = t.substring(0, t.length() - 1);
//						if (t.indexOf("from") > 1) {
//							t = t.replace(" ", "_");
//
//							return t;
//						} else {
//							continue;
//						}
//
//					} catch (UnsupportedEncodingException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		}
//		return "";
//	}
	
	/**
	 * 
	 * @param s with url elements
	 * @return without url elements
	 */
	private final String stripUrlElements(final String s) {

		String temp = s;
		int phpSlashPos = temp.indexOf(".php/") + 5;

		if (phpSlashPos > 5) {
			temp = temp.substring(phpSlashPos, temp.length());
		}
		int equalPos = temp.indexOf("=") + 1;
		if (equalPos > 1) {
			temp = temp.substring(equalPos, temp.length());
		}
		int slashPos = temp.indexOf("/") + 1;
		if (slashPos >= 1) {
			temp = temp.substring(slashPos, temp.length());
		}

		return temp;
	}
	/**
	 * 
	 * @return all article names
	 */
	private final Collection<String> getContent() {
		return content;
	}
	/**
	 * 
	 * @param line of html
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


	@Override
	protected String processHtml(final String s) {
		
		Node mYcontent = getMainContent(s);
		
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
	
	/**
	 * @see NamingEnumeration#hasMore()
	 * @throws NamingException on problems
	 * @return true, if has more
	 */
	public final boolean hasMore() throws NamingException {
		return more;
	}
	/**
	 * @see NamingEnumeration#hasMore()
	 * @return true if has more
	 */
	public final boolean hasMoreElements() {
		return more;
	}
}
