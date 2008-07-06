package net.sourceforge.jwbf.actions.mw.meta;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;

import net.sourceforge.jwbf.actions.mw.util.MWAction;
import net.sourceforge.jwbf.actions.mw.util.ProcessException;
import net.sourceforge.jwbf.bots.MediaWikiBot;
import net.sourceforge.jwbf.contentRep.mw.Siteinfo;

import org.apache.commons.httpclient.methods.GetMethod;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

public class GetSiteinfo extends MWAction {

	private Siteinfo site = new Siteinfo();

	public GetSiteinfo() {
		
		

			try {
				msgs.add(new GetMethod("/api.php?action=query&meta=siteinfo" +
						"&siprop=" + URLEncoder.encode("general|namespaces|interwikimap", MediaWikiBot.CHARSET) +
						"&format=xml"));
				msgs.add(new GetMethod("/api.php?action=query&meta=siteinfo&format=xml"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
	}
	
	
	
	/**
	 * @param s
	 *            the returning text
	 * @return empty string
	 * 
	 */
	public String processAllReturningText(final String s)
			throws ProcessException {
//		System.err.println(s);
		parse(s);
		return "";
	}

	private void parse(final String xml) {
		System.out.println("parse: " + xml);
		SAXBuilder builder = new SAXBuilder();
		Element root = null;
		try {
			Reader i = new StringReader(xml);
			Document doc = builder.build(new InputSource(i));

			root = doc.getRootElement();
			findContent(root);
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	

	@SuppressWarnings("unchecked")
	private void findContent(final Element root) {

		Iterator<Element> el = root.getChildren().iterator();
		while (el.hasNext()) {
			Element element = (Element) el.next();
			if (element.getQualifiedName().equalsIgnoreCase("general")) {

				site.setMainpage(element
						.getAttributeValue("mainpage"));
				site.setBase(element.getAttributeValue("base"));
				site.setSitename(element
						.getAttributeValue("sitename"));
				site.setGenerator(element
						.getAttributeValue("generator"));
				site.setCase(element.getAttributeValue("case"));
				site.setRights(element.getAttributeValue("rights"));
			} else if (element.getQualifiedName().equalsIgnoreCase("ns")) {
				Integer id = Integer.parseInt(element.getAttributeValue("id"));
				String name = element.getText();
				site.addNamespace(id, name);

			} else if (element.getQualifiedName().equalsIgnoreCase("iw")) {
				if (element.getAttribute("prefix") != null) {
					String prefix = element
							.getAttributeValue("prefix");
					String name = element.getAttributeValue("url");
					site.addInterwiki(prefix, name);
				}
			} else {
				findContent(element);
			}
		}
	}

	public Siteinfo getSiteinfo() {
		return site;
	}

	
	
}
