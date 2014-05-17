package net.sourceforge.jwbf.mediawiki.actions.queries;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.eq;
import static com.github.dreamhead.moco.Moco.query;
import static com.github.dreamhead.moco.Moco.uri;
import static org.junit.Assert.assertEquals;

import java.util.List;

import com.github.dreamhead.moco.RequestMatcher;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.sourceforge.jwbf.AbstractIntegTest;
import net.sourceforge.jwbf.GAssert;
import net.sourceforge.jwbf.TestHelper;
import net.sourceforge.jwbf.mediawiki.RequestMatcherBuilder;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemplateUserTitlesIntegTest extends AbstractIntegTest {

  private static final Logger log = LoggerFactory.getLogger(TemplateUserTitlesIntegTest.class);

  RequestMatcherBuilder newBaseMatcher() {
    return new RequestMatcherBuilder() //
        .with(by(uri("/api.php"))) //
        .with(eq(query("action"), "query")) //
        .with(eq(query("eilimit"), "50")) //
        .with(eq(query("einamespace"), "2")) //
        .with(eq(query("eititle"), "Template:Babel")) //
        .with(eq(query("format"), "xml")) //
        .with(eq(query("list"), "embeddedin") //
        );
  }

  RequestMatcher embeddedinTwo = newBaseMatcher() //
      .with(eq(query("eicontinue"), "10|Babel|37163")) //
      .build();
  RequestMatcher embeddedinThree = newBaseMatcher() //
      .with(eq(query("eicontinue"), "10|Babel|39725")) //
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
    ImmutableList<String> expected = ImmutableList.of("User:AxelBoldt", "User:Piotr Gasiorowski",
        "User:RobLa", "User:Taral", "User:Ap", "User:Yargo", "User:Joakim Ziegler", "User:Snorre",
        "User:LA2", "User:Codeczero", "User:Jkominek", "User:Oliver", "User:Walter",
        "User:Poslfit", "User:Qaz");
    GAssert.assertEquals(expected, ImmutableList.copyOf(resultList));
    assertEquals(resultList.size(), ImmutableSet.copyOf(resultList).size());

  }

}
