package net.sourceforge.jwbf.mediawiki.actions.meta;

import java.util.Set;

import com.google.common.collect.Sets;
import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.core.contentRep.Userinfo;
import net.sourceforge.jwbf.extractXml.Element;
import net.sourceforge.jwbf.mediawiki.ApiRequestBuilder;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Stock
 */
public class GetUserinfo extends MWAction implements Userinfo {

  private static final Logger log = LoggerFactory.getLogger(GetUserinfo.class);

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

    for (Element element : root.getChildren()) {
      // blockinfo|hasmsg|groups|rights <- MW 11
      if (element.getQualifiedName().equalsIgnoreCase("userinfo")) {
        username = element.getAttributeValue("name");

      } else if (element.getQualifiedName().equalsIgnoreCase("groups")) {
        for (Element element1 : element.getChildren("g")) {
          String gel = element1.getText();
          groups.add(gel);
        }
      } else if (element.getQualifiedName().equalsIgnoreCase("rights")) {

        for (Element element1 : element.getChildren("r")) {
          String rel = element1.getText();

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
