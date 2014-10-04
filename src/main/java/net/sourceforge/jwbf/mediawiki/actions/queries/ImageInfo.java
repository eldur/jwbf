package net.sourceforge.jwbf.mediawiki.actions.queries;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import net.sourceforge.jwbf.JWBF;
import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.core.actions.RequestBuilder;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.core.actions.util.ProcessException;
import net.sourceforge.jwbf.core.internal.Checked;
import net.sourceforge.jwbf.mapper.XmlConverter;
import net.sourceforge.jwbf.mapper.XmlElement;
import net.sourceforge.jwbf.mediawiki.ApiRequestBuilder;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Action to receive the full address of an image. Like "Img.gif" to
 * "http://wikihost.tld/w/images/x/y/Img.gif".
 *
 * @author Thomas Stock
 */
public class ImageInfo extends MWAction {

  private static final Logger log = LoggerFactory.getLogger(ImageInfo.class);
  private static final Map<String, String> EMPTY_STRING_MAP = Collections.emptyMap();

  public static final String WIDTH = "iiurlwidth";
  public static final String HEIGHT = "iiurlheight";

  private String urlOfImage = "";
  private Get msg;
  private final MediaWikiBot bot;
  private boolean selfEx = true;
  private final ImmutableMap<String, String> params;

  private final String name;

  /**
   * Get an absolute url to an image.
   *
   * @param name of, like "Test.gif"
   */
  public ImageInfo(MediaWikiBot bot, String name) {
    this(bot, name, EMPTY_STRING_MAP);
  }

  public ImageInfo(MediaWikiBot bot, String name, Map<String, String> params) {
    this.bot = bot;
    this.name = name;
    this.params = ImmutableMap.copyOf(params);
    prepareMsg(name);
  }

  private void prepareMsg(String name) {

    RequestBuilder requestBuilder = new ApiRequestBuilder() //
        .action("query") //
        .formatXml() //
        .param("iiprop", "url") //
        .param("prop", "imageinfo") //
        ;

    int width = intOrZero(params.get(WIDTH)).or(0);
    if (width > 0) {
      requestBuilder.param(WIDTH, width);
    }

    int height = intOrZero(params.get(HEIGHT)).or(0);
    if (height > 0) {
      requestBuilder.param(HEIGHT, height);
    }
    requestBuilder.param("titles", "File:" + MediaWiki.urlEncode(name));
    msg = requestBuilder.buildGet();
  }

  private Optional<Integer> intOrZero(String string) {
    if (string == null) {
      return Optional.absent();
    } else {
      try {
        return Optional.of(Integer.parseInt(string));
      } catch (NumberFormatException e) {
        log.warn("\"{}\" is not a number", string);
        return Optional.absent();
      }
    }
  }

  /**
   * @return position like "http://server.tld/path/to/Test.gif"
   */
  public String getUrlAsString() {
    return getUrl().toExternalForm();
  }

  public URL getUrl() {
    String exceptionMsg = "no url for image with name \"" + name + "\"";
    try {
      selfEx = false;
      bot.getPerformedAction(this);
    } catch (ProcessException e) {
      throw ProcessException.joinMsgs(e, exceptionMsg);
    } finally {
      selfEx = true;
    }

    if (urlOfImage.length() < 1) {
      throw new ProcessException(exceptionMsg);
    }

    return JWBF.newURL(Checked.nonBlank(urlOfImage, "image url"));
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated see super
   */
  @Override
  @Deprecated
  public boolean isSelfExecuter() {
    return selfEx;
  }

  public BufferedImage getAsImage() throws IOException {
    return ImageIO.read(getUrl());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String processAllReturningText(String xml) {
    Optional<XmlElement> child = //
        XmlConverter.getChildOpt(xml, "query", "pages", "page", "imageinfo", "ii");
    if (child.isPresent()) {
      urlOfImage = child.get().getAttributeValueOpt("url").or("");
    }
    return "";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public HttpAction getNextMessage() {
    return msg;
  }
}
