package net.sourceforge.jwbf.mediawiki.actions.editing;

import javax.annotation.Nonnull;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.extractXml.Element;
import net.sourceforge.jwbf.mediawiki.ApiRequestBuilder;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.util.DequeMWAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Action class using the MediaWiki-<a href="http://www.mediawiki.org/wiki/API:Changing_wiki_content" >Editing-API</a>. <br />
 * Its job is to get the token for some actions like delete or edit.
 *
 * @author Max Gensthaler
 * @author Thomas Stock
 */
public class GetApiToken extends DequeMWAction {

  private static final Logger log = LoggerFactory.getLogger(GetApiToken.class);

  /**
   * Types that need a token. See API field intoken.
   */
  // TODO this does not feel the elegant way.
  // Probably put complete request URIs into this enum objects
  // to support different URIs for different actions.
  public enum Intoken {
    DELETE, EDIT, MOVE, PROTECT, EMAIL, BLOCK, UNBLOCK, IMPORT
  }

  private static final ImmutableMap<Intoken, Function<Element, String>> TOKEN_FUNCTIONS = ImmutableMap
      .<Intoken, Function<Element, String>>builder() //
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
   * @param title   title of the article to generate the token for
   */
  public GetApiToken(Intoken intoken, String title) {
    super(generateTokenRequest(intoken, title));
    this.intoken = intoken;
    msg = actions.getFirst(); // XXX realy nesessary?

  }

  /**
   * Generates the next MediaWiki API token and adds it to <code>msgs</code>.
   *
   * @param intoken type to get the token for
   * @param title   title of the article to generate the token for
   */
  private static Get generateTokenRequest(Intoken intoken, String title) {
    log.trace("enter GetToken.generateTokenRequest()");
    return new ApiRequestBuilder() //
        .action("query") //
        .formatXml() //
        .param("prop", "info") //
        .param("intoken", intoken.toString().toLowerCase()) //
        .param("titles", MediaWiki.encode(title)) //
        .buildGet();

  }

  /**
   * Returns the requested token after parsing the result from MediaWiki.
   *
   * @return the requested token
   */
  @Nonnull
  protected String getToken() {
    if (token.isPresent()) {
      return token.get();
    } else {
      throw new IllegalArgumentException("The argument 'token' is missing");
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String processReturningText(String s, HttpAction hm) {
    if (hm.getRequest().equals(msg.getRequest())) {
      log.trace("enter GetToken.processAllReturningText(String)");
      log.debug("Got returning text: \"{}\"", s);
      try {
        Element elem = getRootElement(s).getChild("query").getChild("pages").getChild("page");
        // TODO check for null
        token = Optional.fromNullable(elem).transform(TOKEN_FUNCTIONS.get(intoken));
        // TODO check intoken from tokenfunc for null

        if (log.isDebugEnabled()) {
          log.debug("token = {} for: {}", token, msg.getRequest());
        }
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
    return "";
  }

  private static Function<Element, String> tokenFunctionOf(final String key) {
    return new Function<Element, String>() {

      @Override
      public String apply(Element element) {
        Element elementNonNull = Preconditions.checkNotNull(element);
        return Preconditions.checkNotNull(elementNonNull.getAttributeValue(key),
            "no attribute found for key: " + key);
      }
    };
  }

}
