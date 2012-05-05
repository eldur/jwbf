package net.sourceforge.jwbf.core.actions;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @deprecated use post instead.
 * @author Thomas Stock
 *
 */
public class FilePost extends Post {

	private Map<String, Object> parts = new HashMap<String, Object>();
	
	public FilePost(String req, String charset) {
		super(req, charset);
	}
	/**
	 * use uft-8 as default charset
	 * @param req
	 * 
	 */
	public FilePost(String req) {
		this(req, "utf-8");
	}
	
	
	
	public void addPart(String key, String value) {
		parts.put(key, value);
	}
	public void addPart(String key, File value) {
		parts.put(key, value);
	}

	public Map<String, Object> getParts() {
		return parts;
	}
}
