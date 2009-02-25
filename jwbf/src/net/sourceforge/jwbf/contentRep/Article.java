package net.sourceforge.jwbf.contentRep;

import net.sourceforge.jwbf.actions.util.ActionException;
import net.sourceforge.jwbf.actions.util.ProcessException;
import net.sourceforge.jwbf.bots.MediaWikiBot;
import net.sourceforge.jwbf.bots.util.JwbfException;

public class Article extends SimpleArticle {

	private final MediaWikiBot bot;

	
	@Override
	public String getText() {
		if (super.getText().length() < 1) {
			try {
				bot.readContent(super.getLabel());
			} catch (JwbfException e) {
				e.printStackTrace();
			}
		}
		return super.getText();
	}

	public Article(String label, MediaWikiBot bot) {
		this.bot = bot;
		setLabel(label);
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
