package net.sourceforge.jwbf.actions.mw.queries;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;

import net.sourceforge.jwbf.actions.mw.util.MWAction;
import net.sourceforge.jwbf.actions.mw.util.ProcessException;
import net.sourceforge.jwbf.actions.mw.util.VersionException;
import net.sourceforge.jwbf.bots.MediaWikiBot;
import net.sourceforge.jwbf.contentRep.mw.Version;

import org.apache.commons.httpclient.methods.GetMethod;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

public class GetImageInfo extends MWAction {

	private String urlOfImage  = "";

	public GetImageInfo(String name, Version v) throws VersionException {
		
		switch (v) {
		case MW1_09:
		case MW1_10:
			throw new VersionException("Not supportet by this version of MW");
			
		default:
			try {
				msgs.add(new GetMethod("/api.php?action=query&titles=Image:"
						+ URLEncoder.encode(name, MediaWikiBot.CHARSET)
						+ "&prop=imageinfo"
						+ "&iiprop=url"
						+ "&format=xml"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
		
	}

	


	public String getUrlAsString() {
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
			Element element = (Element) el.next();
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
}
