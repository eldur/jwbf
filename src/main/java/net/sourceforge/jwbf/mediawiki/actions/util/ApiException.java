package net.sourceforge.jwbf.mediawiki.actions.util;

import net.sourceforge.jwbf.core.actions.util.ProcessException;

/** @author Thomas Stock */
public class ApiException extends ProcessException {

  private static final long serialVersionUID = -959971173922381579L;
  private final String code;
  private final String value;

  public ApiException(String code, String value) {
    super("API ERROR CODE: " + code + " VALUE: " + value);
    this.code = code;
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public String getCode() {
    return code;
  }
}
