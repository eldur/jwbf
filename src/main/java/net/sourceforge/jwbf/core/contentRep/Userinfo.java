package net.sourceforge.jwbf.core.contentRep;

import java.util.Set;

/**
 * Information about the current user.
 *
 * @author Thomas Stock
 */
public interface Userinfo {
  String RIGHT_EDIT = "edit";
  String RIGHT_WRITEAPI = "writeapi";

  /** @return the rights, like "read, write, ..." */
  Set<String> getRights();

  /** @return the groups, like "user, bot, ..." */
  Set<String> getGroups();

  /** @return the */
  String getUsername();
}
