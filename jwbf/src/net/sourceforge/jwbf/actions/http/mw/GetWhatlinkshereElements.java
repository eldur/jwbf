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

import net.sourceforge.jwbf.JWBF;

import org.apache.commons.httpclient.methods.GetMethod;

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
		String uS = "";
		String fromEl = "";

		try {
			if (from.length() > 0) {
				fromEl = "&from=" + from;
			}
			uS = "/index.php?title=Special:WhatLinksHere/" + URLEncoder.encode(pagename, JWBF.charset)
					+ fromEl + "&dontcountme=s"
					+ "&limit=" + LIMIT;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		msgs.add(new GetMethod(uS));
	}

	/**
	 * 
	 * @param line
	 *            of html text
	 */
	protected void parseHasMore(final String line) {
		String xLine = line.replace("\n", "");
		checkIsContent(xLine);
		if (xLine.contains("from") && xLine.contains("(")) {

			String urlEl = getNextPageId(xLine);

			if (urlEl.indexOf("from") > 1 && hasNoChiled) {
				
				hasNoChiled = false;
				int fromStart = urlEl.indexOf("from");
				nextPage = urlEl.substring(fromStart + 5);
				log.debug("has more: " + nextPage + "\n"
						+ "on: " + line + "\n\n");
			}
			moreCount++;
		}
	}

//	/**
//	 * TODO dont works.
//	 */
//	 void parsePageLinks(final String line) {
//		 System.err.println(line);
//		}
	
	/**
	 * 
	 * @param s
	 *            a
	 * @return a url with includes a "from" variable or an empty string
	 */
	protected String getNextPageId(final String s) {
		String ms = "<a href=\"(.*)\" title(.*)</a>";
		ms = "<a[^>]*href=\"([^(>| )]*\")?[^>]*>[^<]*</a>";
		String tempLine = s.replace("&amp;", "&");

		String[] xLine = tempLine.split("\\(");
		for (int j = 0; j < xLine.length; j++) {

			Matcher myMatcher = Pattern.compile(ms).matcher(tempLine);
			while (myMatcher.find()) {
				String temp = myMatcher.group(1);
				if (temp.length() > 0) {
					try {
						String t = URLDecoder
								.decode(stripUrlElements(temp), JWBF.charset);
						t = stripUrlElements(temp);
						t = t.substring(0, t.length() - 1);

						if (t.indexOf("prev") > 1) {
							continue;
						} else if (t.indexOf("from") > 1) {
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
}
