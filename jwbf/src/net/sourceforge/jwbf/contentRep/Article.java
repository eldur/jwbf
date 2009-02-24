package net.sourceforge.jwbf.contentRep;

import net.sourceforge.jwbf.actions.util.ActionException;
import net.sourceforge.jwbf.actions.util.ProcessException;
import net.sourceforge.jwbf.bots.MediaWikiBot;

public class Article extends SimpleArticle {

	private final MediaWikiBot bot;
	
	public Article(MediaWikiBot bot) {
		this.bot = bot;
	}

	public Article(SimpleArticle ca, MediaWikiBot bot) {
		super(ca);
		this.bot = bot;
	}

	public Article(String text, String label, MediaWikiBot bot) {
		super(text, label);
		this.bot = bot;
	}
	
	public void save() throws ActionException, ProcessException {
		bot.writeContent(this);
	}
	

}
