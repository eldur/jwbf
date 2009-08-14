package net.sourceforge.jwbf.core.contentRep;

import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.actions.util.ProcessException;
import net.sourceforge.jwbf.core.bots.WikiBot;
import net.sourceforge.jwbf.core.bots.util.JwbfException;
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
	
	private int reload = 0;
	private final int textReload = 1 << 1;
	private final int revisionIdReload = 1 << 2;
	private final int minorEditReload = 1 << 3;
	private final int editorReload = 1 << 4;
	private final int editSumReload = 1 << 5;
	
	private boolean isReload(final int reloadVar) {
		return (reload & reloadVar) == 0;
	}
	
	private void setReload(final int reloadVar) {
		reload = reload | reloadVar;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getText() {
		if (isReload(textReload)) {
			setReload(textReload);
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
	public void setText(String text) {
		setReload(textReload);
		super.setText(text);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getRevisionId() {
		
		if (isReload(revisionIdReload)) {
			setReload(revisionIdReload);
			try {
				setRevisionId(bot.readData(super.getTitle()).getRevisionId());
				// TODO does a revision reload test exists ?
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
	public void setRevisionId(String revId) {
		setReload(revisionIdReload);
		super.setRevisionId(revId);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEditor() {
		if (isReload(editorReload)) {
			setReload(editorReload);
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
	public void setEditor(String editor) {
		setReload(editorReload);
		super.setEditor(editor);
	}
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEditSummary() {
		if (isReload(editSumReload)) {
			setReload(editSumReload);
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
	public void setEditSummary(String s) {
		setReload(editSumReload);
		super.setEditSummary(s);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isMinorEdit() {
		if (isReload(editSumReload)) {
			setReload(minorEditReload);
			try {
				setEditSummary(bot.readData(super.getTitle()).getEditSummary());
			} catch (JwbfException e) {
				e.printStackTrace();
			}
		}
		return super.isMinorEdit();
	}
	/**
	 * 
	 * @param bot the
	 * @param title of
	 */
	public Article(WikiBot bot, String title) {
		this.bot = bot;
		setTitle(title);
	}
	/**
	 * 
	 * @param bot the
	 * @param ca the
	 */
	public Article(WikiBot bot, SimpleArticle ca) {
		super(ca);
		this.bot = bot;
	}
	/**
	 * 
	 * @param bot the
	 * @param text the
	 * @param label the
	 * @deprecated use {@link #Article(String)} and {@link #setText(String)} instead.
	 */
	public Article(WikiBot bot, String text, String label) {
		super(text, label);
		this.bot = bot;
	}
	/**
	 * Save this article.
	 * @throws ActionException a
	 * @throws ProcessException a
	 */
	public void save() throws ActionException, ProcessException {
		try {
			bot.writeContent((SimpleArticle) this.clone());
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		reload = 0; //reload ^ revisionIdReload; // TODO Reload only if revId is changed
	}
	/**
	 * Saves with a given comment.
	 * @param summary the
	 * @throws ActionException a
	 * @throws ProcessException a
	 */
	public void save(String summary) throws ActionException, ProcessException {
		setEditSummary(summary);
		save();
	}
	/**
	 * clear content.
	 * @throws ActionException a
	 * @throws ProcessException a
	 */
	public void clear() throws ActionException, ProcessException {
		setText("");
		save();
	}
	/**
	 * Deletes this article, if the user has the required rights.
	 * @throws ActionException a
	 * @throws ProcessException a
	 */
	public void delete() throws ActionException, ProcessException {
		bot.postDelete(getTitle());
	}
	/**
	 * @deprecated do not use this
	 * @return true if
	 */
	public boolean isEmpty() {
		return getText().length() < 1;
	}
	/**
	 * @deprecated do not use this
	 * @return the
	 */
	public WikiBot getBot() {
		return bot;
	}
	

}
