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
package net.sourceforge.jwbf.mediawiki.live;

import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.queries.AllPageTitles;
import net.sourceforge.jwbf.mediawiki.actions.util.RedirectFilter;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Thomas Stock
 */
public class AllPagesTest extends AbstractMediaWikiBotTest {

  /**
   * TODO move to integation tests
   */
  @Ignore
  @Test
  public final void allPagesWikipediaDe() {
    String url = getWikipediaDeUrl();
    bot = new MediaWikiBot(url);
    AllPageTitles gat = new AllPageTitles(bot, null, null, RedirectFilter.all, MediaWiki.NS_MAIN);
    // TODO write a test where query-continue is an html/xml entity like &amp;
    // TODO write a test where query-continue is a json special char like \"
    Iterator<String> is = gat.iterator();
    int i = 0;
    while (is.hasNext()) {
      is.next();
      i++;
      if (i > 55) {
        break;
      }
    }

    assertTrue("i is: " + i, i > 50);
  }

}
