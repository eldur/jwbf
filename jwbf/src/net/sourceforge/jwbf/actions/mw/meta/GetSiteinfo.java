package net.sourceforge.jwbf.actions.mw.meta;

import java.util.Iterator;

import net.sourceforge.jwbf.actions.Get;
import net.sourceforge.jwbf.actions.mw.HttpAction;
import net.sourceforge.jwbf.actions.mw.MediaWiki;
import net.sourceforge.jwbf.contentRep.mw.Siteinfo;
import net.sourceforge.jwbf.live.SiteinfoTest;

import org.jdom.Element;
/**
 * Gets details from the given MediaWiki installation like installed version. 
 * @author Thomas Stock
 * @supportedBy MediaWiki 1.09, 1.10
 * @supportedBy MediaWikiAPI 1.11, 1.12, 1.13
 * @see SiteinfoTest
 * @see Siteinfo
 *
 */
public class GetSiteinfo extends GetVersion {

	
	private Get msg;
	public GetSiteinfo() {

		msg = new Get("/api.php?action=query&meta=siteinfo" + "&siprop="
				+ MediaWiki.encode("general|namespaces") + "&format=xml");

	}
	@Override
	public HttpAction getNextMessage() {
		return msg;
	}

	@SuppressWarnings("unchecked")
	protected void findContent(final Element root) {
		
		Iterator<Element> el = root.getChildren().iterator();
		while (el.hasNext()) {
			Element element = el.next();
			if (element.getQualifiedName().equalsIgnoreCase("general")) {

				site.setMainpage(element
						.getAttributeValue("mainpage"));
				site.setBase(element.getAttributeValue("base"));
				site.setSitename(element
						.getAttributeValue("sitename"));
				site.setGenerator(element
						.getAttributeValue("generator"));
				site.setCase(element.getAttributeValue("case"));
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
