package net.sourceforge.jwbf.mediawiki.actions.util;

import java.util.Arrays;
import java.util.Deque;

import com.google.common.collect.Queues;
import net.sourceforge.jwbf.core.actions.util.HttpAction;

public abstract class DequeMWAction extends MWAction {

  protected Deque<HttpAction> actions;

  protected DequeMWAction(HttpAction... actionArgs) {
    actions = Queues.newLinkedBlockingDeque(Arrays.asList(actionArgs));
  }

  @Override
  public HttpAction getNextMessage() {
    return actions.pollFirst();
  }

  @Override
  public boolean hasMoreMessages() {
    return !actions.isEmpty();
  }

}
