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
package net.sourceforge.jwbf.actions.mediawiki.editing;

import static net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version.MW1_09;
import static net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version.MW1_10;
import static net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version.MW1_11;
import static net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version.MW1_12;
import static net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version.MW1_13;
import static net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version.MW1_14;
import static net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version.MW1_15;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;
import java.util.Iterator;

import net.sourceforge.jwbf.actions.Get;
import net.sourceforge.jwbf.actions.mediawiki.MediaWiki;
import net.sourceforge.jwbf.actions.mediawiki.util.ApiException;
import net.sourceforge.jwbf.actions.mediawiki.util.MWAction;
import net.sourceforge.jwbf.actions.mediawiki.util.SupportedBy;
import net.sourceforge.jwbf.actions.util.ActionException;
import net.sourceforge.jwbf.actions.util.HttpAction;
import net.sourceforge.jwbf.actions.util.ProcessException;
import net.sourceforge.jwbf.bots.MediaWikiBot;
import net.sourceforge.jwbf.contentRep.SimpleArticle;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

/**
 * Reads the content of a given article.
 * 
 * @author Thomas Stock
 * 
 * 
 */
@SupportedBy({ MW1_09, MW1_10, MW1_11, MW1_12, MW1_13, MW1_14, MW1_15 })
public class GetRevision extends MWAction {

	private final SimpleArticle sa;

	public static final int CONTENT = 1 << 1;
	public static final int TIMESTAMP = 1 << 2;
	public static final int USER = 1 << 3;
	public static final int COMMENT = 1 << 4;
	public static final int FIRST = 1 << 5;
	public static final int LAST = 1 << 6;

	private final Logger log = Logger.getLogger(getClass());

	private final int properties;
	
	private final Get msg;
	
	private boolean q = true;

	/**
	 * TODO follow redirects.
	 * TODO change constructor fild ordering; bot
	 * @throws ProcessException 
	 * @throws ActionException 
	 */
	public GetRevision(MediaWikiBot bot, final String articlename, final int properties) throws ActionException, ProcessException {
		super(bot.getVersion());
//		if (!bot.getUserinfo().getRights().contains("read")) {
//			throw new ActionException("reading is not permited, make sure that this account is able to read");
//		} FIXME check if 
		
		this.properties = properties;
		sa = new SimpleArticle();
		sa.setLabel(articlename);
		String uS = "/api.php?action=query&prop=revisions&titles="
				+ MediaWiki.encode(articlename) + "&rvprop="
				+ getDataProperties(properties) + getReversion(properties)
				+ "&rvlimit=1" + "&format=xml";
		msg = new Get(uS);

	}

	/**
	 * {@inheritDoc}
	 */
	public String processReturningText(final String s, HttpAction ha)
			throws ProcessException {
		if (msg.getRequest().equals(ha.getRequest()) && q) {
			log.debug(s);
			parse(s);
			q = false; // FIXME RM q
			
		}
		return "";
	}

	private String getDataProperties(final int property) {
		String properties = "";

		if ((property & CONTENT) > 0) {
			properties += "content|";
		}
		if ((property & COMMENT) > 0) {
			properties += "comment|";
		}
		if ((property & TIMESTAMP) > 0) {
			properties += "timestamp|";
		}
		if ((property & USER) > 0) {
			properties += "user|";
		}
		String enc = MediaWiki.encode(properties.substring(0,
					properties.length() - 1));
	
		return enc;
	}

	private String getReversion(final int property) {
		String properties = "&rvdir=";

		if ((property & FIRST) > 0) {
			properties += "newer";
		} else {
			properties += "older";
		}

		return properties;
	}

	private void parse(final String xml) throws ApiException {

		SAXBuilder builder = new SAXBuilder();
		Element root = null;
		try {
			Reader i = new StringReader(xml);
			Document doc = builder.build(new InputSource(i));

			root = doc.getRootElement();

		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		findContent(root);
	}
	/**
	 * 
	 * @return the
	 */
	public SimpleArticle getArticle() {

		return sa;
	}

	
	private void findContent(final Element root) throws ApiException {
//		if(log.isDebugEnabled())
//			log.debug("try to find content in " + root.getQualifiedName());
		@SuppressWarnings("unchecked")
		Iterator<Element> el = root.getChildren().iterator();
		while (el.hasNext()) {
			Element element = el.next();
			if (element.getQualifiedName().equalsIgnoreCase("error")) {
				throw new ApiException(element.getAttributeValue("code"),
						element.getAttributeValue("info"));
			} else if (element.getQualifiedName().equalsIgnoreCase("rev")) {

				try {
					sa.setText(element.getText());
				} catch (NullPointerException e) {
					if (log.isDebugEnabled()) {
						log.debug("no text found");
					}
				}

				sa.setEditSummary(getAsStringValues(element, "comment"));
				sa.setEditor(getAsStringValues(element, "user"));

				if ((properties & TIMESTAMP) > 0) {

					try {
						sa.setEditTimestamp(getAsStringValues(element,
								"timestamp"));
					} catch (ParseException e) {
						log.debug("timestamp could not be parsed");
					}
				}

			} else {
				findContent(element);
			}

		}

	}

	private String getAsStringValues(Element e, String attrName) {
		String buff = "";
		try {
			buff = e.getAttributeValue(attrName);
			if (buff == null) {
				throw new NullPointerException();
			}
		} catch (Exception npe) {
			// LOG.debug("no value for " + attrName );
			buff = "";
		}
		// LOG.debug("value for " + attrName + " = \"" + buff + "\"");
		return buff;
	}
	/**
	 * {@inheritDoc}
	 */
	public HttpAction getNextMessage() {
		return msg;
	}



}
