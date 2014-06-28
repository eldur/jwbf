package net.sourceforge.jwbf.mediawiki;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.eq;
import static com.github.dreamhead.moco.Moco.query;
import static com.github.dreamhead.moco.Moco.uri;

import java.util.Map;

import com.github.dreamhead.moco.RequestMatcher;
import com.google.common.collect.ImmutableMap;
import net.sourceforge.jwbf.core.actions.ParamTuple;

public class ApiMatcherBuilder {

  private ImmutableMap.Builder<String, String> params = ImmutableMap.builder();

  public ApiMatcherBuilder param(ParamTuple paramTuple) {
    params.put(paramTuple.key(), paramTuple.value());
    return this;
  }

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

  public ApiMatcherBuilder paramNewContinue(MediaWiki.Version version) {
    if (version.greaterEqThen(MediaWiki.Version.MW1_21)) {
      ParamTuple paramTuple = new ParamTuple("continue", "-||");
      param(paramTuple.key(), paramTuple.value());
    }
    return this;
  }
}
