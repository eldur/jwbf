package net.sourceforge.jwbf.actions;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FilePost extends Post {

	private Map<String, Object> parts = new HashMap<String, Object>();
	
	public FilePost(String req) {
		super(req);
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
