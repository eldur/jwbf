package net.sourceforge.jwbf.actions.mw.editing;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.LinkedList;

import net.sourceforge.jwbf.actions.mw.MultiAction;
import net.sourceforge.jwbf.actions.mw.util.MWAction;
import net.sourceforge.jwbf.actions.mw.util.ProcessException;
import net.sourceforge.jwbf.actions.mw.util.VersionException;
import net.sourceforge.jwbf.bots.MediaWikiBot;
import net.sourceforge.jwbf.contentRep.mw.Siteinfo;
import net.sourceforge.jwbf.contentRep.mw.Userinfo;

import org.apache.commons.httpclient.methods.GetMethod;
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
 * Iterable&lt;String&gt; it = bot.performMultiAction(new PostDeleteNew(title));
 * for (String result : it) {
 *     // do nothing - just iterate
 * }
 * </pre>
 *
 * @author Max Gensthaler
 * @supportedBy MediaWiki 1.12
 */
public class PostDelete extends MWAction implements MultiAction<String> {
	private static final Logger LOG = Logger.getLogger(PostDelete.class);
	private String title = "";
	private String token = "";
	private final Collection<String> results = new LinkedList<String>();;

	/**
	 * The public constructor. It will generate a MediaWiki API request which is
	 * then added to <code>msgs</code>. When it is answered, the method
	 * {@link #processAllReturningText(String)} will be called (from outside
	 * this class).
	 *
	 * @param title
	 *            title of the page to delete
	 */
	public PostDelete(String title, Siteinfo si, Userinfo ui) throws ProcessException {
		if (title == null || title.length() == 0) {
			throw new IllegalArgumentException("The argument 'title' must not be \"" + String.valueOf(title) + "\"");
		}
		this.title = title;
		
		switch (si.getVersion()) {
		case MW1_09:
		case MW1_10:
		case MW1_11:
			throw new VersionException("Not supportet by this version of MW");
			
		default:
//			if (!si.isWriteAPI()) {
//				throw new ProcessException("Write API is disabled. To enable it see JavaDoc of class " + getClass().getName());
//			}
			if (!ui.getRights().contains("delete")) {
				throw new ProcessException("Bot has no delete rights. To enable it see JavaDoc of class " + getClass().getName());
			}

			generateTokenRequest(title);
			break;
		}
		
	}

	private PostDelete(String title, String token) {
		if (title == null || title.length() == 0) {
			throw new IllegalArgumentException("The argument 'title' must not be \"" + String.valueOf(title) + "\"");
		}
		if (token == null || token.length() == 0) {
			throw new IllegalArgumentException("The argument 'deleteToken' must not be \"" + String.valueOf(title) + "\"");
		}
		this.title = title;
		this.token = null; // break going to the next MultiAction
		generateDeleteRequest(title, token);
	}

	/**
	 * Generates the next MediaWiki API token and adds it to <code>msgs</code>.
	 */
	private void generateTokenRequest(String title) {
		if( LOG.isTraceEnabled()) {
			LOG.trace("enter PostDeleteNew.generateTokenRequest()");
		}
		try {
			String uS = "/api.php"
					+ "?action=query"
					+ "&prop=info"
					+ "&intoken=delete"
					+ "&titles=" + URLEncoder.encode(title, MediaWikiBot.CHARSET)
					+ "&format=xml";
			msgs.add(new GetMethod(uS));
		} catch (UnsupportedEncodingException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	/**
	 * Generates the next MediaWiki API token and adds it to <code>msgs</code>.
	 */
	private void generateDeleteRequest(String title, String token) {
		if( LOG.isTraceEnabled()) {
			LOG.trace("enter PostDeleteNew.generateDeleteRequest(String)");
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
	 *
	 * @param s the answer to the most recently generated MediaWiki API request
	 *
	 * @return empty string
	 */
	public String processAllReturningText(final String s) throws ProcessException {
		if( LOG.isTraceEnabled()) {
			LOG.trace("enter PostDeleteNew.processAllReturningText(String)");
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

	private boolean containsError(Document doc) throws JDOMException {
		Object node = XPath.selectSingleNode(doc, "/api/error");
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

	private void process(Document doc) throws JDOMException {
		Object node;
		if (null != (node = XPath.selectSingleNode(doc, "/api/query/pages/page"))) {
			// process reply for token request
			Element elem = (Element) node;
			String title = elem.getAttributeValue("title");
			assert title == this.title : "Got a delete token for an unrequested page title.";
			token = elem.getAttributeValue("deletetoken");
		} else if (null != (node = XPath.selectSingleNode(doc, "/api/delete"))) {
			// process reply for delete request
			Element elem = (Element) node;
			results.add("Deleted article '" + elem.getAttributeValue("title") + "'" +
					" with reason '" + elem.getAttributeValue("reason") + "'");
		} else {
			results.add("Unknow reply");
		}
	}

	public MultiAction<String> getNextAction() {
		if( token == null) {
			return null;
		} else {
			return new PostDelete(title, token);
		}
	}

	public Collection<String> getResults() {
		return results;
	}
}
