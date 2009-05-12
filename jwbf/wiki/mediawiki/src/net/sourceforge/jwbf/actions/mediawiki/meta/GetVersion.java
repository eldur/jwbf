package net.sourceforge.jwbf.actions.mediawiki.meta;

import static net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version.MW1_09;
import static net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version.MW1_10;
import static net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version.MW1_11;
import static net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version.MW1_12;
import static net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version.MW1_13;
import static net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version.MW1_14;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;

import net.sourceforge.jwbf.actions.Get;
import net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.actions.mediawiki.util.MWAction;
import net.sourceforge.jwbf.actions.mediawiki.util.SupportedBy;
import net.sourceforge.jwbf.actions.mediawiki.util.VersionException;
import net.sourceforge.jwbf.actions.util.HttpAction;
import net.sourceforge.jwbf.actions.util.ProcessException;
import net.sourceforge.jwbf.contentRep.mediawiki.Siteinfo;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

/**
 * Basic action to receive {@link Version}.
 * 
 * @author Thomas Stock
 *
 */

@SupportedBy({ MW1_09, MW1_10, MW1_11, MW1_12, MW1_13, MW1_14 })
public class GetVersion extends MWAction {

	protected Siteinfo site = new Siteinfo();
	private final Logger log = Logger.getLogger(getClass());
	private final Get msg;
	/* In this case the superconstructor with no value is allowed, because
	 * the versionrequest is mandatory */
	@SuppressWarnings("deprecation")
	public GetVersion() throws VersionException {

			msg = new Get("/api.php?action=query&meta=siteinfo&format=xml");

	}
	
	private void parse(final String xml) throws ProcessException {
		log.debug(xml); // TODO RM

		SAXBuilder builder = new SAXBuilder();
		Element root = null;
		try {
			Reader i = new StringReader(xml);
			Document doc = builder.build(new InputSource(i));

			root = doc.getRootElement();
			findContent(root);
		} catch (JDOMException e) {
			log.error(e.getClass().getName() + e.getLocalizedMessage());
			log.error(xml);
			throw new ProcessException(e.getLocalizedMessage());
		} catch (IOException e) {
			log.error(e.getClass().getName() + e.getLocalizedMessage());
			throw new ProcessException(e.getLocalizedMessage());
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
			} else {
				findContent(element);
			}
		}
	}

	public HttpAction getNextMessage() {
		return msg;
	}
}
