package net.sourceforge.jwbf.mediawiki.actions.queries;

import java.util.Collection;
import java.util.Iterator;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import com.google.common.collect.Lists;

/**
 * Abstract class which is superclass of all titleiterations, represented by the sufix "Titles".
 * 
 * @author Thomas Stock
 * @param <T>
 *          of
 */
@Slf4j
public abstract class TitleQuery<T> extends MWAction implements Iterable<T>, Iterator<T> {

  protected Iterator<T> titleIterator;
  private InnerAction inner;
  private final MediaWikiBot bot;

  /** Information necessary to get the next api page. */
  protected String nextPageInfo = "";

  protected final String getNextPageInfo() {
    return nextPageInfo;
  }

  protected TitleQuery(MediaWikiBot bot) {
    this.bot = bot;
    inner = getInnerAction(bot.getVersion());
  }

  protected InnerAction getInnerAction(Version v) {
    return new InnerAction(v);
  }

  public HttpAction getNextMessage() {
    throw new UnsupportedOperationException();
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  public Iterator<T> iterator() {
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
  public final boolean hasNext() {
    doCollection();
    return titleIterator.hasNext();
  }

  /**
   * {@inheritDoc}
   */
  public final T next() {
    doCollection();
    return titleIterator.next();
  }

  /**
   * {@inheritDoc}
   */
  public final void remove() {
    titleIterator.remove();
  }

  protected abstract HttpAction prepareCollection();

  private boolean hasNextPage() {
    return nextPageInfo != null && nextPageInfo.length() > 0;
  }

  private void doCollection() {

    if (inner.init || (!titleIterator.hasNext() && hasNextPage())) {
      inner.init = false;
      inner.setHasMoreMessages(true);
      inner.msg = prepareCollection();
      bot.performAction(inner);
    }
  }

  protected abstract Collection<T> parseArticleTitles(String s);

  protected abstract String parseHasMore(final String s);

  /**
   * Inner helper class for this type.
   * 
   * @author Thomas Stock
   * 
   */
  public class InnerAction extends MWAction {

    private HttpAction msg;
    private boolean init = true;

    protected InnerAction(Version v) {
      super(v);
    }

    protected void setMessage(HttpAction msg) {
      this.msg = msg;
    }

    /**
     * {@inheritDoc}
     */
    public HttpAction getNextMessage() {
      return msg;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String processAllReturningText(final String s) {
      Collection<T> knownResults = Lists.newArrayList();

      knownResults.addAll(parseArticleTitles(s));
      nextPageInfo = parseHasMore(s);

      titleIterator = knownResults.iterator();
      return "";
    }

  }
}
