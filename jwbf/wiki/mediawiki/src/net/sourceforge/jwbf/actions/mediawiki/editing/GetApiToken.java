package net.sourceforge.jwbf.actions.mediawiki.editing;

import static net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version.MW1_12;
import static net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version.MW1_13;

import java.io.IOException;
import java.io.StringReader;

import net.sourceforge.jwbf.actions.Get;
import net.sourceforge.jwbf.actions.mediawiki.MediaWiki;
import net.sourceforge.jwbf.actions.mediawiki.util.MWAction;
import net.sourceforge.jwbf.actions.mediawiki.util.SupportedBy;
import net.sourceforge.jwbf.actions.mediawiki.util.VersionException;
import net.sourceforge.jwbf.actions.util.HttpAction;
import net.sourceforge.jwbf.actions.util.ProcessException;
import net.sourceforge.jwbf.contentRep.Userinfo;
import net.sourceforge.jwbf.contentRep.mediawiki.Siteinfo;

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
 */
@SupportedBy({ MW1_12, MW1_13 })
final class GetApiToken extends MWAction {
	/** Types that need a token. See API field intoken. */
	// TODO this does not feel the elegant way.
	// Probably put complete request URIs into this enum objects
	// to support different URIs for different actions.
	public enum Intoken { DELETE, EDIT, MOVE, PROTECT, EMAIL };
	private String token = null;
	private Logger log = Logger.getLogger(GetApiToken.class);

	private boolean first = true;

	private Intoken intoken = null;
	
	private Get msg;
	/**
	 * Constructs a new <code>GetToken</code> action.
	 * @param intoken type to get the token for
	 * @param title title of the article to generate the token for
	 * @param si site info object
	 * @param ui user info object
	 * @throws VersionException if this action is not supported of the MediaWiki version connected to
	 */
	GetApiToken(Intoken intoken, String title, Siteinfo si, Userinfo ui) throws VersionException {
		super(si.getVersion());
		this.intoken = intoken;
		generateTokenRequest(intoken, title);
		
	}

	/**
	 * Generates the next MediaWiki API token and adds it to <code>msgs</code>.
	 * @param intoken type to get the token for
	 * @param title title of the article to generate the token for
	 */
	private void generateTokenRequest(Intoken intoken, String title) {
		if (log.isTraceEnabled()) {
			log.trace("enter GetToken.generateTokenRequest()");
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
			if (log.isTraceEnabled()) {
				log.trace("enter GetToken.processAllReturningText(String)");
			}
			if (log.isDebugEnabled()) {
				log.debug("Got returning text: \"" + s + "\"");
			}
			SAXBuilder builder = new SAXBuilder();
			try {
				Document doc = builder.build(new InputSource(
						new StringReader(s)));
				process(doc);
			} catch (JDOMException e) {
				if (s.startsWith("unknown_action:")) {
					log.error(
									"Adding '$wgEnableWriteAPI = true;' to your MediaWiki's LocalSettings.php might remove this problem.",
									e);
				} else {
					log.error(e.getMessage(), e);
				}
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}
		return "";
	}

	public HttpAction getNextMessage() {
		if (first) {
			first = false;
			if (log.isTraceEnabled()) {
				log.trace("enter getApiToken");
			}
			return msg;	
		} 
		return null;
		
	}
	@Override
	public boolean hasMoreMessages() {
		return first;
	}
	
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
			; // do nothing
		}
		if (elem != null) {
			// process reply for token request
			switch (intoken) {
			case DELETE:
				token = elem.getAttributeValue("deletetoken");
				break;
			case EDIT:
				token = elem.getAttributeValue("edittoken");
				break;
			default:
				token = null;
					break;
			}
			
			
		} else {
			log.error("Unknow reply. This is not a token.");
		}
		if (log.isDebugEnabled()) 
			log.debug("found token =" + token + "\n"
					+ "for: " + msg.getRequest() + "\n" 
					);
	}
}
