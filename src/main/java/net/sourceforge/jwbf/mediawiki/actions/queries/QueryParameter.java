package net.sourceforge.jwbf.mediawiki.actions.queries;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public class QueryParameter {

	private HashMap<String, Set<String>> params;

	public QueryParameter() {
		params = new HashMap<>();
	}

	protected boolean check(String key, String[] values){
		return true;
	}

	public QueryParameter param(String key, String... values) {
		if (check(key, values))
			params.put(key, new HashSet<String>(Arrays.asList(values)));
		return this;
	}

	public Iterator<Entry<String, Set<String>>> iterator(){
		return params.entrySet().iterator();
	}

}
