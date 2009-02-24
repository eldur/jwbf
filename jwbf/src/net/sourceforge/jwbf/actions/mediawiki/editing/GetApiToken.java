package net.sourceforge.jwbf.actions.mediawiki.editing;

import java.io.IOException;
import java.io.StringReader;

import net.sourceforge.jwbf.actions.Get;
import net.sourceforge.jwbf.actions.mediawiki.MediaWiki;
import net.sourceforge.jwbf.actions.mediawiki.util.MWAction;
import net.sourceforge.jwbf.actions.mediawiki.util.VersionException;
import net.sourceforge.jwbf.actions.util.HttpAction;
import net.sourceforge.jwbf.actions.util.ProcessException;
import net.sourceforge.jwbf.contentRep.mw.Siteinfo;
import net.sourceforge.jwbf.contentRep.mw.Userinfo;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

/**
 * Action class using the MediaWiki-<a
 * href="http://www.mediawiki.org/wiki/API:Changing_wiki_content"
 * >Editing-API</a>.
 * <br />
 * Its job is to get the token for some actions like delete or edit.
 *
 * @author Max Gensthaler
 * @author Thomas Stock
 * @supportedBy MediaWikiAPI 1.12
 * @supportedBy MediaWikiAPI 1.13
 */
abstract class GetApiToken extends MWAction {
	private static final Logger LOG = Logger.getLogger(GetApiToken.class);
	/** Types that need a token. See API field intoken. */
	// TODO this does not feel the elegant way.
	// Probably put complete request URIs into this enum objects
	// to support different URIs for different actions.
	public enum Intoken { DELETE, EDIT, MOVE, PROTECT, EMAIL };
	private String token = null;

	private boolean first = true;
	private boolean second = true;
	
	private Get msg;
	/**
	 * Constructs a new <code>GetToken</code> action.
	 * @param intoken type to get the token for
	 * @param title title of the article to generate the token for
	 * @param si site info object
	 * @param ui user info object
	 * @throws VersionException if this action is not supported of the MediaWiki version connected to
	 */
	protected GetApiToken(Intoken intoken, String title, Siteinfo si, Userinfo ui) throws VersionException {
		switch (si.getVersion()) {
		case MW1_09:
		case MW1_10:
		case MW1_11:
			throw new VersionException("Not supportet by this version of MediaWiki");
		default:
			generateTokenRequest(intoken, title);
			break;
		}
	}

	/**
	 * Generates the next MediaWiki API token and adds it to <code>msgs</code>.
	 * @param intoken type to get the token for
	 * @param title title of the article to generate the token for
	 */
	private void generateTokenRequest(Intoken intoken, String title) {
		if( LOG.isTraceEnabled()) {
			LOG.trace("enter GetToken.generateTokenRequest()");
		}
			String uS = "/api.php"
					+ "?action=query"
					+ "&prop=info"
					+ "&intoken=" + intoken.toString().toLowerCase()
					+ "&titles=" + MediaWiki.encode(title)
					+ "&format=xml";
			msg = new Get(uS);
	
	}

	/**
	 * Returns the requested token after parsing the result from MediaWiki.
	 * @return the requested token
	 */
	protected String getToken() {
		return token;
	}

	/**
	 * Deals with the MediaWiki API's response by parsing the provided text.
	 * @param s the answer to the most recently generated MediaWiki API request
	 * @return empty string
	 */
	@Override
	public String processReturningText(String s, HttpAction hm)
			throws ProcessException {
		if (hm.getRequest().equals(msg.getRequest())) {
			if (LOG.isTraceEnabled()) {
				LOG.trace("enter GetToken.processAllReturningText(String)");
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug("Got returning text: \"" + s + "\"");
			}
			SAXBuilder builder = new SAXBuilder();
			try {
				Document doc = builder.build(new InputSource(
						new StringReader(s)));
				process(doc);
			} catch (JDOMException e) {
				if (s.startsWith("unknown_action:")) {
					LOG.error(
									"Adding '$wgEnableWriteAPI = true;' to your MediaWiki's LocalSettings.php might remove this problem.",
									e);
				} else {
					LOG.error(e.getMessage(), e);
				}
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return "";
	}

	public HttpAction getNextMessage() {
		if (first) {
			first = false;
			return msg;	
		} else {
			second = false;
			
			return getSecondRequest();
		}
		
	}
	@Override
	public boolean hasMoreMessages() {
		return first || second;
	}
	abstract protected HttpAction getSecondRequest();
	/**
	 * Processing the XML {@link Document} returned from the MediaWiki API.
	 * @param doc XML <code>Document</code>
	 * @throws JDOMException thrown if the document could not be parsed
	 */
	private void process(Document doc) throws JDOMException {
		Element elem = null;
		try {
			elem = doc.getRootElement().getChild("query").getChild("pages").getChild("page");
		} catch (NullPointerException e) {
			// do nothing
		}
		if (elem != null) {
			// process reply for token request
			token = elem.getAttributeValue("deletetoken");
		} else {
			LOG.error("Unknow reply. This is not a token.");
		}
	}
}
