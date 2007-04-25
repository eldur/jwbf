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
 * Optimized and tested for MediaWiki versions 1.9.0, 1.9.3, 1.10.
 * 
 * @author Thomas Stock
 * @author Tobias Knerr
 * 
 */
public class GetWhatlinkshereElements extends GetMultipageNames {

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
			uS = "/index.php?title=Special:WhatLinksHere/"
					+ URLEncoder.encode(pagename, MediaWikiBot.CHARSET)
					+ fromEl + "&dontcountme=s";
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		msgs.add(new GetMethod(uS));

	}

	/**
	 * TODO prepared for test; test fails.
	 * 
	 * @param node
	 *            of html text
	 */
	protected void parseHasMore(final Node node) {

		// the content of the current page
		String content = node.toHtml();

		// results of the parsing
		boolean hasNextPage = false;
		String nextPageFromString = "";

		// get from- and limit-value of the current page
		// using the tab on top of the content area		
		
		int limit;
		int from;
			
		Pattern p = Pattern.compile(
			"^.*?class=\"selected\"> *<a.*?limit=([0-9]+)&amp;from=([0-9]+).*?>.*$",
			Pattern.DOTALL | Pattern.MULTILINE);
		Matcher m = p.matcher(content);

		if (m.find()) {
			limit = Integer.parseInt(m.replaceFirst("$1"));
			from = Integer.parseInt(m.replaceFirst("$2"));			
		}
		
		else{
			
			//try to find a limit-info without from
			Pattern p = Pattern.compile(
				"^.*?class=\"selected\"> *<a.*?limit=([0-9]+).*?>.*$",
				Pattern.DOTALL | Pattern.MULTILINE);
			Matcher m = p.matcher(content);			

			if (m.find()) {
				limit = Integer.parseInt(m.replaceFirst("$1"));
			}
			
		}

		// check wheter the page contains a link to another whatlinkshere
		// with a from-value greater than the current one
		
		p = Pattern.compile(
			"^.*?<a.*?limit=" + limit + "&amp;from=([0-9]+).*?>.*$",
			Pattern.DOTALL | Pattern.MULTILINE);
		m = p.matcher(content);

		if (m.find()) {

			// isolate the from-value
			nextPageFromString = m.replaceFirst("$1");

			// check whether the new from-value is greater than the current one
			if (Integer.parseInt(nextPageFromString) > from) {
				hasNextPage = true;
			} else {

				// if not, this was the link to the previous page.
				// => do (nearly) the same again, but make sure to get the
				// _second_ link of that type.

				p = Pattern.compile( 
					"^.*?<a.*?limit=" + limit + "&amp;from=" + nextPageFromString + ".*?>
					+ ".*?<a.*?limit=" + limit 	+ "&amp;from=([0-9]+).*?>.*$",
					Pattern.DOTALL	| Pattern.MULTILINE);
				m = p.matcher(content);

				if (m.find()) {

					nextPageFromString = m.replaceFirst("$1");
				
					if (Integer.parseInt(nextPageFromString) > from) {
						hasNextPage = true;
					}

				}

			}

		}

		// make the results known
		setHasMore(hasNextPage);
		if (hasNextPage) {
			setNextPage(nextPageFromString);
		}

	}

	/**
	 * 
	 * @param node
	 *            with content of bodyContent div
	 * @return a of articlenames
	 */
	public Collection<String> getArticles(final Node node) {

		Collection<String> col = new Vector<String>();
		
		try {
			NodeList linkList = new NodeList();

			NodeFilter linkFilter = new AndFilter(new TagNameFilter("LI"),
					new HasChildFilter(new TagNameFilter("A")));

			for (NodeIterator e = node.getChildren().elements(); e
					.hasMoreNodes();) {
				e.nextNode().collectInto(linkList, linkFilter);
			}

			NodeIterator bodyEl = linkList.elements();
			while (bodyEl.hasMoreNodes()) {
				String toAdd = bodyEl.nextNode().getChildren().elements()
						.nextNode().toPlainTextString();
				System.err.println(toAdd);
				col.add(toAdd);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return col;
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
