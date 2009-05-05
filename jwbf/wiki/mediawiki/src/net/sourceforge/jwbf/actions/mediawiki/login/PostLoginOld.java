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
package net.sourceforge.jwbf.actions.mediawiki.login;


import net.sourceforge.jwbf.actions.Post;
import net.sourceforge.jwbf.actions.mediawiki.MediaWiki;
import net.sourceforge.jwbf.actions.mediawiki.util.MWAction;
import net.sourceforge.jwbf.actions.mediawiki.util.VersionException;
import net.sourceforge.jwbf.actions.util.CookieException;
import net.sourceforge.jwbf.actions.util.HttpAction;
import net.sourceforge.jwbf.bots.util.LoginData;

import org.apache.commons.httpclient.Cookie;
import org.apache.log4j.Logger;

/**
 * 
 * @author Thomas Stock
 * @supportedBy MediaWiki 1.8.x, 1.9.x
 */
public class PostLoginOld extends MWAction {

	private String username = "";

	private static final Logger LOG = Logger.getLogger(PostLoginOld.class);

	private LoginData login;
	private Post msg;
	
	/**
	 * 
	 * @param username
	 *            the
	 * @param pw
	 *            password
	 */
	public PostLoginOld(final String username, final String pw,
			final String domain, LoginData login) throws VersionException {
		this.username = username;
		this.login = login;

		Post pm = new Post(
				"/index.php?title=Special:Userlogin&action=submitlogin&type=login");
		pm.addParam("wpLoginattempt", "Log in");
		pm.addParam("wpRemember", "1");
		pm.addParam("wpName", username);
		pm.addParam("wpDomain", domain);
		pm.addParam("wpPassword", pw);
	

		msg = pm;

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

		compare = MediaWiki.encode(username);

		if (cs == null) {
			throw new CookieException("Cookiearray is null.");
		}
		if (cs.length == 0) {
			throw new CookieException("No cookies found.");
		} else {
			for (int i = 0; i < cs.length; i++) {
				if (cs[i].toString().contains(compare)) {
					LOG.info("Logged in as: " + username);
					login.setup(0, username, "0", true);
					return;
				}
				if (i == cs.length - 1) {
					throw new CookieException(
							"Login failed: Check Username and Password.");
				}
			}
		}
	}

	public HttpAction getNextMessage() {
		return msg;
	}

}
