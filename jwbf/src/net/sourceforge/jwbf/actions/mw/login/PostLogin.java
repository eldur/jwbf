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


import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import net.sourceforge.jwbf.actions.mw.util.ActionException;
import net.sourceforge.jwbf.actions.mw.util.MWAction;
import net.sourceforge.jwbf.actions.mw.util.ProcessException;
import net.sourceforge.jwbf.bots.MediaWikiBot;
import net.sourceforge.jwbf.bots.util.LoginData;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
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
 * @supportedBy MediaWiki 1.9.x
 */
public class PostLogin extends MWAction {
	
	private static final Logger LOG = Logger.getLogger(PostLogin.class);
	
	private LoginData login = null;
	
	private final String success = "Success";
	private final String wrongPass = "WrongPass";
	private final String notExists = "NotExists";
	
	private String exceptionText = "";

	
	/**
	 * 
	 * @param username the
	 * @param pw password
	 */
	public PostLogin(final String username, final String pw) {

		NameValuePair userid = new NameValuePair("lgname", username);
		NameValuePair password = new NameValuePair("lgpassword", pw);

		PostMethod pm = new PostMethod(
				"/api.php?action=login&format=xml");

		pm.setRequestBody(new NameValuePair[] {userid,
						password });
		pm.getParams().setContentCharset(MediaWikiBot.CHARSET);
		msgs.add(pm);

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

		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		LOG.debug(s);
		findContent(root);
		return s;
	}
	private void findContent(final Element api){
		Element login = api.getChild("login");
		String result = login.getAttributeValue("result");
		if (result.equalsIgnoreCase(success)) {
			try {
				this.login = new LoginData(login.getAttribute("lguserid").getIntValue()
						, login.getAttributeValue("lgusername")
						, login.getAttributeValue("lgtoken"));
			} catch (DataConversionException e) {
				e.printStackTrace();
			}
		} else if (result.equalsIgnoreCase(wrongPass)) {
			exceptionText = "Wrong Password";
		} else if (result.equalsIgnoreCase(notExists)) {
			exceptionText = "No sutch User";
		} 
	}

	public LoginData getLoginData() throws ActionException {
		if (login == null) {
			throw new ActionException(exceptionText);
		}
		return login;
	}
}
