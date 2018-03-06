package net.sourceforge.jwbf.mediawiki.actions.meta;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.mapper.XmlElement;
import net.sourceforge.jwbf.mediawiki.ApiRequestBuilder;
import net.sourceforge.jwbf.mediawiki.MediaWiki;

/**
 * Gets details from the given MediaWiki installation like installed version.
 *
 * @author Thomas Stock
 * @see Siteinfo
 */
public class Siteinfo extends GetVersion {

  private final Get msg;
  private final Map<Integer, String> namespaces = Maps.newHashMap();
  private final Map<String, String> interwiki = Maps.newHashMap();

  public static final String GENERAL = "general";
  public static final String NAMESPACES = "namespaces";
  // : A list of all namespaces
  // # namespacealiases: A list of all namespace aliases
  // # specialpagealiases: A list of all special page aliases
  // # magicwords: A list of magic words and their aliases
  // # statistics: Site statistics à la Special:Statistics

  public static final String INTERWIKIMAP = "interwikimap";
  // : A list of all interwiki prefixes and where they go
  // # dbrepllag: Get information about the database server with the highest
  // replication lag
  // # usergroups: A list of all user groups and their permissions
  // # extensions: A list of extensions installed on the wiki
  // # fileextensions: A list of file extensions allowed to be uploaded
  // # rightsinfo: Get information about the license governing the wiki's
  // content

  /** inits with parameters {@link #GENERAL}, {@link #NAMESPACES}, {@link #INTERWIKIMAP}. */
  public Siteinfo() {
    this(GENERAL, NAMESPACES, INTERWIKIMAP);
  }

  /** @param types the, see {@link #GENERAL}, {@link #INTERWIKIMAP}, ... */
  public Siteinfo(String... types) {
    String result = MediaWiki.pipeJoined(types);
    msg =
        new ApiRequestBuilder() //
            .action("query") //
            .formatXml() //
            .param("meta", "siteinfo") //
            .param("siprop", MediaWiki.urlEncode(result)) //
            .buildGet();
  }

  /** {@inheritDoc} */
  @Override
  public HttpAction getNextMessage() {
    return msg;
  }

  @Override
  protected void findContent(final XmlElement root) {
    super.findContent(root);
    for (XmlElement xmlElement : root.getChildren()) {
      if (xmlElement.getQualifiedName().equalsIgnoreCase("ns")) {
        Integer id = Integer.parseInt(xmlElement.getAttributeValue("id"));
        String name = xmlElement.getText();
        namespaces.put(id, name);

      } else if (xmlElement.getQualifiedName().equalsIgnoreCase("iw")) {
        if (xmlElement.hasAttribute("prefix")) {
          String prefix = xmlElement.getAttributeValue("prefix");
          String name = xmlElement.getAttributeValue("url");
          interwiki.put(prefix, name);
        }
      } else {
        findContent(xmlElement);
      }
    }
  }

  /** @return of */
  public ImmutableMap<Integer, String> getNamespaces() {
    return ImmutableMap.copyOf(namespaces);
  }

  /** @return of */
  public ImmutableMap<String, String> getInterwikis() {
    return ImmutableMap.copyOf(interwiki);
  }
}
