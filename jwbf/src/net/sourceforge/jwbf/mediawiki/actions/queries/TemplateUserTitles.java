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

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.core.actions.util.ProcessException;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.actions.util.SupportedBy;
import net.sourceforge.jwbf.mediawiki.actions.util.VersionException;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import org.apache.log4j.Logger;


/**
 * action class using the MediaWiki-api's "list=embeddedin" that is used to find
 * all articles which use a template.
 * 
 * @author Tobias Knerr
 * @author Thomas Stock
 * @since MediaWiki 1.9.0
 * 
 */
@SupportedBy({ MW1_09, MW1_10, MW1_11, MW1_12, MW1_13, MW1_14, MW1_15, MW1_16 })
public class TemplateUserTitles extends TitleQuery<String> {

  /** constant value for the eilimit-parameter. **/
  private static final int LIMIT = 50;
  private final MediaWikiBot bot;
  /**
   * Collection that will contain the result
   * (titles of articles using the template)
   * after performing the action has finished.
   */
  private Collection<String> titleCollection = new ArrayList<String>();


  private final String templateName;
  private final int [] namespaces;


  private Logger log = Logger.getLogger(getClass());
  /**
   * The public constructor. It will have an MediaWiki-request generated,
   * which is then added to msgs. When it is answered,
   * the method processAllReturningText will be called
   * (from outside this class).
   * For the parameters, see {@link TemplateUserTitles#generateRequest(String, String, String)}
   */
  public TemplateUserTitles(MediaWikiBot bot, String templateName, int ... namespaces) throws VersionException {
    super(bot);
    this.bot = bot;
    this.templateName = templateName;
    this.namespaces = namespaces;

  }


  /**
   * generates the next MediaWiki-request (GetMethod) and adds it to msgs.
   *
   * @param templateName   the name of the template,
   *                      may only be null if eicontinue is not null
   * @param namespace     the namespace(s) that will be searched for links,
   *                      as a string of numbers separated by '|';
   *                      if null, this parameter is omitted
   * @param eicontinue    the value for the eicontinue parameter,
   *                      null for the generation of the initial request
   */
  private HttpAction generateRequest(String templateName, String namespace,
      String eicontinue) {

    String uS = "";
    String titleVal = "";
    if (eicontinue == null) {
      switch (bot.getVersion()) {
        case MW1_09:
        case MW1_10:
          titleVal = "&titles=";
          break;

        default:
          titleVal = "&eititle=";
          break;
      }

      uS = "/api.php?action=query&list=embeddedin"

        + titleVal
        + MediaWiki.encode(templateName)
        + ((namespace != null && namespace.length() != 0) ? ("&einamespace=" + MediaWiki.encode(namespace))
            : "") + "&eilimit=" + LIMIT + "&format=xml";

    } else {

      uS = "/api.php?action=query&list=embeddedin" + "&eicontinue="
      + MediaWiki.encode(eicontinue) + "&eilimit=" + LIMIT
      + ((namespace != null && namespace.length() != 0) ? ("&einamespace=" + MediaWiki.encode(namespace)) : "")
      + "&format=xml";

    }

    return new Get(uS);

  }

  /**
   * deals with the MediaWiki api's response by parsing the provided text.
   *
   * @param s   the answer to the most recently generated MediaWiki-request
   *
   * @return empty string
   */
  public String processAllReturningText(final String s) throws ProcessException {
    //		System.out.println(s);
    parseArticleTitles(s);
    parseHasMore(s);
    titleIterator = titleCollection.iterator();
    return "";
  }

  /**
   * gets the information about a follow-up page from a provided api response.
   * If there is one, a new request is added to msgs by calling generateRequest.
   *	
   * @param s   text for parsing
   */
  @Override
  protected String parseHasMore(final String s) {

    // get the eicontinue-value

    Pattern p = Pattern.compile(
        "<query-continue>.*?"
        + "<embeddedin *eicontinue=\"([^\"]*)\" */>"
        + ".*?</query-continue>",
        Pattern.DOTALL | Pattern.MULTILINE);

    Matcher m = p.matcher(s);

    if (m.find()) {
      return m.group(1);

    } else {
      return "";

    }

  }

  /**
   * picks the article name from a MediaWiki api response.
   *	
   * @param s   text for parsing
   */
  @Override
  protected Collection<String> parseArticleTitles(String s) {

    // get the backlink titles and add them all to the titleCollection

    Pattern p = Pattern.compile(
    "<ei pageid=\".*?\" ns=\".*?\" title=\"(.*?)\" />");

    Matcher m = p.matcher(s);

    while (m.find()) {
      titleCollection.add(m.group(1));
    }

    return titleCollection;
  }




  @Override
  protected HttpAction prepareCollection() {

    if (getNextPageInfo().length() <= 0) {
      return generateRequest(templateName, MWAction
          .createNsString(namespaces), null);
    } else {
      return generateRequest(null, MWAction.createNsString(namespaces),
          getNextPageInfo());
    }

  }


  @Override
  protected Object clone() throws CloneNotSupportedException {
    try {
      return new TemplateUserTitles(bot, templateName, namespaces);
    } catch (VersionException e) {
      throw new CloneNotSupportedException(e.getLocalizedMessage());
    }
  }




}
