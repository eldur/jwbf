package net.sourceforge.jwbf.live.mediawiki;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.util.Collection;
import java.util.Date;
import java.util.Vector;

import net.sourceforge.jwbf.LiveTestFather;
import net.sourceforge.jwbf.actions.mediawiki.util.VersionException;
import net.sourceforge.jwbf.bots.WikiBot;
import net.sourceforge.jwbf.contentRep.Article;

import org.junit.Test;

public abstract class ArticleTest extends LiveTestFather {

	protected static Collection<WikiBot> bots = new Vector<WikiBot>();

	public ArticleTest() {
	}

	/**
	 * 
	 * @throws Exception a
	 */
	@Test
	public final void readWriteDelete() throws Exception {
		
		for (WikiBot bot : bots) {

			String title = "z" + getRandomAlph(6); // create random title
			String user = bot.getUserinfo().getUsername();
			Article a = new Article(bot, title);
			for (int i = 0; i <= 2; i++) {
				String editSum = getRandomAlph(6); // create random edit sum
				a = new Article(bot, title); // create new article with given title
				a.setText(getRandom(42)); // set random text
				Date saveDate = new Date(System.currentTimeMillis());
				a.save(editSum); // save article a with given comment

				Article b = new Article(bot, title); // create new article b
				assertEquals(a.getTitle(), b.getTitle()); // compare title, must work -- see constructor
				assertEquals("text compair fails ", a.getText(), b.getText()); // force bot to load this from wiki
				assertEquals(user, b.getEditor());
				if (i > 1) {
					assertEquals(editSum, b.getEditSummary());
				}
				assertEquals(saveDate.getTime(), b.getEditTimestamp().getTime(), 5000); // max. 5 seconds delta
				
					

			}
			try {
				a.delete(); // clean up
			} catch (VersionException e) {
				e.printStackTrace();
			}

		}
		
	}
	
	/**
	 * 
	 * @throws Exception a
	 */
	@Test
	public final void meta() throws Exception {
		
		for (WikiBot bot : bots) {

		
				String title = "z" + getRandomAlph(6);
				String user = bot.getUserinfo().getUsername();
				String editSum = getRandomAlph(6);
				Article a = new Article(bot, title);
				a.setText(getRandom(42));
				a.setMinorEdit(false);
				
				Date saveDate = new Date(System.currentTimeMillis());
				a.save(editSum); // save article a
				String revIdA = a.getRevisionId();
				
				Article b = new Article(bot, title);
				assertEquals(a.getTitle(), b.getTitle());
				assertEquals(a.getText(), b.getText());
				assertEquals(a.isMinorEdit(), b.isMinorEdit()); // because false is default value
				assertEquals(user, b.getEditor());
				assertEquals(editSum, b.getEditSummary());
				assertEquals(saveDate.getTime(), b.getEditTimestamp().getTime(), 5000); // max. 5 seconds delta
				
				
				
				a.setMinorEdit(true);
				a.save(); // do nothing because no content change
				String revIdAp = a.getRevisionId();
				assertEquals("no change " + bot.getWikiType(), revIdA, revIdAp);
				a.addText(getRandom(48));
				a.save();
				String revIdApp = a.getRevisionId();
				assertNotSame("change expected " + bot.getWikiType(), revIdA, revIdApp);
				assertEquals("minor edit @ " + bot.getWikiType(), a.isMinorEdit(), b.isMinorEdit());
				
				try {
					a.delete(); // clean up
				} catch (VersionException e) {
					e.printStackTrace();
				}
					

			

		}
		
	}
		
	
}
