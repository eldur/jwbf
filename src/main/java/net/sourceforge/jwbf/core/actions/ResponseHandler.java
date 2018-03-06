package net.sourceforge.jwbf.core.actions;

import java.util.Deque;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import net.sourceforge.jwbf.core.actions.util.HttpAction;

public abstract class ResponseHandler<T> implements ContentProcessable {

  private final Deque<HttpAction> actions;

  protected List<String> responeses = Lists.newArrayList();

  protected ResponseHandler(Deque<HttpAction> actions) {
    this.actions = actions;
  }

  public abstract ImmutableList<T> get();

  @Override
  public String processReturningText(String s, HttpAction action) {
    responeses.add(s);

    return null;
  }

  /** {@inheritDoc} */
  @Override
  public HttpAction getNextMessage() {
    return actions.pop();
  }

  /** {@inheritDoc} */
  @Override
  public boolean hasMoreMessages() {
    return !actions.isEmpty();
  }

  @Override
  public boolean isSelfExecuter() {
    return true;
  }
}
