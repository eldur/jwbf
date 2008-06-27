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

import net.sourceforge.jwbf.actions.mw.MultiAction;
import net.sourceforge.jwbf.actions.mw.util.MWAction;
import net.sourceforge.jwbf.actions.mw.util.ProcessException;

import org.apache.commons.httpclient.methods.GetMethod;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

/**
 * 
 * Gets a list of pages recently changed, ordered by modification timestamp.
 * Parameters: rcfrom (paging timestamp), rcto (flt), rcnamespace (flt), rcminor
 * (flt), rcusertype (dflt=not|bot), rcdirection (dflt=older), rclimit (dflt=10,
 * max=500/5000) F
 * 
 * api.php ? action=query & list=recentchanges - List last 10 changes

 * @author Thomas Stock
 * @supportedBy MediaWikiAPI 1.10 recentchanges / rc
 */

public class GetRecentchanges extends MWAction implements MultiAction<String> {

	/** value for the bllimit-parameter. **/
	private int limit = 10;
	
	/**
	 * Collection that will contain the result
	 * (titles of articles linking to the target) 
	 * after performing the action has finished.
	 */
	private Collection<String> titleCollection = new ArrayList<String>();

	/**
	 * information necessary to get the next api page.
	 */

	


	/**
	 * generates the next MediaWiki-request (GetMethod) and adds it to msgs.
	 * @param namespace     the namespace(s) that will be searched for links,
	 *                      as a string of numbers separated by '|';
	 *                      if null, this parameter is omitted
	 */
	protected void generateRequest(String namespace) {
	 
	 	String uS = "";

		uS = "/api.php?action=query&list=recentchanges"
				
				+ ((namespace != null)?("&rcnamespace="+namespace):"")
				//+ "&rcminor="
				//+ "&rcusertype=" // (dflt=not|bot)
				+ "&rclimit=" + limit + "&format=xml";
			
	
			
		msgs.add(new GetMethod(uS));
				
		
	}
	
	/**
	 * 
	 */
	public GetRecentchanges(String ns) {
		generateRequest(ns);
	}
	
	/**
	 * 
	 */
	public GetRecentchanges(int count, String ns) {
		this.limit = count;
		generateRequest(ns);
	}
	
	/**
	 * deals with the MediaWiki api's response by parsing the provided text.
	 *
	 * @param s   the answer to the most recently generated MediaWiki-request
	 *
	 * @return empty string
	 */
	public String processAllReturningText(final String s) throws ProcessException {
		String t = s;
		parseArticleTitles(t);

		return "";
	}
	
	/**
	 * picks the article name from a MediaWiki api response.
	 *	
	 * @param s   text for parsing
	 */
	public void parseArticleTitles(String s) {
		
		SAXBuilder builder = new SAXBuilder();
		Element root = null;
		try {
			Reader i = new StringReader(s);
			Document doc = builder.build(new InputSource(i));
			
			root = doc.getRootElement();

		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		findContent(root);
		

		
	}
	@SuppressWarnings("unchecked")
	private void findContent(final Element root) {
		
		Iterator<Element> el = root.getChildren().iterator();
		while (el.hasNext()) {
			Element element = (Element) el.next();
			if (element.getQualifiedName().equalsIgnoreCase("rc")) {

				titleCollection.add(element.getAttributeValue("title"));

			} else {
				findContent(element);
			}
			
		}
	}

	/**
	 * @return   the collected article names
	 */
	public Collection<String> getResults() {
		return titleCollection;	 
	}
	
	/**
	 * @return   necessary information for the next action
	 *           or null if no next api page exists
	 */
	public GetRecentchanges getNextAction() {
		return null;
	}

}
