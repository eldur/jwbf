package net.sourceforge.jwbf.mediawiki.actions.queries;

import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_15;

import java.io.ByteArrayInputStream;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.core.actions.util.ProcessException;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.actions.util.SupportedBy;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import org.xml.sax.InputSource;

/**
 * Action to receive the title of a random page
 * 
 * TODO write a test that shows compatibility with all these versions
 * 
 * @author Juan Ignacio Cidre
 */
@Slf4j
@SupportedBy({ MW1_15 })
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
    try {
      return bot.performAction(this);
    } catch (ActionException e) {
      throw new ProcessException("Error finding the Random Page " + e.toString());
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String processAllReturningText(String s) {
    XPath parser = XPathFactory.newInstance().newXPath();
    String title = "";
    try {
      XPathExpression titleParser = parser.compile("/api/query/random/page/@title");
      InputSource contenido = new InputSource(new ByteArrayInputStream(s.getBytes(MediaWiki
          .getCharset())));
      title = titleParser.evaluate(contenido);
    } catch (Exception e) {
      throw new ProcessException("Error parsing the title of the Random Page" + e.toString());
    }

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
