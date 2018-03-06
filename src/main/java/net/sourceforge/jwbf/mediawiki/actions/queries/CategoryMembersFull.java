package net.sourceforge.jwbf.mediawiki.actions.queries;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import net.sourceforge.jwbf.mediawiki.contentRep.CategoryItem;

/**
 * A specialization of {@link CategoryMembers} with contains {@link CategoryItem}s.
 *
 * @author Thomas Stock
 */
public class CategoryMembersFull extends CategoryMembers {

  private static final Logger log = LoggerFactory.getLogger(CategoryMembersFull.class);

  private boolean init = true;

  public CategoryMembersFull(
      MediaWikiBot bot, String categoryName, ImmutableList<Integer> namespaces) {
    super(bot, categoryName, namespaces);
  }

  public CategoryMembersFull(MediaWikiBot bot, String categoryName, int... namespaces) {
    this(bot, categoryName, MWAction.nullSafeCopyOf(namespaces));
  }

  @Override
  public HttpAction prepareNextRequest() {
    if (init) {
      init = false;
      return generateFirstRequest();
    } else {
      return generateContinueRequest(getNextPageInfo());
    }
  }

  /** {@inheritDoc} */
  @Override
  protected Iterator<CategoryItem> copy() {
    return new CategoryMembersFull(bot(), categoryName, namespace);
  }
}
