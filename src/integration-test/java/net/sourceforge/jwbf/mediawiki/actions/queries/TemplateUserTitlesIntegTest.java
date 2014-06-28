package net.sourceforge.jwbf.mediawiki.actions.queries;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.github.dreamhead.moco.RequestMatcher;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.sourceforge.jwbf.AbstractIntegTest;
import net.sourceforge.jwbf.GAssert;
import net.sourceforge.jwbf.TestHelper;
import net.sourceforge.jwbf.mediawiki.ApiMatcherBuilder;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemplateUserTitlesIntegTest extends AbstractIntegTest {

  private static final Logger log = LoggerFactory.getLogger(TemplateUserTitlesIntegTest.class);

  ApiMatcherBuilder newBaseMatcher() {
    return ApiMatcherBuilder.of() //
        .param("action", "query") //
        .param("eilimit", "50") //
        .param("einamespace", "2") //
        .param("eititle", "Template:Babel") //
        .param("format", "xml") //
        .param("list", "embeddedin") //
      //  .param(ApiRequestBuilder.NEW_CONTINUE) //
        ;
  }

  RequestMatcher embeddedinTwo = newBaseMatcher() //
      .param("eicontinue", "10|Babel|37163") //
      .build();
  RequestMatcher embeddedinThree = newBaseMatcher() //
      .param("eicontinue", "10|Babel|39725") //
      .build();
  RequestMatcher embeddedinOne = newBaseMatcher().build();

  @Test
  public void test() {

    // GIVEN
    server.request(embeddedinThree).response(TestHelper.anyWikiResponse("embeddedin_3.xml"));
    server.request(embeddedinTwo).response(TestHelper.anyWikiResponse("embeddedin_2.xml"));
    server.request(embeddedinOne).response(TestHelper.anyWikiResponse("embeddedin_1.xml"));
    MediaWikiBot bot = new MediaWikiBot(host());

    // WHEN
    TemplateUserTitles testee = new TemplateUserTitles(bot, "Template:Babel", MediaWiki.NS_USER);
    List<String> resultList = testee.getCopyOf(15);

    // THEN
    ImmutableList<String> expected = ImmutableList
        .of("User:AxelBoldt", "User:Piotr Gasiorowski", "User:RobLa", "User:Taral", "User:Ap",
            "User:Yargo", "User:Joakim Ziegler", "User:Snorre", "User:LA2", "User:Codeczero",
            "User:Jkominek", "User:Oliver", "User:Walter", "User:Poslfit", "User:Qaz");
    GAssert.assertEquals(expected, ImmutableList.copyOf(resultList));
    assertEquals(resultList.size(), ImmutableSet.copyOf(resultList).size());

  }

}
