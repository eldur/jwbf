package net.sourceforge.jwbf.mediawiki.actions.meta;

import java.util.Iterator;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.core.contentRep.Userinfo;
import net.sourceforge.jwbf.mediawiki.ApiRequestBuilder;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;

import org.jdom.Element;

import com.google.common.collect.Sets;

/**
 * @author Thomas Stock
 */
@Slf4j
public class GetUserinfo extends MWAction implements Userinfo {

  private String username = "";
  private final Set<String> rights = Sets.newHashSet();
  private final Set<String> groups = Sets.newHashSet();
  private final Get msg;

  public GetUserinfo() {
    String properties = MediaWiki
        .encode("blockinfo|hasmsg|groups|rights|options|editcount|ratelimits");
    msg = new ApiRequestBuilder() //
        .action("query") //
        .formatXml() //
        .param("meta", "userinfo") //
        .param("uiprop", properties) //
        .buildGet();
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
  @Override
  public Set<String> getRights() {
    return rights;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Set<String> getGroups() {
    return groups;
  }

  /**
   * {@inheritDoc}
   */
  @Override
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
  @Override
  public HttpAction getNextMessage() {
    return msg;
  }
}
