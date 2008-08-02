package net.sourceforge.jwbf.actions.mw.meta;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;

import net.sourceforge.jwbf.actions.mw.util.MWAction;
import net.sourceforge.jwbf.actions.mw.util.ProcessException;
import net.sourceforge.jwbf.contentRep.mw.Siteinfo;

import org.apache.commons.httpclient.methods.GetMethod;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

public class GetVersion extends MWAction {

	protected Siteinfo site = new Siteinfo();
	
	public GetVersion() {
	
			msgs.add(new GetMethod("/api.php?action=query&meta=siteinfo&format=xml"));

	}
	
	private void parse(final String xml) {
//		System.out.println("parse: " + xml);
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
	
	/**
	 * @param s
	 *            the returning text
	 * @return empty string
	 * 
	 */
	public final String processAllReturningText(final String s)
			throws ProcessException {
//		System.err.println(s);
		parse(s);
		return "";
	}

	
	public Siteinfo getSiteinfo() {
		return site;
	}
	
	@SuppressWarnings("unchecked")
	protected void findContent(final Element root) {

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
			} else {
				findContent(element);
			}
		}
	}
}
