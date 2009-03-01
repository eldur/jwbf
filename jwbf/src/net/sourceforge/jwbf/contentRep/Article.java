package net.sourceforge.jwbf.contentRep;

import net.sourceforge.jwbf.actions.util.ActionException;
import net.sourceforge.jwbf.actions.util.ProcessException;
import net.sourceforge.jwbf.bots.WikiBot;
import net.sourceforge.jwbf.bots.util.JwbfException;

public class Article extends SimpleArticle {

	private final WikiBot bot;

	
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

	public Article(WikiBot bot, String title) {
		this.bot = bot;
		setLabel(title);
	}

	public Article(WikiBot bot, SimpleArticle ca) {
		super(ca);
		this.bot = bot;
	}

	public Article(WikiBot bot, String text, String label) {
		super(text, label);
		this.bot = bot;
	}
	
	public void save() throws ActionException, ProcessException {
		bot.writeContent(this);
	}
	
	public void clear() throws ActionException, ProcessException {
		bot.writeContent(new SimpleArticle("", getLabel()));
	}
	
	public boolean isEmpty() {
		return getText().length() < 1;
	}
	


}
