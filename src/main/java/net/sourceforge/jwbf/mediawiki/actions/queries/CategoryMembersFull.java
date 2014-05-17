package net.sourceforge.jwbf.mediawiki.actions.queries;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.core.actions.util.ProcessException;
import net.sourceforge.jwbf.core.bots.util.JwbfException;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import net.sourceforge.jwbf.mediawiki.contentRep.CategoryItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A specialization of {@link CategoryMembers} with contains {@link CategoryItem}s.
 *
 * @author Thomas Stock
 */
public class CategoryMembersFull extends CategoryMembers implements Iterable<CategoryItem>,
    Iterator<CategoryItem> {

  private static final Logger log = LoggerFactory.getLogger(CategoryMembersFull.class);

  private Get msg;
  /**
   * Collection that will contain the result (titles of articles linking to the target) after performing the action has
   * finished.
   */
  private final List<CategoryItem> titleCollection = Lists.newArrayList();
  private Iterator<CategoryItem> titleIterator;

  public CategoryMembersFull(MediaWikiBot bot, String categoryName,
      ImmutableList<Integer> namespaces) {
    super(bot, categoryName, namespaces);
  }

  public CategoryMembersFull(MediaWikiBot bot, String categoryName, int... namespaces) {
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
  @Override
  public HttpAction getNextMessage() {
    return msg;
  }

  /**
   * {@inheritDoc}
   */
  @Override
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

  /**
   * TODO duplication with CategoryMembersSimple
   */
  private void prepareCollection() {

    if (init || (!hasTitleIteratorNext() && hasMoreResults)) {
      if (init) {
        msg = generateFirstRequest();
      } else {
        msg = generateContinueRequest(nextPageInfo);
      }
      init = false;
      try {
        CategoryMembersFull performedAction = bot.getPerformedAction(this);
        // TODO ^^
        setHasMoreMessages(true);
      } catch (ActionException | ProcessException e) {
        log.warn("", e);
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
    return buff;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasNext() {
    prepareCollection();
    return hasTitleIteratorNext();
  }

  private boolean hasTitleIteratorNext() {
    if (titleIterator == null) {
      return false;
    } else {
      return titleIterator.hasNext();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CategoryItem next() {
    prepareCollection();
    return titleIterator.next();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void remove() {
    titleIterator.remove();

  }

  @Override
  protected void finalizeParse() {
    titleIterator = titleCollection.iterator();

  }
}
