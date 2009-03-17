package net.sourceforge.jwbf.contentRep;

import java.util.Date;

import net.sourceforge.jwbf.actions.util.ActionException;
import net.sourceforge.jwbf.actions.util.ProcessException;
import net.sourceforge.jwbf.bots.WikiBot;
import net.sourceforge.jwbf.bots.util.JwbfException;

public class Article extends SimpleArticle {

	private final WikiBot bot;
	// TODO remove all media wiki package references
	private int properties = CONTENT
	| COMMENT | USER;
	
	
	@Override
	public String getText() {
		if (super.getText().length() < 1) {
			try {
				setText(bot.readData(super.getLabel(), CONTENT).getText());
			} catch (JwbfException e) {
				e.printStackTrace();
			}
		}
		return super.getText();
	}

	@Override
	public String getEditor() {
		if (super.getEditor().length() < 1) {
			try {
				setEditor(bot.readData(super.getLabel(), USER).getEditor());
			} catch (JwbfException e) {
				e.printStackTrace();
			}
		}
		return super.getEditor();
	}

	@Override
	public String getEditSummary() {
		if (super.getEditSummary().length() < 1) {
			try {
				setEditSummary(bot.readData(super.getLabel(), COMMENT).getEditSummary());
			} catch (JwbfException e) {
				e.printStackTrace();
			}
		}

		return super.getEditSummary();
	}

	@Override
	public Date getEditTimestamp() {
		// TODO Auto-generated method stub
		return super.getEditTimestamp();
	}

	public Article(WikiBot bot, String title) {
		this.bot = bot;
		setLabel(title);
	}
	public Article(WikiBot bot, String title, int properties) {
		this.bot = bot;
		this.properties = properties;
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
	public void save(String summary) throws ActionException, ProcessException {
		setEditSummary(summary);
		bot.writeContent(this);
		;
	}
	
	public void clear() throws ActionException, ProcessException {
		bot.writeContent(new SimpleArticle("", getLabel()));
	}
	
	public void delete() throws ActionException, ProcessException {
		bot.postDelete(getLabel());
	}
	public boolean isEmpty() {
		return getText().length() < 1;
	}
	


}
