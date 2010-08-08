package net.sourceforge.jwbf.mediawiki.actions.queries;

import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_11;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_12;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_13;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_14;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_15;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_16;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import javax.imageio.ImageIO;

import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.core.actions.util.ProcessException;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.actions.util.SupportedBy;
import net.sourceforge.jwbf.mediawiki.actions.util.VersionException;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

/**
 * Action to receive the full address of an image.
 * Like "Img.gif" to "http://wikihost.tld/w/images/x/y/Img.gif".
 *
 * @author Thomas Stock
 *
 */
@SupportedBy({ MW1_11, MW1_12, MW1_13, MW1_14, MW1_15, MW1_16 })
public class ImageInfo extends MWAction {
  private static final Logger LOGGER = Logger.getLogger(ImageInfo.class);
  private String urlOfImage  = "";
  private Get msg;
  private final MediaWikiBot bot;
  private boolean selfEx = true;

  /**
   *
   * Get an absolute url to an image.
   * @param bot a
   * @param name of, like "Test.gif"
   * @throws VersionException if not supported
   */
  public ImageInfo(MediaWikiBot bot, String name) throws VersionException {
    super(bot.getVersion());
    this.bot = bot;

    if (bot.getVersion().greaterEqThen(Version.MW1_15)) {
      msg = new Get("/api.php?action=query&titles=File:"
          + MediaWiki.encode(name) + "&prop=imageinfo"
          + "&iiprop=url&format=xml");
    } else {
      msg = new Get("/api.php?action=query&titles=Image:"
          + MediaWiki.encode(name) + "&prop=imageinfo"
          + "&iiprop=url&format=xml");
    }

  }


  /**
   * @return position like "http://server.tld/path/to/Test.gif"
   * @throws ProcessException on
   */
  public String getUrlAsString() throws ProcessException {
    try {
      selfEx = false;
      bot.performAction(this);
    } catch (ActionException e1) {
      e1.printStackTrace();
    } finally {
      selfEx = true;
    }
    try {
      new URL(urlOfImage);
    } catch (MalformedURLException e) {
      if (bot.getHostUrl().length() <= 0) {
        throw new ProcessException("please use the constructor with hostUrl; "
            + urlOfImage);
      }
      urlOfImage = bot.getHostUrl() + urlOfImage;
    }
    return urlOfImage;
  }
  /**
   * {@inheritDoc}
   * @deprecated see super
   */
  @Deprecated
  @Override
  public boolean isSelfExecuter() {
    return selfEx;
  }


  /**
   * @return a
   * @throws ProcessException on
   * @throws ActionException on
   * @throws IOException on
   */
  public BufferedImage getAsImage() throws ProcessException
  , ActionException, IOException {
    return ImageIO.read(new URL(getUrlAsString()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String processAllReturningText(String s) throws ProcessException {
    findUrlOfImage(s);
    return "";
  }


  @SuppressWarnings("unchecked")
  private void findContent(final Element root) throws ProcessException {

    Iterator<Element> el = root.getChildren().iterator();
    while (el.hasNext()) {
      Element element = el.next();
      if (element.getQualifiedName().equalsIgnoreCase("ii")) {
        urlOfImage = element.getAttributeValue("url");

        return;
      } else {
        findContent(element);
      }
    }

  }

  private void findUrlOfImage(String s) throws ProcessException {
    SAXBuilder builder = new SAXBuilder();
    Element root = null;
    try {
      Reader i = new StringReader(s);
      Document doc = builder.build(new InputSource(i));
      root = doc.getRootElement();

    } catch (JDOMException e) {
      LOGGER.warn("", e);
    } catch (IOException e) {
      LOGGER.warn("", e);
    }
    if (root != null)
      findContent(root);
    if (urlOfImage.length() < 1)
      throw new ProcessException("Could not find this image");
  }

  /**
   * {@inheritDoc}
   */
  public HttpAction getNextMessage() {
    return msg;
  }
}
