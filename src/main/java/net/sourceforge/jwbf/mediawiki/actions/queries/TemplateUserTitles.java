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

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import net.sourceforge.jwbf.core.actions.RequestBuilder;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.mediawiki.ApiRequestBuilder;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * action class using the MediaWiki-api's "list=embeddedin" that is used to find all articles which
 * use a template.
 *
 * @author Tobias Knerr
 * @author Thomas Stock
 * @since MediaWiki 1.9.0
 */
public class TemplateUserTitles extends TitleQuery<String> {

  private static final Logger log = LoggerFactory.getLogger(TemplateUserTitles.class);

  // TODO do not work with patterns
  private static final Pattern TEMPLATE_USAGE_PATTERN =
      Pattern.compile("<ei pageid=\".*?\" ns=\".*?\" title=\"(.*?)\" />");

  /**
   * constant value for the eilimit-parameter. *
   */
  private static final int LIMIT = 50;
  private final MediaWikiBot bot;

  private final String templateName;
  private final int[] namespaces;

  /**
   * The public constructor. It will have an MediaWiki-request generated, which is then added to
   * msgs. When it is answered, the method processAllReturningText will be called (from outside this
   * class). For the parameters, see {@link TemplateUserTitles#generateRequest(String, int[],
   * String)}
   */
  public TemplateUserTitles(MediaWikiBot bot, String templateName, int... namespaces) {
    super(bot);
    this.bot = bot;
    this.templateName = templateName;
    this.namespaces = namespaces;

  }

  /**
   * generates the next MediaWiki-request (GetMethod) and adds it to msgs.
   *
   * @param templateName the name of the template, not null
   * @param namespaces   the namespace(s) that will be searched for links, as a string of numbers
   *                     separated by '|'; if null, this parameter is omitted
   * @param eicontinue   the value for the eicontinue parameter, null for the generation of the
   *                     initial request
   */
  private HttpAction generateRequest(String templateName, int[] namespaces, String eicontinue) {
    String namespacesValue = MWAction.createNsString(namespaces);
    RequestBuilder requestBuilder = new ApiRequestBuilder() //
        .action("query") //
        .formatXml() //
        .param("list", "embeddedin") //
        .param("eilimit", LIMIT) //
        .param("eititle", MediaWiki.urlEncode(templateName)) //
        ;

    if (!Strings.isNullOrEmpty(namespacesValue)) {
      requestBuilder.param("einamespace", MediaWiki.urlEncode(namespacesValue));
    }
    if (eicontinue != null) {
      requestBuilder.param("eicontinue", MediaWiki.urlEncode(eicontinue));
    }

    return requestBuilder.buildGet();

  }

  /**
   * gets the information about a follow-up page from a provided api response. If there is one, a
   * new request is added to msgs by calling generateRequest.
   *
   * @param s text for parsing
   */
  @Override
  protected String parseHasMore(final String s) {

    // get the eicontinue-value

    Pattern p = Pattern.compile(
        "<query-continue>.*?" + "<embeddedin *eicontinue=\"([^\"]*)\" */>" + ".*?</query-continue>",
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
   * @param s text for parsing
   */
  @Override
  protected ImmutableList<String> parseArticleTitles(String s) {

    Matcher m = TEMPLATE_USAGE_PATTERN.matcher(s);
    ImmutableList.Builder<String> titleCollection = ImmutableList.<String>builder();
    while (m.find()) {
      titleCollection.add(m.group(1));
    }

    return titleCollection.build();
  }

  @Override
  protected HttpAction prepareCollection() {
    if (hasNextPageInfo()) {
      return generateRequest(templateName, namespaces, getNextPageInfo());
    } else {
      return generateRequest(templateName, namespaces, null);
    }

  }

  @Override
  protected Object clone() throws CloneNotSupportedException {
    return new TemplateUserTitles(bot, templateName, namespaces);
  }

}
