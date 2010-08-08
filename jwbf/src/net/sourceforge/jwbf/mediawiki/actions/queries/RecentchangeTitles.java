/*
 * Copyright 2007 Thomas Stock.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Contributors:
 *
 */
package net.sourceforge.jwbf.mediawiki.actions.queries;

import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_09;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_10;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_11;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_12;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_13;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_14;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_15;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_16;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.core.actions.util.ProcessException;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.actions.util.SupportedBy;
import net.sourceforge.jwbf.mediawiki.actions.util.VersionException;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

/**
 *
 * Gets a list of pages recently changed, ordered by modification timestamp.
 * Parameters: rcfrom (paging timestamp), rcto (flt), rcnamespace (flt), rcminor
 * (flt), rcusertype (dflt=not|bot), rcdirection (dflt=older), rclimit (dflt=10,
 * max=500/5000) F
 *
 * api.php ? action=query & list=recentchanges - List last 10 changes
 *
 * @author Thomas Stock
 */
@SupportedBy({ MW1_09, MW1_10, MW1_11, MW1_12, MW1_13, MW1_14, MW1_15, MW1_16 })
public class RecentchangeTitles extends TitleQuery<String> {

  /** value for the bllimit-parameter. **/
  private static final int limit = 10;


  private int find = 1;

  private final MediaWikiBot bot;

  private final int [] namespaces;
  private Logger log = Logger.getLogger(getClass());

  private class RecentInnerAction extends InnerAction {

    protected RecentInnerAction(Version v) throws VersionException {
      super(v);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String processAllReturningText(final String s) throws ProcessException {

      titleCollection.clear();
      parseArticleTitles(s);

      if (log.isInfoEnabled())
        log.info("found: " + titleCollection);
      if (uniqChanges) {
        HashSet<String> hs = new HashSet<String>();
        hs.addAll(titleCollection);
        titleCollection.clear();
        titleCollection.addAll(hs);
      }
      titleIterator = titleCollection.iterator();

      return "";
    }

  }

  /**
   * Collection that will contain the result
   * (titles of articles linking to the target)
   * after performing the action has finished.
   */
  private Collection<String> titleCollection = new Vector<String>();
  private final boolean uniqChanges;

  /**
   * information necessary to get the next api page.
   */




  /**
   * generates the next MediaWiki-request (GetMethod) and adds it to msgs.
   * @param namespace     the namespace(s) that will be searched for links,
   *                      as a string of numbers separated by '|';
   *                      if null, this parameter is omitted
   * @param rcstart timestamp
   */
  private HttpAction generateRequest(int [] namespace, String rcstart) {

    String uS = "";
    if (rcstart.length() > 0) {
      uS = "/api.php?action=query&list=recentchanges"

        + ((namespace != null) ? ("&rcnamespace=" + MediaWiki.encode(MWAction.createNsString(namespace))) : "")
        + "&rcstart=" + rcstart
        //+ "&rcusertype=" // (dflt=not|bot)
        + "&rclimit=" + limit + "&format=xml";
    } else {
      uS = "/api.php?action=query&list=recentchanges"

        + ((namespace != null) ? ("&rcnamespace=" + MediaWiki.encode(MWAction.createNsString(namespace))) : "")
        //+ "&rcminor="
        //+ "&rcusertype=" // (dflt=not|bot)
        + "&rclimit=" + limit + "&format=xml";
    }


    return new Get(uS);


  }

  private HttpAction generateRequest(int [] namespace) {

    return generateRequest(namespace, "");


  }

  /**
   *
   */
  public RecentchangeTitles(MediaWikiBot bot, int... ns) throws VersionException {
    this(bot, false, ns);

  }
  /**
   *
   */
  public RecentchangeTitles(MediaWikiBot bot, boolean uniqChanges, int... ns) throws VersionException {
    super(bot);
    namespaces = ns;
    this.bot = bot;
    this.uniqChanges = uniqChanges;

  }
  /**
   *
   */
  public RecentchangeTitles(MediaWikiBot bot) throws VersionException {
    this(bot, MediaWiki.NS_ALL);


  }







  /**
   * picks the article name from a MediaWiki api response.
   *
   * @param s   text for parsing
   */
  @Override
  protected Collection<String> parseArticleTitles(String s) {
    SAXBuilder builder = new SAXBuilder();
    Element root = null;
    try {
      Reader i = new StringReader(s);
      Document doc = builder.build(new InputSource(i));

      root = doc.getRootElement();

    } catch (JDOMException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    if (root != null)
      findContent(root);
    return titleCollection;


  }
  @SuppressWarnings("unchecked")
  private void findContent(final Element root) {

    Iterator<Element> el = root.getChildren().iterator();
    while (el.hasNext()) {
      Element element = el.next();
      if (element.getQualifiedName().equalsIgnoreCase("rc")) {
        if (find < limit) {
          titleCollection.add(MediaWiki.decode(element.getAttributeValue("title")));
        }

        nextPageInfo = element.getAttribute("timestamp").getValue();
        find++;
      } else {
        findContent(element);
      }

    }
  }





  @Override
  protected HttpAction prepareCollection() {
    find = 1;
    if (getNextPageInfo().length() <= 0) {
      return generateRequest(namespaces);
    } else {
      return generateRequest(namespaces, getNextPageInfo());
    }

  }





  @Override
  protected Object clone() throws CloneNotSupportedException {

    try {
      return new RecentchangeTitles(bot, uniqChanges, namespaces);
    } catch (VersionException e) {
      throw new CloneNotSupportedException(e.getLocalizedMessage());
    }
  }

  @Override
  protected String parseHasMore(String s) {
    return "";
  }

  @Override
  protected InnerAction getInnerAction(
      Version v) throws VersionException {

    return new RecentInnerAction(v);
  }



}
