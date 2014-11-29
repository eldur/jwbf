package net.sourceforge.jwbf.mediawiki.actions.meta;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.core.contentRep.Userinfo;
import net.sourceforge.jwbf.mapper.XmlConverter;
import net.sourceforge.jwbf.mapper.XmlElement;
import net.sourceforge.jwbf.mediawiki.ApiRequestBuilder;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
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
    String properties = //
        MediaWiki.urlEncode("blockinfo|hasmsg|groups|rights|options|editcount|ratelimits");
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
    findContent(XmlConverter.getRootElement(xml));
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
  public ImmutableSet<String> getRights() {
    return ImmutableSet.copyOf(rights);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ImmutableSet<String> getGroups() {
    return ImmutableSet.copyOf(groups);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getUsername() {
    return username;
  }

  protected void findContent(final XmlElement root) {
    for (XmlElement xmlElement : root.getChildren()) {
      if (hasName(xmlElement, "userinfo")) {
        username = xmlElement.getAttributeValue("name");
      } else if (hasName(xmlElement, "groups")) {
        for (XmlElement groupElement : xmlElement.getChildren("g")) {
          groups.add(groupElement.getText());
        }
      } else if (hasName(xmlElement, "rights")) {
        for (XmlElement rightElement : xmlElement.getChildren("r")) {
          rights.add(rightElement.getText());
        }
      }
      findContent(xmlElement);
    }
  }

  private boolean hasName(XmlElement xmlElement, String elementName) {
    return xmlElement.getQualifiedName().equals(elementName);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public HttpAction getNextMessage() {
    return msg;
  }
}
