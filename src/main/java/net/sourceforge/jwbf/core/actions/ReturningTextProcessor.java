package net.sourceforge.jwbf.core.actions;

import net.sourceforge.jwbf.core.actions.util.HttpAction;

public interface ReturningTextProcessor {

  /**
   * @param s the returning text
   * @return the retruning text or a modification of it
   */
  String processReturningText(final String s, HttpAction action);
}
