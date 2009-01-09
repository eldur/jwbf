package net.sourceforge.jwbf.actions.mw.util;

import static net.sourceforge.jwbf.bots.MediaWikiBot.NS_CATEGORY;
import static net.sourceforge.jwbf.bots.MediaWikiBot.NS_MAIN;
import static net.sourceforge.jwbf.bots.MediaWikiBot.NS_TEMPLATE;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class NsTest {

	@Test
	public final void testNsCreate() {
		
		String s = MWAction.createNsString(NS_MAIN, NS_TEMPLATE, NS_CATEGORY);
		assertEquals(NS_MAIN + "|" + NS_TEMPLATE + "|" + NS_CATEGORY, s);
	}

}
