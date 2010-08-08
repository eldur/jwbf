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

import java.util.Collection;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.actions.util.RedirectFilter;
import net.sourceforge.jwbf.mediawiki.actions.util.SupportedBy;
import net.sourceforge.jwbf.mediawiki.actions.util.VersionException;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import org.apache.log4j.Logger;


/**
 * action class using the MediaWiki-api's "list=backlinks".
 *
 * @author Thomas Stock
 * @author Tobias Knerr
 * @since JWBF 1.1
 */
@SupportedBy({ MW1_09, MW1_10, MW1_11, MW1_12, MW1_13, MW1_14, MW1_15, MW1_16 })
public class BacklinkTitles extends TitleQuery<String> {

  /**
   * enum that defines the three posibilities of dealing with
   * article lists including both redirects and non-redirects.
   * <ul>
   * <li>all: List all pages regardless of their redirect flag</li>
   * <li>redirects: Only list redirects</li>
   * <li>nonredirects: Don't list redirects</li>
   * </ul>
   */
  private Logger log = Logger.getLogger(getClass());

  /** constant value for the bllimit-parameter. **/
  private static final int LIMIT = 50;

  /** object creating the requests that are sent to the api. */
  private RequestBuilder requestBuilder = null;

  private final String articleName;



  private MediaWikiBot bot;
  private final RedirectFilter redirectFilter;
  private final int [] namespaces;
  /**
   * The public constructor. It will have a MediaWiki-request generated,
   * which is then added to msgs. When it is answered,
   * the method processAllReturningText will be called
   * (from outside this class).
   *
   * @param articleName    the title of the article, != null
   * @param namespaces      the namespace(s) that will be searched for links,
   *                       as a string of numbers separated by '|';
   *                       if null, this parameter is omitted.
   *                       See for e.g. {@link MediaWiki#NS_ALL}.
   * @param redirectFilter filter that determines how to handle redirects,
   *                       must be all for MW versions before 1.11; != null
   * @param bot	         a
   *
   * @throws VersionException  if general functionality or parameter values
   *                           are not compatible with apiVersion value
   */
  public BacklinkTitles(MediaWikiBot bot, String articleName, RedirectFilter redirectFilter,
      int... namespaces)
  throws VersionException {
    super(bot);
    assert bot != null;
    assert articleName != null && redirectFilter != null;
    if (bot.getVersion() == Version.MW1_09 && redirectFilter != RedirectFilter.all) {
      throw new VersionException("redirect filtering is not available in this MediaWiki version");
    }
    this.redirectFilter = redirectFilter;
    this.namespaces = namespaces;

    this.articleName = articleName;
    this.bot = bot;
    requestBuilder = createRequestBuilder(bot.getVersion());

  }
  /**
   *
   * @param articleName a
   * @param bot a
   * @throws VersionException if action is not supported
   */
  public BacklinkTitles(MediaWikiBot bot, String articleName)
  throws VersionException {
    this(bot, articleName, RedirectFilter.all, null);

  }



  @Override
  protected Object clone() throws CloneNotSupportedException {
    try {
      return new BacklinkTitles(bot, articleName, redirectFilter, namespaces);
    } catch (VersionException e) {
      throw new CloneNotSupportedException(e.getLocalizedMessage());
    }
  }


  /**
   * gets the information about a follow-up page from a provided api response.
   * If there is one, the information for the next page parameter is
   * added to the nextPageInfo field.
   *
   * @param s   text for parsing
   */
  @Override
  protected String parseHasMore(final String s) {

    // get the blcontinue-value
    Pattern p = Pattern.compile(
        "<query-continue>.*?"
        + "<backlinks *blcontinue=\"([^\"]*)\" */>"
        + ".*?</query-continue>",
        Pattern.DOTALL | Pattern.MULTILINE);

    Matcher m = p.matcher(s);

    if (m.find()) {
      return m.group(1);
    } else {
      return "";
    }

  }

