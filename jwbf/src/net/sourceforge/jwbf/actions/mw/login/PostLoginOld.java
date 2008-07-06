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
package net.sourceforge.jwbf.actions.mw.login;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import net.sourceforge.jwbf.actions.mw.util.CookieException;
import net.sourceforge.jwbf.actions.mw.util.MWAction;
import net.sourceforge.jwbf.bots.MediaWikiBot;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

/**
 * 
 * @author Thomas Stock
 * @supportedBy MediaWiki 1.8.x, 1.9.x
 */
public class PostLoginOld extends MWAction {

	private String username = "";

	private static final Logger LOG = Logger.getLogger(PostLoginOld.class);

	/**
	 * 
	 * @param username
	 *            the
	 * @param pw
	 *            password
	 */
	public PostLoginOld(final String username, final String pw,
			final String domain) {
		this.username = username;

		NameValuePair action = new NameValuePair("wpLoginattempt", "Log in");
		NameValuePair url = new NameValuePair("wpRemember", "1");
		NameValuePair userid = new NameValuePair("wpName", username);
		NameValuePair dom = new NameValuePair("wpDomain", domain);

		String pwLabel = "wpPassword";

		NameValuePair password = new NameValuePair(pwLabel, pw);

		PostMethod pm = new PostMethod(
				"/index.php?title=Special:Userlogin&action=submitlogin&type=login");

		pm.getParams().setContentCharset(MediaWikiBot.CHARSET);

		pm.setRequestBody(new NameValuePair[] { action, url, userid, dom,
				password });

		msgs.add(pm);

	}

	/**
	 * @param cs
	 *            the
	 * @throws CookieException
	 *             when no cookies returning
	 */
	public void validateAllReturningCookies(final Cookie[] cs)
			throws CookieException {
		String compare = username;
		try {
			compare = URLEncoder.encode(username, MediaWikiBot.CHARSET);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (cs == null) {
			throw new CookieException("Cookiearray is null.");
		}
		if (cs.length == 0) {
			throw new CookieException("No cookies found.");
		} else {
			for (int i = 0; i < cs.length; i++) {
				if (cs[i].toString().contains(compare)) {
					LOG.info("Logged in as: " + username);
					return;
				}
				if (i == cs.length - 1) {
					throw new CookieException(
							"Login failed: Check Username and Password.");
				}
			}
		}
	}

//	private class Requestor {
//
//		Requestor() {
//
//		}
//	}
}
