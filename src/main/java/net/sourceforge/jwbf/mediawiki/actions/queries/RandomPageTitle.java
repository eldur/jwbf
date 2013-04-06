package net.sourceforge.jwbf.mediawiki.actions.queries;

import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_15;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_16;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_17;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_18;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_19;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_20;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.actions.util.SupportedBy;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

/**
 * Action to receive the title of a random page
 * 
 * TODO write a test that shows compatibility with all these versions
 * 
 * @author Juan Ignacio Cidre
 */
@Slf4j
@SupportedBy({ MW1_15, MW1_16, MW1_17, MW1_18, MW1_19, MW1_20 })
public class RandomPageTitle extends MWAction {

  private Get msg;
  private final MediaWikiBot bot;

  /**
   * 
   * Creates the class. Defines the invocation to MediaWiki that is needed in order to get a random
   * page
   * 
   * @param name
   *          of, like "Test.gif"
   */
  public RandomPageTitle(MediaWikiBot bot) {
    super(bot.getVersion());
    this.bot = bot;

    msg = new Get(MediaWiki.URL_API
        + "?action=query&list=random&rnnamespace=0&rnlimit=1&format=xml");

  }

  /**
   * @return Title of a random page
   * 
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
