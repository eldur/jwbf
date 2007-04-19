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
 * Philipp Kohl 
 */
package net.sourceforge.jwbf.actions.http.mw;


import net.sourceforge.jwbf.actions.http.Action;
import net.sourceforge.jwbf.actions.http.CookieException;
import net.sourceforge.jwbf.bots.MediaWikiBot;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 * 
 * @author Thomas Stock
 * 
 */
public class PostLogin extends Action {


	private String username = "";
	
	/**
	 * 
	 * @param username the
	 * @param pw password
	 */
	public PostLogin(final String username, final String pw) {
		this.username = username;

		NameValuePair action = new NameValuePair("wpLoginattempt", "Log in");
		NameValuePair url = new NameValuePair("wpRemember", "1");
		NameValuePair userid = new NameValuePair("wpName", username);
		NameValuePair password = new NameValuePair("wpPassword", pw);

		PostMethod pm = new PostMethod(
				"/index.php?title=Special:Userlogin&action=submitlogin&type=login");

		pm.setRequestBody(new NameValuePair[] { action, url, userid,
						password });
		pm.getParams().setContentCharset(MediaWikiBot.CHARSET);
		msgs.add(pm);

	}
	/**
	 * @param cs the
	 * @throws CookieException when no cookies returning
	 */
	public void validateAllReturningCookies(final Cookie[] cs) throws CookieException {
		String compare = username.replace('_', ' ').replace(' ', '+');
		if (cs == null) {
			throw new CookieException("Cookiearray is null.");
		}
		if (cs.length == 0) {
			throw new CookieException("No cookies found.");
		} else {
			for (int i = 0; i < cs.length; i++) {								
				if (cs[i].toString().contains(compare)) {
					log.info("Logged in as: " + username);
					return;
				}
				if (i == cs.length - 1) {
					throw new CookieException(
							"Login failed: Check Username and Password.");
				}
			}
		}
	}

}
