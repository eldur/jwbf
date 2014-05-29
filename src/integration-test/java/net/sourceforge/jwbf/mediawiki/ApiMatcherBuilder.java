package net.sourceforge.jwbf.mediawiki;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.eq;
import static com.github.dreamhead.moco.Moco.query;
import static com.github.dreamhead.moco.Moco.uri;

import java.util.Map;

import com.github.dreamhead.moco.RequestMatcher;
import com.google.common.collect.ImmutableMap;

public class ApiMatcherBuilder {

  private ImmutableMap.Builder<String, String> params = ImmutableMap.builder();

  public ApiMatcherBuilder param(String key, String value) {
    params.put(key, value);
    return this;
  }

  public static ApiMatcherBuilder of() {
    return new ApiMatcherBuilder();
  }

  public RequestMatcher build() {
    RequestMatcherBuilder apiBuilder = new RequestMatcherBuilder() //
        .with(by(uri("/api.php")));

    for (Map.Entry<String, String> param : params.build().entrySet()) {
      apiBuilder.with(eq(query(param.getKey()), param.getValue()));
    }
    return apiBuilder.build();
  }
}
