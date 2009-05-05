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


import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import net.sourceforge.jwbf.actions.Post;
import net.sourceforge.jwbf.actions.mediawiki.util.MWAction;
import net.sourceforge.jwbf.actions.mediawiki.util.VersionException;
import net.sourceforge.jwbf.actions.util.HttpAction;
import net.sourceforge.jwbf.actions.util.ProcessException;
import net.sourceforge.jwbf.bots.util.LoginData;

import org.apache.log4j.Logger;
import org.jdom.DataConversionException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

/**
 * 
 * @author Thomas Stock
 * @supportedBy MediaWikiAPI 1.11, 1.12, 1.13, 1.14

 */
public class PostLogin extends MWAction {
	
	private static final Logger log = Logger.getLogger(PostLogin.class);
	private final Post msg;
	
	
	private final String success = "Success";
	private final String wrongPass = "WrongPass";
	private final String notExists = "NotExists";
	private LoginData login = null;
	
	/**
	 * 
	 * @param username the
	 * @param pw password
	 */
	public PostLogin(final String username, final String pw, LoginData login) throws VersionException {
		this.login = login;
		Post pm = new Post(
				"/api.php?action=login&format=xml");
		pm.addParam("lgname", username);
		pm.addParam("lgpassword", pw);
		
		msg = pm;
		

	}

	/**
	 * @param s incomming
	 * @return after testing
	 */
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
			throw new ProcessException("No regular content was found, check your api\n::" + s );
		} catch (Exception e) {
			log.error(e.getClass().getName() + e.getLocalizedMessage());
			throw new ProcessException(e.getLocalizedMessage());
		}

		
		return s;
	} 
	private void findContent(final Element api) throws ProcessException, NullPointerException {
		
		Element loginEl = api.getChild("login");
		String result = loginEl.getAttributeValue("result");
		if (result.equalsIgnoreCase(success)) {
			try {
				login.setup(loginEl.getAttribute("lguserid").getIntValue()
						, loginEl.getAttributeValue("lgusername")
						, loginEl.getAttributeValue("lgtoken"), true);
			} catch (DataConversionException e) {
				e.printStackTrace();
			}
		} else if (result.equalsIgnoreCase(wrongPass)) {
			throw new ProcessException("Wrong Password");
		} else if (result.equalsIgnoreCase(notExists)) {
			throw new ProcessException("No sutch User");
		} 
		
	}

	public HttpAction getNextMessage() {
		return msg;
	}
}
