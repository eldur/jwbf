package net.sourceforge.jwbf.mediawiki.actions.meta;

import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_11;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_12;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_13;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_14;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_15;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_16;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.util.SupportedBy;

import org.jdom.Element;
/**
 * Gets details from the given MediaWiki installation like installed version.
 * @author Thomas Stock
 * @see Siteinfo
 *
 */
@SupportedBy({ MW1_11, MW1_12, MW1_13, MW1_14, MW1_15, MW1_16 })
public class Siteinfo extends GetVersion {


  private Get msg;
  private Map<Integer, String> namespaces = new HashMap<Integer, String>();
  private Map<String, String> interwiki = new HashMap<String, String>();

  public static final String GENERAL = "general";
  public static final String NAMESPACES = "namespaces"; // : A list of all namespaces
  //	# namespacealiases: A list of all namespace aliases
  //	# specialpagealiases: A list of all special page aliases
  //	# magicwords: A list of magic words and their aliases
  //	# statistics: Site statistics à la Special:Statistics
  /**
   * @since {@link Version#MW1_11}
   */
  public static final String INTERWIKIMAP = "interwikimap"; // : A list of all interwiki prefixes and where they go
  //	# dbrepllag: Get information about the database server with the highest replication lag
  //	# usergroups: A list of all user groups and their permissions
  //	# extensions: A list of extensions installed on the wiki
  //	# fileextensions: A list of file extensions allowed to be uploaded
  //	# rightsinfo: Get information about the license governing the wiki's content
  /**
   *
   * inits with parameters {@link #GENERAL}, {@link #NAMESPACES}, {@link #INTERWIKIMAP}.
   */
  public Siteinfo() {
    this(GENERAL, NAMESPACES, INTERWIKIMAP);
  }
  /**
   *
   * @param types the, see {@link #GENERAL}, {@link #INTERWIKIMAP}, ...
   */
  public Siteinfo(String... types) {
    StringBuffer x = new StringBuffer();
    for (int i = 0; i < types.length; i++) {
      x.append(types[i] + "|");
    }
    String result = x.substring(0, x.length() - 1);
    msg = new Get("/api.php?action=query&meta=siteinfo" + "&siprop="
        + MediaWiki.encode(result) + "&format=xml");
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
        if (element.getAttribute("prefix") != null) {
          String prefix = element
          .getAttributeValue("prefix");
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
   *
   * @return of
   */
  public Map<Integer, String> getNamespaces() {
    return Collections.unmodifiableMap(namespaces);
  }


  /**
   *
   * @return of
   */
  public int [] getNamespacesArray() {
    Set<Integer> ks = getNamespaces().keySet();
    int [] x = new int [ks.size()];
    int i = 0;
    for (int value : ks) {
      x[i++] = value;
    }
    return x;
  }

  private void addInterwiki(String prefix, String name) {
    interwiki.put(prefix, name);
  }
  /**
   *
   * @return of
   */
  public Map<String, String> getInterwikis() {
    return Collections.unmodifiableMap(interwiki);
  }


}
