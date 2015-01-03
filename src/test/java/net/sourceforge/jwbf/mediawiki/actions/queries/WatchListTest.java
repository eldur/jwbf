package net.sourceforge.jwbf.mediawiki.actions.queries;

import java.util.Iterator;
import net.sourceforge.jwbf.AbstractIntegTest;
import net.sourceforge.jwbf.core.contentRep.WatchResponse;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import org.junit.Test;

public class WatchListTest extends AbstractIntegTest {
	@Test
	public void test() {
		MediaWikiBot bot = new MediaWikiBot("http://fr.wikipedia.org/w/");
		bot.login("Hunsu", "password");
		QueryParameter params = new QueryParameter();
		params.param("wlprop", "user", "timestamp", "comment", "title");
		WatchList testee = new WatchList(bot, 50, params, 0);
		Iterator<WatchResponse> iterator = testee.iterator();
		while (iterator.hasNext())
			System.out.println(iterator.next());
	}

}
