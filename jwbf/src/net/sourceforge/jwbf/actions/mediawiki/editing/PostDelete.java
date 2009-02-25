package net.sourceforge.jwbf.actions.mediawiki.editing;

import java.io.IOException;
import java.io.StringReader;

import net.sourceforge.jwbf.actions.Post;
import net.sourceforge.jwbf.actions.mediawiki.MediaWiki;
import net.sourceforge.jwbf.actions.mediawiki.util.VersionException;
import net.sourceforge.jwbf.actions.util.HttpAction;
import net.sourceforge.jwbf.actions.util.ProcessException;
import net.sourceforge.jwbf.contentRep.mediawiki.Siteinfo;
import net.sourceforge.jwbf.contentRep.mediawiki.Userinfo;
import net.sourceforge.jwbf.live.mediawiki.DeleteTest;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
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
 * <pre>
 * String name = ...
 * MediaWikiBot bot = ...
 * Siteinfo si = bot.getSiteinfo();
 * Userinfo ui = bot.getUserinfo();
 * bot.performAction(new PostDelete(name, si, ui));
 * </pre>
 *
 * @author Max Gensthaler
 * @supportedBy MediaWikiAPI 1.12
 * @supportedBy MediaWikiAPI 1.13
 * @see DeleteTest
 */
public class PostDelete extends GetApiToken {
	private static final Logger LOG = Logger.getLogger(PostDelete.class);
	

	private final String title;
	private HttpAction msg;
	
	/**
	 * Constructs a new <code>PostDelete</code> action.
	 * @param title title of the article to delete
	 * @param si site info object
	 * @param ui user info object
	 * @throws ProcessException on inner problems like a version mismatch
	 */
	public PostDelete(String title, Siteinfo si, Userinfo ui) throws ProcessException {
		super(Intoken.DELETE, title, si, ui);
		this.title = title;
		if (title == null || title.length() == 0) {
			throw new IllegalArgumentException("The argument 'title' must not be \"" + String.valueOf(title) + "\"");
		}
		
		if (false == ui.getRights().contains("delete")) {
			throw new ProcessException("The given user doesn't have the rights to delete. Adding '$wgGroupPermissions['bot']['delete'] = true;' to your MediaWiki's LocalSettings.php might remove this problem.");
		}
		switch (si.getVersion()) {
		case MW1_09:
		case MW1_10:
		case MW1_11:
			throw new VersionException("Not supportet by this version of MW");
		}
		
	}

	/**
	 * @return the delete action
	 */
	protected HttpAction getSecondRequest() {
		HttpAction msg = null;
		if (getToken() == null || getToken().length() == 0) {
			throw new IllegalArgumentException(
					"The argument 'token' must not be \""
							+ String.valueOf(getToken()) + "\"");
		}
		if (LOG.isTraceEnabled()) {
			LOG.trace("enter PostDelete.generateDeleteRequest(String)");
		}

		String uS = "/api.php" + "?action=delete" + "&title="
				+ MediaWiki.encode(title) + "&token="
				+ MediaWiki.encode(getToken()) + "&format=xml";
		if (LOG.isDebugEnabled()) {
			LOG.debug("delete url: \"" + uS + "\"");
		}
		Post pm = new Post(uS);
		msg = pm;

		this.msg = msg;
		return msg;
	}

	
	/**
	 * Deals with the MediaWiki API's response by parsing the provided text.
	 * @param s the answer to the most recently generated MediaWiki API request
	 * @param hm the requestor message
	 * @return empty string
	 */
	@Override
	public String processReturningText(String s, HttpAction hm)
			throws ProcessException {
		super.processReturningText(s, hm);
		
		
		if (msg != null && hm.getClass().equals(msg.getRequest())) {
			if (LOG.isTraceEnabled()) {
				LOG.trace("enter PostDelete.processAllReturningText(String)");
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug("Got returning text: \"" + s + "\"");
			}
			SAXBuilder builder = new SAXBuilder();
			try {
				Document doc = builder.build(new InputSource(
						new StringReader(s)));
				if (false == containsError(doc)) {
					process(doc);
				}
			} catch (JDOMException e) {
				if (s.startsWith("unknown_action:")) {
					LOG
							.error(
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



	/**
	 * Determines if the given XML {@link Document} contains an error message
	 * which then would printed by the logger.
	 * @param doc XML <code>Document</code>
	 * @throws JDOMException thrown if the document could not be parsed
	 */
	private boolean containsError(Document doc) throws JDOMException {
		Element elem = doc.getRootElement().getChild("error");
		if( elem != null) {
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
		Element elem = doc.getRootElement().getChild("delete");
		if (elem != null) {
			// process reply for delete request
			if(LOG.isInfoEnabled()) {
				LOG.info("Deleted article '" + elem.getAttributeValue("title") + "'" +
					" with reason '" + elem.getAttributeValue("reason") + "'");
			}
		} else {
			LOG.error("Unknow reply. This is not a reply for a delete action.");
		}
	}
}
