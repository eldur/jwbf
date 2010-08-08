package net.sourceforge.jwbf.mediawiki.actions.queries;

import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_11;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_12;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_13;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_14;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_15;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_16;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.core.actions.util.ProcessException;
import net.sourceforge.jwbf.core.bots.util.JwbfException;
import net.sourceforge.jwbf.mediawiki.actions.util.SupportedBy;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import net.sourceforge.jwbf.mediawiki.contentRep.CategoryItem;

import org.apache.log4j.Logger;

/**
 * A specialization of {@link CategoryMembers} with contains {@link CategoryItem}s.
 * 
 * @author Thomas Stock
 */
@SupportedBy({ MW1_11, MW1_12, MW1_13, MW1_14, MW1_15, MW1_16})
public class CategoryMembersFull extends CategoryMembers implements Iterable<CategoryItem>, Iterator<CategoryItem> {


  private Get msg;
  /**
   * Collection that will contain the result
   * (titles of articles linking to the target)
   * after performing the action has finished.
   */
  private Collection<CategoryItem> titleCollection = new ArrayList<CategoryItem>();
  private Iterator<CategoryItem> titleIterator;

  private Logger log = Logger.getLogger(getClass());

  /**
   *
   * @throws ActionException on any kind of http or version problems
   * @throws ProcessException on inner problems like a version mismatch
   */
  public CategoryMembersFull(MediaWikiBot bot,
      String categoryName , int... namespaces) throws ActionException, ProcessException {
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
        if (log.isDebugEnabled())
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
  public String processAllReturningText(String s) throws ProcessException {
    titleCollection.clear();
    String buff = super.processAllReturningText(s);


    if (log.isDebugEnabled())
      log.debug(titleCollection);
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
