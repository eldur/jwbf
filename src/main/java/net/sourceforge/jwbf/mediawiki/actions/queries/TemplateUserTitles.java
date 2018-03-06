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

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import net.sourceforge.jwbf.core.actions.RequestBuilder;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.mapper.XmlConverter;
import net.sourceforge.jwbf.mapper.XmlElement;
import net.sourceforge.jwbf.mediawiki.ApiRequestBuilder;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

/**
 * action class using the MediaWiki-api's "list=embeddedin" that is used to find all articles which
 * use a template.
 *
 * @author Tobias Knerr
 * @author Thomas Stock
 * @since MediaWiki 1.9.0
 */
public class TemplateUserTitles extends BaseQuery<String> {

  private static final Logger log = LoggerFactory.getLogger(TemplateUserTitles.class);

  private final MediaWikiBot bot;

  private final String templateName;
  private final ImmutableList<Integer> namespaces;
  private final int limit;

  public TemplateUserTitles(MediaWikiBot bot, String templateName, int... namespaces) {
    this(bot, 50, templateName, MWAction.nullSafeCopyOf(namespaces));
  }

  TemplateUserTitles(
      MediaWikiBot bot, int limit, String templateName, ImmutableList<Integer> namespaces) {
    super(bot);
    this.bot = bot;
    this.templateName = templateName;
    this.namespaces = namespaces;
    this.limit = limit;
  }

  /**
   * gets the information about a follow-up page from a provided api response. If there is one, a
   * new request is added to msgs by calling generateRequest.
   *
   * @param xml text for parsing
   */
  @Override
  protected Optional<String> parseHasMore(final String xml) {
    return parseXmlHasMore(xml, "embeddedin", "eicontinue", "eicontinue");
  }

  @Override
  protected ImmutableList<String> parseElements(String xml) {
    Optional<XmlElement> children = XmlConverter.getChildOpt(xml, "query", "embeddedin");
    ImmutableList.Builder<String> titleCollection = ImmutableList.builder();
    if (children.isPresent()) {
      for (XmlElement e : children.get().getChildren("ei")) {
        titleCollection.add(e.getAttributeValue("title"));
      }
    }

    return titleCollection.build();
  }

  @Override
  protected HttpAction prepareNextRequest() {
    RequestBuilder requestBuilder =
        new ApiRequestBuilder() //
            .action("query") //
            .paramNewContinue(bot.getVersion()) //
            .formatXml() //
            .param("list", "embeddedin") //
            .param("eilimit", limit) //
            .param("eititle", MediaWiki.urlEncode(templateName)) //
        ;

    String namespacesValue = MWAction.createNsString(namespaces);
    if (!Strings.isNullOrEmpty(namespacesValue)) {
      requestBuilder.param("einamespace", MediaWiki.urlEncode(namespacesValue));
    }

    Optional<String> eicontinue = nextPageInfoOpt();
    if (eicontinue.isPresent()) {
      requestBuilder.param("eicontinue", MediaWiki.urlEncode(eicontinue.get()));
    }

    return requestBuilder.buildGet();
  }

  @Override
  protected Iterator<String> copy() {
    return new TemplateUserTitles(bot, limit, templateName, namespaces);
  }
}
