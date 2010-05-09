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
 * Carlos Valenzuela
 */
package net.sourceforge.jwbf.mediawiki.actions.login;


import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_11;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_12;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_13;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_14;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_15;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.jwbf.core.actions.Post;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.core.actions.util.ProcessException;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.actions.util.SupportedBy;
import net.sourceforge.jwbf.mediawiki.contentRep.LoginData;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;
/**
 *
 * @author Thomas Stock
 */
@SupportedBy({ MW1_11, MW1_12, MW1_13, MW1_14, MW1_15 })
public class PostLogin extends MWAction {

	private final Logger log = Logger.getLogger(PostLogin.class);
	private Post msg;


	private final String success = "Success";
	private final String wrongPass = "WrongPass";
	private final String notExists = "NotExists";
	private final String needToken = "NeedToken";
	private LoginData login = null;
    private boolean reTry = false;
    private boolean reTryLimit = true;
    private final String username;
    private final String pw;
    private final String domain;

	/**
	 *
	 * @param username the
	 * @param pw password
	 * @param domain a
	 * @param login a
	 */
	public PostLogin(final String username, final String pw, final String domain, LoginData login) {
		super();
		this.login = login;
		this.username = username;
		this.pw = pw;
		this.domain = domain;
		msg = getLoginMsg(username, pw, domain, null);

	}

    private Post getLoginMsg(final String username, final String pw,
            final String domain, final String token) {
        Post pm = new Post("/api.php?action=login&format=xml");
        pm.addParam("lgname", username);
        pm.addParam("lgpassword", pw);
        if (domain != null)
            pm.addParam("lgdomain", domain);
        if (token != null) {
            pm.addParam("lgtoken", token);
        }
        return pm;
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String processAllReturningText(final String s) throws ProcessException {
		SAXBuilder builder = new SAXBuilder();
		Element root = null;
		try {
			Reader i = new StringReader(s);
			Document doc = builder.build(new InputSource(i));

			root = doc.getRootElement();
			findContent(root);
		} catch (JDOMException e) {
			log.error(e.getClass().getName() + e.getLocalizedMessage());
		} catch (IOException e) {
			log.error(e.getClass().getName() + e.getLocalizedMessage());
		} catch (NullPointerException e) {
			log.error(e.getClass().getName() + e.getLocalizedMessage());
			throw new ProcessException("No regular content was found, check your api\n::" + s);
		} catch (Exception e) {
			log.error(e.getClass().getName() + e.getLocalizedMessage());
			throw new ProcessException(e.getLocalizedMessage());
		}


		return s;
	}
	/**
	 *
	 * @param startElement the, where the search begins
	 * @throws ProcessException if problems with login
	 */
	private void findContent(final Element startElement) throws ProcessException {

		Element loginEl = startElement.getChild("login");
		String result = loginEl.getAttributeValue("result");
    if (result.equalsIgnoreCase(success)) {
      Map<String, String> properties = new HashMap<String, String>();
      properties.put("userId", loginEl.getAttribute("lguserid").toString());
      login.setup(loginEl.getAttributeValue("lgusername"), true);
    } else if (result.equalsIgnoreCase(needToken) && reTryLimit ) {
			msg = getLoginMsg(username, pw, domain, loginEl.getAttributeValue("token"));
			reTry = true;
			reTryLimit = false;
		} else if (result.equalsIgnoreCase(wrongPass)) {
			throw new ProcessException("Wrong Password");
		} else if (result.equalsIgnoreCase(notExists)) {
			throw new ProcessException("No such User");
		}

	}
	/**
	 * {@inheritDoc}
	 */
	public HttpAction getNextMessage() {
		return msg;
	}

    /* (non-Javadoc)
     * @see net.sourceforge.jwbf.mediawiki.actions.util.MWAction#hasMoreMessages()
     */
    @Override
    public boolean hasMoreMessages() {
        boolean temp = super.hasMoreMessages() || reTry;
        reTry  = false;
        return temp;
    }

}
