package net.sourceforge.jwbf.mediawiki.contentRep;

public class LogItem {
  private final String title;
  private final String type;
  private final String user;

  public LogItem(String title, String type, String user) {
    this.title = title;
    this.type = type;
    this.user = user;
  }

  public String getTitle() {
    return title;
  }

  public String getType() {
    return type;
  }

  public String getUser() {
    return user;
  }

  @Override
  public String toString() {
    return "* " + getTitle() + " was " + getType() + " by " + getUser();
  }
}
