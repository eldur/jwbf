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
package net.sourceforge.jwbf.actions.mw.editing;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Iterator;

import net.sourceforge.jwbf.actions.mw.util.ApiException;
import net.sourceforge.jwbf.actions.mw.util.MWAction;
import net.sourceforge.jwbf.actions.mw.util.ProcessException;
import net.sourceforge.jwbf.bots.MediaWikiBot;
import net.sourceforge.jwbf.contentRep.mw.SimpleArticle;

import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

/**
 * @author Thomas Stock
 * @since MediaWiki 1.9.0
 *
 */
public class GetRevision extends MWAction {
	
	private final SimpleArticle sa;
	public static final int CONTENT = 1 << 1;
	public static final int TIMESTAMP = 1 << 2;
	public static final int USER = 1 << 3;
	public static final int COMMENT = 1 << 4;
	public static final int FIRST = 1 << 5;
	public static final int LAST = 1 << 6;

	private static final Logger LOG = Logger.getLogger(GetRevision.class);
	
	/**
	 * TODO follow redirects.
	 */
	public GetRevision(final String articlename, final int property) {
		sa = new SimpleArticle();
		sa.setLabel(articlename);
		String uS = "";
		URI uri = null;
		try {
			uS = "/api.php?action=query&prop=revisions&titles="
					+ URLEncoder.encode(articlename, MediaWikiBot.CHARSET)
					+ "&rvprop=" + getDataProperties(property)
					+ getReversion(property)
					+ "&rvlimit=1"
					+ "&format=xml";
			uri = new URI(uS);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		LOG.debug(uS);
		msgs.add(new GetMethod(uri.toString()));
		
	}
	/**
	 * @param s
	 *            the returning text
	 * @return empty string
	 * 
	 */
	public String processAllReturningText(final String s) throws ProcessException {

		parse(s);
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
		String enc = ""; 
		
		try {
			enc = URLEncoder.encode(properties.substring(0, properties.length() - 1), MediaWikiBot.CHARSET);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
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
	
	
	private void parse(final String xml) throws ApiException{
		LOG.debug(xml);
		SAXBuilder builder = new SAXBuilder();
		Element root = null;
		try {
			Reader i = new StringReader(xml);
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

	public SimpleArticle getArticle() {
		
		
		return sa;
	}
	
	@SuppressWarnings("unchecked")
	private void findContent(final Element root) throws ApiException {
		
		
		
		Iterator<Element> el = root.getChildren().iterator();
		while (el.hasNext()) {
			Element element = (Element) el.next();
			if (element.getQualifiedName().equalsIgnoreCase("error")) {
				throw new ApiException(element.getAttributeValue("code"),
						element.getAttributeValue("info"));
			} else if (element.getQualifiedName().equalsIgnoreCase("rev")) {

				try {
					sa.setText(element.getText());
//					LOG.debug("found text");
				} catch (NullPointerException e) {
					// TODO: handle exception
				}

			
					sa.setEditSummary(getAsStringValues(element,"comment"));
					sa.setEditor(getAsStringValues(element,"user"));
			

				try {
					sa.setEditTimestamp(getAsStringValues(element,"timestamp"));
				} catch (ParseException e) {
					e.printStackTrace();
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

}
