package net.sourceforge.jwbf.mediawiki.wikidata;

import static org.junit.Assert.assertEquals;

import javax.annotation.Nonnull;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.sourceforge.jwbf.TestHelper;
import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.mapper.JsonMapper;
import net.sourceforge.jwbf.mediawiki.ApiRequestBuilder;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import net.sourceforge.jwbf.mediawiki.contentRep.MediaWikiArticle;
import org.json.JSONObject;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.Claim;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.dumpfiles.JsonConverter;

public class WikiDataTest {

  @Test
  @Ignore
  public void test() {
    // GIVEN
    // TODO do not work with a live system
    String wikidataApiLiveUrl = "https://www.wikidata.org/w/api.php";
    String enWikipediaLiveUrl = "http://en.wikipedia.org/w/api.php";
    TestHelper.assumeReachable(wikidataApiLiveUrl, enWikipediaLiveUrl); // will skip the test if not
    MediaWikiBot wikidataBot = new MediaWikiBot(wikidataApiLiveUrl);
    MediaWikiBot enWikiBot = new MediaWikiBot(enWikipediaLiveUrl);


    // WHEN
    // http://en.wikipedia.org/w/api.php?action=query&prop=revisions&titles=Hamburg&format=jsonfm&
    // prop=pageprops&ppprop=wikibase_item
    MediaWikiArticle article = enWikiBot.getArticle("Hamburg");
    String entity = article.getWikiDataEntity();
    GetClaims getClaims = wikidataBot.getPerformedAction(new GetClaims(entity));

    // THEN
    assertEquals("Q1055", entity);
    // TODO better assertions
    ImmutableList<Claim> claims = getClaims.get();
    assertEquals("must fail", claims);
  }

  // TODO extract to file
  private static class GetClaims extends MWAction implements JsonMapper.ToJsonFunction {
    private static Logger log = LoggerFactory.getLogger(GetClaims.class);
    private final Get getClaim;
    private String result = "";

    public GetClaims(String entity) {
      // XXX Hint from lorinczz / Task: #9
      // action=wbgetentities&format=json&sites=dewiki&titles=Hamburg
      getClaim = new ApiRequestBuilder() //
          .action("wbgetclaims") //
          .formatJson() //
          .param("entity", entity) //
          .buildGet();

    }

    @Override
    public String processAllReturningText(String text) {
      result = text;
      return "doNotCallThis";
    }

    public ImmutableList<Claim> get() {
      // XXX runtime exception
      return new JsonMapper(this).get(result, ImmutableList.class);
    }

    @Override
    public HttpAction getNextMessage() {
      return getClaim;
    }

    @Nonnull
    @Override
    public Object toJson(@Nonnull String jsonString, Class<?> clazz) {

      log.info("{}", jsonString.substring(0, 200));
      DataObjectFactoryImpl dataObjectFactory = new DataObjectFactoryImpl();
      String baseIri = "org.wikidata"; // TODO where to get this from response?
      JsonConverter jsonConverter = new JsonConverter(baseIri, dataObjectFactory);
      String itemIdString = "Q1"; // TODO where to get this from response?


      ItemDocument documents = jsonConverter.convertToItemDocument(new JSONObject(jsonString), itemIdString);
      /*
      {"claims":{"P31":[{"id":"q1055$536549ED-F2DD-4B8E-8C1D-B9212956DF3E","mainsnak":{"snaktype":"value","property":"P31","datatype":"wikibase-item","datavalue":{"value":{"entity-type":"item","numeric-id":

        org.json.JSONException: JSONObject["claims"] is not a JSONArray.
        at org.json.JSONObject.getJSONArray(JSONObject.java:559)
        at org.wikidata.wdtk.dumpfiles.JsonConverter.convertToItemDocument(JsonConverter.java:179)
      */
      List<StatementGroup> statementGroups = documents.getStatementGroups();
      List<Claim> claims = Lists.newArrayList();
      for (StatementGroup group : statementGroups) {
        for (Statement s : group.getStatements()) {
          // The word ^ 'Statement' overlaps with common types: java.sql, java.beans, junit.runnermodels, ...

          claims.add(s.getClaim());
        }
      }
      return ImmutableList.copyOf(claims);
    }
  }

}
