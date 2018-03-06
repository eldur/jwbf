package net.sourceforge.jwbf.core.contentRep;

import java.util.Date;

/** @author Thomas Stock */
public interface ArticleMeta extends ContentAccessable {
  /**
   * TODO method is untested and MediaWiki special.
   *
   * @return true if is
   * @deprecated actual the is no possibility to check this, because it depends on localization.
   *     e.g. #REDIRECT or #OHJAUS or #WEITERLEITUNG or #REDIRECTION
   */
  @Deprecated
  boolean isRedirect();

  /** @return the */
  Date getEditTimestamp();

  /** @return the */
  String getRevisionId();
}
