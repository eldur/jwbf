package net.sourceforge.jwbf.mediawiki;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.eq;
import static com.github.dreamhead.moco.Moco.query;
import static com.github.dreamhead.moco.Moco.uri;

import java.util.Map;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.RequestMatcher;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
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

    ImmutableSet.Builder<String> paramNames = ImmutableSet.builder();
    for (Map.Entry<String, String> param : params.build().entrySet()) {
      String key = param.getKey();
      RequestExtractor<String[]> query = query(key);
      apiBuilder.with(eq(query, param.getValue()));
      paramNames.add(key);
    }

    apiBuilder.with(new AllParamsPresent(paramNames.build()));
    return apiBuilder.build();
  }

  public ApiMatcherBuilder paramNewContinue(MediaWiki.Version version) {
    if (version.greaterEqThen(MediaWiki.Version.MW1_21)) {
      ParamTuple paramTuple = new ParamTuple("continue", "-||");
      param(paramTuple.key(), paramTuple.value());
    }
    return this;
  }

  private class AllParamsPresent extends HttpRequestMatcher {

    private final ImmutableSet<String> params;

    public AllParamsPresent(ImmutableSet<String> params) {
      this.params = params;
    }

    @Override
    public boolean matchHttp(HttpRequest request) {
      ImmutableMap<String, String[]> queries = request.getQueries();
      ImmutableSet<String> keys = queries.keySet();
      return params.equals(keys);
    }

  }

  private abstract class HttpRequestMatcher implements RequestMatcher {

    @Override
    public final boolean match(Request request) {
      if (request instanceof HttpRequest) {
        return matchHttp((HttpRequest) request);
      }
      return false;
    }

    abstract boolean matchHttp(HttpRequest httpRequest);

    @Override
    public RequestMatcher apply(MocoConfig config) {
      throw new UnsupportedOperationException();
    }
  }
}
