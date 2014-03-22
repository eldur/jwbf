package net.sourceforge.jwbf.mediawiki.actions.editing;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jwbf.core.RequestBuilder;
import net.sourceforge.jwbf.core.actions.Post;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.core.actions.util.ProcessException;
import net.sourceforge.jwbf.mediawiki.ApiRequestBuilder;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import org.jdom.Document;
import org.jdom.Element;

/**
 * Action class using the MediaWiki-API's <a href="http://www.mediawiki.org/wiki/API:Edit_-_Delete">"action=delete"</a>.
 * <p>
 * To allow your bot to delete articles in your MediaWiki add the following line to your MediaWiki's LocalSettings.php:<br>
 * 
 * <pre>
 * $wgEnableWriteAPI = true;
 * $wgGroupPermissions['bot']['delete'] = true;
 * </pre>
 * <p>
 * Delete an article with
 * 
 * <pre>
 * String name = ...
 * MediaWikiBot bot = ...
 * Siteinfo si = bot.getSiteinfo();
 * Userinfo ui = bot.getUserinfo();
 * bot.performAction(new PostDelete(name, si, ui));
 * </pre>
 * 
 * @author Max Gensthaler
 */
@Slf4j
public class PostDelete extends MWAction {

  private final String title;
  private String reason;

  private final GetApiToken token;
  private boolean delToken = true;

  /**
   * Constructs a new <code>PostDelete</code> action.
   */
  public PostDelete(MediaWikiBot bot, String title) {
    token = new GetApiToken(GetApiToken.Intoken.DELETE, title, bot.getVersion(), bot.getUserinfo());
    this.title = title;
    if (title == null || title.length() == 0) {
      throw new IllegalArgumentException("The argument 'title' must not be null or empty");
    }

    if (!bot.getUserinfo().getRights().contains("delete")) {
      throw new ProcessException("The given user doesn't have the rights to delete. "
          + "Add '$wgGroupPermissions['bot']['delete'] = true;' "
          + "to your MediaWiki's LocalSettings.php might solve this problem.");
    }

  }

  /**
   * Constructs a new <code>PostDelete</code> action.
   * 
   * @param bot
   *          MediaWikiBot
   * @param title
   *          the title of the page to delete
   * @param reason
   *          reason for the deletion (may be null) in case of a precessing exception in case of an action exception
   */
  public PostDelete(MediaWikiBot bot, String title, String reason) {
    this(bot, title);
    this.reason = reason;
  }

  /**
   * @return the delete action
   */
  private HttpAction getSecondRequest() {
    Post msg = null;
    if (token.getToken() == null || token.getToken().length() == 0) {
      throw new IllegalArgumentException("The argument 'token' must not be \""
          + String.valueOf(token.getToken()) + "\"");
    }
    if (log.isTraceEnabled()) {
      log.trace("enter PostDelete.generateDeleteRequest(String)");
    }
    RequestBuilder requestBuilder = new ApiRequestBuilder() //
        .action("delete") //
        .formatXml() //
        .param("title", MediaWiki.encode(title)) //
        .param("token", MediaWiki.encode(token.getToken())) //
    ;

    if (reason != null) {
      requestBuilder.param("reason", MediaWiki.encode(reason));
    }
    msg = requestBuilder.buildPost();
    log.debug("delete url: \"{}\"", msg.getRequest());

    return msg;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String processReturningText(String s, HttpAction hm) {
    super.processReturningText(s, hm);

    if (delToken) {
      token.processReturningText(s, hm);
      delToken = false;
    } else {

      if (log.isTraceEnabled()) {
        log.trace("enter PostDelete.processAllReturningText(String)");
      }
      if (log.isDebugEnabled()) {
        log.debug("Got returning text: \"" + s + "\"");
      }
      try {
        Element doc = getRootElementWithError(s);
        if (getErrorElement(doc) == null) {
          process(doc);
        }
      } catch (IllegalArgumentException e) {
        String msg = e.getMessage();
        if (s.startsWith("unknown_action:")) {
          msg = "unknown_action; Adding '$wgEnableWriteAPI = true;' to your MediaWiki's "
              + "LocalSettings.php might remove this problem.";
        }
        log.error(msg, e);
        throw new ProcessException(msg, e);
      }
      setHasMoreMessages(false);
    }

    return "";
  }

  /**
   * Determines if the given XML {@link Document} contains an error message which then would printed by the logger.
   */
  @Override
  protected Element getErrorElement(Element rootElement) {
    Element containsError = super.getErrorElement(rootElement);
    if (containsError != null) {
      log.warn(containsError.getAttributeValue("info"));
      if (containsError.getAttributeValue("code").equals("inpermissiondenied")) {
        log.error("Adding '$wgGroupPermissions['bot']['delete'] = true;'"
            + " to your MediaWiki's LocalSettings.php might remove this problem.");
      }
    }
    return containsError;
  }

  private void process(Element rootElement) {
    Element elem = rootElement.getChild("delete");
    if (elem != null) {
      // process reply for delete request
      if (log.isInfoEnabled()) {
        log.info("Deleted article '" + elem.getAttributeValue("title") + "'" + " with reason '"
            + elem.getAttributeValue("reason") + "'");
      }
    } else {
      log.error("Unknow reply. This is not a reply for a delete action.");
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public HttpAction getNextMessage() {
    if (token.hasMoreMessages()) {
      setHasMoreMessages(true);
      return token.getNextMessage();
    }
    return getSecondRequest();
  }
}
