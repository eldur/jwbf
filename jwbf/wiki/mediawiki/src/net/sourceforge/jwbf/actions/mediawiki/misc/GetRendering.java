package net.sourceforge.jwbf.actions.mediawiki.misc;

import static net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version.MW1_12;
import static net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version.MW1_13;
import static net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version.MW1_14;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.NoSuchElementException;

import net.sourceforge.jwbf.actions.Get;
import net.sourceforge.jwbf.actions.mediawiki.MediaWiki;
import net.sourceforge.jwbf.actions.mediawiki.util.MWAction;
import net.sourceforge.jwbf.actions.mediawiki.util.SupportedBy;
import net.sourceforge.jwbf.actions.mediawiki.util.VersionException;
import net.sourceforge.jwbf.actions.util.ActionException;
import net.sourceforge.jwbf.actions.util.HttpAction;
import net.sourceforge.jwbf.actions.util.ProcessException;
import net.sourceforge.jwbf.bots.MediaWikiBot;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;
/**
 * 
 * Implements function to render wikitext on remote 
 * <a href="http://www.mediawiki.org/wiki/API:Expanding_templates_and_rendering#parse">parse</a>.
 * 
 * @author Thomas Stock
 *
 */
@SupportedBy({ MW1_12, MW1_13, MW1_14 })
public class GetRendering extends MWAction {

	private final Get msg;
	private String html = "";
	private final MediaWikiBot bot;
	
	/**
	 * 
	 * @param wikitext
	 * @param bot
	 * @throws VersionException if not supported
	 */
	public GetRendering(String wikitext, MediaWikiBot bot) throws VersionException {
		this.bot = bot;
		switch (bot.getVersion()) {
		case MW1_09:
		case MW1_10:
		case MW1_11:
			throw new VersionException(bot.getVersion().name() + " does not support this action");


		default:
			msg = new Get("/api.php?action=parse&text=" + MediaWiki.encode(wikitext) + "&titles=API&format=xml");
		}
		
	}

	public HttpAction getNextMessage() {
		return msg;
	}

	@Override
	public String processAllReturningText(String s) throws ProcessException {
		html = findElement("text", s).getTextTrim();
		html = html.replace("\n", "");
		switch (bot.getVersion()) {
		case MW1_12:
			break;
		default:
			int last = html.lastIndexOf("<!--");
			html = html.substring(0, last);
		}
		return "";
	}
	
	protected Element findElement(String elementName, String xml) {
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
		return findContent(root, elementName);
		
	}
	
	private Element findContent(final Element e, final String name) {
		Element found = null;
		Iterator<Element> el = e.getChildren().iterator();
		while (el.hasNext()) {
			Element element = el.next();
			
			if (element.getQualifiedName().equalsIgnoreCase(name)) {
				System.out.println(element.getQualifiedName());
				return element;

			} else {
				found = findContent(element, name);
			}

		} 
		if (found == null)
		throw new NoSuchElementException();
		return found;
	}

	public void update() {
		try {
			bot.performAction(this);
		} catch (ActionException e) {
			e.printStackTrace();
		} catch (ProcessException e) {
			e.printStackTrace();
		}
	}
	
	public String getHtml() {
		if (html.length() < 1) {
			update();
		}
		return html;
	}

}
