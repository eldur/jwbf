package net.sourceforge.jwbf.mediawiki.actions.editing;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;

import net.sourceforge.jwbf.core.actions.RequestBuilder;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.core.actions.util.PermissionException;
import net.sourceforge.jwbf.core.internal.Checked;
import net.sourceforge.jwbf.mapper.XmlConverter;
import net.sourceforge.jwbf.mediawiki.ApiRequestBuilder;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

/**
 * Action class using the MediaWiki-API's to allow your bot to move articles in your MediaWiki add
 * the following line to your MediaWiki's LocalSettings.php:<br>
 *
 * <pre>
 * $wgEnableWriteAPI = true;
 * $wgGroupPermissions['bot']['move'] = true;
 * $wgGroupPermissions['bot']['movefile'] = true;            // optional
 * $wgGroupPermissions['bot']['move-subpages'] = true;       // optional
 * $wgGroupPermissions['bot']['move-rootuserpages'] = true;  // optional
 * </pre>
 *
 * Move an article with
 *
 * <pre>
 * String oldtitle = ...
 * String newtitle = ...
 * String reason = ...
 * Boolean withsubpages = ...
 * Boolean noredirect = ...
 * MediaWikiBot bot = ...
 * bot.performAction(new MovePage(bot, oldtitle, newtitle, reason, withsubpages, noredirect));
 * </pre>
 *
 * @author Christoph Giesel
 * @see <a href="http://www.mediawiki.org/wiki/API:Edit_-_Move">"action=move"</a>
 */
public class MovePage extends MWAction {

  private static final Logger log = LoggerFactory.getLogger(MovePage.class);

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
   * @param bot the MediaWikiBot
   * @param oldtitle title to move
   * @param newtitle new title
   * @param reason reason why to move
   * @param withsubpages if <b>TRUE</b> also move the subpages
   * @param noredirect if <b>TRUE</b> create no redirects
   */
  public MovePage(
      MediaWikiBot bot,
      String oldtitle,
      String newtitle,
      String reason,
      boolean withsubpages,
      boolean noredirect) {
    token = new GetApiToken(GetApiToken.Intoken.MOVE, oldtitle);
    this.oldtitle = Checked.nonBlank(oldtitle, "oldtitle");
    this.newtitle = Checked.nonBlank(newtitle, "newtitle");
    this.reason = reason;
    this.withsubpages = withsubpages;
    this.noredirect = noredirect;

    checkPermissions(bot.getUserinfo().getRights(), withsubpages);
  }

  @VisibleForTesting
  void checkPermissions(Set<String> permissions, boolean withSubPages) {
    if (!permissions.contains("move")) {
      throw new PermissionException(
          "The given user doesn't have the rights to move. "
              + "Add '$wgGroupPermissions['bot']['move'] = true;' "
              + "to your MediaWikis LocalSettings.php might solve this problem.");
    }

    if (withSubPages && !permissions.contains("move-subpages")) {
      throw new PermissionException(
          "The given user doesn't have the rights to move subpages. "
              + "Add '$wgGroupPermissions['bot']['move-subpages'] = true;' "
              + "to your MediaWikis LocalSettings.php might solve this problem.");
    }
  }

  /** @return the delete action */
  private HttpAction getSecondRequest() {
    RequestBuilder requestBuilder =
        new ApiRequestBuilder() //
            .action("move") //
            .formatXml() //
            .param("from", MediaWiki.urlEncode(oldtitle)) //
            .param("to", MediaWiki.urlEncode(newtitle)) //
            .postParam(token.get().token()) //
            .param("movetalk", "") // XXX
        ;

    if (withsubpages) {
      requestBuilder.param("movesubpages", "");
    }
    if (noredirect) {
      requestBuilder.param("noredirect", "");
    }
    if (!Strings.isNullOrEmpty(reason)) {
      requestBuilder.param("reason", MediaWiki.urlEncode(reason));
    }

    return requestBuilder.buildPost();
  }

  /** {@inheritDoc} */
  @Override
  public String processReturningText(String xml, HttpAction hm) {
    XmlConverter.failOnError(xml);
    if (moveToken) {
      token.processReturningText(xml, hm);
      moveToken = false;
    } else {
      log.debug("Got returning text: \"{}\"", xml);
      setHasMoreMessages(false);
    }

    return "";
  }

  /** {@inheritDoc} */
  @Override
  public HttpAction getNextMessage() {
    if (token.hasMoreActions()) {
      setHasMoreMessages(true);
      return token.popAction();
    }
    return getSecondRequest();
  }
}
