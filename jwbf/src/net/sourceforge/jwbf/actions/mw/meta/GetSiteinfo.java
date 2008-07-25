package net.sourceforge.jwbf.actions.mw.meta;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;

import net.sourceforge.jwbf.bots.MediaWikiBot;

import org.apache.commons.httpclient.methods.GetMethod;
import org.jdom.Element;

public class GetSiteinfo extends GetVersion {

	

	public GetSiteinfo() {
		
		

			try {
				msgs.add(new GetMethod("/api.php?action=query&meta=siteinfo" +
						"&siprop=" + URLEncoder.encode("general|namespaces|interwikimap", MediaWikiBot.CHARSET) +
						"&format=xml"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
	}
	
	
	

//	private void parse(final String xml) {
//		System.out.println("parse: " + xml);
//		SAXBuilder builder = new SAXBuilder();
//		Element root = null;
//		try {
//			Reader i = new StringReader(xml);
//			Document doc = builder.build(new InputSource(i));
//
//			root = doc.getRootElement();
//			findContent(root);
//		} catch (JDOMException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	

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

	

	
	
}
