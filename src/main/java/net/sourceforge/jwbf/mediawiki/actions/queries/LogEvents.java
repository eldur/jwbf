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

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import net.sourceforge.jwbf.core.actions.RequestBuilder;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.core.internal.NonnullFunction;
import net.sourceforge.jwbf.mapper.XmlConverter;
import net.sourceforge.jwbf.mapper.XmlElement;
import net.sourceforge.jwbf.mediawiki.ApiRequestBuilder;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import net.sourceforge.jwbf.mediawiki.contentRep.LogItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * List log events, filtered by time range, event type, user type, or the page it applies to.
 * Ordered by event timestamp. Parameters: letype (flt), lefrom (paging timestamp), leto (flt),
 * ledirection (dflt=older), leuser (flt), letitle (flt), lelimit (dflt=10, max=500/5000) api.php ?
 * action=query &amp; list=logevents - List last 10 events of any type extension point
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

  /**
   * Collection that will contain the result (titles of articles linking to the target) after
   * performing the action has finished.
   */
  private final ImmutableList<String> type;

  /**
   * information necessary to get the next api page.
   *
   * @param type of like {@link #MOVE}
   */
  public LogEvents(MediaWikiBot bot, String type) {
    this(bot, 50, ImmutableList.<String>of(type));
  }

  /**
   * @param type of like {@link #MOVE}
   *             @deprecated
   */
  @Deprecated
  public LogEvents(MediaWikiBot bot, String[] type) {
    this(bot, 50, type);
  }

  /**
   * @param limit  of events
   * @param first  of like {@link #MOVE}
   * @param others like {@link #DELETE, ...}
   */
  public LogEvents(MediaWikiBot bot, int limit, String first, String... others) {
    this(bot, limit, ImmutableList.<String>builder() //
        .add(first) //
        .addAll(MWAction.nullSafeCopyOf(others)) //
        .build());
  }

  /**
   * @param limit of events
   * @param type  of like {@link #MOVE}
   * @deprecated
   */
  @Deprecated
  public LogEvents(MediaWikiBot bot, int limit, String[] type) {
    this(bot, limit, MWAction.nullSafeCopyOf(type));
  }

  LogEvents(MediaWikiBot bot, int limit, ImmutableList<String> logtypes) {
    super(bot);
    this.type = logtypes; // String because logtypes is an extension point
    Preconditions.checkArgument(limit > 0, "limit must be > 0, but was " + limit);
    this.limit = limit;
  }

  private RequestBuilder generateRequest(ImmutableList<String> logtypes) {

    RequestBuilder requestBuilder = new ApiRequestBuilder() //
        .action("query") //
        .paramNewContinue(bot().getVersion()) //
        .formatXml() //
        .param("list", "logevents") //
        .param("lelimit", limit) //
        ;
    if (logtypes.size() > 0) {
      requestBuilder.param("letype", Joiner.on("|").join(logtypes));
    }
    return requestBuilder;
  }

  @Override
  protected ImmutableList<LogItem> parseArticleTitles(String xml) {

    ImmutableList.Builder<LogItem> builder = ImmutableList.builder();
    Optional<XmlElement> child = XmlConverter.getChildOpt(xml, "query", "logevents");
    if (child.isPresent()) {
      List<XmlElement> items = child.get().getChildren("item");
      for (XmlElement item : items) {
        String title = item.getAttributeValue("title");
        String typeOf = item.getAttributeValue("type");
        String user = item.getAttributeValue("user");
        builder.add(new LogItem(title, typeOf, user));

      }
    }
    return builder.build();

  }

  @Override
  protected Optional<String> parseHasMore(final String s) {
    if (bot().getVersion().greaterEqThen(MediaWiki.Version.MW1_23)) {
      return parseXmlHasMore(s, "logevents", "lestart", "lecontinue");
    } else {
      log.warn("continuation is not supported");
      return Optional.absent();
    }
  }

  @Override
  protected HttpAction prepareCollection() {
    if (hasNextPageInfo()) {
      return generateRequest(type) //
          .param("lecontinue", MediaWiki.urlEncode(getNextPageInfo())) //
          .buildGet();
    } else {
      return generateRequest(type).buildGet();
    }

  }

  @Override
  protected Iterator<LogItem> copy() {
    return new LogEvents(bot(), limit, type);
  }

  static Function<LogItem, String> toTitles() {
    return new NonnullFunction<LogItem, String>() {
      @Nonnull
      @Override
      protected String applyNonnull(@Nonnull LogItem in) {
        return in.getTitle();
      }
    };
  }
}
