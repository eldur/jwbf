package net.sourceforge.jwbf.live;

import static org.junit.Assert.assertEquals;

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
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @throws Exception a
	 */
	@Test
	public final void readWriteDelete() throws Exception {
		
		for (WikiBot bot : bots) {

			for (int i = 0; i <= 2; i++) {
				String title = "z" + getRandomAlph(6);
				String user = bot.getUserinfo().getUsername();
				String editSum = getRandomAlph(6);
				Article a = new Article(bot, title);
				a.setText(getRandom(42));
				Date saveDate = new Date(System.currentTimeMillis());
				a.save(editSum);

				Article b = new Article(bot, title);
				assertEquals(a.getLabel(), b.getLabel());
				assertEquals(a.getText(), b.getText());
				assertEquals(user, b.getEditor());
				assertEquals(editSum, b.getEditSummary());
				assertEquals(saveDate.getTime(), b.getEditTimestamp().getTime(), 5000); // max. 5 seconds delta
				try {
					a.delete(); // clean up
				} catch (VersionException e) {
					e.printStackTrace();
				}
				
				

			}
			// TODO RESUME here

		}
		
	}
	
	
		
	
}
