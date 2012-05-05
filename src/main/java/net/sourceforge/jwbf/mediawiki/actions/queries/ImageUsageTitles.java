/*
 * Copyright 2007 Tobias Knerr.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * Contributors:
 * Tobias Knerr
 * 
 */
package net.sourceforge.jwbf.mediawiki.actions.queries;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_09;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_10;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_11;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_12;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_13;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_14;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_15;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_16;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_17;

import java.util.Collection;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.actions.util.SupportedBy;
import net.sourceforge.jwbf.mediawiki.actions.util.VersionException;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

/**
 * action class using the MediaWiki-api's "list=imagelinks" and later imageUsage.
 * 
 * @author Tobias Knerr
 * @author Thomas Stock
 * @since MediaWiki 1.9.0
 * 
 */
@Slf4j
@SupportedBy({ MW1_09, MW1_10, MW1_11, MW1_12, MW1_13, MW1_14, MW1_15, MW1_16, MW1_17 })
public class ImageUsageTitles extends TitleQuery<String> {

  /** constant value for the illimit-parameter. **/
  private static final int LIMIT = 50;

  private boolean init = true;

  private final MediaWikiBot bot;

  private final String imageName;
  private final int [] namespaces;
  private final VersionHandler handler;

  /**
   * The public constructor. It will have an MediaWiki-request generated,
   * which is then added to msgs. When it is answered,
   * the method processAllReturningText will be called
   * (from outside this class).
   * For the parameters, see {@link ImageUsageTitles#generateRequest(String, String, String)}
   */
  public ImageUsageTitles(MediaWikiBot bot, String imageName, int... namespaces) throws VersionException {
    super(bot);
    this.bot = bot;
    this.imageName = imageName;
    this.namespaces = namespaces;
    switch (bot.getVersion()) {
      case MW1_09:
      case MW1_10:
        handler = new Mw1_09Handler();
        break;

      case MW1_11:
      case MW1_12:
      case MW1_13:
      case MW1_14:
      case MW1_15:
      case MW1_16:
        handler = new Mw1_11Handler();
        break;

      case MW1_17:
      default:
        handler = new DefaultHandler();
        break;
    }
  }


  public ImageUsageTitles(MediaWikiBot bot, String nextPageInfo) throws VersionException {
    this(bot, nextPageInfo, MediaWiki.NS_ALL);

  }

  /**
   * generates the next MediaWiki-request (GetMethod) and adds it to msgs.
   *
   * @param imageName     the title of the image, not null
   * @param namespace     the namespace(s) that will be searched for links,
   *                      as a string of numbers separated by '|';
   *                      if null, this parameter is omitted
   * @param ilcontinue    the value for the ilcontinue parameter,
   *                      null for the generation of the initial request
   * @return a
   */
  private Get generateRequest(String imageName, String namespace,
      String ilcontinue) {

    if (ilcontinue == null) {
      return handler.generateRequest(imageName, namespace);

    } else {
      return handler.generateContinueRequest(imageName, namespace,
          ilcontinue);

    }

  }



  /**
   * gets the information about a follow-up page from a provided api response.
   * If there is one, a new request is added to msgs by calling generateRequest.
   *	
   * @param s   text for parsing
   */
  @Override
  protected String parseHasMore(final String s) {

    return handler.parseHasMore(s);

  }

  /**
   * picks the article name from a MediaWiki api response.
   *	
   * @param s   text for parsing
   */
  @Override
  protected Collection<String> parseArticleTitles(String s) {

    return handler.parseArticleTitles(s);

  }


