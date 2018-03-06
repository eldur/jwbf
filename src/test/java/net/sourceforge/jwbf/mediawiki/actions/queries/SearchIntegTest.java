package net.sourceforge.jwbf.mediawiki.actions.queries;

import static org.junit.Assert.assertEquals;

import java.util.EnumSet;
import java.util.List;

import org.junit.Test;

import com.github.dreamhead.moco.RequestMatcher;
import com.google.common.collect.ImmutableList;

import net.sourceforge.jwbf.AbstractIntegTest;
import net.sourceforge.jwbf.core.contentRep.SearchResult;
import net.sourceforge.jwbf.mediawiki.ApiMatcherBuilder;
import net.sourceforge.jwbf.mediawiki.actions.queries.Search.SearchInfo;
import net.sourceforge.jwbf.mediawiki.actions.queries.Search.SearchProps;
import net.sourceforge.jwbf.mediawiki.actions.queries.Search.SearchWhat;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

public class SearchIntegTest extends AbstractIntegTest {
  private static final ApiMatcherBuilder MATCHERS_COMMON =
      ApiMatcherBuilder.of() //
          .param("action", "query") //
          .param("continue", "-||") //
          .param("format", "json") //
          .param("list", "search") //
          .param("srinfo", "totalhits") //
          .param("srlimit", "50") //
          .param("srnamespace", "0") //
          .param("srprop", "size") //
          .param("srsearch", "meaning") //
          .param("srwhat", "text");

  private static final RequestMatcher[] MATCHERS = {
    MATCHERS_COMMON.build(), MATCHERS_COMMON.param("sroffset", "50").build()
  };

