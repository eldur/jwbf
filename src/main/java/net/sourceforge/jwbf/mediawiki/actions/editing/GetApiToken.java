package net.sourceforge.jwbf.mediawiki.actions.editing;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.core.contentRep.Userinfo;
import net.sourceforge.jwbf.mediawiki.ApiRequestBuilder;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;

import org.jdom.Element;

/**
 * Action class using the MediaWiki-<a
 * href="http://www.mediawiki.org/wiki/API:Changing_wiki_content" >Editing-API</a>. <br />
 * Its job is to get the token for some actions like delete or edit.
 * 
 * @author Max Gensthaler
 * @author Thomas Stock
 */
@Slf4j
public final class GetApiToken extends MWAction {
  /** Types that need a token. See API field intoken. */
  // TODO this does not feel the elegant way.
  // Probably put complete request URIs into this enum objects
  // to support different URIs for different actions.
  public enum Intoken {
    DELETE, EDIT, MOVE, PROTECT, EMAIL, BLOCK, UNBLOCK, IMPORT
  }

  private String token = "";

  private boolean first = true;

  private Intoken intoken = null;

  private Get msg;

  /**
   * Constructs a new <code>GetToken</code> action.
   * 
   * @param intoken
   *          type to get the token for
   * @param title
   *          title of the article to generate the token for
   * @param ui
   *          user info object
   * 
   *          if this action is not supported of the MediaWiki version connected to
   */
  public GetApiToken(Intoken intoken, String title, Version v, Userinfo ui) {
    super(v);
    this.intoken = intoken;
    generateTokenRequest(intoken, title);

  }

  /**
   * Generates the next MediaWiki API token and adds it to <code>msgs</code>.
   * 
   * @param intoken
   *          type to get the token for
   * @param title
   *          title of the article to generate the token for
   */
  private void generateTokenRequest(Intoken intoken, String title) {
    if (log.isTraceEnabled()) {
      log.trace("enter GetToken.generateTokenRequest()");
    }
    msg = new ApiRequestBuilder() //
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
  protected String getToken() {
    return token;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String processReturningText(String s, HttpAction hm) {
    if (hm.getRequest().equals(msg.getRequest())) {
      if (log.isTraceEnabled()) {
        log.trace("enter GetToken.processAllReturningText(String)");
      }
      if (log.isDebugEnabled()) {
        log.debug("Got returning text: \"" + s + "\"");
      }
      try {
        process(getRootElement(s));
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

  /**
   * {@inheritDoc}
   */
  public HttpAction getNextMessage() {
    if (first) {
      first = false;
      if (log.isTraceEnabled()) {
        log.trace("enter getApiToken");
      }
      return msg;
    }
    return null;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasMoreMessages() {
    return first;
  }

  private void process(Element rootElement) {
    try {
      Element elem = rootElement.getChild("query").getChild("pages").getChild("page");

      // process reply for token request
      switch (intoken) {
      case DELETE:
        token = elem.getAttributeValue("deletetoken");
        break;
      case EDIT:
        token = elem.getAttributeValue("edittoken");
        break;
      case MOVE:
        token = elem.getAttributeValue("movetoken");
        break;
      case PROTECT:
        token = elem.getAttributeValue("protecttoken");
        break;
      case EMAIL:
        token = elem.getAttributeValue("emailtoken");
        break;
      case BLOCK:
        token = elem.getAttributeValue("blocktoken");
        break;
      case UNBLOCK:
        token = elem.getAttributeValue("unblocktoken");
        break;
      case IMPORT:
        token = elem.getAttributeValue("importtoken");
        break;
      default:
        throw new IllegalArgumentException();
      }
    } catch (RuntimeException e) {
      throw new RuntimeException("Unknow reply. This is not a token.", e);
    }

    if (log.isDebugEnabled()) {
      log.debug("found token =" + token + "\n" + "for: " + msg.getRequest() + "\n");
    }
  }
}
