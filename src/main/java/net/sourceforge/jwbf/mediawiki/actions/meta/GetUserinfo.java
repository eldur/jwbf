package net.sourceforge.jwbf.mediawiki.actions.meta;

import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_15;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_16;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_17;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_18;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_19;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_20;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.core.contentRep.Userinfo;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.actions.util.SupportedBy;

import org.jdom.Element;

/**
 * 
 * @author Thomas Stock
 * 
 */
@Slf4j
@SupportedBy({ MW1_15, MW1_16, MW1_17, MW1_18, MW1_19, MW1_20 })
public class GetUserinfo extends MWAction implements Userinfo {

  private String username = "";
  private final Set<String> rights = new HashSet<String>();
  private final Set<String> groups = new HashSet<String>();
  private Get msg;

  public GetUserinfo(Version v) {
    super(v);
    msg = new Get(MediaWiki.URL_API + "?" + "action=query&" + "meta=userinfo&" + "uiprop="
        + MediaWiki.encode("blockinfo|hasmsg|groups|rights|options|editcount|ratelimits") + "&"
        + "format=xml");

  }

  private void parse(final String xml) {
    log.debug(xml);
    rights.clear();
    groups.clear();
    Element root = getRootElement(xml);
    findContent(root);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final String processAllReturningText(final String s) {
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
      // blockinfo|hasmsg|groups|rights <- MW 11
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
