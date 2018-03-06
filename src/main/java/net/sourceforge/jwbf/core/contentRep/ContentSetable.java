package net.sourceforge.jwbf.core.contentRep;

public interface ContentSetable {

  /** @param s the */
  void setEditSummary(final String s);

  /** @param minor the */
  void setMinorEdit(final boolean minor);

  /** @param title the label, like "Main Page" */
  void setTitle(final String title);

  /** @param text the content of the article */
  void setText(final String text);

  /** @param text to add to content of the article */
  void addText(final String text);

  /** @param text to add to content of the article */
  void addTextnl(final String text);

  /** @param editor the */
  void setEditor(final String editor);
}
