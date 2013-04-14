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

import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_15;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_16;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_17;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_18;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_19;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_20;

import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.core.actions.util.ProcessException;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.actions.util.SupportedBy;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import net.sourceforge.jwbf.mediawiki.contentRep.LogItem;

import org.jdom.Element;

import com.google.common.collect.Lists;

/**
 * 
 * List log events, filtered by time range, event type, user type, or the page it applies to.
 * Ordered by event timestamp. Parameters: letype (flt), lefrom (paging timestamp), leto (flt),
 * ledirection (dflt=older), leuser (flt), letitle (flt), lelimit (dflt=10, max=500/5000)
 * 
 * api.php ? action=query & list=logevents - List last 10 events of any type
 * 
 * TODO This is a semi-complete extension point
 * 
 * @author Thomas Stock
 * 
 */
@Slf4j
@SupportedBy({ MW1_15, MW1_16, MW1_17, MW1_18, MW1_19, MW1_20 })
public class LogEvents extends MWAction implements Iterator<LogItem>, Iterable<LogItem> {

  /** value for the bllimit-parameter. * */

  public static final String BLOCK = "block";
  public static final String PROTECT = "protect";
  public static final String RIGHTS = "rights";
  public static final String DELETE = "delete";
  public static final String UPLOAD = "upload";
  public static final String MOVE = "move";
  public static final String IMPORT = "mport";
  public static final String PATROL = "patrol";
  public static final String MERGE = "merge";

  private final int limit;

  private Get msg;
  private final MediaWikiBot bot;
  /* first run variable */
  private boolean init = true;
  private boolean selvEx = true;
  /**
   * Collection that will contain the result (titles of articles linking to the target) after
   * performing the action has finished.
   */
  private Collection<LogItem> logCollection = Lists.newArrayList();
  private Iterator<LogItem> logIterator = null;
  private final String[] type;
  private String nextPageInfo = "";
  private boolean hasMoreResults = true;

  /**
   * information necessary to get the next api page.
   * 
   * @param type
   *          of like {@link #MOVE}
   * 
   */
  public LogEvents(MediaWikiBot bot, String type) {
    this(bot, new String[] { type });
  }

  /**
   * @param type
   *          of like {@link #MOVE}
   */
  public LogEvents(MediaWikiBot bot, String[] type) {
    this(bot, 50, type.clone());
  }

  /**
   * @param limit
   *          of events
   * @param type
   *          of like {@link #MOVE}
   */
  public LogEvents(MediaWikiBot bot, int limit, String type) {
    this(bot, limit, new String[] { type });
  }

  /**
   * @param limit
   *          of events
   * @param type
   *          of like {@link #MOVE}
   * 
   */
  public LogEvents(MediaWikiBot bot, int limit, String[] type) {
    super(bot.getVersion());
    this.bot = bot;
    this.type = type;
    this.limit = limit;
  }

  /**
   * generates the next MediaWiki-request (GetMethod) and adds it to msgs.
   * 
   * @param logtype
   *          type of log, like upload
   */
  private Get generateRequest(String... logtype) {

    String uS = "";

    uS = MediaWiki.URL_API + "?action=query&list=logevents";
    if (logtype.length > 0) {
      StringBuffer logtemp = new StringBuffer();
      for (int i = 0; i < logtype.length; i++) {
        logtemp.append(logtype[i] + "|");
      }
      uS += "&letype=" + logtemp.substring(0, logtemp.length() - 1);
    }

    uS += "&lelimit=" + limit + "&format=xml";

    return new Get(uS);

  }

  /**
   * generates the next MediaWiki-request (GetMethod) and adds it to msgs.
   * 
   * @param logtype
   *          type of log, like upload
   */
  private Get generateContinueRequest(String[] logtype, String continueing) {

    String uS = "";

    uS = MediaWiki.URL_API + "?action=query&list=logevents";
    if (logtype.length > 0) {
      StringBuffer logtemp = new StringBuffer();
      for (int i = 0; i < logtype.length; i++) {
        logtemp.append(logtype[i] + "|");
      }
      uS += "&letype=" + logtemp.substring(0, logtemp.length() - 1);
    }

    uS += "&lelimit=" + limit + "&format=xml";

    return new Get(uS);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String processAllReturningText(final String s) {
    logCollection.clear();
    parseArticleTitles(s);
    parseHasMore(s);
    logIterator = logCollection.iterator();
    return "";
  }

  /**
   * picks the article name from a MediaWiki api response.
   * 
   * @param xml
   *          text for parsing
   */
  private void parseArticleTitles(String xml) {

    Element root = getRootElement(xml);
    findContent(root);

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

    Pattern p = Pattern.compile("<query-continue>.*?" + "<logevents *lestart=\"([^\"]*)\" */>"
        + ".*?</query-continue>", Pattern.DOTALL | Pattern.MULTILINE);

    Matcher m = p.matcher(s);

    if (m.find()) {
      nextPageInfo = m.group(1);
      hasMoreResults = true;
    } else {
      hasMoreResults = false;
    }
    if (log.isDebugEnabled()) {
      log.debug("has more = " + hasMoreResults);
    }

  }

  @SuppressWarnings("unchecked")
  private void findContent(final Element root) {

    Iterator<Element> el = root.getChildren().iterator();
    while (el.hasNext()) {
      Element element = el.next();
      if (element.getQualifiedName().equalsIgnoreCase("item")) {

        LogItem l = new LogItem();
        l.setTitle(element.getAttributeValue("title"));
        l.setType(element.getAttributeValue("type"));
        l.setUser(element.getAttributeValue("user"));
        logCollection.add(l);

      } else {
        findContent(element);
      }

    }
  }

  private void prepareCollection() {

    if (init || (!logIterator.hasNext() && hasMoreResults)) {
      if (init) {
        msg = generateRequest(type);
      } else {
        msg = generateContinueRequest(type, nextPageInfo);
      }
      init = false;
      try {
        selvEx = false; // TODO not good
        bot.performAction(this);
        selvEx = true; // TODO not good
        setHasMoreMessages(true);
        if (log.isDebugEnabled())
          log.debug("preparing success");
      } catch (ActionException e) {
        e.printStackTrace();
        setHasMoreMessages(false);
      } catch (ProcessException e) {
        e.printStackTrace();
        setHasMoreMessages(false);
      }

    }
  }

  /**
   * {@inheritDoc}
   */
  public HttpAction getNextMessage() {
    return msg;
  }

  /**
   * {@inheritDoc}
   */
  public boolean hasNext() {
    prepareCollection();
    return logIterator.hasNext();
  }

  /**
   * {@inheritDoc}
   */
  public LogItem next() {
    prepareCollection();
    return logIterator.next();
  }

  /**
   * {@inheritDoc}
   */
  public void remove() {
    logIterator.remove();

  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  public Iterator<LogItem> iterator() {
    try {
      return (Iterator<LogItem>) clone();
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Object clone() throws CloneNotSupportedException {
    return new LogEvents(bot, limit, type);
  }

  /**
   * {@inheritDoc}
   * 
   * @deprecated see super
   */
  @Deprecated
  @Override
  public boolean isSelfExecuter() {
    return selvEx;
  }

}
