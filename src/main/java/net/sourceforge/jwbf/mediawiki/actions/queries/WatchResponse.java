package net.sourceforge.jwbf.mediawiki.actions.queries;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.LowerCaseStrategy.class)
public class WatchResponse {
	private final int  ns;
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
	private final String type;
	private final boolean anon;
	private final boolean minor;
	private final boolean bot;
	private final boolean newFlag;

	//TODO: anon, bot newFlag and minor
	public WatchResponse(
			@JsonProperty("ns") int ns,
			@JsonProperty("title") String title,
			@JsonProperty("user") String user,
			@JsonProperty("comment") String comment,
			@JsonProperty("parsedcomment") String parsedComment,
			@JsonProperty("timestamp") @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ss'Z'") Date timestamp,
			@JsonProperty("notificationtimestamp") @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ss'Z'") Date notificationTimestamp,
			@JsonProperty("pageid") int pageid,
			@JsonProperty("revid") int revid,
			@JsonProperty("old_revid") int oldRevid,
			@JsonProperty("oldlen") int oldLen,
			@JsonProperty("newlen") int newLen,
			@JsonProperty("patrolled") boolean patrolled,
			@JsonProperty("type") String type,
			@JsonProperty("anon") String anon,
			@JsonProperty("minor") String minor,
			@JsonProperty("bot") String bot, @JsonProperty("new") String newFlag) {
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
		this.type = type;
		this.anon = anon != null;
		this.minor = minor != null;
		this.bot = bot != null;
		this.newFlag = newFlag != null;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public int getNamespace() {
		return ns;
	}

	public String getTitle() {
		return title;
	}

	public String getUser() {
		return user;
	}

	public String getComment() {
		return comment;
	}

	public String getParsedComment() {
		return parsedComment;
	}

	public Date getNotificationTimestamp() {
		return notificationTimestamp;
	}

	public int getPageid() {
		return pageid;
	}

	public int getRevid() {
		return revid;
	}

	public int getOldRevid() {
		return oldRevid;
	}

	public int getOldLen() {
		return oldLen;
	}

	public int getNewLen() {
		return newLen;
	}

	public boolean isPatrolled() {
		return patrolled;
	}

	public String getType() {
		return type;
	}

	public boolean isAnonymousEdit() {
		return anon;
	}


	public boolean isMinorEdit() {
		return minor;
	}

	public boolean isBotEdit() {
		return bot;
	}

	public boolean isNewPage() {
		return newFlag;
	}

	// TODO: should we print all non null properties?
	public String toString() {
		return "Title: " + title + " " + timestamp;
	}

}
