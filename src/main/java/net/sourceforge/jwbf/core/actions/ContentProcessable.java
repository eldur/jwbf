/*
 * Copyright 2007 Thomas Stock.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Contributors:
 *
 */
package net.sourceforge.jwbf.core.actions;

import net.sourceforge.jwbf.core.actions.util.HttpAction;

/** @author Thomas Stock */
public interface ContentProcessable extends ReturningTextProcessor {

  /** @return the of messages in this action */
  HttpAction getNextMessage();

  /** @return true if */
  boolean hasMoreMessages();

  /**
   * Use this in self maintaining lists for e.g., to prevent user to perform this action manually.
   *
   * @return true if
   * @deprecated better encapsulate action if no external execution shuld be avalibal.
   */
  @Deprecated
  boolean isSelfExecuter();
}
