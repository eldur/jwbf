package net.sourceforge.jwbf.live.mediawiki;

import static net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version.MW1_09;
import static net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version.MW1_10;
import static net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version.MW1_11;
import static net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version.MW1_12;
import static net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version.MW1_13;
import static net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version.MW1_14;
import static net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version.MW1_15;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import net.sourceforge.jwbf.bots.WikiBot;
import net.sourceforge.jwbf.contentRep.Article;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
/**
 * 
 * @author Thomas Stock
 *
 */
public class BasicTest extends ArticleTest {
	/**
	 * Do.
	 * @throws Exception a
	 */
	@BeforeClass
	public static void setUp() throws Exception {
		PropertyConfigurator.configureAndWatch("test4log4j.properties",
				60 * 1000);
		
		bots.add(getMediaWikiBot(MW1_09, true));
		bots.add(getMediaWikiBot(MW1_10, true));
		bots.add(getMediaWikiBot(MW1_11, true));
		bots.add(getMediaWikiBot(MW1_12, true));
		bots.add(getMediaWikiBot(MW1_13, true));
		bots.add(getMediaWikiBot(MW1_14, true));
		bots.add(getMediaWikiBot(MW1_15, true));
	}
	/**
	 * Required for extension.
	 * @throws Exception a
	 */
	@Before
	public void doNothing() throws Exception  {
		  
	}
	
	@Test
	public final void articleTest() throws Exception {
		WikiBot bot = bots.iterator().next();
		Article a = new Article(bot, "Test");
		a.setText("a");
		final String aText = a.getText();
		a.save();
		assertFalse(a.isMinorEdit());
		final String firstEdit = a.getRevisionId();
		a.setMinorEdit(true);
		a.save("comment");
		final String secondEdit = a.getRevisionId();
		assertEquals("same rev ID", firstEdit, secondEdit);
		a.addText(getRandom(16));
		String aaText = a.getText();
		a.save();
		final String thirdEdit = a.getRevisionId();
		assertTrue(a.isMinorEdit());
		assertFalse("text should be differ:\n" + aaText + "\n" + aText , aaText.equals(aText));
		assertTrue("dif rev ID, both: " + thirdEdit
				, Integer.parseInt(firstEdit) != Integer.parseInt(thirdEdit));
		
		
	}
	
	

}
