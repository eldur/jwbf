package net.sourceforge.jwbf.mediawiki.actions.editing;

import static net.sourceforge.jwbf.mediawiki.MediaWiki.Version;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableSet;

import net.sourceforge.jwbf.TestHelper;
import net.sourceforge.jwbf.core.contentRep.Userinfo;

@RunWith(MockitoJUnitRunner.class)
public class PostDeleteTest {

  @Mock private Userinfo userinfo;

  private PostDelete testee;

  @Before
  public void before() {
    Set<String> setWithDelete = ImmutableSet.of("delete");
    when(userinfo.getRights()).thenReturn(setWithDelete);
    testee = Mockito.spy(new PostDelete(userinfo, "TitleToDelete", "Unused"));
  }

  @Test(expected = NullPointerException.class)
  public void testParseXml_npe() {
    // GIVEN
    String xml = null;

    // WHEN
    testee.parseXml(xml);
  }

  @Test
  public void testParseXml() {
    // GIVEN
    String xml = TestHelper.wikiResponse(Version.MW1_23, "delete.xml");

    // WHEN
    testee.parseXml(xml);

    // THEN
    Mockito.verify(testee)
        .logDeleted(
            "Delete0",
            "content was: \"A\" "
                + "(and the only contributor was \"[[Special:Contributions/Admin|Admin]]\")");
  }
}