  /**
   * picks the article name from a MediaWiki api response.
   *
   * @param s   text for parsing
   */
  @Override
  protected Collection<String> parseArticleTitles(String s) {

    // get the other backlink titles and add them all to the titleCollection
    Collection<String> titleCollection = new Vector<String>();

    Pattern p = Pattern.compile(
    "<bl pageid=\".*?\" ns=\".*?\" title=\"([^\"]*)\" (redirect=\"\" )?/>");

    Matcher m = p.matcher(s);

    while (m.find()) {
      titleCollection.add(m.group(1));

    }
    return titleCollection;

  }

  /**
   * creates a request builder for the given API version.
   * @param apiVersion a, for which the request builder is working.
   * @throws VersionException
   *             if no request builder class for the apiVersion is known
   * @return a
   */
  private RequestBuilder createRequestBuilder(Version apiVersion)
  throws VersionException {

    switch (apiVersion) {

      case MW1_09:
      case MW1_10:
        return new RequestBuilder1x09();

      default: //MW1_11 and up
        return new RequestBuilder1x11();

    }

  }

  /** interface for classes that create a request strings. */
  private interface RequestBuilder {

    /**
     * generates an initial MediaWiki-request.
     * @param articleName a
     * @param redirectFilter a
     * @param namespace a
     * @return the request in string form
     */
    String buildInitialRequest(String articleName,
        RedirectFilter redirectFilter,
        int [] namespace);

    /**
     * generates a follow-up MediaWiki-request.
     * @param blcontinue key for continuing
     * @return the request in string form
     */
    String buildContinueRequest(String blcontinue);

  }

  /** request builder for MW versions 1_11 to (at least) 1_13. */
  private static class RequestBuilder1x11 implements RequestBuilder {
    /**
     * {@inheritDoc}
     */
    public String buildInitialRequest(String articleName,
        RedirectFilter redirectFilter, int [] namespace)  {

      return "/api.php?action=query&list=backlinks"
      + "&bltitle=" + MediaWiki.encode(articleName)
      + ((namespace != null && MWAction.createNsString(namespace).length() != 0) ? ("&blnamespace=" + MediaWiki.encode(MWAction.createNsString(namespace))) : "")
      + "&blfilterredir=" + MediaWiki.encode(redirectFilter.toString())
      + "&bllimit=" + LIMIT + "&format=xml";
    }
    /**
     * {@inheritDoc}
     */
    public String buildContinueRequest(String blcontinue) {

      return "/api.php?action=query&list=backlinks"
      + "&blcontinue=" + MediaWiki.encode(blcontinue)
      + "&bllimit=" + LIMIT + "&format=xml";
    }

  }

  /** request builder for MW versions 1_09 and 1_10. */
  private static class RequestBuilder1x09 implements RequestBuilder {
    /**
     * {@inheritDoc}
     */
    public String buildInitialRequest(String articleName,
        RedirectFilter redirectFilter, int [] namespace)  {

      return "/api.php?action=query&list=backlinks"
      + "&titles=" + MediaWiki.encode(articleName)
      + ((namespace != null && MWAction.createNsString(namespace).length() != 0)
          ? ("&blnamespace=" + MediaWiki.encode(MWAction.createNsString(namespace))) : "")
          + "&blfilterredir=" + MediaWiki.encode(redirectFilter.toString())
          + "&bllimit=" + LIMIT + "&format=xml";
    }
    /**
     * {@inheritDoc}
     */
    public String buildContinueRequest(String blcontinue) {

      return "/api.php?action=query&list=backlinks"
      + "&blcontinue=" + MediaWiki.encode(blcontinue)
      + "&bllimit=" + LIMIT + "&format=xml";
    }

  }

  @Override
  protected HttpAction prepareCollection() {
    if (getNextPageInfo().length() > 0) {
      return new Get(requestBuilder.buildContinueRequest(getNextPageInfo()));
    } else {
      return new Get(requestBuilder.buildInitialRequest(articleName, redirectFilter, namespaces));
    }
  }


}
