package net.sourceforge.jwbf.core.contentRep;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.LowerCaseStrategy.class)
//@JsonInclude(Include.NON_DEFAULT)
public class WatchResponse {
	private int ns;
	private String title;
	private String user;
	@JsonProperty("comment")
	private String comment;
	@JsonProperty("parsedcomment")
	private String parsedComment;
	private Date timestamp;
	private Date notificationTimestamp;
	@JsonProperty("pageid")
	private int pageid;
	@JsonProperty("revid")
	private int revid;
	@JsonProperty("old_revid")
	private int oldRevid;
	@JsonProperty("oldlen")
	private int oldLen;
	@JsonProperty("newlen")
	private int newLen;
	@JsonProperty("patrolled")
	private boolean patrolled;
	private String type;
	@JsonProperty("anon")
	private String anon;
	public String getAnon() {
		return anon;
	}

	public void setAnon(String anon) {
		this.anon = anon;
	}

	public String getMinor() {
		return minor;
	}

	public void setMinor(String minor) {
		this.minor = minor;
	}

	public String getBot() {
		return bot;
	}

	public void setBot(String bot) {
		this.bot = bot;
	}

	public String getNewFlag() {
		return newFlag;
	}

	public void setNewFlag(String newFlag) {
		this.newFlag = newFlag;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@JsonProperty("minor")
	private String minor;
	@JsonProperty("bot")
	private String bot;
	@JsonProperty("new")
	private String newFlag;

	@JsonProperty("ns")
	public int getNs() {
		return ns;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	@JsonProperty("timestamp")
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'hh:mm:ss'Z'")
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public void setNs(int ns) {
		this.ns = ns;
	}

	@JsonProperty("user")
	public void setUser(String user) {
		this.user = user;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setParsedComment(String parsedComment) {
		this.parsedComment = parsedComment;
	}

	@JsonProperty("notificationtimestamp")
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'hh:mm:ss'Z'")
	public void setNotificationTimestamp(Date notificationTimestamp) {
		this.notificationTimestamp = notificationTimestamp;
	}

	public void setPageid(int pageid) {
		this.pageid = pageid;
	}

	public void setRevid(int revid) {
		this.revid = revid;
	}

	public void setOldRevid(int oldRevid) {
		this.oldRevid = oldRevid;
	}

	public void setOldLen(int oldLen) {
		this.oldLen = oldLen;
	}

	public void setNewLen(int newLen) {
		this.newLen = newLen;
	}

	public void setPatrolled(boolean patrolled) {
		this.patrolled = patrolled;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getNamespace() {
		return ns;
	}

	@JsonProperty("title")
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

	@JsonProperty("type")
	public String getType() {
		return type;
	}

	public String toString(){
		return "Title: " + title + " " + timestamp;
	}


}
