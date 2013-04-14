package net.sourceforge.jwbf.core;

import java.util.List;
import java.util.Map.Entry;

import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.core.actions.Post;

import com.google.common.base.Joiner;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class RequestBuilder {

  private final Multimap<String, String> params = ArrayListMultimap.create();
  private String path;

  public RequestBuilder(String path) {
    this.path = path;
  }

  public RequestBuilder param(String key, String value) {
    if (!params.containsEntry(key, value)) {
      params.put(key, value);
    }
    return this;
  }

  public Post buildPost() {
    return new Post(build());
  }

  public Get buildGet() {
    return new Get(build());
  }

  public String build() {

    String paramString = "";
    if (!params.isEmpty()) {
      List<String> values = Lists.newArrayList();
      for (Entry<String, String> entry : params.entries()) {
        values.add(entry.getKey() + "=" + entry.getValue());
      }
      paramString = "?" + Joiner.on("&").join(values);
    }
    return path + paramString;
  }

}
