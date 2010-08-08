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

import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_11;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_12;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_13;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_14;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_15;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_16;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.core.actions.util.ProcessException;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.util.SupportedBy;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import org.apache.log4j.Logger;

/**
 * A specialization of {@link CategoryMembers} with contains {@link String}s.
 * @author Thomas Stock
 */
@SupportedBy({ MW1_11, MW1_12, MW1_13, MW1_14, MW1_15, MW1_16 })
public class CategoryMembersSimple  implements Iterable<String>, Iterator<String> {

  private Get msg;
  private final CategoryMembers cm;
  /**
   * Collection that will contain the result
   * (titles of articles linking to the target)
   * after performing the action has finished.
   */
  private Collection<String> titleCollection = new ArrayList<String>();
  private Iterator<String> titleIterator;
  private Logger log = Logger.getLogger(getClass());



  /**
   * @param categoryName like "Buildings" or "Chemical elements"
   * 		without prefix "Category:" in {@link MediaWiki#NS_MAIN}
   * @param bot a
   * @throws ActionException on any kind of http or version problems
   * @throws ProcessException on inner problems like mw version
   * 
   */
  public CategoryMembersSimple(MediaWikiBot bot, String categoryName) throws ActionException, ProcessException {
    this(bot, categoryName, MediaWiki.NS_MAIN);

  }
  /**
   * @param categoryName like "Buildings" or "Chemical elements" without prefix "Category:"
   * @param bot a
   * @param namespaces for search
   * @throws ActionException on any kind of http or version problems
   * @throws ProcessException on inner problems like mw version
   * 
   */
  public CategoryMembersSimple(MediaWikiBot bot, String categoryName,
      int... namespaces) throws ActionException, ProcessException {
    cm = new CategoryMembers(bot, categoryName, namespaces) {

      public HttpAction getNextMessage() {
        return msg;
      }

      @Override
      protected void finalizeParse() {
        titleIterator = titleCollection.iterator();

      }

      @Override
      protected void addCatItem(String title, int pageid, int ns) {
        titleCollection.add(title);

      }

      @Override
      public String processAllReturningText(String s) throws ProcessException {

        if (log.isDebugEnabled())
          log.debug("processAllReturningText");
        titleCollection.clear();
        String buff = super.processAllReturningText(s);

        titleIterator = titleCollection.iterator();
        return buff;
      }
    };

  }



  private synchronized void prepareCollection() {

    if (cm.init || (!titleIterator.hasNext() && cm.hasMoreResults)) {
      if (cm.init) {
        cm.setHasMoreMessages(true); // FIXME check if other action should have this too
        msg = cm.generateFirstRequest();
      } else {
        msg = cm.generateContinueRequest(cm.nextPageInfo);
      }
      cm.init = false;
      try {

        cm.bot.performAction(cm);
        cm.setHasMoreMessages(true);
        if (log.isDebugEnabled())
          log.debug("preparing success");
      } catch (ActionException e) {
        e.printStackTrace();
        cm.setHasMoreMessages(false);
      } catch (ProcessException e) {
        e.printStackTrace();
        cm.setHasMoreMessages(false);
      }

    }
  }


  public Iterator<String> iterator() {
    return this;
  }

  public boolean hasNext() {
    prepareCollection();
    return titleIterator.hasNext();
  }

  public String next() {
    prepareCollection();
    return titleIterator.next();
  }

  public void remove() {
    titleIterator.remove();

  }


}
