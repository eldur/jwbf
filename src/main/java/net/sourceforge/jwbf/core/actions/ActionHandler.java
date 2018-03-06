package net.sourceforge.jwbf.core.actions;

import com.google.common.annotations.Beta;

import net.sourceforge.jwbf.core.actions.util.HttpAction;

@Beta
public interface ActionHandler {

  /** @return messages of this action and remove it */
  HttpAction popAction();

  /** @return true if */
  boolean hasMoreActions();

  void processReturningText(final String data, HttpAction action);
}
