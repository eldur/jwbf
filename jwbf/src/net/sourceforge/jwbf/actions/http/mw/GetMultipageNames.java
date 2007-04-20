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

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import org.htmlparser.Node;

/**
 * 
 * @author Thomas Stock
 * 
 */
public abstract class GetMultipageNames extends GetHTML implements NamingEnumeration {

	protected Collection<String> content = new Vector<String>();


	private String nextPage = "";

	/**
	 * 
	 * @param categoryname name category 
	 * @param c conent of previous category page
	 */
	public GetMultipageNames(final String categoryname, Collection<String> c) {

		this.content = c;
		addNextPage(categoryname);
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
	 * @param pagename name of the category 
	 */
	private void addNextPage(final String pagename) { 
		addNextPage(pagename, "");
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
		return processHtml(encode(s));
	}
	/**
	 * 
	 * @param s the whole html file
	 * @return an empty string, because it reads only the category elements not
	 * the content.
	 */
	protected abstract String processHtml(final String s);

	
	protected void setNextPage(final String s) {
		nextPage = s;
	}
	/**
	 * 
	 * @param line of html text
	 */
	public abstract void parseHasMore(final Node node);

	

//	/**
//	 * 
//	 * @param s
//	 *            a
//	 * @return a url with includes a "from" variable or an empty string
//	 */
//	public abstract String getNextPageId(final Node node);

	
	
	/**
	 * is unused.
	 * @see NamingEnumeration#close()
	 * @throws NamingException on problems
	 */
	public void close() throws NamingException {
		// do notihng
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
	 * @return a
	 */
	public final Object nextElement() {
		return nextPage;
	}
	
}
