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

import net.sourceforge.jwbf.bots.MediaWikiBot;

import org.apache.commons.httpclient.methods.GetMethod;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.Span;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;

/**
 * 
 * @author Thomas Stock
 * 
 */
public class GetHTML extends MWAction {

	/**
	 * 
	 *
	 */
	public GetHTML() {
		// do nothing
	}

	/**
	 * 
	 * @param articlename
	 *            the
	 */
	public GetHTML(final String articlename) {
		String uS = "";
		try {
			uS = "/index.php?title="
					+ URLEncoder.encode(articlename, MediaWikiBot.CHARSET)
					+ "&dontcountme=s";
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		msgs.add(new GetMethod(uS));
	}
	/**
	 * 
	 * @param text of html
	 * @return a node with bodyContent div
	 */
	public Node getMainContent(final String text) {
		Parser p = new Parser();
		Node n = new Span();

		try {
			p.setInputHTML(text);

			// Find bodycontent
			NodeList bcList = new NodeList();
			NodeFilter bcFilter = new AndFilter(new TagNameFilter("DIV"),
					new HasAttributeFilter("id", "bodyContent"));

			for (NodeIterator e = p.elements(); e.hasMoreNodes();) {
				e.nextNode().collectInto(bcList, bcFilter);
			}

			NodeIterator ni = bcList.elements();
			n = (Node) ni.nextNode();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return n;
	}

}
