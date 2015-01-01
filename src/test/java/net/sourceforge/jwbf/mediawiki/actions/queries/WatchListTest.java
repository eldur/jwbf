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

		WatchList testee = new WatchList(bot, 3, 0);
		testee.addParam("wlprop", "user", "timestamp", "comment", "title");
		Iterator<WatchResponse> iterator = testee.iterator();
		while (iterator.hasNext())
			System.out.println(iterator.next());
	}

}
