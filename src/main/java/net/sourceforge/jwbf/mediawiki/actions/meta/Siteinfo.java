package net.sourceforge.jwbf.mediawiki.actions.meta;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.extractXml.Element;
import net.sourceforge.jwbf.mediawiki.ApiRequestBuilder;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;

import com.google.common.collect.Maps;

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
  public static final String NAMESPACES = "namespaces"; // : A list of all
                                                        // namespaces
  // # namespacealiases: A list of all namespace aliases
  // # specialpagealiases: A list of all special page aliases
  // # magicwords: A list of magic words and their aliases
  // # statistics: Site statistics à la Special:Statistics

  public static final String INTERWIKIMAP = "interwikimap"; // : A list of all
                                                            // interwiki
                                                            // prefixes and
                                                            // where they go

  // # dbrepllag: Get information about the database server with the highest
  // replication lag
  // # usergroups: A list of all user groups and their permissions
  // # extensions: A list of extensions installed on the wiki
  // # fileextensions: A list of file extensions allowed to be uploaded
  // # rightsinfo: Get information about the license governing the wiki's
  // content
  /**
   * inits with parameters {@link #GENERAL}, {@link #NAMESPACES}, {@link #INTERWIKIMAP}.
   */
  public Siteinfo() {
    this(GENERAL, NAMESPACES, INTERWIKIMAP);
  }

  /**
   * @param types
   *          the, see {@link #GENERAL}, {@link #INTERWIKIMAP}, ...
   */
  public Siteinfo(String... types) {
    StringBuffer x = new StringBuffer();
    for (int i = 0; i < types.length; i++) {
      x.append(types[i] + "|");
    }
    String result = x.substring(0, x.length() - 1);
    msg = new ApiRequestBuilder() //
        .action("query") //
        .formatXml() //
        .param("meta", "siteinfo") //
        .param("siprop", MediaWiki.encode(result)) //
        .buildGet();

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public HttpAction getNextMessage() {
    return msg;
  }

  @Override
  @SuppressWarnings("unchecked")
  protected void findContent(final Element root) {
    super.findContent(root);
    Iterator<Element> el = root.getChildren().iterator();
    while (el.hasNext()) {
      Element element = el.next();
      if (element.getQualifiedName().equalsIgnoreCase("ns")) {
        Integer id = Integer.parseInt(element.getAttributeValue("id"));
        String name = element.getText();
        addNamespace(id, name);

      } else if (element.getQualifiedName().equalsIgnoreCase("iw")) {
        if (element.hasAttribute("prefix")) {
          String prefix = element.getAttributeValue("prefix");
          String name = element.getAttributeValue("url");
          addInterwiki(prefix, name);
        }
      } else {
        findContent(element);
      }
    }
  }

  private void addNamespace(Integer id, String name) {
    namespaces.put(id, name);
  }

  /**
   * @return of
   */
  public Map<Integer, String> getNamespaces() {
    return Collections.unmodifiableMap(namespaces);
  }

  private void addInterwiki(String prefix, String name) {
    interwiki.put(prefix, name);
  }

  /**
   * @return of
   */
  public Map<String, String> getInterwikis() {
    return Collections.unmodifiableMap(interwiki);
  }

}
