package net.sourceforge.jwbf.actions.mediawiki.util;

import static net.sourceforge.jwbf.actions.mediawiki.MediaWiki.NS_CATEGORY;
import static net.sourceforge.jwbf.actions.mediawiki.MediaWiki.NS_MAIN;
import static net.sourceforge.jwbf.actions.mediawiki.MediaWiki.NS_TEMPLATE;
import static org.junit.Assert.assertEquals;
import net.sourceforge.jwbf.actions.mediawiki.MediaWiki;

import org.junit.Test;

public class NsTest {

	@Test
	public final void testNsCreate() {
		
		String s = MWAction.createNsString(NS_MAIN, NS_TEMPLATE, NS_CATEGORY);
		assertEquals(NS_MAIN + "|" + NS_TEMPLATE + "|" + NS_CATEGORY, s);
	}

	@Test
	public final void testEntities() {
		
		

		String s = "&#039;";
		String t = "'";
		assertEquals(t, MediaWiki.decode(s));
		s = "&quot;";
		t = "\"";
		assertEquals(t, MediaWiki.decode(s));
	}
}
