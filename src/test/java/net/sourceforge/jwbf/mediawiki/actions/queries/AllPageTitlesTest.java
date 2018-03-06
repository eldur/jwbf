package net.sourceforge.jwbf.mediawiki.actions.queries;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.actions.util.RedirectFilter;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

@RunWith(MockitoJUnitRunner.class)
public class AllPageTitlesTest {

  @Mock private MediaWikiBot bot;

  private AllPageTitles testee;

  @Before
  public void before() {
    testee = new AllPageTitles(bot);
  }

  @Test
  public void testGenerateRequest() {
    // GIVEN
    Optional<String> from = Optional.absent();
    String prefix = null;
    RedirectFilter rf = null;
    String namespace = null;

    // WHEN
    Get allPagesRequest = testee.generateRequest(from, prefix, rf, namespace);

    // THEN
    assertEquals(
        "/api.php?action=query&apfilterredir=nonredirects&aplimit=50&format=xml&list=allpages", //
        allPagesRequest.getRequest());
  }

  @Test
  public void testGenerateRequest_with_prefix() {
    // GIVEN
    Optional<String> from = Optional.absent();
    String prefix = "Test";
    RedirectFilter rf = null;
    String namespace = null;

    // WHEN
    Get allPagesRequest = testee.generateRequest(from, prefix, rf, namespace);

    // THEN
    assertEquals(
        "/api.php?action=query&apfilterredir=nonredirects&aplimit=50"
            + "&apprefix=Test&format=xml&list=allpages",
        allPagesRequest.getRequest());
  }

  @Test
  public void testGenerateRequest_with_namespaces() {
    // GIVEN
    Optional<String> from = Optional.absent();
    String prefix = null;
    RedirectFilter rf = null;
    String namespace = MWAction.createNsString(MediaWiki.NS_CATEGORY);

    // WHEN
    Get allPagesRequest = testee.generateRequest(from, prefix, rf, namespace);

    // THEN
    assertEquals(
        "/api.php?action=query&apfilterredir=nonredirects&aplimit=50"
            + "&apnamespace=14&format=xml&list=allpages",
        allPagesRequest.getRequest());
  }

  @Test
  public void testFindRedirectFilterValue_null() {
    // GIVEN
    RedirectFilter rf = null;

    // WHEN
    String filterValue = testee.findRedirectFilterValue(rf);

    // THEN
    assertEquals("nonredirects", filterValue);
  }

  @Test
  public void testFindRedirectFilterValue_none() {

    // GIVEN
    RedirectFilter rf = RedirectFilter.nonredirects;

    // WHEN
    String filterValue = testee.findRedirectFilterValue(rf);

    // THEN
    assertEquals("nonredirects", filterValue);
  }

  @Test
  public void testFindRedirectFilterValue_all() {

    // GIVEN
    RedirectFilter rf = RedirectFilter.all;

    // WHEN
    String filterValue = testee.findRedirectFilterValue(rf);

    // THEN
    assertEquals("all", filterValue);
  }

  @Test
  public void testFindRedirectFilterValue_redirects() {

    // GIVEN
    RedirectFilter rf = RedirectFilter.redirects;

    // WHEN
    String filterValue = testee.findRedirectFilterValue(rf);

    // THEN
    assertEquals("redirects", filterValue);
  }

  @Test
  public void testParseArticleTitles() {
    // GIVEN / WHEN
    ImmutableList<String> result = testee.parseElements(BaseQueryTest.emptyXml());

    // THEN
    assertTrue(result.isEmpty());
  }
}
