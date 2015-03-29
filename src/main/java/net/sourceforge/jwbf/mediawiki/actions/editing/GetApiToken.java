package net.sourceforge.jwbf.mediawiki.actions.editing;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Optional;
import net.sourceforge.jwbf.core.Optionals;
import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.core.actions.ParamTuple;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.mapper.JsonMapper;
import net.sourceforge.jwbf.mediawiki.ApiRequestBuilder;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.util.DequeMWAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class get the token for some actions like delete or edit.
 *
 * @author Max Gensthaler
 * @author Thomas Stock
 * @see <a href="http://www.mediawiki.org/wiki/API:Changing_wiki_content" >Editing-API</a>
 */
public class GetApiToken extends DequeMWAction<GetApiToken.TokenResponse> {

  private JsonMapper mapper = new JsonMapper();

  @Override
  public TokenResponse get() {
    return new TokenResponse() {

      /**
       * Returns the requested urlEncodedToken after parsing the result from MediaWiki.
       *
       * @return the requested urlEncodedToken
       * @deprecated
       */
      @Nonnull
      @Override
      @Deprecated
      public ParamTuple<String> urlEncodedToken() {
        return new ParamTuple<>("token", MediaWiki.urlEncode(getToken()));
      }

      private String getToken() {
        return Optionals.getOrThrow(GetApiToken.this.token, "The argument 'token' is missing");
      }

      @Nonnull
      @Override
      public ParamTuple<String> token() {
        return new ParamTuple<>("token", getToken());
      }
    };
  }

  public interface TokenResponse {

    /**
     * @deprecated prefer {@link #token()} with postPram.
     */
    @Deprecated
    ParamTuple<String> urlEncodedToken();

    ParamTuple<String> token();
  }

  private static final Logger log = LoggerFactory.getLogger(GetApiToken.class);

  /**
   * Types that need a urlEncodedToken. See API field intoken.
   */
  // TODO this does not feel the elegant way.
  // Probably put complete request URIs into this enum objects
  // to support different URIs for different actions.
  public enum Intoken {
    DELETE("delete", "deletetoken"), EDIT("edit", "edittoken"), MOVE("move", "movetoken"),
    PROTECT("protect", "protecttoken"), EMAIL("email", "emailtoken"), BLOCK("block",
        "blocktoken"), UNBLOCK("unblock", "unblocktoken"), IMPORT("import", "IMPORT"),
    WATCH("watch", "watchtoken");

    private String requestName;
    private String responseName;

    Intoken(String requestName, String responseName) {
      this.requestName = requestName;
      this.responseName = responseName;
    }

    public String getRequestName() {
      return requestName;
    }

    public String getResponseName() {
      return responseName;
    }
  }

  private Optional<String> token = Optional.absent();

  private final Intoken intoken;

  private final HttpAction msg;

  /**
   * Constructs a new <code>GetToken</code> action.
   *
   * @param intoken
   *            type to get the token for
   * @param titles
   *            title of the article to generate the token for
   */
  public GetApiToken(Intoken intoken, String... titles) {
    super(generateTokenRequest(intoken, titles));
    this.intoken = intoken;
    msg = actions.getFirst(); // XXX realy nesessary?

  }

  /**
   * Generates the next MediaWiki API urlEncodedToken and adds it to <code>msgs</code>.
   *
   * @param intoken
   *            type to get the urlEncodedToken for
   * @param titles
   *            title of the article to generate the urlEncodedToken for
   */
  private static Get generateTokenRequest(Intoken intoken, String... titles) {
    return new ApiRequestBuilder() //
        .action("query") //
        .formatJson() //
        .param("prop", "info") //
        .param("intoken", intoken.getRequestName()) //
        .param("titles", MediaWiki.urlEncode(MediaWiki.pipeJoined(titles))) //
        .buildGet();

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void processReturningText(String s, HttpAction hm) {
    if (hm.getRequest().equals(msg.getRequest())) {
      log.debug("Got returning text: \"{}\"", s);
      try {
        JsonNode node = mapper.toJsonNode(s).path("query").path("pages");
        String fieldName = node.fieldNames().next();
        node = node.get(fieldName).get(intoken.getResponseName());
        token = Optional.of(node.asText());
        // TODO check intoken from tokenfunc for null

        log.debug("urlEncodedToken = {} for: {}", token, msg.getRequest());
        // TODO check catch
      } catch (IllegalArgumentException e) {
        if (s.startsWith("unknown_action:")) {
          log.error("Adding '$wgEnableWriteAPI = true;' "
              + "to your MediaWiki's LocalSettings.php might remove this problem.", e);
        } else {
          log.error(e.getMessage(), e);
        }
      }
    }
  }

}
