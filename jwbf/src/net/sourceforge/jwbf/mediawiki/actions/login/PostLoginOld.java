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
package net.sourceforge.jwbf.mediawiki.actions.login;


import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_09;

import java.util.Map;

import net.sourceforge.jwbf.core.actions.CookieValidateable;
import net.sourceforge.jwbf.core.actions.Post;
import net.sourceforge.jwbf.core.actions.util.CookieException;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.actions.util.SupportedBy;
import net.sourceforge.jwbf.mediawiki.contentRep.LoginData;

import org.apache.log4j.Logger;

/**
 *
 * @author Thomas Stock
 */
@SupportedBy({ MW1_09 })
public class PostLoginOld extends MWAction implements CookieValidateable {
	private String username = "";

	private static final Logger LOGGER = Logger.getLogger(PostLoginOld.class);

	private LoginData login;
	private Post msg;

	/**
	 *
	 * @param username
	 *            the
	 * @param pw
	 *            password
	 * @param domain a
	 * @param login a
	 */
	public PostLoginOld(final String username, final String pw,
			final String domain, LoginData login) {
		super();
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
	 *            a
	 * @param hm
	 *            the method object
	 * @throws CookieException
	 *             never
	 *
	 */
	public final void validateReturningCookies(final Map<String, String> cs, HttpAction hm)
			throws CookieException {
		validateAllReturningCookies(cs);

	}


	/**
	 * @param cs
	 *            the
	 * @throws CookieException
	 *             when no cookies returning
	 */
	public void validateAllReturningCookies(final Map<String, String> cs)
			throws CookieException {
		String compare = username;

		compare = MediaWiki.encode(username);

		if (cs == null) {
			throw new CookieException("Cookiearray is null.");
		}
		if (cs.isEmpty()) {
			throw new CookieException("No cookies found.");
		} else {

			if (cs.containsValue(compare)) {
			  if (LOGGER.isInfoEnabled())
			    LOGGER.info("Logged in as: " + username);
				login.setup(username, true);
				return;
			} else {
				throw new CookieException(
						"Login failed: Check Username and Password.");
			}



		}
	}
	/**
	 * {@inheritDoc}
	 */
	public HttpAction getNextMessage() {
		return msg;
	}

}
