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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.core.actions.RequestBuilder;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.mediawiki.ApiRequestBuilder;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import net.sourceforge.jwbf.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.actions.util.RedirectFilter;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Action class using the MediaWiki-api's "list=allpages".
 *
 * @author Tobias Knerr
 * @author Thomas Stock
 */
public class AllPageTitles extends TitleQuery<String> {

  private static final Logger log = LoggerFactory.getLogger(AllPageTitles.class);

  /**
   * Pattern to parse returned page, @see {@link #parseHasMore(String)}.
   */
  private static final Pattern HAS_MORE_PATTERN = Pattern.compile(
      "<query-continue>.*?<allpages *apfrom=\"([^\"]*)\" */>.*?</query-continue>", Pattern.DOTALL
          | Pattern.MULTILINE);
  private static final Pattern HAS_MORE_PATTERN_20 = Pattern.compile(
      "<query-continue>.*?<allpages *apcontinue=\"([^\"]*)\" */>.*?</query-continue>",
      Pattern.DOTALL | Pattern.MULTILINE);
  private static final Pattern ARTICLE_TITLES_PATTERN = Pattern
      .compile("<p pageid=\".*?\" ns=\".*?\" title=\"(.*?)\" />");
  /** Pattern to parse returned page, @see {@link #parseArticleTitles(String)} */
  /**
   * Constant value for the aplimit-parameter. *
   */
  private static final int LIMIT = 50;

  /**
   * Information given in the constructor, necessary for creating next action.
   */
  private final String prefix;
  private final int[] namespaces;

  private final MediaWikiBot bot;

  private final String from;

  private final RedirectFilter rf;

  /**
   * The public constructor. It will have an MediaWiki-request generated, which is then added to msgs. When it is
   * answered, the method processAllReturningText will be called (from outside this class).
   *
   * @param from       page title to start from, may be null
   * @param prefix     restricts search to titles that begin with this value, may be null
   * @param rf         include redirects in the list
   * @param namespaces the namespace(s) that will be searched for links, as a string of numbers separated by '|'; if null, this
   *                   parameter is omitted TODO are multible namespaces allowed?
   */
  public AllPageTitles(MediaWikiBot bot, String from, String prefix, RedirectFilter rf,
      int... namespaces) {
    super(bot);

    this.bot = bot;
    this.rf = rf;
    this.prefix = prefix;
    this.namespaces = namespaces;
    this.from = from;
  }

  public AllPageTitles(MediaWikiBot bot, int... namespaces) {
    this(bot, null, null, RedirectFilter.nonredirects, namespaces);
  }

  /**
   * Generates the next MediaWiki-request (GetMethod) and adds it to msgs.
   *
   * @param from      page title to start from, may be null
   * @param prefix    restricts search to titles that begin with this value, may be null
   * @param rf        include redirects in the list
   * @param namespace the namespace(s) that will be searched for links, as a string of numbers separated by '|'; if null, this
   *                  parameter is omitted
   * @return a
   */
  protected Get generateRequest(Optional<String> from, String prefix, RedirectFilter rf,
      String namespace) {
    RequestBuilder requestBuilder = new ApiRequestBuilder() //
        .action("query") //
        .formatXml() //
        .param("list", "allpages") //
        .param("apfilterredir", findRedirectFilterValue(rf)) //
        .param("aplimit", LIMIT) //
        ;

    if (from.isPresent()) {
      requestBuilder.param("apfrom", MediaWiki.urlEncode(from.get()));
    }
    if (!Strings.isNullOrEmpty(prefix)) {
      requestBuilder.param("apprefix", MediaWiki.urlEncode(prefix));
    }
    if (!Strings.isNullOrEmpty(namespace)) {
      requestBuilder.param("apnamespace", MediaWiki.urlEncode(namespace));
    }
    return requestBuilder.buildGet();
  }

  protected String findRedirectFilterValue(RedirectFilter rf) {
    if (rf == RedirectFilter.all) {
      return "all";
    } else if (rf == RedirectFilter.redirects) {
      return "redirects";
    } else {
      return "nonredirects";
    }
  }

  /**
   * Picks the article name from a MediaWiki api response.
   *
   * @param s text for parsing
   * @return a
   */
  @Override
  protected ImmutableList<String> parseArticleTitles(String s) {
    ImmutableList.Builder<String> titles = ImmutableList.<String>builder();
    Matcher m = ARTICLE_TITLES_PATTERN.matcher(s);
    while (m.find()) {
      String title = MediaWiki.htmlUnescape(m.group(1));
      log.debug("Found article title: \"{}\"", title);
      titles.add(title);
    }
    return titles.build();
  }

  /**
   * Gets the information about a follow-up page from a provided api response. If there is one, a new request is added
   * to msgs by calling generateRequest. If no exists, the string is empty.
   *
   * @param s text for parsing
   * @return the
   */
  @Override
  protected String parseHasMore(final String s) {

    Pattern hasMorePattern = null;
    switch (botVersion()) {
    case MW1_15:
    case MW1_16:
    case MW1_17:
    case MW1_18:
    case MW1_19:
      hasMorePattern = HAS_MORE_PATTERN;
      break;

    default:
      hasMorePattern = HAS_MORE_PATTERN_20;
      break;
    }

    Matcher m = hasMorePattern.matcher(s);
    if (m.find()) {
      return m.group(1);
    } else {
      return "";
    }
  }

  private Version botVersion() {
    Version version = bot.getVersion();
    return Optional.fromNullable(version) //
        .or(Version.UNKNOWN);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected HttpAction prepareCollection() {
    return generateRequest(nextPageInfoOpt(), prefix, rf, MWAction.createNsString(namespaces));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Object clone() throws CloneNotSupportedException {
    return new AllPageTitles(bot, from, prefix, rf, namespaces);
  }

}
