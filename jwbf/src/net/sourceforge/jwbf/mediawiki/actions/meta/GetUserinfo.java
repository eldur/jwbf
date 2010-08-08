package net.sourceforge.jwbf.mediawiki.actions.meta;

import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_11;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_12;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_13;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_14;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_15;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_16;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.core.actions.util.ProcessException;
import net.sourceforge.jwbf.core.contentRep.Userinfo;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.actions.util.SupportedBy;
import net.sourceforge.jwbf.mediawiki.actions.util.VersionException;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;
/**
 *
 * @author Thomas Stock
 *
 */
@SupportedBy({ MW1_11, MW1_12, MW1_13, MW1_14, MW1_15, MW1_16 })
public class GetUserinfo extends MWAction implements Userinfo {


  private final Logger log = Logger.getLogger(getClass());
  private String username = "";
  private final Set<String> rights = new HashSet<String>();
  private final Set<String> groups = new HashSet<String>();
  private Get msg;
  /**
   *
   * @param v a
   * @throws VersionException  a
   */
  public GetUserinfo(Version v) throws VersionException {
    super(v);
    switch (v) {
      case MW1_11:
        msg = new Get("/api.php?" + "action=query&" + "meta=userinfo&"
            + "uiprop="
            + MediaWiki.encode("blockinfo|hasmsg|groups|rights") + "&"
            + "format=xml");

        break;
      default:
        msg = new Get(
            "/api.php?"
            + "action=query&"
            + "meta=userinfo&"
            + "uiprop="
            + MediaWiki
            .encode("blockinfo|hasmsg|groups|rights|options|editcount|ratelimits")
            + "&" + "format=xml");

        break;
    }

  }

  private void parse(final String xml) {
    log.debug(xml);
    rights.clear();
    groups.clear();
    SAXBuilder builder = new SAXBuilder();
    Element root = null;
    try {
      Reader i = new StringReader(xml);
      Document doc = builder.build(new InputSource(i));

      root = doc.getRootElement();
      findContent(root);
    } catch (JDOMException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final String processAllReturningText(final String s)
  throws ProcessException {
    parse(s);
    return "";
  }
  /**
   * {@inheritDoc}
   */
  public Set<String> getRights() {
    return rights;
  }
  /**
   * {@inheritDoc}
   */
  public Set<String> getGroups() {
    return groups;
  }
  /**
   * {@inheritDoc}
   */
  public String getUsername() {
    return username;
  }

  @SuppressWarnings("unchecked")
  protected void findContent(final Element root) {

    Iterator<Element> el = root.getChildren().iterator();

    while (el.hasNext()) {
      Element element = el.next();
      // blockinfo|hasmsg|groups|rights   <- MW 11
      if (element.getQualifiedName().equalsIgnoreCase("userinfo")) {
        username = element.getAttributeValue("name");


      } else if (element.getQualifiedName().equalsIgnoreCase("groups")) {
        Iterator<Element> git = element.getChildren("g").iterator();
        while (git.hasNext()) {
          String gel = git.next().getTextTrim();
          groups.add(gel);
        }
      } else if (element.getQualifiedName().equalsIgnoreCase("rights")) {

        Iterator<Element> rit = element.getChildren("r").iterator();
        while (rit.hasNext()) {
          String rel = rit.next().getTextTrim();

          rights.add(rel);
        }
      }
      findContent(element);

    }


  }
  /**
   * {@inheritDoc}
   */
  public HttpAction getNextMessage() {
    return msg;
  }
}
