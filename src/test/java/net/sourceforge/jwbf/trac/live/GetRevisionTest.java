package net.sourceforge.jwbf.trac.live;
//package net.sourceforge.jwbf.live.trac;
//
//import static org.junit.Assert.assertTrue;
//import net.sourceforge.jwbf.LiveTestFather;
//import net.sourceforge.jwbf.bots.TracWikiBot;
//import net.sourceforge.jwbf.contentRep.SimpleArticle;
//
//import org.apache.log4j.Logger;
//import org.apache.log4j.PropertyConfigurator;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//public class GetRevisionTest extends LiveTestFather {
//	private Logger log = Logger.getLogger(getClass());
//	private TracWikiBot bot;
//
//	/**
//	 * Setup log4j.
//	 * 
//	 * @throws Exception
//	 *             a
//	 */
//	@BeforeClass
//	public static void setUpBeforeClass() throws Exception {
//		PropertyConfigurator.configureAndWatch("test4log4j.properties",
//				60 * 1000);
//	}
//
//	/**
//	 * Test
//	 * 
//	 * @throws Exception
//	 *             a
//	 */
//	@Test
//	public final void getRevisionTrac() throws Exception {
//		bot = new TracWikiBot(getValue("trac_0_10_5_url"));
//
//		doTest(bot);
//	}
//	
//	/**
//	 * Test
//	 * 
//	 * @throws Exception
//	 *             a
//	 */
//	@Test
//	public final void getRevisionTrac0_11_1() throws Exception {
//		bot = new TracWikiBot(getValue("trac_0_11_2_1_url"));
//
//		doTest(bot);
//	}
//
//	private final void doTest(TracWikiBot bot) throws Exception {
//		String label = getValue("trac_0_10_5_article");
//
//		SimpleArticle sa = bot.readContent(label);
//
//		assertTrue("text should be longer then 10 chars", sa.getText().length() > 10);
//		log.info("text: " + sa.getText().substring(0, 10) + "...");
//		assertTrue("editor maybe not okay: " + sa.getEditor(), sa.getEditor()
//				.length() > 4);
//		log.info("author: " + sa.getEditor());
//		log.info("edittime: " + sa.getEditTimestamp());
//		// assertTrue("editsumm maybe not okay: " + sa.getEditSummary(),
//		// sa.getEditSummary().length() > 4);
//		log.info("editsumm: " + sa.getEditSummary());
//	}
//
//}
