package net.sourceforge.jwbf.core.actions;

import net.sourceforge.jwbf.core.actions.util.HttpAction;

/**
 * TODO Usage of this class.
 * @author Thomas Stock
 *
 */
public class Get implements HttpAction {

	private final String req;
	private final String charset;

	/**
	 *
	 * @param req like index.html?parm=value
	 * @param charset like utf-8
	 */
	public Get(String req, String charset) {
		this.req = req;
		this.charset = charset;
	}

	/**
	 * Use utf-8 as default charset.
	 * @param req a
	 */
	public Get(String req) {
		this(req, "utf-8");
	}
	/**
	 * {@inheritDoc}
	 */
	public String getRequest() {
		return req;
	}
	/**
	 * {@inheritDoc}
	 */
	public String getCharset() {
		return charset;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return getCharset() + getRequest();
	}

	/* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((charset == null) ? 0 : charset.hashCode());
    result = prime * result + ((req == null) ? 0 : req.hashCode());
    return result;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Get other = (Get) obj;
    if (charset == null) {
      if (other.charset != null)
        return false;
    } else if (!charset.equals(other.charset))
      return false;
    if (req == null) {
      if (other.req != null)
        return false;
    } else if (!req.equals(other.req))
      return false;
    return true;
  }



}
