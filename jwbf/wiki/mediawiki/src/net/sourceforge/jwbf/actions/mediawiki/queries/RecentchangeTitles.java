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
package net.sourceforge.jwbf.actions.mediawiki.queries;

import static net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version.MW1_09;
import static net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version.MW1_10;
import static net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version.MW1_11;
import static net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version.MW1_12;
import static net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version.MW1_13;
import static net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version.MW1_14;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import net.sourceforge.jwbf.actions.Get;
import net.sourceforge.jwbf.actions.mediawiki.MediaWiki;
import net.sourceforge.jwbf.actions.mediawiki.util.SupportedBy;
import net.sourceforge.jwbf.actions.mediawiki.util.VersionException;
import net.sourceforge.jwbf.actions.util.ActionException;
import net.sourceforge.jwbf.actions.util.HttpAction;
import net.sourceforge.jwbf.actions.util.ProcessException;
import net.sourceforge.jwbf.bots.MediaWikiBot;

import org.apache.log4j.Logger;
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
 * 
 * @author Thomas Stock
 * @supportedBy MediaWikiAPI 1.09, 1.10, 1.11, 1.12, 1.13, 1.14
 */
@SupportedBy({MW1_09, MW1_10, MW1_11, MW1_12, MW1_13, MW1_14})
public class RecentchangeTitles extends TitleQuery {

	/** value for the bllimit-parameter. **/
	private final int limit = 10;
	private Get msg;
	private String timestamp = "";
	
	private boolean init = true;
	private int find = 1;
	
	private final MediaWikiBot bot;
	
	private final int [] namespaces;
	private Logger log = Logger.getLogger(getClass());
	/**
	 * Collection that will contain the result
	 * (titles of articles linking to the target) 
	 * after performing the action has finished.
	 */
	private Collection<String> titleCollection = new Vector<String>();

	/**
	 * information necessary to get the next api page.
	 */

	


	/**
	 * generates the next MediaWiki-request (GetMethod) and adds it to msgs.
	 * @param namespace     the namespace(s) that will be searched for links,
	 *                      as a string of numbers separated by '|';
	 *                      if null, this parameter is omitted
	 * @param rcstart timestamp
	 */
	private void generateRequest(int [] namespace, String rcstart) {
	 
	 	String uS = "";
	 	if (rcstart.length() > 0) {
		uS = "/api.php?action=query&list=recentchanges"
				
				+ ((namespace != null)?("&rcnamespace="+MediaWiki.encode(createNsString(namespace))):"")
				+ "&rcstart=" + rcstart
				//+ "&rcusertype=" // (dflt=not|bot)
				+ "&rclimit=" + limit + "&format=xml";
	 	} else {
	 		uS = "/api.php?action=query&list=recentchanges"
				
				+ ((namespace != null)?("&rcnamespace="+MediaWiki.encode(createNsString(namespace))):"")
				//+ "&rcminor="
				//+ "&rcusertype=" // (dflt=not|bot)
				+ "&rclimit=" + limit + "&format=xml";
	 	}
	
			
		msg = new Get(uS);
				
		
	}
	
	private void generateRequest(int [] namespace) {
		 
		generateRequest(namespace, "");
				
		
	}
	
	/**
	 * 
	 */
	public RecentchangeTitles(MediaWikiBot bot, int... ns) throws VersionException {
		super(bot.getVersion());
		namespaces = ns;
		this.bot = bot;
		
	}
	/**
	 * 
	 */
	public RecentchangeTitles(MediaWikiBot bot) throws VersionException {
		this(bot, MediaWiki.NS_ALL);
		
	
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
		titleCollection.clear();
		parseArticleTitles(t);
		if (log.isInfoEnabled())
			log.info("found: " + titleCollection);
		titleIterator = titleCollection.iterator();
		
		return "";
	}
	
	/**
	 * picks the article name from a MediaWiki api response.
	 *	
	 * @param s   text for parsing
	 */
	private void parseArticleTitles(String s) {
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
			Element element = el.next();
			if (element.getQualifiedName().equalsIgnoreCase("rc")) {
				if (find < limit) {
				titleCollection.add(MediaWiki.decode(element.getAttributeValue("title")));
	
				}
				
				timestamp = element.getAttribute("timestamp").getValue();
				find++;
			} else {
				findContent(element);
			}
			
		}
	}


	
	public HttpAction getNextMessage() {
		setHasMoreMessages(false);
		return msg;
	}
	
	protected void prepareCollection() {

		if (init || (!titleIterator.hasNext() && timestamp.length() > 0)) {
			
			if(init) {
				generateRequest(namespaces);
			} else {
				generateRequest(namespaces, timestamp);
			}
			init = false;
			
			try {
				if (log.isDebugEnabled())
					log.debug("request more ...");
				bot.performAction(this);
				setHasMoreMessages(true);
				find = 1;
				
			} catch (ActionException e) {
				e.printStackTrace();
				timestamp = "";
			} catch (ProcessException e) {
				e.printStackTrace();
				timestamp = "";
			}

		}
	}

	
	
	

	@Override
	protected Object clone() throws CloneNotSupportedException {

		try {
			return new RecentchangeTitles(bot, namespaces);
		} catch (VersionException e) {
			throw new CloneNotSupportedException(e.getLocalizedMessage());
		}
	}

	

}