  @Override
  protected HttpAction prepareCollection() {

    if (getNextPageInfo().length() <= 0) {
      return generateRequest(imageName, MWAction
          .createNsString(namespaces), null);
    } else {

      return generateRequest(imageName, null, getNextPageInfo());
    }

  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected Object clone() throws CloneNotSupportedException {
    try {
      return new ImageUsageTitles(bot, imageName, namespaces);
    } catch (VersionException e) {
      throw new CloneNotSupportedException(e.getLocalizedMessage());
    }
  }

  private abstract class VersionHandler {
    VersionHandler() {

    }
    public abstract Get generateRequest(String imageName, String namespace);

    public abstract Get generateContinueRequest(String imageName, String namespace,
        String ilcontinue);
    public abstract String parseHasMore(final String s);

    public abstract Collection<String> parseArticleTitles(String s);
  }

  private class DefaultHandler extends VersionHandler {

    @Override
    public Get generateContinueRequest(String imageName, String namespace,
        String ilcontinue) {

      String uS = "/api.php?action=query&list=imageusage"
          + "&iucontinue=" + MediaWiki.encode(ilcontinue)
          + "&iulimit=" + LIMIT + "&format=xml"
          + "&iutitle=" + MediaWiki.encode(imageName);
      return new Get(uS);
    }

    @Override
    public Get generateRequest(String imageName, String namespace) {

      String uS = "/api.php?action=query&list=imageusage"
          + "&iutitle="
          + MediaWiki.encode(imageName)
          + ((namespace != null && namespace.length() != 0) ? ("&iunamespace=" + MediaWiki
              .encode(namespace))
              : "") + "&iulimit=" + LIMIT + "&format=xml";
      return new Get(uS);

    }

    @Override
    public Collection<String> parseArticleTitles(String s) {
      Collection<String> titleCollection = new Vector<String>();

      Pattern p = Pattern.compile(
          "<iu pageid=\".*?\" ns=\".*?\" title=\"(.*?)\" />");

      Matcher m = p.matcher(s);

      while (m.find()) {
        titleCollection.add(m.group(1));
      }
      return titleCollection;
    }

    @Override
    public String parseHasMore(String s) {

      Pattern p = Pattern.compile(
          "<query-continue>.*?"
              + "<imageusage *iucontinue=\"([^\"]*)\" */>"
              + ".*?</query-continue>",
              Pattern.DOTALL | Pattern.MULTILINE);

      Matcher m = p.matcher(s);

      if (m.find()) {
        return m.group(1);
      } else {
        return "";
      }

    }

  }

  /**
   * VersionHandler for MW versions 1.10 .. 1.16.
   * This one is identical to the one for 1.17 except for
   * the iutitle parameter in generateContinueRequest.
   * 
   */
  private class Mw1_11Handler extends VersionHandler {

    @Override
    public Get generateContinueRequest(String imageName, String namespace,
        String ilcontinue) {

      String uS = "/api.php?action=query&list=imageusage"
          + "&iucontinue=" + MediaWiki.encode(ilcontinue)
          + "&iulimit=" + LIMIT + "&format=xml";
      return new Get(uS);
    }

    @Override
    public Get generateRequest(String imageName, String namespace) {

      String uS = "/api.php?action=query&list=imageusage"
          + "&iutitle="
          + MediaWiki.encode(imageName)
          + ((namespace != null && namespace.length() != 0) ? ("&iunamespace=" + MediaWiki
              .encode(namespace))
              : "") + "&iulimit=" + LIMIT + "&format=xml";
      return new Get(uS);

    }

    @Override
    public Collection<String> parseArticleTitles(String s) {
      Collection<String> titleCollection = new Vector<String>();

      Pattern p = Pattern.compile(
          "<iu pageid=\".*?\" ns=\".*?\" title=\"(.*?)\" />");

      Matcher m = p.matcher(s);

      while (m.find()) {
        titleCollection.add(m.group(1));
      }
      return titleCollection;
    }

    @Override
    public String parseHasMore(String s) {

      Pattern p = Pattern.compile(
          "<query-continue>.*?"
              + "<imageusage *iucontinue=\"([^\"]*)\" */>"
              + ".*?</query-continue>",
              Pattern.DOTALL | Pattern.MULTILINE);

      Matcher m = p.matcher(s);

      if (m.find()) {
        return m.group(1);
      } else {
        return "";
      }

    }

  }

  private class Mw1_09Handler extends VersionHandler {

    @Override
    public Get generateContinueRequest(String imageName, String namespace,
        String ilcontinue) {

      String uS = "/api.php?action=query&list=imagelinks"
          + "&ilcontinue=" + MediaWiki.encode(ilcontinue)
          + "&illimit=" + LIMIT + "&format=xml";
      return new Get(uS);
    }

    @Override
    public Get generateRequest(String imageName, String namespace) {

      String uS = "/api.php?action=query&list=imagelinks"
          + "&titles="
          + MediaWiki.encode(imageName)
          + ((namespace != null && namespace.length() != 0) ? ("&ilnamespace=" + MediaWiki.encode(namespace))
              : "") + "&illimit=" + LIMIT + "&format=xml";
      return new Get(uS);

    }

    @Override
    public Collection<String> parseArticleTitles(String s) {
      Collection<String> titleCollection = new Vector<String>();
      Pattern p = Pattern.compile(
          "<il pageid=\".*?\" ns=\".*?\" title=\"(.*?)\" />");

      Matcher m = p.matcher(s);

      while (m.find()) {
        titleCollection.add(m.group(1));
      }
      return titleCollection;
    }

    @Override
    public String parseHasMore(String s) {

      Pattern p = Pattern.compile("<query-continue>.*?"
          + "<imagelinks *ilcontinue=\"([^\"]*)\" */>"
          + ".*?</query-continue>", Pattern.DOTALL
          | Pattern.MULTILINE);

      Matcher m = p.matcher(s);

      if (m.find()) {
        return m.group(1);
      } else {
        return "";
      }

    }

  }

}
