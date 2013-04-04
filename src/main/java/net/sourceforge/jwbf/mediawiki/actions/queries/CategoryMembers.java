/*
 * Copyright 2007 Thomas Stock.
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
 * 
 */
package net.sourceforge.jwbf.mediawiki.actions.queries;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jwbf.core.RequestBuilder;
import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

/**
 * A abstract action class using the MediaWiki-api's "list=categorymembers ". For further
 * information see <a href=
 * "http://www.mediawiki.org/wiki/API:Query_-_Lists#categorymembers_.2F_cm">API documentation</a>.
 * 
 * @author Thomas Stock
 */
@Slf4j
abstract class CategoryMembers extends MWAction {

  /** constant value for the bllimit-parameter. **/
  protected static final int LIMIT = 50;

  protected final MediaWikiBot bot;
  /**
   * information necessary to get the next api page.
   */
  protected String nextPageInfo = null;
  protected boolean hasMoreResults = false;

  protected boolean init = true;
  /**
   * Name of the category.
   */
  protected final String categoryName;

  protected RequestGenerator requestBuilder = null;

  protected final int[] namespace;
  private String namespaceStr = "";

  /**
   * The private constructor, which is used to create follow-up actions.
   * 
   * 
   * on version problems
   */
  protected CategoryMembers(MediaWikiBot bot, String categoryName, int[] namespace) {
    super(bot.getVersion());
    this.namespace = namespace.clone();
    namespaceStr = createNsString(namespace);
    this.categoryName = categoryName.replace(" ", "_");
    this.bot = bot;
    createRequestor();

  }

  private void createRequestor() {
    requestBuilder = new RequestGenerator();

  }

  /**
   * generates the next MediaWiki-request (GetMethod) and adds it to msgs.
   * 
   * @return a
   */
  protected final Get generateFirstRequest() {

    return new Get(requestBuilder.first(categoryName));
  }

  /**
   * generates the next MediaWiki-request (GetMethod) and adds it to msgs.
   * 
   * 
   * @param cmcontinue
   *          the value for the blcontinue parameter, null for the generation of the initial request
   * @return a
   */
  protected final Get generateContinueRequest(String cmcontinue) {

    try {

      return new Get(requestBuilder.continiue(cmcontinue));

    } catch (NullPointerException e) {
      e.printStackTrace();
    }
    return null;

  }

  /**
   * deals with the MediaWiki api's response by parsing the provided text.
   * 
   * @param s
   *          the answer to the most recently generated MediaWiki-request
   * 
   * @return empty string
   */
  @Override
  public String processAllReturningText(final String s) {
    parseArticleTitles(s);
    parseHasMore(s);
    return "";
  }

  /**
   * gets the information about a follow-up page from a provided api response. If there is one, a
   * new request is added to msgs by calling generateRequest.
   * 
   * @param s
   *          text for parsing
   */
  private void parseHasMore(final String s) {

    // get the blcontinue-value

    Pattern p = Pattern.compile("<query-continue>.*?"
        + "<categorymembers *cmcontinue=\"([^\"]*)\" */>" + ".*?</query-continue>", Pattern.DOTALL
        | Pattern.MULTILINE);

    Matcher m = p.matcher(s);

    if (m.find()) {
      nextPageInfo = m.group(1);
      hasMoreResults = true;
    } else {
      hasMoreResults = false;
    }
    if (log.isDebugEnabled())
      log.debug("has more = " + hasMoreResults);

  }

  /**
   * picks the article name from a MediaWiki api response.
   * 
   * @param s
   *          text for parsing
   */
  private final void parseArticleTitles(String s) {

    // get the backlink titles and add them all to the titleCollection

    Pattern p = Pattern.compile("<cm pageid=\"(.*?)\" ns=\"(.*?)\" title=\"(.*?)\" />");

    Matcher m = p.matcher(s);

    while (m.find()) {

      addCatItem(m.group(3), Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));

    }
    finalizeParse();
  }

  protected abstract void finalizeParse();

  protected abstract void addCatItem(String title, int pageid, int ns);

  protected class RequestGenerator {

    private static final String CMTITLE = "cmtitle";

    RequestGenerator() {

    }

    String continiue(String cmcontinue) {
      RequestBuilder requestBuilder = newRequestBuilder();

      requestBuilder.param("cmcontinue", MediaWiki.encode(cmcontinue));
      requestBuilder.param(CMTITLE, "Category:" + MediaWiki.encode(categoryName));
      // TODO: do not add Category: - instead, change other methods' descs (e.g.
      // in MediaWikiBot)
      return requestBuilder.build();
    }

    private RequestBuilder newRequestBuilder() {
      RequestBuilder requestBuilder = new RequestBuilder(MediaWiki.URL_API);
      if (namespaceStr.length() > 0) {
        requestBuilder.param("cmnamespace", MediaWiki.encode(namespaceStr));
      }

      requestBuilder //
          .param("action", "query") //
          .param("list", "categorymembers") //
          .param("cmlimit", LIMIT + "") //
          .param("format", "xml") //
      ;
      return requestBuilder;
    }

    String first(String categoryName) {

      RequestBuilder requestBuilder = newRequestBuilder();
      requestBuilder.param(CMTITLE, "Category:" + MediaWiki.encode(categoryName));

      return requestBuilder.build();
    }

  }

}
