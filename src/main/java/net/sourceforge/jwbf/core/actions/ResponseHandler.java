package net.sourceforge.jwbf.core.actions;

import java.util.Deque;

import net.sourceforge.jwbf.core.actions.util.HttpAction;

public abstract class ResponseHandler<T> implements ContentProcessable {

  private final Deque<HttpAction> actions;

  protected String latestResponse = "";

  protected ResponseHandler(Deque<HttpAction> actions) {
    this.actions = actions;
  }

  public abstract T get();

  @Override
  public String processReturningText(String s, HttpAction action) {
    latestResponse = s;

    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public HttpAction getNextMessage() {
    return actions.pop();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasMoreMessages() {
    return !actions.isEmpty();
  }

  @Override
  public boolean isSelfExecuter() {
    return true;
  }

}
