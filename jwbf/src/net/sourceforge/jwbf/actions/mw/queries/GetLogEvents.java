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
package net.sourceforge.jwbf.actions.mw.queries;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.sourceforge.jwbf.actions.mw.util.MWAction;
import net.sourceforge.jwbf.actions.mw.util.ProcessException;
import net.sourceforge.jwbf.contentRep.mw.LogItem;

import org.apache.commons.httpclient.methods.GetMethod;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

/**
 * 
 * List log events, filtered by time range, event type, user type, or the page
 * it applies to. Ordered by event timestamp. Parameters: letype (flt), lefrom
 * (paging timestamp), leto (flt), ledirection (dflt=older), leuser (flt),
 * letitle (flt), lelimit (dflt=10, max=500/5000)
 * 
 * api.php ? action=query & list=logevents      - List last 10 events of any type
 * 
 * TODO This is a semi-complete extension point
 * @author Thomas Stock
 * @supportedBy MediaWikiAPI 1.11 logevents / le (semi-complete)
 * 
 */

public class GetLogEvents extends MWAction  {

	/** value for the bllimit-parameter. * */
	private int limit = 10;

	/**
	 * Collection that will contain the result (titles of articles linking to
	 * the target) after performing the action has finished.
	 */
	private Collection<LogItem> logArray = new ArrayList<LogItem>();

	/**
	 * information necessary to get the next api page.
	 */

	/**
	 * generates the next MediaWiki-request (GetMethod) and adds it to msgs.
	 * 
	 * @param logtype
	 *            type of log, like upload
	 */
	protected void generateRequest(String... logtype) {

		String uS = "";

		uS = "/api.php?action=query&list=logevents";
		if (logtype.length > 0) {
			String logtemp = "";
			for (int i = 0; i < logtype.length; i++) {
				logtemp += logtype[i] + "|";
			}
			uS += "&letype=" + logtemp.substring(0, logtemp.length() - 1);
		}
			
		uS += "&lelimit=" + limit + "&format=xml";

		msgs.add(new GetMethod(uS));

	}

	
	/**
	 * 
	 */
	public GetLogEvents(String... type) {
		generateRequest(type);
	}

	/**
	 * 
	 */
	public GetLogEvents(int limit, String... type) {
		this.limit = limit;
		generateRequest(type);
	}

	/**
	 * deals with the MediaWiki api's response by parsing the provided text.
	 * 
	 * @param s
	 *            the answer to the most recently generated MediaWiki-request
	 * 
	 * @return empty string
	 */
	public String processAllReturningText(final String s)
			throws ProcessException {
		String t = s;
		parseArticleTitles(t);

		return "";
	}

	/**
	 * picks the article name from a MediaWiki api response.
	 * 
	 * @param s
	 *            text for parsing
	 */
	public void parseArticleTitles(String s) {

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
		findContent(root);

	}

	@SuppressWarnings("unchecked")
	private void findContent(final Element root) {

		Iterator<Element> el = root.getChildren().iterator();
		while (el.hasNext()) {
			Element element = (Element) el.next();
			if (element.getQualifiedName().equalsIgnoreCase("item")) {

				LogItem l = new LogItem();
				l.setTitle(element.getAttributeValue("title"));
				l.setType(element.getAttributeValue("type"));
				l.setUser(element.getAttributeValue("user"));
				logArray.add(l);

			} else {
				findContent(element);
			}

		}
	}

	/**
	 * @return the collected article names
	 */
	public Iterator<LogItem> getResults() {
		return logArray.iterator();
	}



}
