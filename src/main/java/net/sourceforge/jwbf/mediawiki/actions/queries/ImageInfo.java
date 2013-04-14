package net.sourceforge.jwbf.mediawiki.actions.queries;

import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_15;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_16;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_17;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_18;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_19;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_20;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.ImageIO;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.core.actions.util.ProcessException;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.actions.util.SupportedBy;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import org.apache.commons.lang.math.NumberUtils;
import org.jdom.Element;

import com.google.common.base.Strings;

/**
 * Action to receive the full address of an image. Like "Img.gif" to
 * "http://wikihost.tld/w/images/x/y/Img.gif".
 * 
 * @author Thomas Stock
 * 
 */
@Slf4j
@SupportedBy({ MW1_15, MW1_16, MW1_17, MW1_18, MW1_19, MW1_20 })
public class ImageInfo extends MWAction {
  private static final Map<String, String> EMPTY_STRING_MAP = Collections.emptyMap();

  public static final String WIDTH = "iiurlwidth";
  public static final String HEIGHT = "iiurlheight";

  private String urlOfImage = "";
  private Get msg;
  private final MediaWikiBot bot;
  private boolean selfEx = true;
  private Map<String, String> map = new HashMap<String, String>();

  final private String name;

  /**
   * Get an absolute url to an image.
   * 
   * @param name
   *          of, like "Test.gif"
   */
  public ImageInfo(MediaWikiBot bot, String name) {
    this(bot, name, EMPTY_STRING_MAP);
  }

  public ImageInfo(MediaWikiBot bot, String name, Map<String, String> params) {
    super(bot.getVersion());
    this.bot = bot;
    this.name = name;
    map.putAll(params);
    prepareMsg(name);
  }

  /**
   * TODO change params to a map
   */
  public ImageInfo(MediaWikiBot bot, String name, String[][] params) {
    super(bot.getVersion());
    this.bot = bot;
    this.name = name;
    if (params != null) {
      for (String[] param : params) {
        if (param.length == 2) {
          String key = param[0];
          String value = param[1];
          if (key != null && value != null)
            map.put(key, value);
        }
      }
    }
    prepareMsg(name);
  }

  private void prepareMsg(String name) {
    int width = NumberUtils.toInt(map.get(WIDTH));
    int height = NumberUtils.toInt(map.get(HEIGHT));
    String addProps = "";
    if (width > 0)
      addProps += "&" + WIDTH + "=" + width;
    if (height > 0)
      addProps += "&" + HEIGHT + "=" + height;

    if (bot.getVersion().greaterEqThen(Version.MW1_15)) {
      msg = new Get(MediaWiki.URL_API + "?action=query&titles=File:" + MediaWiki.encode(name)
          + "&prop=imageinfo" + addProps + "&iiprop=url&format=xml");
    } else {
      msg = new Get(MediaWiki.URL_API + "?action=query&titles=Image:" + MediaWiki.encode(name)
          + "&prop=imageinfo" + addProps + "&iiprop=url&format=xml");
    }
  }

  /**
   * @return position like "http://server.tld/path/to/Test.gif"
   */
  public String getUrlAsString() {
    String exceptionMsg = "no url for image with name \"" + name + "\"";
    try {
      selfEx = false;
      try {
        bot.performAction(this);
      } catch (ProcessException e) {
        throw new ActionException(exceptionMsg);
      }
    } finally {
      selfEx = true;
    }
    if (Strings.isNullOrEmpty(urlOfImage)) {
      throw new ActionException(exceptionMsg);
    }

    try {
      new URL(urlOfImage);
    } catch (MalformedURLException e) {
      if (bot.getHostUrl().length() <= 0) {
        throw new ActionException("please use the constructor with hostUrl; " + urlOfImage);
      }
      urlOfImage = bot.getHostUrl() + urlOfImage;
    }
    return urlOfImage;
  }

  public URL getUrl() {
    try {
      return new URL(getUrlAsString());
    } catch (MalformedURLException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @deprecated see super
   */
  @Deprecated
  @Override
  public boolean isSelfExecuter() {
    return selfEx;
  }

  public BufferedImage getAsImage() throws IOException {
    return ImageIO.read(new URL(getUrlAsString()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String processAllReturningText(String s) {
    findUrlOfImage(s);
    return "";
  }

  @SuppressWarnings("unchecked")
  private void findContent(final Element root) {

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

  private void findUrlOfImage(String s) {
    Element root = getRootElementWithError(s);
    findContent(root);
    if (urlOfImage.length() < 1) {
      throw new ProcessException("Could not find this image " + s);
    }
  }

  /**
   * {@inheritDoc}
   */
  public HttpAction getNextMessage() {
    return msg;
  }
}
