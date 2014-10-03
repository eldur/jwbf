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

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.core.actions.RequestBuilder;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.mapper.XmlConverter;
import net.sourceforge.jwbf.mapper.XmlElement;
import net.sourceforge.jwbf.mediawiki.ApiRequestBuilder;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import net.sourceforge.jwbf.mediawiki.contentRep.LogItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * List log events, filtered by time range, event type, user type, or the page it applies to.
 * Ordered by event timestamp. Parameters: letype (flt), lefrom (paging timestamp), leto (flt),
 * ledirection (dflt=older), leuser (flt), letitle (flt), lelimit (dflt=10, max=500/5000) api.php ?
 * action=query &amp; list=logevents - List last 10 events of any type TODO This is a semi-complete
 * extension point
 *
 * @author Thomas Stock
 */
public class LogEvents extends BaseQuery<LogItem> {

  private static final Logger log = LoggerFactory.getLogger(LogEvents.class);

  public static final String BLOCK = "block";
  public static final String PROTECT = "protect";
  public static final String RIGHTS = "rights";
  public static final String DELETE = "delete";
  public static final String UPLOAD = "upload";
  public static final String MOVE = "move";
  public static final String IMPORT = "import";
  public static final String PATROL = "patrol";
  public static final String MERGE = "merge";

  private final int limit;

  private Get msg;
  private boolean init = true;
  /**
   * Collection that will contain the result (titles of articles linking to the target) after
   * performing the action has finished.
   */
  private final String[] type;

  /**
   * information necessary to get the next api page.
   *
   * @param type of like {@link #MOVE}
   */
  public LogEvents(MediaWikiBot bot, String type) {
    this(bot, new String[] { type });
  }

  /**
   * @param type of like {@link #MOVE}
   */
  public LogEvents(MediaWikiBot bot, String[] type) {
    this(bot, 50, type.clone());
  }

  /**
   * @param limit of events
   * @param type  of like {@link #MOVE}
   */
  public LogEvents(MediaWikiBot bot, int limit, String type) {
    this(bot, limit, new String[] { type });
  }

  /**
   * @param limit of events
   * @param type  of like {@link #MOVE}
   */
  public LogEvents(MediaWikiBot bot, int limit, String[] type) {
    super(bot);
    this.type = type;
    this.limit = limit;
  }

  private Get generateRequest(String... logtype) {

    RequestBuilder requestBuilder = new ApiRequestBuilder() //
        .action("query") //
        .formatXml() //
        .param("list", "logevents") //
        .param("lelimit", limit) //
        ;
    if (logtype.length > 0) {
      requestBuilder.param("letype", Joiner.on("|").join(logtype));
    }

    return requestBuilder.buildGet();

  }

  private Get generateContinueRequest(String[] logtype) {
    // FIXME continueing is unused
    return generateRequest(logtype);
  }

  @Override
  protected ImmutableList<LogItem> parseArticleTitles(String xml) {

    ImmutableList.Builder<LogItem> builder = ImmutableList.builder();

    List<XmlElement> itmes = XmlConverter.getChild(xml, "query", "logevents").getChildren("item");
    for (XmlElement item : itmes) {
      String title = item.getAttributeValue("title");
      String typeOf = item.getAttributeValue("type");
      String user = item.getAttributeValue("user");
      builder.add(new LogItem(title, typeOf, user));

    }
    return builder.build();

  }

  @Override
  protected Optional<String> parseHasMore(final String s) {

    // get the blcontinue-value
    // FIXME remove this Pattern
    Pattern p = Pattern //
        .compile("<query-continue>.*?<logevents *lestart=\"([^\"]*)\" */>.*?</query-continue>",
            Pattern.DOTALL | Pattern.MULTILINE);

    Matcher m = p.matcher(s);
    if (m.find()) {
      return Optional.of(m.group(1));
    } else {
      return Optional.absent();
    }
  }

  @Override
  protected HttpAction prepareCollection() {

    if (hasNextPageInfo()) {
      return generateContinueRequest(type); // TODO nextpageinfo
    } else {
      return generateRequest(type);
    }

  }

  @Override
  protected Iterator<LogItem> copy() {
    return new LogEvents(bot(), limit, type);
  }

}