  private static final String[] RESPONSES = {
    "{\"continue\":{\"sroffset\":50,\"continue\":\"-||\"},\"batchcomplete\":\"\","
        + "\"query\":{\"searchinfo\":{\"totalhits\":193,\"suggestion\":\"meeting\"},"
        + "\"search\":[{\"ns\":0,\"title\":\"Design/WikiFont\",\"size\":8159},{\"ns\":0,"
        + "\"title\":\"Parsoid/Bibliography\",\"size\":304},{\"ns\":0,"
        + "\"title\":\"Bug management/Phabricator etiquette\",\"size\":3036},"
        + "{\"ns\":0,\"title\":\"VisualEditor/Design/User testing\",\"size\":10968},"
        + "{\"ns\":0,\"title\":\"Bugzilla/Fields\",\"size\":8187},"
        + "{\"ns\":0,\"title\":\"Bug management/How to triage\",\"size\":15155},"
        + "{\"ns\":0,\"title\":\"Snippets/Image Expand on Hover\",\"size\":2279},"
        + "{\"ns\":0,\"title\":\"Gerrit/Commit message guidelines\",\"size\":6443},"
        + "{\"ns\":0,\"title\":\"Subversion/Code review/tags\",\"size\":3070},"
        + "{\"ns\":0,\"title\":\"EventLogging/OperationalSupport\",\"size\":1127},"
        + "{\"ns\":0,\"title\":\"Wikimedia Mobile engineering/Language support\",\"size\":2431},"
        + "{\"ns\":0,\"title\":\"Subversion/Code review\",\"size\":3607},"
        + "{\"ns\":0,\"title\":\"Universal Language Selector/Testing\",\"size\":6583},"
        + "{\"ns\":0,\"title\":\"Multilingual MediaWiki\",\"size\":33884},"
        + "{\"ns\":0,\"title\":\"Snippets/Rotating Text\",\"size\":3233},"
        + "{\"ns\":0,\"title\":\"Requests for comment/Associated namespaces\",\"size\":11825},"
        + "{\"ns\":0,\"title\":\"Article feedback/Data\",\"size\":11109},"
        + "{\"ns\":0,\"title\":\"GNU Free Documentation License\",\"size\":20122},"
        + "{\"ns\":0,\"title\":\"Wikimedia Engineering/Report/2013/May\",\"size\":18366},"
        + "{\"ns\":0,\"title\":\"MediaWiki testimonials\",\"size\":11499},"
        + "{\"ns\":0,\"title\":\"Content translation/Translation tools\",\"size\":13962},"
        + "{\"ns\":0,\"title\":\"Wikimedia Engineering/Report/2011/June\",\"size\":18066},"
        + "{\"ns\":0,\"title\":\"VisualEditor/Change markers\",\"size\":1180},"
        + "{\"ns\":0,\"title\":\"Accessibility guide for developers\",\"size\":9666},"
        + "{\"ns\":0,\"title\":\"Requests for comment/Unified ZERO design\",\"size\":11391},"
        + "{\"ns\":0,\"title\":\"Wikibase/DataModel/Primer\",\"size\":11834},"
        + "{\"ns\":0,\"title\":\"Content translation/Technical Architecture\",\"size\":10415},"
        + "{\"ns\":0,\"title\":\"Wikimedia Engineering/Report/2012/November\",\"size\":14851},"
        + "{\"ns\":0,\"title\":\"Engineering Community Team/Meetings/2014-04-08\","
        + "\"size\":22411},"
        + "{\"ns\":0,\"title\":\"Typography refresh\",\"size\":21281},"
        + "{\"ns\":0,\"title\":\"Requests for comment/Deprecating inline styles\","
        + "\"size\":11264},"
        + "{\"ns\":0,\"title\":\"VisualEditor/Design/Software overview\",\"size\":15976},"
        + "{\"ns\":0,\"title\":\"WikiReleaseTeam/Google Code-in 2014/Research WikiApiary "
        + "Sites Listed as Defunct/241-260\",\"size\":2446},"
        + "{\"ns\":0,\"title\":\"Wikibase/DataModel\",\"size\":43360},"
        + "{\"ns\":0,\"title\":\"MediaWiki UI\",\"size\":5984},"
        + "{\"ns\":0,\"title\":\"MediaWiki Developer Meet-Up 2009/Notes/WikiWord\",\"size\":879},"
        + "{\"ns\":0,\"title\":\"Localisation\",\"size\":57062},"
        + "{\"ns\":0,\"title\":\"Flow\",\"size\":43020},"
        + "{\"ns\":0,\"title\":\"Directionality support\",\"size\":8300},"
        + "{\"ns\":0,\"title\":\"Release notes/1.24\",\"size\":36849},"
        + "{\"ns\":0,\"title\":\"Bug management/status\",\"size\":108631},"
        + "{\"ns\":0,\"title\":\"Gerrit/Code review/Getting reviews\",\"size\":7049},"
        + "{\"ns\":0,\"title\":\"HTML5\",\"size\":17617},"
        + "{\"ns\":0,\"title\":\"Mobile design/Wikipedia "
        + "navigation/Back behavior\",\"size\":5939},"
        + "{\"ns\":0,\"title\":\"Style guide/Color selection and accessibility\",\"size\":4359},"
        + "{\"ns\":0,\"title\":\"Wikimedia Foundation Design/Agora icon set\",\"size\":6974},"
        + "{\"ns\":0,\"title\":\"WYSIWYG editor\",\"size\":25613},"
        + "{\"ns\":0,\"title\":\"Improved Wikipedia Zero Landing Page\",\"size\":6358},"
        + "{\"ns\":0,\"title\":\"Flow/Editing comments\",\"size\":14568},"
        + "{\"ns\":0,\"title\":\"Pending Changes enwiki trial/NovemberRelease"
        + "DesignChanges\",\"size\":4955}]}}",
    "{\"continue\":{\"sroffset\":100,\"continue\":\"-||\"},"
        + "\"batchcomplete\":\"\",\"query\":{\"searchinfo\":"
        + "{\"totalhits\":193},\"search\":["
        + "{\"ns\":0,\"title\":\"VisualEditor/Node types\",\"size\":2925},"
        + "{\"ns\":0,\"title\":\"Events/The Noun Project Iconathon\",\"size\":4479},"
        + "{\"ns\":0,\"title\":\"WikiReleaseTeam/Interviews with end users\",\"size\":4325},"
        + "{\"ns\":0,\"title\":\"ResourceLoader/Default modules\",\"size\":50359},"
        + "{\"ns\":0,\"title\":\"Requests for comment/Structured logging\",\"size\":18798},"
        + "{\"ns\":0,\"title\":\"Flow/Architecture/NoSQL\",\"size\":7668},"
        + "{\"ns\":0,\"title\":\"Analytics/Wikistats\",\"size\":13278},"
        + "{\"ns\":0,\"title\":\"Editor engagement/Participant Lifecycle\",\"size\":4728},"
        + "{\"ns\":0,\"title\":\"Wikitext parser/Stage 1: Formal grammar\",\"size\":2452},"
        + "{\"ns\":0,\"title\":\"Micro Design Improvements\",\"size\":23576},"
        + "{\"ns\":0,\"title\":\"Requests for comment/LESS\",\"size\":20724},"
        + "{\"ns\":0,\"title\":\"Quality Assurance/Browser testing/Writing "
        + "tests\",\"size\":19856},"
        + "{\"ns\":0,\"title\":\"Requests for comment/Update our code to use "
        + "RDFa 1.1 instead of RDFa 1.0\",\"size\":5749},"
        + "{\"ns\":0,\"title\":\"Translation UX/Design feedback 4\",\"size\":11800},"
        + "{\"ns\":0,\"title\":\"Content translation/Dictionaries\",\"size\":5601},"
        + "{\"ns\":0,\"title\":\"Requests for comment/Workflow\",\"size\":19492},"
        + "{\"ns\":0,\"title\":\"San Francisco Hackathon January "
        + "2012/Teams\",\"size\":16753},"
        + "{\"ns\":0,\"title\":\"Wikidata Toolkit\",\"size\":14410},"
        + "{\"ns\":0,\"title\":\"Autoblock\",\"size\":7340},"
        + "{\"ns\":0,\"title\":\"MediaWiki 1.22/wmf15\",\"size\":14226},"
        + "{\"ns\":0,\"title\":\"VisualEditor/status\",\"size\":381136},"
        + "{\"ns\":0,\"title\":\"Mentorship programs/2014-03-11 IRC Q&A session "
        + "on GSoC and FOSS OPW\",\"size\":23199},"
        + "{\"ns\":0,\"title\":\"Mobile design/Wikipedia navigation/Release"
        + " plan\",\"size\":3789},"
        + "{\"ns\":0,\"title\":\"New-installer issues\",\"size\":17869},"
        + "{\"ns\":0,\"title\":\"Requests for comment/Zero architecture\",\"size\":22522},"
        + "{\"ns\":0,\"title\":\"Release notes/1.21\",\"size\":27648},"
        + "{\"ns\":0,\"title\":\"Requests for comment/UploadWizard: scale to "
        + "sister projects\",\"size\":20709},"
        + "{\"ns\":0,\"title\":\"Wiki Loves Monuments mobile "
        + "application/Marketing/Fact sheet\",\"size\":4438},"
        + "{\"ns\":0,\"title\":\"Bangalore DevCamp November 2012/demos\",\"size\":2372},"
        + "{\"ns\":0,\"title\":\"Requests for comment/API "
        + "roadmap/Naming Cleanup\",\"size\":326},"
        + "{\"ns\":0,\"title\":\"Sites using MediaWiki/en\",\"size\":122970},"
        + "{\"ns\":0,\"title\":\"Architecture meetings/WMF Engineering "
        + "All-Hands 2013\",\"size\":13750},"
        + "{\"ns\":0,\"title\":\"VisualEditor/Feedback/Archive/2012/06\",\"size\":57364},"
        + "{\"ns\":0,\"title\":\"Article feedback/Version 5/Moderation "
        + "guidelines\",\"size\":15985},"
        + "{\"ns\":0,\"title\":\"Wiki Loves Monuments mobile "
        + "application/Feedback\",\"size\":17186},"
        + "{\"ns\":0,\"title\":\"Wikimedia "
        + "Engineering/Report/2011/February\",\"size\":17327},"
        + "{\"ns\":0,\"title\":\"Analytics/Development Process\",\"size\":7101},"
        + "{\"ns\":0,\"title\":\"Hackathon/Laptop setup/Windows "
        + "command line\",\"size\":4110},"
        + "{\"ns\":0,\"title\":\"Hackathon/Laptop setup/OSX command line\",\"size\":3702},"
        + "{\"ns\":0,\"title\":\"Hackathon/Laptop setup/Linux command line\",\"size\":3700},"
        + "{\"ns\":0,\"title\":\"Onboarding new Wikipedians/Account "
        + "creation pathways\",\"size\":7699},"
        + "{\"ns\":0,\"title\":\"Article feedback/UX Research\",\"size\":69784},"
        + "{\"ns\":0,\"title\":\"Athena\",\"size\":14033},"
        + "{\"ns\":0,\"title\":\"Requests for "
        + "comment/ResourceLoader CSS Extensions\",\"size\":16045},"
        + "{\"ns\":0,\"title\":\"Quality assessment tools for Wikipedia "
        + "readers\",\"size\":17494},"
        + "{\"ns\":0,\"title\":\"WikiLove/Data/de\",\"size\":2344},"
        + "{\"ns\":0,\"title\":\"Mediawiki/wikitech/Help:Getting Started "
        + "\\\"random user\\\" comments on page\",\"size\":1639},"
        + "{\"ns\":0,\"title\":\"WikiLove/Data/ar\",\"size\":2317},"
        + "{\"ns\":0,\"title\":\"WikiLove/Data/en-gb\",\"size\":1162},"
        + "{\"ns\":0,\"title\":\"WikiLove/Data\",\"size\":1144}]}}"
  };

