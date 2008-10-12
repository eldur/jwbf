package net.sourceforge.jwbf.actions.mw.editing;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import net.sourceforge.jwbf.actions.mw.util.MWAction;
import net.sourceforge.jwbf.actions.mw.util.ProcessException;
import net.sourceforge.jwbf.actions.mw.util.VersionException;
import net.sourceforge.jwbf.bots.MediaWikiBot;
import net.sourceforge.jwbf.contentRep.mw.Siteinfo;
import net.sourceforge.jwbf.contentRep.mw.Userinfo;
import net.sourceforge.jwbf.contentRep.mw.Version;

import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.xml.sax.InputSource;

/**
 * Action class using the MediaWiki-API's <a
 * href="http://www.mediawiki.org/wiki/API:Edit_-_Delete">"action=delete"</a>.
 *
 * <p>
 * To allow your bot to delete articles in your MediaWiki add the following line
 * to your MediaWiki's LocalSettings.php:<br>
 *
 * <pre>
 * $wgEnableWriteAPI = true;
 * $wgGroupPermissions['bot']['delete'] = true;
 * </pre>
 *
 * <p>
 * Delete an article with
 *
 * <pre>
 * String title = ...
 * MediaWikiBot bot = ...
 * Siteinfo si = bot.getSiteinfo();
 * Userinfo ui = bot.getUserinfo();
 * GetToken t = new GetToken(Intoken.DELETE, name, si, ui);
 * bot.performAction(t);
 * bot.performAction(new PostDelete(name, t.getToken(), si, ui));
 * </pre>
 *
 * @author Max Gensthaler
 * @supportedBy MediaWikiAPI 1.12
 * @supportedBy MediaWikiAPI 1.13
 */
public class PostDelete extends MWAction {
	private static final Logger LOG = Logger.getLogger(PostDelete.class);

	/**
	 * Constructs a new <code>GetToken</code> action.
	 * @param title title of the article to generate the token for
	 * @param token the token returned by {@link GetToken#getToken()}
	 * @param si site info object
	 * @param ui user info object
	 * @throws ProcessException on inner problems like a version mismatch
	 */
	public PostDelete(String title, String token, Siteinfo si, Userinfo ui) throws ProcessException {
		if (title == null || title.length() == 0) {
			throw new IllegalArgumentException("The argument 'title' must not be \"" + String.valueOf(title) + "\"");
		}
		if (token == null || token.length() == 0) {
			throw new IllegalArgumentException("The argument 'deleteToken' must not be \"" + String.valueOf(title) + "\"");
		}
		checkVersion(si.getVersion());
		generateDeleteRequest(title, token);
	}

	private void checkVersion(Version version) throws VersionException {
		switch (version) {
		case MW1_09:
		case MW1_10:
		case MW1_11:
			throw new VersionException("Not supportet by this version of MW");
		}
	}

	/**
	 * Generates the next MediaWiki API token and adds it to <code>msgs</code>.
	 * @param title title of the article to generate the token for
	 * @param token the token returned by {@link GetToken#getToken()}
	 */
	private void generateDeleteRequest(String title, String token) {
		if( LOG.isTraceEnabled()) {
			LOG.trace("enter PostDelete.generateDeleteRequest(String)");
		}
		try {
			String uS = "/api.php"
				+ "?action=delete"
				+ "&title=" + URLEncoder.encode(title, MediaWikiBot.CHARSET)
				+ "&token=" + URLEncoder.encode(token, MediaWikiBot.CHARSET)
				+ "&format=xml"
				;
			if( LOG.isDebugEnabled()) {
				LOG.debug("delete url: \""+uS+"\"");
			}
			PostMethod pm = new PostMethod(uS);
			msgs.add(pm);
		} catch (UnsupportedEncodingException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	/**
	 * Deals with the MediaWiki API's response by parsing the provided text.
	 * @param s the answer to the most recently generated MediaWiki API request
	 * @return empty string
	 */
	@Override
	public String processAllReturningText(final String s) throws ProcessException {
		if( LOG.isTraceEnabled()) {
			LOG.trace("enter PostDelete.processAllReturningText(String)");
		}
		if( LOG.isDebugEnabled()) {
			LOG.debug("Got returning text: \""+s+"\"");
		}
		SAXBuilder builder = new SAXBuilder();
		try {
			Document doc = builder.build(new InputSource(new StringReader(s)));
			if(false == containsError(doc)) {
				process(doc);
			}
		} catch (JDOMException e) {
			if(s.startsWith("unknown_action:")) {
				LOG.error("Adding '$wgEnableWriteAPI = true;' to your MediaWiki's LocalSettings.php might remove this problem.", e);
			} else {
				LOG.error(e.getMessage(), e);
			}
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
		return "";
	}

	/**
	 * Determines if the given XML {@link Document} contains an error message
	 * which then would printed by the logger.
	 * @param doc XML <code>Document</code>
	 * @throws JDOMException thrown if the document could not be parsed
	 */
	private boolean containsError(Document doc) throws JDOMException {
		// Object node = XPath.selectSingleNode(doc, "/api/error");
		Object node = doc.getRootElement().getChild("error");
		if( node != null) {
			Element elem = (Element) node;
			LOG.error(elem.getAttributeValue("info"));
			if(elem.getAttributeValue("code").equals("inpermissiondenied")) {
				LOG.error("Adding '$wgGroupPermissions['bot']['delete'] = true;' to your MediaWiki's LocalSettings.php might remove this problem.");
			}
			return true;
		}
		return false;
	}

	/**
	 * Processing the XML {@link Document} returned from the MediaWiki API.
	 * @param doc XML <code>Document</code>
	 * @throws JDOMException thrown if the document could not be parsed
	 */
	private void process(Document doc) throws JDOMException {
		// Object node = XPath.selectSingleNode(doc, "/api/delete");
		Object node = doc.getRootElement().getChild("delete");
		if (node != null) {
			// process reply for delete request
			Element elem = (Element) node;
			if(LOG.isInfoEnabled()) {
				LOG.info("Deleted article '" + elem.getAttributeValue("title") + "'" +
					" with reason '" + elem.getAttributeValue("reason") + "'");
			}
		} else {
			LOG.error("Unknow reply. This is not a reply for a delete action.");
		}
	}
}
