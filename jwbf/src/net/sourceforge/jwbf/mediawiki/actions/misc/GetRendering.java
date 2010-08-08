package net.sourceforge.jwbf.mediawiki.actions.misc;

import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_12;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_13;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_14;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_15;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_16;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.NoSuchElementException;

import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.core.actions.util.ProcessException;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.actions.util.SupportedBy;
import net.sourceforge.jwbf.mediawiki.actions.util.VersionException;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;
/**
 *
 * Implements function to render wikitext on remote
 * <a href="http://www.mediawiki.org/wiki/API:Expanding_templates_and_rendering#parse">parse</a>.
 *
 * @author Thomas Stock
 *
 */
@SupportedBy({ MW1_12, MW1_13, MW1_14, MW1_15, MW1_16 })
public class GetRendering extends MWAction {

  private final Get msg;
  private String html = "";
  private final MediaWikiBot bot;
  private boolean isSelfEx = true;

  /**
   *
   * @param bot a
   * @param wikitext a
   * @throws VersionException if not supported
   */
  public GetRendering(MediaWikiBot bot, String wikitext) throws VersionException {
    super(bot.getVersion());
    this.bot = bot;
    msg = new Get("/api.php?action=parse&text=" + MediaWiki.encode(wikitext) + "&titles=API&format=xml");


  }
  /**
   * {@inheritDoc}
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
  public HttpAction getNextMessage() {
    return msg;
  }
  /**
   * {@inheritDoc}
   */
  @Override
  public String processAllReturningText(String s) throws ProcessException {
    html = findElement("text", s).getTextTrim();
    html = html.replace("\n", "");
    switch (bot.getVersion()) {
      case MW1_12:
        break;
      default:
        int last = html.lastIndexOf("<!--");
        html = html.substring(0, last);
    }
    return "";
  }

  protected Element findElement(String elementName, String xml) {
    SAXBuilder builder = new SAXBuilder();
    Element root = null;
    try {
      Reader i = new StringReader(xml);
      Document doc = builder.build(new InputSource(i));

      root = doc.getRootElement();

    } catch (JDOMException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    if (root != null)
      return findContent(root, elementName);
    else
      return null; // XXX okay ?
  }


  private Element findContent(final Element e, final String name) {
    Element found = null;
    @SuppressWarnings("unchecked")
    Iterator<Element> el = e.getChildren().iterator();
    while (el.hasNext()) {
      Element element = el.next();

      if (element.getQualifiedName().equalsIgnoreCase(name)) {
        //				System.out.println(element.getQualifiedName());
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
   *
   * @return the
   */
  public String getHtml() {
    if (html.length() < 1) {
      update();
    }
    return html;
  }

}
