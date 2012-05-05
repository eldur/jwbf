package net.sourceforge.jwbf.core.contentRep;

import java.util.Date;

import net.sourceforge.jwbf.core.bots.WikiBot;
import net.sourceforge.jwbf.core.bots.util.JwbfException;

/**
 * 
 * @author Thomas Stock
 * 
 */
public class Article implements ArticleMeta, ContentSetable {

  private final WikiBot bot;

  private final SimpleArticle sa;

  private int reload = 0;
  private static final int TEXT_RELOAD = 1 << 1;
  private static final int REVISION_ID_RELOAD = 1 << 2;
  private static final int MINOR_EDIT_RELOAD = 1 << 3;
  private static final int EDITOR_RELOAD = 1 << 4;
  private static final int EDIT_SUM_RELOAD = 1 << 5;
  private static final int EDIT_DATE_RELOAD = 1 << 6;

  private boolean isReload(final int reloadVar) {
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
    if (isReload(TEXT_RELOAD)) {
      setReload(TEXT_RELOAD);
      try {
        setText(bot.readData(sa.getTitle()).getText());
      } catch (JwbfException e) {
        throw new RuntimeException(e); // XXX check
      }
    }
    return sa.getText();
  }

  /**
   * {@inheritDoc}
   */
  public void setText(String text) {
    setReload(TEXT_RELOAD);
    sa.setText(text);
  }

  /**
   * {@inheritDoc}
   */
  public String getRevisionId() {

    if (isReload(REVISION_ID_RELOAD)) {
      setReload(REVISION_ID_RELOAD);
      try {
        sa.setRevisionId(bot.readData(sa.getTitle()).getRevisionId());
      } catch (JwbfException e) {
        throw new RuntimeException(e); // XXX check
      }
    }
    return sa.getRevisionId();
  }

  public String getEditor() {
    if (isReload(EDITOR_RELOAD)) {
      setReload(EDITOR_RELOAD);
      try {
        setEditor(bot.readData(sa.getTitle()).getEditor());
      } catch (JwbfException e) {
        throw new RuntimeException(e); // XXX check
      }
    }
    return sa.getEditor();
  }

  public void setEditor(String editor) {
    setReload(EDITOR_RELOAD);
    sa.setEditor(editor);
  }

  public String getEditSummary() {
    if (isReload(EDIT_SUM_RELOAD)) {
      setReload(EDIT_SUM_RELOAD);
      try {
        setEditSummary(bot.readData(sa.getTitle()).getEditSummary());
      } catch (JwbfException e) {
        throw new RuntimeException(e); // XXX check
      }
    }

    return sa.getEditSummary();
  }

  public void setEditSummary(String s) {
    setReload(EDIT_SUM_RELOAD);
    sa.setEditSummary(s);
  }

  public boolean isMinorEdit() {
    if (isReload(MINOR_EDIT_RELOAD)) {
      setReload(MINOR_EDIT_RELOAD);
      try {
        setMinorEdit(bot.readData(sa.getTitle()).isMinorEdit());
      } catch (JwbfException e) {
        throw new RuntimeException(e); // XXX check
      }
    }
    return sa.isMinorEdit();
  }

  /**
   * 
   * @param bot
   *          the
   * @param title
   *          of
   */
  public Article(WikiBot bot, String title) {
    this.bot = bot;
    sa = new SimpleArticle(title);
  }

  /**
   * 
   * @param bot
   *          the
   * @param sa
   *          the
   */
  public Article(WikiBot bot, SimpleArticle sa) {
    this.sa = sa;
    this.bot = bot;
  }

  /**
   * 
   * @param bot
   *          the
   * @param text
   *          the
   * @param label
   *          the
   * @deprecated use {@link #Article(String)} and {@link #setText(String)}
   *             instead.
   */
  @Deprecated
  public Article(WikiBot bot, String text, String title) {
    sa = new SimpleArticle(text, title);
    this.bot = bot;
  }

  /**
   * Save this article.
   */
  public void save() {
    bot.writeContent(sa);
    unSetReload(REVISION_ID_RELOAD);
    setReload(TEXT_RELOAD);
  }

  /**
   * Saves with a given comment.
   * 
   * @param summary
   *          the
   */
  public void save(String summary) {
    setEditSummary(summary);
    save();
  }

  /**
   * clear content.
   * 
   */
  public void clear() {
    setText("");
    save();
  }

  /**
   * Deletes this article, if the user has the required rights.
   * 
   */
  public void delete() {
    bot.delete(sa.getTitle());
  }

  /**
   * @deprecated do not use this TODO why?
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
    if (isReload(EDIT_DATE_RELOAD)) {
      setReload(EDIT_DATE_RELOAD);
      try {
        sa.setEditTimestamp(bot.readData(sa.getTitle()).getEditTimestamp());
      } catch (JwbfException e) {
        throw new RuntimeException(e); // XXX check
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
    setText(getText() + text);

  }

  /**
   * {@inheritDoc}
   */
  public void addTextnl(String text) {
    setText(getText() + "\n" + text);

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
