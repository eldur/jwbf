package net.sourceforge.jwbf.mediawiki.actions.queries;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.common.base.MoreObjects;

import net.sourceforge.jwbf.mediawiki.actions.queries.WatchList.EditType;

/**
 * A class that holds all the properties that could be returned when doing a watchlist query.
 *
 * @author Rabah Meradi
 */
@JsonNaming(PropertyNamingStrategy.LowerCaseStrategy.class)
public class WatchResponse {
  private final int ns;
  private final String title;
  private final String user;
  private final String comment;
  private final String parsedComment;
  private final Date timestamp;
  private final Date notificationTimestamp;
  private final int pageid;
  private final int revid;
  private final int oldRevid;
  private final int oldLen;
  private final int newLen;
  private final boolean patrolled;
  private final EditType type;
  private final boolean anon;
  private final boolean minor;
  private final boolean bot;
  private final boolean newFlag;

  @SuppressWarnings("checkstyle:parameternumber")
  public WatchResponse(
      @JsonProperty("ns") int ns,
      @JsonProperty("title") String title,
      @JsonProperty("user") String user,
      @JsonProperty("comment") String comment,
      @JsonProperty("parsedcomment") String parsedComment,
      @JsonProperty("timestamp") //
          @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ss'Z'") //
          Date timestamp,
      @JsonProperty("notificationtimestamp") //
          @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ss'Z'") //
          Date notificationTimestamp,
      @JsonProperty("pageid") int pageid,
      @JsonProperty("revid") int revid,
      @JsonProperty("old_revid") int oldRevid,
      @JsonProperty("oldlen") int oldLen,
      @JsonProperty("newlen") int newLen,
      @JsonProperty("patrolled") boolean patrolled,
      @JsonProperty("type") String type,
      @JsonProperty("anon") String anon,
      @JsonProperty("minor") String minor,
      @JsonProperty("bot") String bot,
      @JsonProperty("new") String newFlag) {
    this.ns = ns;
    this.title = title;
    this.user = user;
    this.comment = comment;
    this.parsedComment = parsedComment;
    this.timestamp = timestamp;
    this.notificationTimestamp = notificationTimestamp;
    this.pageid = pageid;
    this.revid = revid;
    this.oldRevid = oldRevid;
    this.oldLen = oldLen;
    this.newLen = newLen;
    this.patrolled = patrolled;
    this.type = getEditType(type);
    this.anon = anon != null;
    this.minor = minor != null;
    this.bot = bot != null;
    this.newFlag = newFlag != null;
  }

  /**
   * Create a EditType from the name type used by MW
   *
   * @param typeName the name returned by MW
   * @return the editType
   */
  private EditType getEditType(String typeName) {
    for (EditType type : EditType.values()) {
      if (type.toString().equals(typeName)) {
        return type;
      }
    }
    return null;
  }

  /**
   * The time and date of the change
   *
   * @return time and date of the change
   */
  public Date getTimestamp() {
    return timestamp;
  }

  /**
   * The namespace of the page
   *
   * @return namespace
   */
  public int getNamespace() {
    return ns;
  }

  /**
   * Get the title of the page
   *
   * @return title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Get user who made the change
   *
   * @return the user
   */
  public String getUser() {
    return user;
  }

  /**
   * The edit/log comment
   *
   * @return edit/log comment
   */
  public String getComment() {
    return comment;
  }

  /**
   * The edit/log comment in HTML format with wikilinks and section references expanded into
   * hyperlinks
   *
   * @return the parsed comment
   */
  public String getParsedComment() {
    return parsedComment;
  }

  /**
   * Get the timestamp of when the user was last notified about the edit
   *
   * @return the notification timestamp
   */
  public Date getNotificationTimestamp() {
    return notificationTimestamp;
  }

  /**
   * Get the page ID
   *
   * @return the page ID
   */
  public int getPageid() {
    return pageid;
  }

  /**
   * Get the page revision ID
   *
   * @return the revision ID
   */
  public int getRevid() {
    return revid;
  }

  /**
   * Get the revision ID of the page before the change
   *
   * @return the old revision ID
   */
  public int getOldRevid() {
    return oldRevid;
  }

  /**
   * Get the page size before the change
   *
   * @return the old size
   */
  public int getOldLen() {
    return oldLen;
  }

  /**
   * Get the page size after the change
   *
   * @return the new size
   */
  public int getNewLen() {
    return newLen;
  }

  /**
   * Whether the change is patrolled.
   *
   * @return true if the change was patrolled, false otherwise
   */
  public boolean isPatrolled() {
    return patrolled;
  }

  /**
   * Return the type of this Edit
   *
   * @return the type of the edit
   */
  public EditType getType() {
    return type;
  }

  /**
   * Return whatever the change was made by anonymous user or not
   *
   * @return true if the change was made by anonymous user, false otherwise
   */
  public boolean isAnonymousEdit() {
    return anon;
  }

  /**
   * Return whatever the change was marked as minor or not
   *
   * @return true if the change was marked as minor, false otherwise
   */
  public boolean isMinorEdit() {
    return minor;
  }

  /**
   * Return whatever the change was made by a bot or not
   *
   * @return true if the change was made by a bot, false otherwise
   */
  public boolean isBotEdit() {
    return bot;
  }

  /**
   * Return whatever the change is a page creation or not
   *
   * @return true if it's a page creation, false otherwise
   */
  public boolean isNewPage() {
    return newFlag;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this) //
        .add("ns", ns) //
        .add("title", title) //
        .add("user", user) //
        .add("comment", comment) //
        .add("parsedComment", parsedComment) //
        .add("timestamp", timestamp) //
        .add("notificationTimestamp", notificationTimestamp) //
        .add("pageid", pageid) //
        .add("revid", revid) //
        .add("oldRevid", oldRevid) //
        .add("oldLen", oldLen) //
        .add("newLen", newLen) //
        .add("patrolled", patrolled) //
        .add("type", type) //
        .add("anon", anon) //
        .add("minor", minor) //
        .add("bot", bot) //
        .add("newFlag", newFlag) //
        .toString();
  }

  // TODO implement hashCode and equals like SimpleArticle#hashCode(),
  // SimpleArticle#equals()
}
