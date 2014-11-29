package net.sourceforge.jwbf.mediawiki.actions.queries;

import com.github.dreamhead.moco.RequestMatcher;
import com.google.common.collect.ImmutableList;
import net.sourceforge.jwbf.GAssert;
import net.sourceforge.jwbf.mediawiki.ApiMatcherBuilder;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import net.sourceforge.jwbf.mediawiki.MocoIntegTest;
import org.junit.Test;

public class ImageUsageTitlesIntegTest extends MocoIntegTest {

  public ImageUsageTitlesIntegTest(MediaWiki.Version version) {
    super(version);
  }

  ApiMatcherBuilder newBaseMatcher() {
    return ApiMatcherBuilder.of() //
        .param("action", "query") //
        .paramNewContinue(version()) //
        .param("format", "xml") //
        .param("iulimit", "3") //
        .param("iunamespace", "0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15") //
        .param("iutitle", "Any.gif") //
        .param("list", "imageusage") //
        ;
  }

  RequestMatcher imageUsageTitles0 = newBaseMatcher() //
      .build();

  RequestMatcher imageUsageTitles1 = newBaseMatcher() //
      .param("iucontinue", "6|Any.gif|5962") //
      .build();

  @Test
  public void test() {
    // GIVEN
    applySiteinfoXmlToServer();
    server.request(imageUsageTitles0).response(mwFileOf(version(), "imageUsageTitles0.xml"));
    server.request(imageUsageTitles1).response(mwFileOf(version(), "imageUsageTitles1.xml"));

    // WHEN
    ImmutableList<String> imageUsageTitles =
        new ImageUsageTitles(bot(), 3, "Any.gif", MediaWiki.NS_EVERY).getCopyOf(4);

    // THEN
    GAssert.assertEquals(
        ImmutableList.of("TitleWithImg0", "TitleWithImg1", "TitleWithImg2", "TitleWithImg3"),
        imageUsageTitles);
  }
}
