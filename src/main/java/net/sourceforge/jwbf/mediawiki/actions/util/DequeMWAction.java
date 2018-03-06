package net.sourceforge.jwbf.mediawiki.actions.util;

import java.util.Arrays;
import java.util.Deque;

import com.google.common.collect.Queues;

import net.sourceforge.jwbf.core.actions.ActionHandler;
import net.sourceforge.jwbf.core.actions.util.HttpAction;

public abstract class DequeMWAction<T> implements ActionHandler {

  protected Deque<HttpAction> actions;

  protected DequeMWAction(HttpAction... actionArgs) {
    actions = Queues.newLinkedBlockingDeque(Arrays.asList(actionArgs));
  }

  @Override
  public HttpAction popAction() {
    return actions.pop();
  }

  @Override
  public boolean hasMoreActions() {
    return !actions.isEmpty();
  }

  public abstract T get();
}
