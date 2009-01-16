package net.sourceforge.jwbf.contentRep.mw;

import net.sourceforge.jwbf.actions.mw.util.ActionException;
import net.sourceforge.jwbf.actions.mw.util.ProcessException;
import net.sourceforge.jwbf.bots.MediaWikiBotImpl;

public class Article extends SimpleArticle {

	private final MediaWikiBotImpl bot;
	
	public Article(MediaWikiBotImpl bot) {
		this.bot = bot;
	}

	public Article(ContentAccessable ca, MediaWikiBotImpl bot) {
		super(ca);
		this.bot = bot;
	}

	public Article(String text, String label, MediaWikiBotImpl bot) {
		super(text, label);
		this.bot = bot;
	}
	
	public void save() throws ActionException, ProcessException {
		bot.writeContent(this);
	}
	

}
