package net.sourceforge.jwbf.mediawiki.actions.queries;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dreamhead.moco.RequestMatcher;
import com.google.common.collect.ImmutableList;

import net.sourceforge.jwbf.GAssert;
import net.sourceforge.jwbf.mediawiki.ApiMatcherBuilder;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import net.sourceforge.jwbf.mediawiki.MocoIntegTest;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

public class TemplateUserTitlesIntegTest extends MocoIntegTest {

  private static final Logger log = LoggerFactory.getLogger(TemplateUserTitlesIntegTest.class);

  public TemplateUserTitlesIntegTest(MediaWiki.Version version) {
    super(version);
  }

  ApiMatcherBuilder newBaseMatcher() {
    return ApiMatcherBuilder.of() //
        .param("action", "query") //
        .param("eilimit", "3") //
        .param("einamespace", "0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15") //
        .param("eititle", "Template:TestTemplate") //
        .param("format", "xml") //
        .param("list", "embeddedin") //
        .paramNewContinue(version()) //
    ;
  }

  RequestMatcher embeddedinTwo =
      newBaseMatcher() //
          .param("eicontinue", "10|TestTemplate|5743") //
          .build();

  RequestMatcher embeddedinOne = newBaseMatcher().build();

  @Test
  public void test() {

    // GIVEN
    applySiteinfoXmlToServer();
    server.request(embeddedinTwo).response(mwFileOf(version(), "embeddedin_2.xml"));
    server.request(embeddedinOne).response(mwFileOf(version(), "embeddedin_1.xml"));
    MediaWikiBot bot = new MediaWikiBot(host());

    // WHEN
    TemplateUserTitles testee =
        new TemplateUserTitles(
            bot, 3, "Template:TestTemplate", MWAction.nullSafeCopyOf(MediaWiki.NS_ALL));
    ImmutableList<String> resultList = testee.getCopyOf(4);

    // THEN
    ImmutableList<String> expected = //
        ImmutableList.of("TestTemplate0", "TestTemplate1", "TestTemplate2", "TestTemplate3");

    GAssert.assertEquals(expected, resultList);
  }
}
