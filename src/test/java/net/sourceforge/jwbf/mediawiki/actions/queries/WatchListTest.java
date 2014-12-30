package net.sourceforge.jwbf.mediawiki.actions.queries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.jwbf.AbstractIntegTest;
import net.sourceforge.jwbf.core.contentRep.WatchResponse;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import org.junit.Test;

public class WatchListTest extends AbstractIntegTest {
  @Test
  public void test() {
    MediaWikiBot bot = new MediaWikiBot("http://fr.wikipedia.org/w/");
    bot.login("Hunsu", "password");
    HashMap<String, List<String>> params = new HashMap<>();
    List<String> values = new ArrayList<String>();
    values.add("user"); values.add("timestamp");
    values.add("comment"); values.add("title");

    params.put("wlprop", values);

    WatchList testee =
        new WatchList(bot, params, 0);
    Iterator<WatchResponse> iterator = testee.iterator();
    while(iterator.hasNext())
    System.out.println(iterator.next());
  }

}
