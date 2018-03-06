package net.sourceforge.jwbf.core.actions.util;

/** @author Thomas Stock */
public interface HttpAction {
  /** @return the like "/?get=val" */
  String getRequest();

  /** @return like uft-8 */
  String getCharset();
}
