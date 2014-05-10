package net.sourceforge.jwbf.mediawiki.actions.queries;

import java.util.Iterator;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jwbf.core.Optionals;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import com.google.common.annotations.Beta;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * Abstract class which is superclass of all titleiterations, represented by the sufix "Titles".
 * 
 * @author Thomas Stock
 * @param <T>
 *          of
 */
@Slf4j
abstract class TitleQuery<T> implements Iterable<T>, Iterator<T> {

  private Iterator<T> titleIterator;
  private final TitleQueryAction inner;
  private final MediaWikiBot bot;
  private ImmutableList<T> oldTitlesForLogging = ImmutableList.of();

  /** Information necessary to get the next api page. */
  private Optional<String> nextPageInfo = Optional.absent();

  protected final String setNextPageInfo(String nextPageInfo) {
    this.nextPageInfo = Optionals.absentIfEmpty(nextPageInfo);
    return nextPageInfo;
  }

  protected final String getNextPageInfo() {
    return nextPageInfoOpt().get();
  }

  protected final Optional<String> nextPageInfoOpt() {
    return nextPageInfo;
  }

  protected boolean hasNextPageInfo() {
    return nextPageInfo.isPresent();
  }

  protected TitleQuery(MediaWikiBot bot) {
    this.bot = bot;
    inner = getInnerAction();
  }

  protected TitleQueryAction getInnerAction() {
    return new TitleQueryAction();
  }

  @Beta
  public Iterable<T> lazy() {
    return this;
  }

  @Beta
  public ImmutableList<T> getCopyOf(int count) {
    return ImmutableList.copyOf(Iterables.limit(lazy(), count));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @SuppressWarnings("unchecked")
  public final Iterator<T> iterator() {
    try {
      return (Iterator<T>) clone();
    } catch (CloneNotSupportedException e) {
      log.error("cloning should be supported");
      e.printStackTrace();
      return null;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final boolean hasNext() {
    doCollection();
    return titleIterator.hasNext();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final T next() {
    doCollection();
    return titleIterator.next();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final void remove() {
    titleIterator.remove();
  }

  protected abstract HttpAction prepareCollection();

  private void doCollection() {

    if (inner.init || (!titleIterator.hasNext() && hasNextPageInfo())) {
      inner.init = false;
      inner.setHasMoreMessages(true);
      inner.msg = prepareCollection();
      bot.performAction(inner);
    }
  }

  protected abstract ImmutableList<T> parseArticleTitles(String s);

  protected abstract String parseHasMore(final String s);

  /**
   * Inner helper class for this type.
   * 
   * @author Thomas Stock
   */
  class TitleQueryAction extends MWAction {

    private HttpAction msg;
    private boolean init = true;

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpAction getNextMessage() {
      return msg;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String processAllReturningText(final String s) {
      ImmutableList<T> newTitles = parseArticleTitles(s);
      if (log.isWarnEnabled()) {
        if (oldTitlesForLogging.equals(newTitles)) {
          log.warn("previous response has same payload");
          // namespaces or same edits in recentchanges
        }
      }
      oldTitlesForLogging = newTitles;
      setNextPageInfo(parseHasMore(s));

      titleIterator = oldTitlesForLogging.iterator();
      return "";
    }

  }
}
