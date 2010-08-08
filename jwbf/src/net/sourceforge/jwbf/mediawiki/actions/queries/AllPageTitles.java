/*
 * Copyright 2007 Tobias Knerr.
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
 * Tobias Knerr
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

import java.util.Collection;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.actions.util.RedirectFilter;
import net.sourceforge.jwbf.mediawiki.actions.util.SupportedBy;
import net.sourceforge.jwbf.mediawiki.actions.util.VersionException;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import org.apache.log4j.Logger;

/**
 * Action class using the MediaWiki-api's "list=allpages".
 * 
 * @author Tobias Knerr
 * @author Thomas Stock
 * 
 */
@SupportedBy({ MW1_09, MW1_10, MW1_11, MW1_12, MW1_13, MW1_14, MW1_15, MW1_16 })
public class AllPageTitles extends TitleQuery<String> {

  private static final Logger LOG = Logger.getLogger(AllPageTitles.class);

  /** Pattern to parse returned page, @see {@link #parseHasMore(String)}. */
  private static final Pattern HAS_MORE_PATTERN = Pattern
  .compile(
      "<query-continue>.*?<allpages *apfrom=\"([^\"]*)\" */>.*?</query-continue>",
      Pattern.DOTALL | Pattern.MULTILINE);
  private static final Pattern ARTICLE_TITLES_PATTERN = Pattern
  .compile("<p pageid=\".*?\" ns=\".*?\" title=\"(.*?)\" />");
  /** Pattern to parse returned page, @see {@link #parseArticleTitles(String)} */
  /** Constant value for the aplimit-parameter. **/
  private static final int LIMIT = 50;


  /**
   * Information given in the constructor, necessary for creating next action.
   */
  private String prefix;
  private String namespaces;

  private MediaWikiBot bot;

  private String from;

  private RedirectFilter rf;



  /**
   * The public constructor. It will have an MediaWiki-request generated,
   * which is then added to msgs. When it is answered, the method
   * processAllReturningText will be called (from outside this class). For the
   * parameters, see
   * {@link AllPageTitles#generateRequest(String, String, boolean, boolean, String)}
   * 
   * @param from
   *            page title to start from, may be null
   * @param prefix
   *            restricts search to titles that begin with this value, may be
   *            null
   * @param rf
   *            include redirects in the list
   * @param bot a
   * @param namespaces
   *            the namespace(s) that will be searched for links, as a string
   *            of numbers separated by '|'; if null, this parameter is
   *            omitted
   * @throws VersionException if version is incompatible
   */
  public AllPageTitles(MediaWikiBot bot, String from,
      String prefix, RedirectFilter rf, int ... namespaces) throws VersionException {
    this(bot, from, prefix, rf, MWAction.createNsString(namespaces));

  }
  /**
   * 
   * @param bot a
   * @param namespaces the
   * @throws VersionException if version is incompatible
   */
  public AllPageTitles(MediaWikiBot bot, int ... namespaces) throws VersionException {
    this(bot, null, null, RedirectFilter.nonredirects, namespaces);

  }

  /**
   * @param from a
   * @param prefix a
   * @param rf the
   * @param namespaces the
   * @param bot the
   * @throws VersionException if not supported
   */
  protected AllPageTitles(MediaWikiBot bot, String from
      , String prefix, RedirectFilter rf, String namespaces) throws VersionException {
    super(bot);


    this.bot = bot;
    this.rf = rf;
    this.prefix = prefix;
    this.namespaces = namespaces;
    this.from = from;
    generateRequest(from, prefix, rf, namespaces);

  }

  /**
   * Generates the next MediaWiki-request (GetMethod) and adds it to msgs.
   * 
   * @param from
   *            page title to start from, may be null
   * @param prefix
   *            restricts search to titles that begin with this value, may be
   *            null
   * @param rf include redirects in the list
   * @param namespace
   *            the namespace(s) that will be searched for links, as a string
   *            of numbers separated by '|'; if null, this parameter is
   *            omitted
   * @return a
   */
  private Get generateRequest(String from, String prefix,
      RedirectFilter rf, String namespace) {
    if (LOG.isTraceEnabled()) {
      LOG.trace("enter GetAllPagetitles.generateRequest"
          + "(String,String,boolean,boolean,String)");
    }

    String apfilterredir;
    if (rf == RedirectFilter.all) {
      apfilterredir = "all";
    } else if (rf == RedirectFilter.redirects) {
      apfilterredir = "redirects";
    } else {
      apfilterredir = "nonredirects";
    }


    String uS = "/api.php?action=query&list=allpages&"
      + ((from != null && from.length() > 0) ? ("&apfrom=" + MediaWiki.encode(from)) : "")
      + ((prefix != null) ? ("&apprefix=" + MediaWiki.encode(prefix))
          : "")
          + ((namespace != null && namespace.length() != 0) ? ("&apnamespace=" + namespace)
              : "") + "&apfilterredir=" + apfilterredir + "&aplimit="
              + LIMIT + "&format=xml";
    return new Get(uS);

  }



  /**
   * Picks the article name from a MediaWiki api response.
   * 
   * @param s
   *            text for parsing
   * @return a
   */
  @Override
  protected Collection<String> parseArticleTitles(String s) {
    if (LOG.isTraceEnabled()) {
      LOG.trace("enter GetAllPagetitles.parseArticleTitles(String)");
    }
    Collection<String> c = new Vector<String>();
    Matcher m = ARTICLE_TITLES_PATTERN.matcher(s);
    while (m.find()) {
      String title = MediaWiki.decode(m.group(1));
      if (LOG.isDebugEnabled()) {
        LOG.debug("Found article title: \"" + title + "\"");
      }
      c.add(title);
    }
    return c;
  }
  /**
   * Gets the information about a follow-up page from a provided api response.
   * If there is one, a new request is added to msgs by calling
   * generateRequest. If no exists, the string is empty.
   * 
   * @param s
   *            text for parsing
   * @return the
   */
  @Override
  protected String parseHasMore(final String s) {
    if (LOG.isTraceEnabled()) {
      LOG.trace("enter GetAllPagetitles.parseHasMore(String)");
    }
    Matcher m = HAS_MORE_PATTERN.matcher(s);
    if (m.find()) {
      return  m.group(1);
    } else {
      return "";
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected HttpAction prepareCollection() {

    return generateRequest(getNextPageInfo(), prefix, rf, namespaces);

  }
  /**
   * {@inheritDoc}
   */
  @Override
  protected Object clone() throws CloneNotSupportedException {
    try {
      return new AllPageTitles(bot, from, prefix, rf, namespaces);
    } catch (VersionException e) {
      throw new CloneNotSupportedException(e.getLocalizedMessage());
    }
  }


}
