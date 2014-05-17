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

import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.mediawiki.actions.misc.GetRendering;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Thomas Stock
 */
public class RenderingTest extends AbstractMediaWikiBotTest {

  @Ignore("check this")
  @Test
  public final void getRenderingWikipediaDe() {
    String liveUrl = getWikipediaDeUrl();
    bot = new MediaWikiBot(liveUrl);
    // doTest(bot);
  }

  @Test(expected = ActionException.class)
  public final void getRenderingPerformManual() {
    String liveUrl = getWikipediaDeUrl();
    bot = new MediaWikiBot(liveUrl);
    GetRendering r = new GetRendering(bot, "bert");
    bot.performAction(r);
  }

}
