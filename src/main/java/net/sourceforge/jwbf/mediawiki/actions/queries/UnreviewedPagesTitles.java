/*
 * Copyright 2007, 2016 Thomas Stock, Marco Ammon.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sourceforge.jwbf.mediawiki.actions.queries;

  


import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.sourceforge.jwbf.core.actions.RequestBuilder;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.mapper.XmlConverter;
import net.sourceforge.jwbf.mapper.XmlElement;
import net.sourceforge.jwbf.mediawiki.ApiRequestBuilder;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gets a list of pages which have not been reviewed yet.
 *
 * @author Thomas Stock, Marco Ammon
 */
public class UnreviewedPagesTitles extends BaseQuery<String> {

  private static final Logger log = LoggerFactory.getLogger(net.sourceforge.jwbf.mediawiki.actions.queries.UnreviewedPagesTitles.class);

  /**
   * value for the urlimit-parameter. 
   *
   */
  private static int LIMIT;

  private final MediaWikiBot bot;

  private final int[] namespaces;

  /**
   * generates the next MediaWiki-request (GetMethod) and adds it to msgs.
   *
   * @param namespace the namespace(s) that will be searched for links, as a string of numbers
   *                  separated by '|'; if null, this parameter is omitted
   * @param urstart   Start listing at this page title
   * 
   * @param urend     Stop listing at this page title
   * 
   */
  
  private HttpAction generateRequest(int[] namespace, String urstart, String urend) {
    
    RequestBuilder requestBuilder = new ApiRequestBuilder() //
        .action("query") //
        .formatXml() //
        .param("list", "unreviewedpages") //
        .param("urlimit", LIMIT) //
        ;
    if (namespace != null) {
      requestBuilder.param("urnamespace", MediaWiki.urlEncode(MWAction.createNsString(namespace)));
    }
    if (urstart.length() > 0) {
      requestBuilder.param("urstart", urstart);
    }
    if(urend.length() > 0){
        requestBuilder.param("urend", urend);
    }

    return requestBuilder.buildGet();

  }
  private HttpAction generateRequest(int[] namespace, String urstart) {
      return generateRequest(namespace, urstart, "");
  }
  
  private HttpAction generateRequest(int[] namespace) {
    return generateRequest(namespace, "", "");
  }

  /**
   *
   */
  public UnreviewedPagesTitles(MediaWikiBot bot, int... ns) {
    super(bot);
    namespaces = ns;
    this.bot = bot;
  }

  /**
   *
   */
  public UnreviewedPagesTitles(MediaWikiBot bot) {
    this(bot, MediaWiki.NS_ALL);
  }

  /**
   * picks the article name from a MediaWiki api response.
   *
   * @param s text for parsing
   */
  @Override
  protected ImmutableList<String> parseElements(String s) {
    XmlElement root = XmlConverter.getRootElement(s);
    List<String> titleCollection = Lists.newArrayList();
    findContent(root, titleCollection);
    return ImmutableList.copyOf(titleCollection);

  }

  private void findContent(final XmlElement root, List<String> titleCollection) {

    for (XmlElement xmlElement : root.getChildren()) {
      if (xmlElement.getQualifiedName().equalsIgnoreCase("p")) {
        titleCollection.add(MediaWiki.htmlUnescape(xmlElement.getAttributeValue("title")));
        //Not sure about this, RecentChanges doesn't have timestamp either
        setNextPageInfo(xmlElement.getAttributeValue("title"));
      } else {
        findContent(xmlElement, titleCollection);
      }

    }
  }

  @Override
  protected HttpAction prepareNextRequest() {
    if (hasNextPageInfo()) {
      return generateRequest(namespaces, getNextPageInfo());
    } else {
      return generateRequest(namespaces);
    }

  }

  @Override
  protected Iterator<String> copy() {
    return new net.sourceforge.jwbf.mediawiki.actions.queries.UnreviewedPagesTitles(bot, namespaces);
  }

  @Override
  protected Optional<String> parseHasMore(String s) {
    return Optional.absent();
  }

}

