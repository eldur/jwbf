package net.sourceforge.jwbf.mediawiki.actions.queries;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.mediawiki.ApiRequestBuilder;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

/**
 * Action to receive the title of a random page TODO write a test that shows compatibility with all these versions
 * 
 * @author Juan Ignacio Cidre
 */
@Slf4j
public class RandomPageTitle extends MWAction {

  private final Get msg;
  private final MediaWikiBot bot;

  /**
   * Creates the class. Defines the invocation to MediaWiki that is needed in order to get a random page
   */
  public RandomPageTitle(MediaWikiBot bot) {
    super(bot.getVersion());
    this.bot = bot;

    msg = new ApiRequestBuilder() //
        .action("query") //
        .formatXml() //
        .param("list", "random") //
        .param("rnnamespace", "0") // TODO select namespace
        .param("rnlimit", "1") // TODO select random count
        .buildGet();

  }

  /**
   * @return Title of a random page
   */
  public String getTitle() {
    return bot.performAction(this);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String processAllReturningText(String s) {
    String title = evaluateXpath(s, "/api/query/random/page/@title");
    log.debug("Title: " + title);
    return title;
  }

  /**
   * {@inheritDoc}
   */
  public HttpAction getNextMessage() {
    return msg;
  }
}
