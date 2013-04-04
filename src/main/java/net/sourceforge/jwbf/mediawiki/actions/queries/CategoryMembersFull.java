package net.sourceforge.jwbf.mediawiki.actions.queries;

import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_15;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_16;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_17;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_18;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_19;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_20;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.core.actions.util.ProcessException;
import net.sourceforge.jwbf.core.bots.util.JwbfException;
import net.sourceforge.jwbf.mediawiki.actions.util.SupportedBy;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import net.sourceforge.jwbf.mediawiki.contentRep.CategoryItem;

/**
 * A specialization of {@link CategoryMembers} with contains {@link CategoryItem}s.
 * 
 * @author Thomas Stock
 */
@Slf4j
@SupportedBy({ MW1_15, MW1_16, MW1_17, MW1_18, MW1_19, MW1_20 })
public class CategoryMembersFull extends CategoryMembers implements Iterable<CategoryItem>,
    Iterator<CategoryItem> {

  private Get msg;
  /**
   * Collection that will contain the result (titles of articles linking to the target) after
   * performing the action has finished.
   */
  private Collection<CategoryItem> titleCollection = new ArrayList<CategoryItem>();
  private Iterator<CategoryItem> titleIterator;

  /**
   * 
   * 
   *           on any kind of http or version problems
   * 
   *           on inner problems like a version mismatch
   */
  public CategoryMembersFull(MediaWikiBot bot, String categoryName, int... namespaces)
      {
    super(bot, categoryName, namespaces);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void addCatItem(String title, int pageid, int ns) {
    CategoryItem ci = new CategoryItem();
    ci.setTitle(title);
    ci.setPageid(pageid);
    ci.setNamespace(ns);
    titleCollection.add(ci);

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
  public Iterator<CategoryItem> iterator() {
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Object clone() throws CloneNotSupportedException {
    try {
      return new CategoryMembersFull(bot, categoryName, namespace);
    } catch (JwbfException e) {
      throw new CloneNotSupportedException(e.getLocalizedMessage());
    }
  }

  private void prepareCollection() {

    if (init || (!titleIterator.hasNext() && hasMoreResults)) {
      if (init) {
        msg = generateFirstRequest();
      } else {
        msg = generateContinueRequest(nextPageInfo);
      }
      init = false;
      try {

        bot.performAction(this);
        setHasMoreMessages(true);
        log.debug("preparing success");
      } catch (ActionException e) {
        e.printStackTrace();
        setHasMoreMessages(false);
      } catch (ProcessException e) {
        e.printStackTrace();
        setHasMoreMessages(false);
      }

    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String processAllReturningText(String s) {
    titleCollection.clear();
    String buff = super.processAllReturningText(s);

    if (log.isDebugEnabled())
      log.debug(titleCollection.toString());
    return buff;
  }

  /**
   * {@inheritDoc}
   */
  public boolean hasNext() {
    prepareCollection();
    return titleIterator.hasNext();
  }

  /**
   * {@inheritDoc}
   */
  public CategoryItem next() {
    prepareCollection();
    return titleIterator.next();
  }

  /**
   * {@inheritDoc}
   */
  public void remove() {
    titleIterator.remove();

  }

  @Override
  protected void finalizeParse() {
    titleIterator = titleCollection.iterator();

  }
}
