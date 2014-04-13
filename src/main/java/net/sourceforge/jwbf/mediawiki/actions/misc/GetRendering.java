package net.sourceforge.jwbf.mediawiki.actions.misc;

import java.util.Iterator;
import java.util.NoSuchElementException;

import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.core.actions.util.ProcessException;
import net.sourceforge.jwbf.mediawiki.ApiRequestBuilder;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import org.jdom.Element;

/**
 * Implements function to render wikitext on remote <a href=
 * "http://www.mediawiki.org/wiki/API:Expanding_templates_and_rendering#parse" >parse</a>.
 * 
 * @author Thomas Stock
 */
public class GetRendering extends MWAction {

  private final Get msg;
  private String html = "";
  private final MediaWikiBot bot;
  private boolean isSelfEx = true;

  public GetRendering(MediaWikiBot bot, String wikitext) {
    this.bot = bot;
    msg = new ApiRequestBuilder() //
        .action("parse") //
        .formatXml() //
        .param("text", MediaWiki.encode(wikitext)) //
        .param("title", "API") //
        .buildGet();

  }

  /**
   * {@inheritDoc}
   * 
   * @deprecated see super
   */
  @Deprecated
  @Override
  public boolean isSelfExecuter() {
    return isSelfEx;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public HttpAction getNextMessage() {
    return msg;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String processAllReturningText(String s) {
    html = findElement("text", s).getTextTrim();
    html = html.replace("\n", "");
    int last = html.lastIndexOf("<!--");
    html = html.substring(0, last);
    return "";
  }

  protected Element findElement(String elementName, String xml) {
    Element root = getRootElement(xml);
    return findContent(root, elementName);
  }

  private Element findContent(final Element e, final String name) {
    Element found = null;
    @SuppressWarnings("unchecked")
    Iterator<Element> el = e.getChildren().iterator();
    while (el.hasNext()) {
      Element element = el.next();

      if (element.getQualifiedName().equalsIgnoreCase(name)) {
        return element;

      } else {
        found = findContent(element, name);
      }

    }
    if (found == null) {
      throw new NoSuchElementException();
    }
    return found;
  }

  private void update() {
    try {
      isSelfEx = false;
      bot.performAction(this);

    } catch (ActionException e) {
      e.printStackTrace();
    } catch (ProcessException e) {
      e.printStackTrace();
    } finally {
      isSelfEx = true;
    }
  }

  /**
   * @return the
   */
  public String getHtml() {
    if (html.length() < 1) {
      update();
    }
    return html;
  }

}
