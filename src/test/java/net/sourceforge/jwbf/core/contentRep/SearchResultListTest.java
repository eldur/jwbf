package net.sourceforge.jwbf.core.contentRep;

import com.google.common.io.Resources;
import net.sourceforge.jwbf.mapper.JsonMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class SearchResultListTest {

  JsonMapper testee;

  @Before
  public void before() {
    testee = new JsonMapper();
  }

  @Test
  public void testJsonParsing() throws IOException {
    // GIVEN
    String content =
        Resources.toString(
            Resources.getResource("mediawiki/v1-23/search.json"), StandardCharsets.UTF_8);

    // WHEN
    SearchResultList srl = testee.get(content, SearchResultList.class);

    // THEN
    assertThat(srl, notNullValue());
    assertTrue("can continue", srl.canContinue());
    assertThat(srl.getBatchComplete(), is(""));
    assertThat(srl.getTotalHits(), is(193));
    assertThat(srl.getSuggestion(), is("meeting"));
    assertEquals(5, srl.getResults().size());

    SearchResult result = srl.getResults().get(0);
    assertThat(result, notNullValue());
    assertThat(result.getNamespace(), is(0));
    assertThat(result.getTitle(), is("Design/WikiFont"));
    assertThat(
        result.getSnippet(),
        is(
            "pixel-perfect so they retain their "
                + "<span class=\"searchmatch\">meaning</span> and "
                + "look sharp in all sizes. An icon should fully express its intended "
                + "<span class=\"searchmatch\">meaning</span> without any text "
                + "companion. In selective"));
    assertThat(result.getSize(), is(8159));
    assertThat(result.getWordCount(), is(890));
    assertThat(result.getTimestamp(), is("2014-10-24T18:41:45Z"));
    assertThat(result.getRedirectSnippet(), is(nullValue()));
  }
}
