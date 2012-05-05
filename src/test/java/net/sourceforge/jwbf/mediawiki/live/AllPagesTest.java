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

import static net.sourceforge.jwbf.TestHelper.assumeReachable;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.queries.AllPageTitles;
import net.sourceforge.jwbf.mediawiki.actions.util.RedirectFilter;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import org.junit.Test;

/**
 * 
 * @author Thomas Stock
 * 
 */
public class AllPagesTest extends AbstractMediaWikiBotTest {

  /**
   * Test category read. Test category must have more then 50 members.
   * 
   * @throws Exception
   *           a
   */
  @Test
  public final void allPagesWikipediaDe() throws Exception {
    String url = "http://de.wikipedia.org/w/index.php";
    assumeReachable(url);
    bot = new MediaWikiBot(url);
    doTest();
  }

  @Test
  public final void allPagesTitle0() throws Exception {
    String url = "http://de.wikipedia.org/w/index.php"; // TODO replace with a
                                                        // local test
    assumeReachable(url);
    bot = new MediaWikiBot(url);
    AllPageTitles all = new AllPageTitles(bot, null, null, RedirectFilter.all,
        MediaWiki.NS_ALL);
    for (String title : all) {
      title.getClass();
      break;
    }
  }

  private void doTest() {
    AllPageTitles gat = new AllPageTitles(bot, null, null, RedirectFilter.all,
        MediaWiki.NS_MAIN);

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
