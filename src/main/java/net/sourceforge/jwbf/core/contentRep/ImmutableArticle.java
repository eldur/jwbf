package net.sourceforge.jwbf.core.contentRep;

import java.util.Date;

import com.google.common.annotations.Beta;

@Beta
public class ImmutableArticle implements ArticleMeta, ContentAccessable {

  private final boolean redirect;
  private final long editTimestamp;
  private final String revisionId;
  private final String editSummary;
  private final String editor;
  private final boolean minorEdit;
  private final String title;
  private final String text;

  @Override
  public boolean isRedirect() {
    return redirect;
  }

  @Override
  public Date getEditTimestamp() {
    return new Date(editTimestamp);
  }

  @Override
  public String getRevisionId() {
    return revisionId;
  }

  @Override
  public String getEditSummary() {
    return editSummary;
  }

  @Override
  public String getEditor() {
    return editor;
  }

  @Override
  public boolean isMinorEdit() {
    return minorEdit;
  }

  @Override
  public String getTitle() {
    return title;
  }

  @Override
  public String getText() {
    return text;
  }

  private ImmutableArticle(Builder builder) {
    this.editor = builder.editor;
    this.editSummary = builder.editSummary;
    this.editTimestamp = builder.editTimestamp;
    this.minorEdit = builder.minorEdit;
    this.redirect = builder.redirect;
    this.revisionId = builder.revisionId;
    this.text = builder.text;
    this.title = builder.title;
  }

  public static ImmutableArticle copyOf(Article in) {
    return copyOf(in.getSimpleArticle());
  }

  public static ImmutableArticle copyOf(SimpleArticle in) {
    return new Builder() //
        .withEditor(in.getEditor()) //
        .withEditorSummary(in.getEditSummary()) //
        .withRevisionId(in.getRevisionId()) //
        .withText(in.getText()) //
        .withTitle(in.getTitle()) //
        .withEditTimestamp(in.getEditTimestamp()) //
        .withMinorEdit(in.isMinorEdit()) //
        .withRedirect(in.isRedirect()) //
        .build();
  }

  private static class Builder {

    private String editSummary;
    private String editor;
    private boolean redirect;
    private long editTimestamp;
    private String revisionId;
    private boolean minorEdit;
    private String title;
    private String text;

    public ImmutableArticle build() {
      return new ImmutableArticle(this);
    }

    public Builder withEditor(String editor) {
      this.editor = editor;
      return this;
    }

    public Builder withEditorSummary(String editSummary) {
      this.editSummary = editSummary;
      return this;
    }

    public Builder withRevisionId(String revisionId) {
      this.revisionId = revisionId;
      return this;
    }

    public Builder withText(String text) {
      this.text = text;
      return this;
    }

    public Builder withTitle(String title) {
      this.title = title;
      return this;
    }

    public Builder withEditTimestamp(Date editTimestamp) {
      this.editTimestamp = editTimestamp.getTime();
      return this;
    }

    public Builder withMinorEdit(boolean minorEdit) {
      this.minorEdit = minorEdit;
      return this;
    }

    public Builder withRedirect(boolean redirect) {
      this.redirect = redirect;
      return this;
    }
  }
}
