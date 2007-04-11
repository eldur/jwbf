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
package net.sourceforge.jwbf.actions.http;

import java.util.List;
import java.util.Vector;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.log4j.Logger;

/**
 * 
 * @author Thomas Stock
 * 
 */
public abstract class Action implements ContentProcessable {

	protected Logger log;

	protected List<HttpMethod> msgs;

	/**
	 * 
	 * 
	 */
	public Action() {
		msgs = new Vector<HttpMethod>();
		log = Logger.getLogger(Action.class);
	}

	/**
	 * 
	 * @return a
	 */
	public final List<HttpMethod> getMessages() {
		return msgs;
	}

	/**
	 * @param s
	 *            the returning text
	 * @param hm
	 *            the method object
	 * @return the returning text
	 * 
	 */
	public String processReturningText(final String s, final HttpMethod hm) {
		return processAllReturningText(s);
	}

	/**
	 * @param cs
	 *            a
	 * @param hm
	 *            the method object
	 * @throws CookieException
	 *             never
	 * 
	 */
	public void validateReturningCookies(Cookie[] cs, HttpMethod hm)
			throws CookieException {
		validateAllReturningCookies(cs);

	}

	/**
	 * @param cs
	 *            a
	 * @throws CookieException
	 *             never
	 * 
	 */
	public void validateAllReturningCookies(final Cookie[] cs)
			throws CookieException {
		// do nothing
	}

	/**
	 * @param s
	 *            the returning text
	 * @return the returning text
	 * 
	 */
	public String processAllReturningText(final String s) {
		return s;
	}

}
