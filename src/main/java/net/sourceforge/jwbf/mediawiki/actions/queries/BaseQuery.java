package net.sourceforge.jwbf.mediawiki.actions.queries;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.Beta;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import net.sourceforge.jwbf.core.Optionals;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.core.internal.Checked;
import net.sourceforge.jwbf.mapper.XmlConverter;
import net.sourceforge.jwbf.mapper.XmlElement;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

/**
 * @param <T> of
 * @author Thomas Stock
 */
public abstract class BaseQuery<T> implements Iterable<T>, Iterator<T>, Cloneable {

  private static final Logger log = LoggerFactory.getLogger(BaseQuery.class);

  private Iterator<T> titleIterator = ImmutableList.<T>of().iterator();
  private final QueryAction inner;
  private final MediaWikiBot bot;
  private ImmutableList<T> oldTitlesForLogging = ImmutableList.of();

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

  protected BaseQuery(MediaWikiBot bot) {
    this.bot = Checked.nonNull(bot, "bot");
    inner = getInnerAction();
  }

  private QueryAction getInnerAction() {
    return new QueryAction();
  }

  @Beta
  public Iterable<T> lazy() {
    return this;
  }

  @Beta
  public ImmutableList<T> getCopyOf(int count) {
    return ImmutableList.copyOf(Iterables.limit(lazy(), count));
  }

  /** {@inheritDoc} */
  @Override
  public final Iterator<T> iterator() {
    return copy();
  }

  protected abstract Iterator<T> copy();

  /** {@inheritDoc} */
  @Override
  public boolean hasNext() {
    doCollection();
    return titleIterator.hasNext();
  }

  /** {@inheritDoc} */
  @Override
  public T next() {
    doCollection();
    return titleIterator.next();
  }

  /** {@inheritDoc} */
  @Override
  public void remove() {
    throw new UnsupportedOperationException("do not change this iteration");
  }

  /** XML related methods will be removed. */
  @Deprecated
  protected Optional<String> parseXmlHasMore(
      String xml, String elementName, String attributeKey, String newContinueKey) {
    XmlElement rootElement = XmlConverter.getRootElement(xml);
    Optional<XmlElement> aContinue = rootElement.getChildOpt("continue");
    if (aContinue.isPresent()) {
      return aContinue.get().getAttributeValueOpt(newContinueKey);
    } else {
      // XXX fallback for < MW1_19
      XmlElement queryContinue = rootElement.getChild("query-continue").getChild(elementName);
      return queryContinue.getAttributeValueOpt(attributeKey);
    }
  }

  /**
   * @return the first and all following requests; depends on {@link #parseHasMore(String)}. Its
   *     implementation may ask {@link #nextPageInfoOpt()} for continuation value.
   */
  protected abstract HttpAction prepareNextRequest();

  private void doCollection() {

    if (inner.init || (!titleIterator.hasNext() && hasNextPageInfo())) {
      inner.init = false;
      inner.setHasMoreMessages(true);
      inner.msg = prepareNextRequest();
      bot.getPerformedAction(inner);
    }
  }

  /**
   * @param s content form the remote api; maybe xml or json. It depends on {@link
   *     #prepareNextRequest()}
   * @return elements that was found in the given string
   */
  protected abstract ImmutableList<T> parseElements(String s);

  /**
   * @param s content form the remote api; maybe xml or json.
   * @return a token, that will be used from {@link #prepareNextRequest()}
   */
  protected abstract Optional<String> parseHasMore(final String s);

  protected MediaWikiBot bot() {
    return bot;
  }

  class QueryAction extends MWAction {

    private HttpAction msg;
    private boolean init = true;

    /** {@inheritDoc} */
    @Override
    public HttpAction getNextMessage() {
      return msg;
    }

    /** {@inheritDoc} */
    @Override
    public final String processAllReturningText(final String s) {
      ImmutableList<T> newTitles = parseElements(s);
      setNextPageInfo(parseHasMore(s).orNull());
      if (log.isWarnEnabled()) {
        if (oldTitlesForLogging.equals(newTitles) && !oldTitlesForLogging.isEmpty()) {
          log.warn("previous response has same payload");
          // namespaces or same edits in recentchanges
        }
        oldTitlesForLogging = newTitles;
      }

      titleIterator = newTitles.iterator();
      return "";
    }
  }

  @Override
  protected Object clone() throws CloneNotSupportedException {
    super.clone();
    throw new CloneNotSupportedException();
  }
}
