/**
 * 
 */
package net.sourceforge.jwbf.live.mediawiki;

import net.sourceforge.jwbf.LiveTestFather;
import net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.actions.mediawiki.util.VersionException;
import net.sourceforge.jwbf.actions.util.ActionException;
import net.sourceforge.jwbf.actions.util.ProcessException;
import net.sourceforge.jwbf.bots.MediaWikiAdapterBot;
import net.sourceforge.jwbf.contentRep.ContentAccessable;
import net.sourceforge.jwbf.contentRep.SimpleArticle;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Thomas
 *
 */
public class DeleteTest extends LiveTestFather {
	private MediaWikiAdapterBot bot = null;
	private static final int COUNT = 1;
	@BeforeClass
	public static void setUp() throws Exception {
		PropertyConfigurator.configureAndWatch("test4log4j.properties",
				60 * 1000);
	}
	private final void prepare(MediaWikiAdapterBot bot) throws Exception {
		SimpleArticle a = new SimpleArticle("Delete", "0");
		
		for (int i = 0; i < COUNT; i++) {
			a.setLabel("Delete " + i);
			a.setText("delete me");
			bot.writeContent(a);
		}
	}
	
	private final void delete(MediaWikiAdapterBot bot) throws ActionException, ProcessException {
		
		for (int i = 0; i < COUNT ; i++) {
			bot.postDelete("Delete " + i);
		}
	}
	
	private final void test(MediaWikiAdapterBot bot) throws ActionException, ProcessException {
	
		for (int i = 0; i < COUNT; i++) {
			ContentAccessable ca = bot.readContent("Delete " + i);
			Assert.assertTrue("textlength of Delete " + i + " is not 0", ca.getText().length() == 0);
		}
	}
	
	
	/**
	 * Test category read. Test category must have more then 50 members.
	 * @throws Exception a
	 */
	@Test(expected=VersionException.class)
	public final void deleteWikiMW1_09() throws Exception {
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_09_url"));
		bot.login(getValue("wikiMW1_09_user"), getValue("wikiMW1_09_pass"));
		Assert.assertTrue( "Wrong Wiki Version " + bot.getVersion() , Version.MW1_09.equals(bot.getVersion()));
		prepare(bot);
		delete(bot);
		test(bot);
		
		
	}
	/**
	 * Test category read. Test category must have more then 50 members.
	 * @throws Exception a
	 */
	@Test(expected=VersionException.class)
	public final void deleteWikiMW1_10() throws Exception {
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_10_url"));
		bot.login(getValue("wikiMW1_10_user"), getValue("wikiMW1_10_pass"));
		Assert.assertTrue( "Wrong Wiki Version " + bot.getVersion() , Version.MW1_10.equals(bot.getVersion()));
		prepare(bot);
		delete(bot);
		test(bot);
		
		
	}
	
	/**
	 * Test category read. Test category must have more then 50 members.
	 * @throws Exception a
	 */
	@Test(expected=VersionException.class)
	public final void deleteWikiMW1_11() throws Exception {
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_11_url"));
		bot.login(getValue("wikiMW1_11_user"), getValue("wikiMW1_11_pass"));
		Assert.assertTrue( "Wrong Wiki Version " + bot.getVersion() , Version.MW1_11.equals(bot.getVersion()));
		
		prepare(bot);
		delete(bot);
		test(bot);
		
		
	}
	
	/**
	 * Test category read. Test category must have more then 50 members.
	 * @throws Exception a
	 */
	@Test
	public final void deleteWikiMW1_12() throws Exception {
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_12_url"));
		bot.login(getValue("wikiMW1_12_user"), getValue("wikiMW1_12_pass"));
		Assert.assertTrue( "Wrong Wiki Version " + bot.getVersion() , Version.MW1_12.equals(bot.getVersion()));
		prepare(bot);
		delete(bot);
		test(bot);
		
		
	}
	/**
	 * Test category read. Test category must have more then 50 members.
	 * @throws Exception a
	 */
	@Test
	public final void deleteWikiMW1_13() throws Exception {
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_13_url"));
		bot.login(getValue("wikiMW1_13_user"), getValue("wikiMW1_13_pass"));
		
		Assert.assertTrue( "Wrong Wiki Version " + bot.getVersion() , Version.MW1_13.equals(bot.getVersion()));
		prepare(bot);
		delete(bot);
		test(bot);
		
		
	}
	
	
}
