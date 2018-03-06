package net.sourceforge.jwbf.mediawiki.actions.editing;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

import net.sourceforge.jwbf.core.Optionals;
import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.core.actions.ParamTuple;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.core.internal.NonnullFunction;
import net.sourceforge.jwbf.mapper.XmlConverter;
import net.sourceforge.jwbf.mapper.XmlElement;
import net.sourceforge.jwbf.mediawiki.ApiRequestBuilder;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.util.DequeMWAction;

/**
 * This class get the token for some actions like delete or edit.
 *
 * @author Max Gensthaler
 * @author Thomas Stock
 * @see <a href="http://www.mediawiki.org/wiki/API:Changing_wiki_content" >Editing-API</a>
 */
public class GetApiToken extends DequeMWAction<GetApiToken.TokenResponse> {

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

    /** @deprecated prefer {@link #token()} with postPram. */
    @Deprecated
    ParamTuple<String> urlEncodedToken();

    ParamTuple<String> token();
  }

  private static final Logger log = LoggerFactory.getLogger(GetApiToken.class);

  /** Types that need a urlEncodedToken. See API field intoken. */
  // TODO this does not feel the elegant way.
  // Probably put complete request URIs into this enum objects
  // to support different URIs for different actions.
  public enum Intoken {
    DELETE,
    EDIT,
    MOVE,
    PROTECT,
    EMAIL,
    BLOCK,
    UNBLOCK,
    IMPORT
  }

  private static final ImmutableMap<Intoken, Function<XmlElement, String>> TOKEN_FUNCTIONS =
      ImmutableMap.<Intoken, Function<XmlElement, String>>builder() //
          .put(Intoken.DELETE, tokenFunctionOf("deletetoken")) //
          .put(Intoken.EDIT, tokenFunctionOf("edittoken")) //
          .put(Intoken.MOVE, tokenFunctionOf("movetoken")) //
          .put(Intoken.PROTECT, tokenFunctionOf("protecttoken")) //
          .put(Intoken.EMAIL, tokenFunctionOf("emailtoken")) //
          .put(Intoken.BLOCK, tokenFunctionOf("blocktoken")) //
          .put(Intoken.UNBLOCK, tokenFunctionOf("unblocktoken")) //
          .put(Intoken.IMPORT, tokenFunctionOf("IMPORT")) //
          .build();

  private Optional<String> token = Optional.absent();

  private final Intoken intoken;

  private final HttpAction msg;

  /**
   * Constructs a new <code>GetToken</code> action.
   *
   * @param intoken type to get the token for
   * @param title title of the article to generate the token for
   */
  public GetApiToken(Intoken intoken, String title) {
    super(generateTokenRequest(intoken, title));
    this.intoken = intoken;
    msg = actions.getFirst(); // XXX realy nesessary?
  }

  /**
   * Generates the next MediaWiki API urlEncodedToken and adds it to <code>msgs</code>.
   *
   * @param intoken type to get the urlEncodedToken for
   * @param title title of the article to generate the urlEncodedToken for
   */
  private static Get generateTokenRequest(Intoken intoken, String title) {
    return new ApiRequestBuilder() //
        .action("query") //
        .formatXml() //
        .param("prop", "info") //
        .param("intoken", intoken.toString().toLowerCase()) //
        .param("titles", MediaWiki.urlEncode(title)) //
        .buildGet();
  }

  /** {@inheritDoc} */
  @Override
  public void processReturningText(String s, HttpAction hm) {
    if (hm.getRequest().equals(msg.getRequest())) {
      log.debug("Got returning text: \"{}\"", s);
      try {
        Optional<XmlElement> elem = XmlConverter.getChildOpt(s, "query", "pages", "page");
        token = elem.transform(TOKEN_FUNCTIONS.get(intoken));
        // TODO check intoken from tokenfunc for null

        log.debug("urlEncodedToken = {} for: {}", token, msg.getRequest());
        // TODO check catch
      } catch (IllegalArgumentException e) {
        if (s.startsWith("unknown_action:")) {
          log.error(
              "Adding '$wgEnableWriteAPI = true;' "
                  + "to your MediaWiki's LocalSettings.php might remove this problem.",
              e);
        } else {
          log.error(e.getMessage(), e);
        }
      }
    }
  }

  private static Function<XmlElement, String> tokenFunctionOf(final String key) {
    return new NonnullFunction<XmlElement, String>() {

      @Nonnull
      @Override
      public String applyNonnull(@Nonnull XmlElement xmlElement) {
        return xmlElement.getAttributeValueNonNull(key);
      }
    };
  }
}