  @Test
  public void test() {
    // GIVEN
    for (int i = 0; i < RESPONSES.length; i++) {
      server.get(MATCHERS[i]).response(RESPONSES[i]);
    }
    MediaWikiBot bot = new MediaWikiBot(host());

    // WHEN
    Search testee =
        new Search(
            bot,
            "meaning",
            EnumSet.of(SearchWhat.text),
            EnumSet.of(SearchInfo.totalhits),
            EnumSet.of(SearchProps.size),
            0);
    List<SearchResult> resultList = testee.getCopyOf(55);

    // THEN
    ImmutableList<String> firstBatchTitles =
        ImmutableList.of(
            "Design/WikiFont", "Parsoid/Bibliography", "Bug management/Phabricator etiquette");
    ImmutableList<String> secondBatchTitles =
        ImmutableList.of(
            "VisualEditor/Node types",
            "Events/The Noun Project Iconathon",
            "WikiReleaseTeam/Interviews with end users");
    assertTitlesEqual(firstBatchTitles, resultList, 0);
    assertTitlesEqual(secondBatchTitles, resultList, 50);
  }

  private void assertTitlesEqual(
      ImmutableList<String> expected, List<SearchResult> actual, int offsetInActual) {
    for (int i = 0; i < expected.size(); i++) {
      assertEquals(expected.get(i), actual.get(offsetInActual + i).getTitle());
    }
  }
}
