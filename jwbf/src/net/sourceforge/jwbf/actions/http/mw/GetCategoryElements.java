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

import org.apache.commons.httpclient.methods.GetMethod;

/**
 * TODO eldurloki: working on type media and/or article.
 * @author Thomas Stock
 * 
 * 
 */
public class GetCategoryElements extends GetMultipageNames {

	private boolean subContent = false;

	private byte type = 00;

	public static final byte ARTICLE = 10;

	public static final byte MEDIA = 01;

	/**
	 * 
	 * @param categoryname
	 *            the
	 * @param c
	 *            a
	 * @param type
	 *            of category elements, MEDIA | ARTICLE
	 */
	public GetCategoryElements(final String categoryname, Collection<String> c,
			final byte type) {
		super(categoryname, c);
		this.type = type;
	}

	/**
	 * 
	 * @param categoryname
	 *            the
	 * @param from
	 *            where category colection begins, like "from=D"
	 * @param c
	 *            a
	 * @param type
	 *            of category elements, MEDIA | ARTICLE
	 */
	public GetCategoryElements(final String categoryname, final String from,
			Collection<String> c, final byte type) {

		super(categoryname, from, c);
		this.type = type;
	}

	/**
	 * if line is betwene lines <!-- start content --> and <!-- end content -->
	 * returns, set inner variable on true.
	 * 
	 * @param s
	 *            line of html file
	 */
	protected void checkIsContent(final String s) {

		if (s.indexOf("<!-- start content -->") > 1) {
			subContent = true;

		} else if (s.indexOf("<!-- end content -->") > 1) {
			isContent = false;
		}
		// no subcategories
		if (s.indexOf("mw-pages") > 1 && subContent) {
			isContent = true;
		}

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
			uS = "/index.php?title=" + URLEncoder.encode(pagename, "UTF-8")
					+ fromEl + "&dontcountme=s";
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
				log.debug("has more: " + nextPage);
			}
			moreCount++;
		}
	}

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
				String temp = myMatcher.group(1); // + " - " +
				// myMatcher.group(2) + " -
				// " + myMatcher.group(3);
				if (temp.length() > 0) {
					try {
						String t = URLDecoder.decode(stripUrlElements(temp),
								"UTF-8");
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
}
