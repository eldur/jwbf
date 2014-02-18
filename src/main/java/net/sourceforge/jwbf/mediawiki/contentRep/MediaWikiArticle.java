package net.sourceforge.jwbf.mediawiki.contentRep;

import net.sourceforge.jwbf.core.bots.WikiBot;
import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;

public class MediaWikiArticle extends Article {

  public MediaWikiArticle(WikiBot bot, SimpleArticle sa) {
    super(bot, sa);
  }

  public String getWikiDataEntity() {
    return "Q1055";
  }
}
