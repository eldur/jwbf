package net.sourceforge.jwbf.live;

import java.util.Collection;
import java.util.Vector;

import net.sourceforge.jwbf.bots.WikiBot;
import net.sourceforge.jwbf.contentRep.Article;

import org.junit.Test;

public abstract class GeneralArticleTest {
	protected static Collection<WikiBot> bots = new Vector<WikiBot>();
	
	/**
	 * 
	 * @throws Exception a
	 */
	@Test
	public final void articleReadText() throws Exception {
		
		for (WikiBot bot : bots) {
			Article a = new Article(bot, "value");
			a.getText();
		}
	}
}
