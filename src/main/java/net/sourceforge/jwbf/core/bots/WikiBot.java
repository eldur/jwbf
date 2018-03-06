package net.sourceforge.jwbf.core.bots;

import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.core.contentRep.Userinfo;

/**
 * Main interface for all kinds of wikibots.
 *
 * @author Thomas Stock
 */
public interface WikiBot {

  /** @deprecated use {@link #readData(String)} */
  @Deprecated
  SimpleArticle readData(final String name, final int properties);

  SimpleArticle readData(final String name);

  void writeContent(SimpleArticle sa);

  void delete(String title);

  void login(String user, String passwd);

  Userinfo getUserinfo();

  String getWikiType();
}
