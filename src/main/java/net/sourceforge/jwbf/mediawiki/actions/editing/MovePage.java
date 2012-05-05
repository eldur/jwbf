package net.sourceforge.jwbf.mediawiki.actions.editing;

import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_12;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_13;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_14;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_15;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_16;

import java.io.IOException;
import java.io.StringReader;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jwbf.core.actions.Post;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.core.actions.util.ProcessException;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.actions.util.SupportedBy;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

/**
 * Action class using the MediaWiki-API's <a
 * href="http://www.mediawiki.org/wiki/API:Edit_-_Move">"action=move"</a>.
 * 
 * <p>
 * To allow your bot to move articles in your MediaWiki add the following line
 * to your MediaWiki's LocalSettings.php:<br>
 * 
 * <pre>
 * $wgEnableWriteAPI = true;
 * $wgGroupPermissions['bot']['move'] = true;
 * $wgGroupPermissions['bot']['movefile'] = true;            // optional
 * $wgGroupPermissions['bot']['move-subpages'] = true;       // optional
 * $wgGroupPermissions['bot']['move-rootuserpages'] = true;  // optional
 * </pre>
 * 
 * <p>
 * Move an article with
 * 
 * <pre>
 * String oldtitle = ...
 * String newtitle = ...
 * String reason = ...
 * Boolean withsubpages = ...
 * Boolean noredirect = ...
 * 
 * MediaWikiBot bot = ...
 * bot.performAction(new MovePage(bot, oldtitle, newtitle, reason, withsubpages, noredirect));
 * </pre>
 * 
 * @author Christoph Giesel
 */
@Slf4j
@SupportedBy({ MW1_12, MW1_13, MW1_14, MW1_15, MW1_16 })
public class MovePage extends MWAction {

  private final String oldtitle;
  private final String newtitle;
  private final String reason;
  private final boolean withsubpages;
  private final boolean noredirect;
  private final GetApiToken token;
  private boolean moveToken = true;

  /**
   * Constructs a new <code>MovePage</code> action.
   * 
   * @param bot
   *          the MediaWikiBot
   * @param oldtitle
   *          title to move
   * @param newtitle
   *          new title
   * @param reason
   *          reason why to move
   * @param withsubpages
   *          if <b>TRUE</b> also move the subpages
   * @param noredirect
   *          if <b>TRUE</b> create no redirects
   * @throws ProcessException
   *           ProcessException
   * @throws ActionException
   *           ActionException
   */
  public MovePage(MediaWikiBot bot, String oldtitle, String newtitle,
      String reason, boolean withsubpages, boolean noredirect)
      throws ProcessException, ActionException {
    super(bot.getVersion());
    token = new GetApiToken(GetApiToken.Intoken.MOVE, oldtitle,
        bot.getVersion(), bot.getUserinfo());
    this.oldtitle = oldtitle;
    this.newtitle = newtitle;
    this.reason = reason;
    this.withsubpages = withsubpages;
    this.noredirect = noredirect;

    if (oldtitle == null || oldtitle.length() == 0 || newtitle == null
        || newtitle.length() == 0) {
      throw new IllegalArgumentException(
          "The arguments 'oldtitle' and 'newtitle' must not be null or empty");
    }

    if (!bot.getUserinfo().getRights().contains("move")) {
      throw new ProcessException(
          "The given user doesn't have the rights to move. "
              + "Add '$wgGroupPermissions['bot']['move'] = true;' "
              + "to your MediaWiki's LocalSettings.php might solve this problem.");
    }

    if (withsubpages
        && !bot.getUserinfo().getRights().contains("move-subpages")) {
      throw new ProcessException(
          "The given user doesn't have the rights to move subpages. "
              + "Add '$wgGroupPermissions['bot']['move-subpages'] = true;' "
              + "to your MediaWiki's LocalSettings.php might solve this problem.");
    }
  }

  /**
   * @return the delete action
   */
  private HttpAction getSecondRequest() {
    HttpAction msg = null;
    if (token.getToken() == null || token.getToken().length() == 0) {
      throw new IllegalArgumentException("The argument 'token' must not be \""
          + String.valueOf(token.getToken()) + "\"");
    }
    if (log.isTraceEnabled()) {
      log.trace("enter MovePage.generateMoveRequest(String)");
    }

    String uS = "/api.php"
        + "?action=move"
        + "&from="
        + MediaWiki.encode(oldtitle)
        + "&to="
        + MediaWiki.encode(newtitle)
        + "&token="
        + MediaWiki.encode(token.getToken())
        + (withsubpages ? "&movesubpages" : "")
        + (noredirect ? "&noredirect" : "")
        + ((reason != null && reason.length() != 0) ? "&reason="
            + MediaWiki.encode(reason) : "") + "&movetalk&format=xml";

    if (log.isDebugEnabled()) {
      log.debug("move url: \"" + uS + "\"");
    }
    msg = new Post(uS);

    return msg;
  }

  /**
   * 
   * {@inheritDoc}
   */
  @Override
  public String processReturningText(String s, HttpAction hm)
      throws ProcessException {
    super.processReturningText(s, hm);

    if (moveToken) {
      token.processReturningText(s, hm);
      moveToken = false;
    } else {

      if (log.isTraceEnabled()) {
        log.trace("enter MovePage.processAllReturningText(String)");
      }
      if (log.isDebugEnabled()) {
        log.debug("Got returning text: \"" + s + "\"");
      }
      SAXBuilder builder = new SAXBuilder();
      try {
        Document doc = builder.build(new InputSource(new StringReader(s)));
        if (!containsError(doc)) {
          process(doc);
        }
      } catch (JDOMException e) {
        String msg = e.getMessage();
        if (s.startsWith("unknown_action:")) {
          msg = "unknown_action; Adding '$wgEnableWriteAPI = true;' to your MediaWiki's "
              + "LocalSettings.php might remove this problem.";
        }
        log.error(msg, e);
        throw new ProcessException(msg, e);
      } catch (IOException e) {
        log.error(e.getMessage(), e);
        throw new ProcessException(e);
      }
      setHasMoreMessages(false);
    }

    return "";
  }

  /**
   * Determines if the given XML {@link Document} contains an error message
   * which then would printed by the logger.
   * 
   * @param doc
   *          XML <code>Document</code>
   * @throws JDOMException
   *           thrown if the document could not be parsed
   * @return if
   */
  private boolean containsError(Document doc) {
    Element elem = doc.getRootElement().getChild("error");
    if (elem != null) {
      log.error(elem.getAttributeValue("code") + ": "
          + elem.getAttributeValue("info"));

      return true;
    }
    return false;
  }

  /**
   * Processing the XML {@link Document} returned from the MediaWiki API.
   * 
   * @param doc
   *          XML <code>Document</code>
   * @throws JDOMException
   *           thrown if the document could not be parsed
   */
  private void process(Document doc) {
    Element elem = doc.getRootElement().getChild("move");
    if (elem != null) {
      // process reply for delete request
      if (log.isInfoEnabled()) {
        log.info("Moved article '" + elem.getAttributeValue("from") + "' to '"
            + elem.getAttributeValue("to") + "'" + " with reason '"
            + elem.getAttributeValue("reason") + "'");
      }
    } else {
      log.error("Unknow reply. This is not a reply for a delete action.");
    }
  }

  /**
   * {@inheritDoc}
   */
  public HttpAction getNextMessage() {
    if (token.hasMoreMessages()) {
      setHasMoreMessages(true);
      return token.getNextMessage();
    }
    return getSecondRequest();
  }
}