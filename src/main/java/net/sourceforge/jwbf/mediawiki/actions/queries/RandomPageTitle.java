package net.sourceforge.jwbf.mediawiki.actions.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

import net.sourceforge.jwbf.core.Optionals;
import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.mapper.XmlConverter;
import net.sourceforge.jwbf.mediawiki.ApiRequestBuilder;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

/**
 * Action to receive the title of a random page
 *
 * @author Juan Ignacio Cidre
 */
public class RandomPageTitle extends MWAction {

  private static final Logger log = LoggerFactory.getLogger(RandomPageTitle.class);

  private final Get msg;
  private final MediaWikiBot bot;

  private Optional<String> title = Optional.absent();

  /**
   * Creates the class. Defines the invocation to MediaWiki that is needed in order to get a random
   * page
   */
  public RandomPageTitle(MediaWikiBot bot) {
    this.bot = bot;

    msg =
        new ApiRequestBuilder() //
            .action("query") //
            .formatXml() //
            .param("list", "random") //
            .param("rnnamespace", "0") // TODO select namespace
            .param("rnlimit", "1") // TODO select random count
            .buildGet();
  }

  /** @return Title of a random page */
  public String getTitle() {
    if (!title.isPresent()) {
      // XXX feels bad
      title = Optional.fromNullable(bot.getPerformedAction(this).getTitle());
    }
    return title.get();
  }

  /** {@inheritDoc} */
  @Override
  public String processAllReturningText(String xml) {
    String xpathResult = XmlConverter.evaluateXpath(xml, "/api/query/random/page/@title");
    title = Optionals.absentIfEmpty(xpathResult);
    log.debug("Title: {}", title);
    return "";
  }

  /** {@inheritDoc} */
  @Override
  public HttpAction getNextMessage() {
    return msg;
  }
}
