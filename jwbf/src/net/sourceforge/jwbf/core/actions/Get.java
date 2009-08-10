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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Get) {
			return toString().equals(obj.toString());
		}
		return super.equals(obj);
	}
	
	
}
