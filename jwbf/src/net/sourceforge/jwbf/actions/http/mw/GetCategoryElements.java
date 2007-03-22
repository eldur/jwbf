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
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import net.sourceforge.jwbf.actions.http.Action;

import org.apache.commons.httpclient.methods.GetMethod;

/**
 * 
 * @author Thomas Stock
 * 
 */
public class GetCategoryElements extends Action implements NamingEnumeration {

	private Collection<String> content = new Vector<String>();

	private int moreCount;

	private boolean isContent = false;

	private boolean hasNoChiled = true;

	private String nextPage = "";

	/**
	 * 
	 * @param categoryname name category 
	 * @param c conent of previous category page
	 */
	public GetCategoryElements(final String categoryname, Collection<String> c) {

		this.content = c;
		addCatPage(categoryname);
	}
	/**
	 * 
	 * @param categoryname name of category
	 * @param from where to begin the collection of content articles
	 * @param c conent of previous category page
	 */
	public GetCategoryElements(final String categoryname, final String from,
			Collection<String> c) {

		this.content = c;
		addCatPage(categoryname, from);
	}
	/**
	 * creates the GET request for the action.
	 * @param catname name of the category 
	 */
	private void addCatPage(final String catname) { 
		addCatPage(catname, "");
	}
	/**
	 * creates the GET request for the action.
	 * @param catname name of the category 
	 * @param from start bye article
	 */
	
	private void addCatPage(final String catname, final String from) {
		String uS = "";
		String fromEl = "";

		try {
			if (from.length() > 0) {
				fromEl = "&from=" + from;
			}
			uS = "/index.php?title=" + URLEncoder.encode(catname, "UTF-8")
					+ fromEl + "&dontcountme=s";
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		msgs.add(new GetMethod(uS));
	}
	/**
	 * @param s whole html text
	 * @return text of this page
	 * @see Action#processAllReturningText(String)
	 */
	public final String processAllReturningText(final String s) {
		return read(s);
	}
	/**
	 * 
	 * @param s the whole html file
	 * @return an empty string, because it reads only the category elements not
	 * the content.
	 */
	private String read(final String s) {

		moreCount = 0;
		String[] lines = s.split("\n");

		for (int i = 0; i < lines.length; i++) {
			parsePageLinks(lines[i]);

			parseHasMore(lines[i]);

		}

		log.debug("Pagecount: " + content.size());

		return "";
	}

	
	/**
	 * if line is betwene lines <!-- start content --> and <!-- end
	 * content --> returns, set inner variable on true.
	 * 
	 * @param s line of html file
	 */
	private void checkIsContent(final String s) {

		if (s.indexOf("<!-- start content -->") > 1) {
			isContent = true;
		} else if (s.indexOf("<!-- end content -->") > 1) {
			isContent = false;
		}

	}
	/**
	 * 
	 * @param line of html text
	 */
	private void parseHasMore(final String line) {
		String xLine = line.replace("\n", "");
		checkIsContent(xLine);
		if (xLine.contains("from") && xLine.contains("(") && isContent) {

			String urlEl = getUrl(xLine);

			if (urlEl.indexOf("from") > 1 && hasNoChiled) {

				hasNoChiled = false;
				int fromStart = urlEl.indexOf("from");
				nextPage = urlEl.substring(fromStart + 5);
				log.debug("has more: " + nextPage);
			}
			moreCount++;
		}
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
			String temp = myMatcher.group(1);
			if (temp.length() > 0) {
				try {

					content.add(URLDecoder.decode(stripUrlElements(temp), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		myMatcher.appendTail(myStringBuffer);
	}
	/**
	 * 
	 * @param s with url elements
	 * @return without url elements
	 */
	final String stripUrlElements(final String s) {

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
	 * @param s
	 *            a
	 * @return a url with includes a "from" variable or an empty string
	 */
	private String getUrl(final String s) {
		String ms = "<a href=\"(.*)\" title(.*)</a>";
		ms = "<a[^>]*href=\"([^(>| )]*\")?[^>]*>[^<]*</a>";
		String tempLine = s.replace("&amp;", "&");

		String[] xLine = tempLine.split("\\(");
		for (int j = 0; j < xLine.length; j++) {

			Matcher myMatcher = Pattern.compile(ms).matcher(tempLine);
			while (myMatcher.find()) {
				String temp = myMatcher.group(1); // + " - " +
				// myMatcher.group(2) + " -
				// " + myMatcher.group(3);
				if (temp.length() > 0) {
					try {
						String t = URLDecoder
								.decode(stripUrlElements(temp), "UTF-8");
						t = stripUrlElements(temp);
						t = t.substring(0, t.length() - 1);
						if (t.indexOf("from") > 1) {
							t = t.replace(" ", "_");

							return t;
						} else {
							continue;
						}

					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return "";
	}
	/**
	 * 
	 * @return all article names
	 */
	
	final Collection< ? extends String> getContent() {
		return content;
	}
	/**
	 * is unused.
	 * @see NamingEnumeration#close()
	 * @throws NamingException on problems
	 */
	public void close() throws NamingException {
		// do notihng
	}
	/**
	 * @see NamingEnumeration#hasMore()
	 * @throws NamingException on problems
	 * @return true, if has more
	 */
	public final boolean hasMore() throws NamingException {
		return !hasNoChiled;
	}
	/**
	 * @see NamingEnumeration#next()
	 * @throws NamingException on problems
	 * @return a
	 */
	public final Object next() throws NamingException {
		return nextPage;
	}
	/**
	 * @see NamingEnumeration#hasMore()
	 * @return true if has more
	 */
	public final boolean hasMoreElements() {
		return !hasNoChiled;
	}
	/**
	 * @return a
	 */
	public final Object nextElement() {
		return nextPage;
	}

}
