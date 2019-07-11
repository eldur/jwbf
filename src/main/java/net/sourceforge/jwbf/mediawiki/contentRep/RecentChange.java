package net.sourceforge.jwbf.mediawiki.contentRep;

import java.util.Date;
import java.util.Objects;

/**
 * Represents an item that was recently changed in a wiki.
 */
public class RecentChange {
  public enum ChangeType {

    EDIT("edit"), // Regular page edits
    EXTERNAL("external"), // External edits
    NEW("new"), // Page creations (Uploads are not listed as new but as log)
    LOG("log"), // Log entries
    CATEGORIZE("categorize"); // Page categorizations

    private final String changeType;

    ChangeType(final String changeType) {
      this.changeType = changeType;
    }

    public String getChangeType() {
      return changeType;
    }

    public static ChangeType parse(final String stringValue) {
      for (final ChangeType type : values()) {
        if (type.getChangeType().equals(stringValue)) {
          return type;
        }
      }
      return null;
    }
  }

  private final ChangeType type;
  private final int namespace;
  private final String title;
  private final long pageId;
  private final long revisionId;
  private final long oldRevisionId;
  private final long rcId;
  private final String user;
  private final long userId;
  private final long oldLength;
  private final long newLength;
  private final Date timestamp;
  private final String comment;

  public RecentChange(ChangeType type, int namespace, String title, long pageId, long revisionId,
                      long oldRevisionId, long rcId, String user, long userId, long oldLength,
                      long newLength, Date timestamp, String comment) {
    this.type = type;
    this.namespace = namespace;
    this.title = title;
    this.pageId = pageId;
    this.revisionId = revisionId;
    this.oldRevisionId = oldRevisionId;
    this.rcId = rcId;
    this.user = user;
    this.userId = userId;
    this.oldLength = oldLength;
    this.newLength = newLength;
    this.timestamp = timestamp;
    this.comment = comment;
  }

  public ChangeType getType() {
    return type;
  }

  public int getNamespace() {
    return namespace;
  }

  public String getTitle() {
    return title;
  }

  public long getPageId() {
    return pageId;
  }

  public long getRevisionId() {
    return revisionId;
  }

  public long getOldRevisionId() {
    return oldRevisionId;
  }

  public long getRcId() {
    return rcId;
  }

  public String getUser() {
    return user;
  }

  public long getUserId() {
    return userId;
  }

  public long getOldLength() {
    return oldLength;
  }

  public long getNewLength() {
    return newLength;
  }

  public Date getTimestamp() {
    return timestamp;
  }

  public String getComment() {
    return comment;
  }

  @Override
  public String toString() {
    return "RecentChange{" +
        "type=" + type +
        ", namespace=" + namespace +
        ", title='" + title + '\'' +
        ", pageId=" + pageId +
        ", revisionId=" + revisionId +
        ", oldRevisionId=" + oldRevisionId +
        ", rcId=" + rcId +
        ", user='" + user + '\'' +
        ", userId=" + userId +
        ", oldLength=" + oldLength +
        ", newLength=" + newLength +
        ", timestamp=" + timestamp +
        ", comment='" + comment + '\'' +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final RecentChange that = (RecentChange) o;
    return namespace == that.namespace &&
        pageId == that.pageId &&
        revisionId == that.revisionId &&
        oldRevisionId == that.oldRevisionId &&
        rcId == that.rcId &&
        userId == that.userId &&
        oldLength == that.oldLength &&
        newLength == that.newLength &&
        type == that.type &&
        Objects.equals(title, that.title) &&
        Objects.equals(user, that.user) &&
        Objects.equals(timestamp, that.timestamp) &&
        Objects.equals(comment, that.comment);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, namespace, title, pageId, revisionId, oldRevisionId,
        rcId, user, userId, oldLength, newLength, timestamp, comment);
  }

  public static Builder builder() {
    return new Builder();
  }


  public static final class Builder {
    private ChangeType type;
    private int namespace;
    private String title;
    private long pageId;
    private long revisionId;
    private long oldRevisionId;
    private long rcId;
    private String user;
    private long userId;
    private long oldLength;
    private long newLength;
    private Date timestamp;
    private String comment;

    private Builder() {
    }

    public Builder type(ChangeType type) {
      this.type = type;
      return this;
    }

    public Builder namespace(int namespace) {
      this.namespace = namespace;
      return this;
    }

    public Builder title(String title) {
      this.title = title;
      return this;
    }

    public Builder pageId(long pageId) {
      this.pageId = pageId;
      return this;
    }

    public Builder revisionId(long revisionId) {
      this.revisionId = revisionId;
      return this;
    }

    public Builder oldRevisionId(long oldRevisionId) {
      this.oldRevisionId = oldRevisionId;
      return this;
    }

    public Builder rcId(long rcId) {
      this.rcId = rcId;
      return this;
    }

    public Builder user(String user) {
      this.user = user;
      return this;
    }

    public Builder userId(long userId) {
      this.userId = userId;
      return this;
    }

    public Builder oldLength(long oldLength) {
      this.oldLength = oldLength;
      return this;
    }

    public Builder newLength(long newLength) {
      this.newLength = newLength;
      return this;
    }

    public Builder timestamp(Date timestamp) {
      this.timestamp = timestamp;
      return this;
    }

    public Builder comment(String comment) {
      this.comment = comment;
      return this;
    }

    public RecentChange build() {
      return new RecentChange(type, namespace, title, pageId, revisionId, oldRevisionId,
          rcId, user, userId, oldLength, newLength, timestamp, comment);
    }
  }
}
