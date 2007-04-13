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
import java.util.Collection;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import net.sourceforge.jwbf.actions.http.Action;
/**
 * 
 * @author Thomas Stock
 * 
 */
public abstract class GetMultipageNames extends Action implements NamingEnumeration {

	private Collection<String> content = new Vector<String>();

	protected int moreCount;

	protected boolean isContent = false;

	protected boolean hasNoChiled = true;

	protected String nextPage = "";

	/**
	 * 
	 * @param categoryname name category 
	 * @param c conent of previous category page
	 */
	public GetMultipageNames(final String categoryname, Collection<String> c) {

		this.content = c;
		addCatPage(categoryname);
	}
	/**
	 * 
	 * @param categoryname name of category
	 * @param from where to begin the collection of content articles
	 * @param c conent of previous category page
	 */
	public GetMultipageNames(final String categoryname, final String from,
			Collection<String> c) {

		this.content = c;
		addNextPage(categoryname, from);
	}
	/**
	 * creates the GET request for the action.
	 * @param catname name of the category 
	 */
	private void addCatPage(final String catname) { 
		addNextPage(catname, "");
	}
	/**
	 * creates the GET request for the action.
	 * @param pagename name of a next 
	 * @param from start bye article
	 */
	
	protected abstract void addNextPage(final String pagename, final String from);
	
	/**
	 * @param s whole html text
	 * @return text of this page
	 * @see Action#processAllReturningText(String)
	 */
	public final String processAllReturningText(final String s) {
		String temp = "";
			try {
				temp = new String(s.getBytes(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				temp = s;
			}
		return read(temp);
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
			checkIsContent(lines[i]);
			if (isContent) {
				parsePageLinks(lines[i]);
	
				parseHasMore(lines[i]);
			}

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
	protected void checkIsContent(final String s) {

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
	protected abstract void parseHasMore(final String line);
	/**
	 * 
	 * @param line of html
	 */
	 void parsePageLinks(final String line) {
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
	protected abstract String getNextPageId(final String s);
	/**
	 * 
	 * @return all article names
	 */
	
	final Collection<String> getContent() {
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
