package net.sourceforge.jwbf.inyoka.live;

import static net.sourceforge.jwbf.TestHelper.assumeReachable;
import static org.junit.Assert.assertTrue;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.inyoka.bots.InyokaWikiBot;

import org.junit.Ignore;
import org.junit.Test;

@Slf4j
public class GetRevisionTest {
  private InyokaWikiBot bot;

  /**
   * Test write and read
   * 
   * @throws Exception
   *           a
   */
  @Ignore("seems that the page layout changend")
  @Test
  public final void getRevisionInyoka() throws Exception {
    String url = "http://wiki.ubuntuusers.de/";
    assumeReachable(url);
    bot = new InyokaWikiBot(url);
    doTest(bot);
  }

  private final void doTest(InyokaWikiBot bot) throws Exception {
    // TODO not a really good test
    String label;
    label = "Startseite";
    Article sa = bot.getArticle(label);

    assertTrue(sa.getText().length() > 10);
    log.info("text: " + sa.getText().substring(0, 10) + "...");
    // assertTrue("editor maybe not okay: " + sa.getEditor(),
    // sa.getEditor().length() > 4);
    log.info("author: " + sa.getEditor());
    log.info("edittime: " + sa.getEditTimestamp());
    // assertTrue("editsumm maybe not okay: " + sa.getEditSummary(),
    // sa.getEditSummary().length() > 4);
    log.info("editsumm: " + sa.getEditSummary());
  }

}
