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
package net.sourceforge.jwbf.actions;

import java.util.List;
import java.util.Vector;

import net.sourceforge.jwbf.actions.mw.util.CookieException;
import net.sourceforge.jwbf.actions.mw.util.ProcessException;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
/**
 * Simple method to get plain HTML or XML data e.g. from custom specialpages
 * or xml newsfeeds or something else.
 * 
 * @author Thomas Stock
 * 
 *
 */
public class GetPage implements ContentProcessable {

	private List<HttpMethod> msgs = new Vector<HttpMethod>();
	
	private String text = "";
	
	/**
	 * 
	 * @param u like "/index.php?title=Special:Recentchanges&feed=rss"
	 */
	public GetPage(String u) {		
		msgs.add(new GetMethod(u));
		
	}
	/**
	 * @see ContentProcessable#getMessages()
	 * @return a
	 */
	public List<HttpMethod> getMessages() {
		return msgs;
	}
	/**
	 * @see ContentProcessable#processReturningText(String, HttpMethod)
	 * @param s the returning text
	 * @param hm the
	 * @throws ProcessException on any problems with inner browser
	 * @return the returning text
	 */
	public String processReturningText(String s, HttpMethod hm) throws ProcessException {
		text = s;
		return s;
	}

	/**
	 * @see ContentProcessable#validateReturningCookies(Cookie[], HttpMethod)
	 * @param cs a
	 * @param hm a
	 * @throws CookieException on cookie problems
	 */
	public void validateReturningCookies(Cookie[] cs, HttpMethod hm)
			throws CookieException {

	}
	/**
	 * 
	 * @return the requested text
	 */
	public String getText() {
		return text;
	}

	
}
