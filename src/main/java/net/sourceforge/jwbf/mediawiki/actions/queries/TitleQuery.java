package net.sourceforge.jwbf.mediawiki.actions.queries;

import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

/**
 * @deprecated use {@code BaseQuery} instead.
 */
@Deprecated
public abstract class TitleQuery<T> extends BaseQuery<T> {
  protected TitleQuery(MediaWikiBot bot) {
    super(bot);
  }
}
