package net.sourceforge.jwbf.contentRep;

import java.util.Date;

import net.sourceforge.jwbf.actions.util.ActionException;
import net.sourceforge.jwbf.actions.util.ProcessException;
import net.sourceforge.jwbf.bots.WikiBot;
import net.sourceforge.jwbf.bots.util.JwbfException;
/**
 * 
 * @author Thomas Stock
 *
 */
public class Article extends SimpleArticle {




	/**
	 * 
	 */
	private static final long serialVersionUID = 5892823865821665643L;
	private final WikiBot bot;
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getText() {
		if (super.getText().length() < 1) {
			try {
				setText(bot.readData(super.getTitle()).getText());
			} catch (JwbfException e) {
				e.printStackTrace();
			}
		}
		return super.getText();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getRevisionId() {
		if (super.getRevisionId().length() < 1) {
			try {
				setRevisionId(bot.readData(super.getTitle()).getRevisionId());
			} catch (JwbfException e) {
				e.printStackTrace();
			}
		}
		return super.getRevisionId();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEditor() {
		if (super.getEditor().length() < 1) {
			try {
				setEditor(bot.readData(super.getTitle()).getEditor());
			} catch (JwbfException e) {
				e.printStackTrace();
			}
		}
		return super.getEditor();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEditSummary() {
		if (super.getEditSummary().length() < 1) {
			try {
				setEditSummary(bot.readData(super.getTitle()).getEditSummary());
			} catch (JwbfException e) {
				e.printStackTrace();
			}
		}

		return super.getEditSummary();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Date getEditTimestamp() {
		return super.getEditTimestamp();
	}

	public Article(WikiBot bot, String title) {
		this.bot = bot;
		setTitle(title);
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
	
	public WikiBot getBot() {
		return bot;
	}


}
