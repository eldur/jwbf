package net.sourceforge.jwbf.core.actions;

import static com.github.dreamhead.moco.Moco.and;
import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.uri;

import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Ignore;
import org.junit.Test;

import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.RequestMatcher;
import com.google.common.collect.ImmutableList;

import net.sourceforge.jwbf.AbstractIntegTest;
import net.sourceforge.jwbf.GAssert;
import net.sourceforge.jwbf.mediawiki.ApiRequestBuilder;
import net.sourceforge.jwbf.mediawiki.MediaWiki;

public class HttpActionClientIT extends AbstractIntegTest {

  private HttpActionClient testee;

  private final RequestMatcher matcher = and(by(uri("/api.php")));

  /** http://www.mediawiki.org/wiki/Manual:Maxlag_parameter */
  @Test
  @Ignore
  public void testMaxlag() {
    // GIVEN
    server.request(matcher).response(Moco.text("fail"));

    testee =
        HttpActionClient.builder() //
            .withClient(HttpClientBuilder.create().build()) //
            .withUrl("http://www.mediawiki.org/w/") //
            .build();
    Get search =
        new ApiRequestBuilder() //
            .paramNewContinue(MediaWiki.Version.getLatest()) //
            .formatJson() //
            .action("query") //
            .param("list", "search") //
            .param("srsearch", "wikipedia") //
            .param("limit", "1") //
            .param("maxlag", "-1")
            // XXX ^^ hmm mixing performance and api payload sounds strange
            // .. next we get a param like useCompression=gzip ..
            // see https://bugzilla.wikimedia.org/show_bug.cgi?id=64508
            .buildGet();
    ResponseHandler<String> getResponse =
        ContentProcessableBuilder.create(testee).withActions(search).build();
    // WHEN

    testee.performAction(getResponse);

    // THEN
    ImmutableList<String> expected =
        ImmutableList.<String>builder() //
            .add("mustFail") //
            .build();

    GAssert.assertEquals(expected, getResponse.get());
  }
}
