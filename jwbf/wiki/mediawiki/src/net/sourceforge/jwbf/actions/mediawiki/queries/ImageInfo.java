package net.sourceforge.jwbf.actions.mediawiki.queries;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import net.sourceforge.jwbf.actions.Get;
import net.sourceforge.jwbf.actions.mediawiki.MediaWiki;
import net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.actions.mediawiki.util.MWAction;
import net.sourceforge.jwbf.actions.mediawiki.util.VersionException;
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
 * @author Thomas Stock
 * 
 * @supportedBy MediaWikiAPI 1.11, 1.12, 1.13
 *
 */

public class ImageInfo extends MWAction {

	private String urlOfImage  = "";
	private Get msg;
	private final String hostUrl;
	
	/**
	 * 
	 * Get an absolute url to an image.
	 * @param name of, like "Test.gif"
	 * @param v of
	 * @param botHostUrl the, {@link MediaWikiBot#getHostUrl()}
	 * @throws VersionException if not supported
	 * 
	 */
	public ImageInfo(String name, Version v, String botHostUrl) throws VersionException {
		this.hostUrl = botHostUrl;
		switch (v) {
		case MW1_09:
		case MW1_10:
			throw new VersionException("Not supportet by this version of MW");

		default:
			msg = new Get("/api.php?action=query&titles=Image:"
					+ MediaWiki.encode(name) + "&prop=imageinfo"
					+ "&iiprop=url" + "&format=xml");

			break;
		}

	}
	/**
	 * 
	 * Get an absolute url to an image.
	 * @param name of, like "Test.gif"
	 * @param v of
	 * @throws VersionException if not supported
	 * 
	 */
	public ImageInfo(String name, Version v) throws VersionException {
		this(name, v, "");
	}

	/**
	 * @return position like "http://server.tld/path/to/Test.gif"
	 */
	public String getUrlAsString() throws ProcessException {
		try {
			new URL(urlOfImage);
		} catch (MalformedURLException e) {
			if (hostUrl.length() <= 0) {
				throw new ProcessException("please use the constructor with hostUrl");
			}
			urlOfImage = hostUrl + urlOfImage;
		}
		return urlOfImage  ;
	}

	@Override
	public String processAllReturningText(String s) throws ProcessException {
		findUrlOfImage(s);
		return "";
	}
	

	@SuppressWarnings("unchecked")
	private void findContent(final Element root) {

		Iterator<Element> el = root.getChildren().iterator();
		while (el.hasNext()) {
			Element element = el.next();
			if (element.getQualifiedName().equalsIgnoreCase("ii")) {
				urlOfImage = element.getAttributeValue("url");
				return;
			} else {
				findContent(element);
			}

		}
	}

	private void findUrlOfImage(String s) {
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

	public HttpAction getNextMessage() {
		return msg;
	}
}
