package net.sourceforge.jwbf.core.contentRep;

import java.util.Date;

import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.actions.util.ProcessException;
import net.sourceforge.jwbf.core.bots.WikiBot;
import net.sourceforge.jwbf.core.bots.util.JwbfException;
/**
 *
 * @author Thomas Stock
 *
 */
public class Article implements ArticleMeta, ContentSetable {
	/**
	 *
	 */
	private static final long serialVersionUID = 5892823865821665643L;

	private final WikiBot bot;

	private final SimpleArticle sa;

	private int reload = 0;
	private static final int textReload = 1 << 1;
	private static final int revisionIdReload = 1 << 2;
	private static final int minorEditReload = 1 << 3;
	private static final int editorReload = 1 << 4;
	private static final int editSumReload = 1 << 5;
	private static final int editDateReload = 1 << 6;

	private boolean isReload(final int reloadVar) {
		if (bot.hasCacheHandler()) {
			return true;
		}
		return (reload & reloadVar) == 0;
	}

	private void setReload(final int reloadVar) {
		reload = reload | reloadVar;
	}

	private void unSetReload(final int reloadVar) {
		reload = (reload | reloadVar) ^ reloadVar;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getText() {
		if (isReload(textReload)) {
			setReload(textReload);
			try {
				setText(bot.readData(sa.getTitle()).getText());
			} catch (JwbfException e) {
				e.printStackTrace();
			}
		}
		return sa.getText();
	}
	/**
	 * {@inheritDoc}
	 */
	public void setText(String text) {
		setReload(textReload);
		sa.setText(text);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getRevisionId() {

		if (isReload(revisionIdReload)) {
			setReload(revisionIdReload);
			try {
				sa.setRevisionId(bot.readData(sa.getTitle()).getRevisionId());
			} catch (JwbfException e) {
				e.printStackTrace();
			}
		}
		return sa.getRevisionId();
	}




	public String getEditor() {
		if (isReload(editorReload)) {
			setReload(editorReload);
			try {
				setEditor(bot.readData(sa.getTitle()).getEditor());
			} catch (JwbfException e) {
				e.printStackTrace();
			}
		}
		return sa.getEditor();
	}

	public void setEditor(String editor) {
		setReload(editorReload);
		sa.setEditor(editor);
	}



	public String getEditSummary() {
		if (isReload(editSumReload)) {
			setReload(editSumReload);
			try {
				setEditSummary(bot.readData(sa.getTitle()).getEditSummary());
			} catch (JwbfException e) {
				e.printStackTrace();
			}
		}

		return sa.getEditSummary();
	}

	public void setEditSummary(String s) {
		setReload(editSumReload);
		sa.setEditSummary(s);
	}


	public boolean isMinorEdit() {
		if (isReload(minorEditReload)) {
			setReload(minorEditReload);
			try {
				setMinorEdit(bot.readData(sa.getTitle()).isMinorEdit());
			} catch (JwbfException e) {
				e.printStackTrace();
			}
		}
		return sa.isMinorEdit();
	}
	/**
	 *
	 * @param bot the
	 * @param title of
	 */
	public Article(WikiBot bot, String title) {
		this.bot = bot;
		sa = new SimpleArticle(title);
	}
	/**
	 *
	 * @param bot the
	 * @param sa the
	 */
	public Article(WikiBot bot, SimpleArticle sa) {
		this.sa = sa;
		this.bot = bot;
	}
	/**
	 *
	 * @param bot the
	 * @param text the
	 * @param label the
	 * @deprecated use {@link #Article(String)} and {@link #setText(String)} instead.
	 */
	@Deprecated
  public Article(WikiBot bot, String text, String title) {
		sa = new SimpleArticle(text, title);
		this.bot = bot;
	}
	/**
	 * Save this article.
	 * @throws ActionException a
	 * @throws ProcessException a
	 */
	public void save() throws ActionException, ProcessException {
		bot.writeContent(sa);
		if (bot.hasCacheHandler()) {
			reload = 0;
		}
		unSetReload(revisionIdReload);
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
		bot.postDelete(sa.getTitle());
	}
	/**
	 * @deprecated do not use this
	 * @return true if
	 */
	@Deprecated
  public boolean isEmpty() {
		return getText().length() < 1;
	}
	/**
	 * @deprecated do not use this
	 * @return the
	 */
	@Deprecated
  public WikiBot getBot() {
		return bot;
	}
	/**
	 * {@inheritDoc}
	 */
	public String getTitle() {
		// TODO is here a reload mechanism required ?
		return sa.getTitle();
	}
	/**
	 * @return the edittimestamp in UTC
	 */
	public Date getEditTimestamp() {
		if (isReload(editDateReload)) {
			setReload(editDateReload);
			try {
				sa.setEditTimestamp(bot.readData(sa.getTitle()).getEditTimestamp());
			} catch (JwbfException e) {
				e.printStackTrace();
			}
		}
		return sa.getEditTimestamp();
	}
	/**
	 * {@inheritDoc}
	 */
	public boolean isRedirect() {
		return sa.isRedirect();
	}
	/**
	 * {@inheritDoc}
	 */
	public void addText(String text) {
		sa.addText(text);

	}
	/**
	 * {@inheritDoc}
	 */
	public void addTextnl(String text) {
		sa.addTextnl(text);

	}
	/**
	 * {@inheritDoc}
	 */
	public void setMinorEdit(boolean minor) {
		sa.setMinorEdit(minor);

	}
	/**
	 * {@inheritDoc}
	 */
	public void setTitle(String title) {
		sa.setTitle(title);

	}

	public SimpleArticle getSimpleArticle() {
		return sa;
	}


}
