package net.sourceforge.jwbf.mediawiki.actions.queries;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.core.actions.util.ProcessException;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.actions.util.VersionException;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

/**
 * Abstract class which is superclass of all titleiterations, represented by the sufix "Titles".
 * 
 * @author Thomas Stock
 * @param <T> of
 */
@Slf4j
public abstract class TitleQuery<T> implements Iterable<T>, Iterator<T> {

  protected Iterator<T> titleIterator;
  private InnerAction inner;
  private final MediaWikiBot bot;

  /** Information necessary to get the next api page. */
  protected String nextPageInfo = "";

  protected final String getNextPageInfo() {
    return nextPageInfo;
  }

  protected TitleQuery(MediaWikiBot bot) throws VersionException {
    this.bot = bot;
    inner = getInnerAction(bot.getVersion());
  }

  protected InnerAction getInnerAction(Version v) throws VersionException {
    return new InnerAction(v);
  }
  /**
   * {@inheritDoc}
   */
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
      try {
        inner.setHasMoreMessages(true);
        inner.msg = prepareCollection();

        bot.performAction(inner);



      } catch (ActionException ae) {
        ae.printStackTrace();

      } catch (ProcessException e) {
        e.printStackTrace();

      }
    }
  }

  protected abstract Collection<T> parseArticleTitles(String s);
  protected abstract String parseHasMore(final String s);
  /**
   * Inner helper class for this type.
   * @author Thomas Stock
   *
   */
  public class InnerAction extends MWAction {

    private HttpAction msg;
    private boolean init = true;

    protected InnerAction(Version v) throws VersionException {
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
    public String processAllReturningText(final String s)
        throws ProcessException {
      Collection<T> knownResults = new Vector<T>();

      knownResults.addAll(parseArticleTitles(s));
      nextPageInfo = parseHasMore(s);

      titleIterator = knownResults.iterator();
      return "";
    }

  }
}
