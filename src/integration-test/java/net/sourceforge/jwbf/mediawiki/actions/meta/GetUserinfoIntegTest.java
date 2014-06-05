package net.sourceforge.jwbf.mediawiki.actions.meta;

import static org.junit.Assert.assertEquals;

import com.github.dreamhead.moco.RequestMatcher;
import net.sourceforge.jwbf.GAssert;
import net.sourceforge.jwbf.mediawiki.ApiMatcherBuilder;
import net.sourceforge.jwbf.mediawiki.ConfKey;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import net.sourceforge.jwbf.mediawiki.MocoIntegTest;
import org.junit.Test;

public class GetUserinfoIntegTest extends MocoIntegTest {

  public GetUserinfoIntegTest(MediaWiki.Version version) {
    super(version);
  }

  public static RequestMatcher newUserInfoMatcher() {
    return ApiMatcherBuilder.of() //
        .param("format", "xml") //
        .param("meta", "userinfo") //
        .param("uiprop", "blockinfo|hasmsg|groups|rights|options|editcount|ratelimits") //
        .build();
  }

  @Test
  public void test() {
    // GIVEN
    server.request(newUserInfoMatcher()).response(mwFileOf(version(), "userinfo.xml"));
    GetUserinfo testee = new GetUserinfo();

    // WHEN
    GetUserinfo performedAction = bot().getPerformedAction(testee);

    // THEN
    assertEquals("Admin", performedAction.getUsername());
    GAssert
        .assertEquals(splittedConfigOfString(ConfKey.USERINFO_RIGHTS), performedAction.getRights());
    GAssert
        .assertEquals(splittedConfigOfString(ConfKey.USERINFO_GROUPS), performedAction.getGroups());
  }

}
