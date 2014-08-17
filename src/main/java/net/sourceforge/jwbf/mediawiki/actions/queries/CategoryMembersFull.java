package net.sourceforge.jwbf.mediawiki.actions.queries;

import com.google.common.collect.ImmutableList;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
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
public class CategoryMembersFull extends CategoryMembers {

  private static final Logger log = LoggerFactory.getLogger(CategoryMembersFull.class);

  public CategoryMembersFull(MediaWikiBot bot, String categoryName,
      ImmutableList<Integer> namespaces) {
    super(bot, categoryName, namespaces);
  }

  public CategoryMembersFull(MediaWikiBot bot, String categoryName, int... namespaces) {
    super(bot, categoryName, namespaces);
  }

  @Override
  public HttpAction prepareCollection() {
    if (init) {
      init = false;
      return generateFirstRequest();
    } else {
      return generateContinueRequest(nextPageInfo);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Object clone() throws CloneNotSupportedException {
    super.clone();
    try {
      return new CategoryMembersFull(bot, categoryName, namespace);
    } catch (JwbfException e) {
      throw new CloneNotSupportedException(e.getLocalizedMessage());
    }
  }

}
