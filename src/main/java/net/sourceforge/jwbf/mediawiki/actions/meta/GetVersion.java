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
 * Carlos Valenzuela
 */

package net.sourceforge.jwbf.mediawiki.actions.meta;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

import net.sourceforge.jwbf.JWBF;
import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.mapper.XmlConverter;
import net.sourceforge.jwbf.mapper.XmlElement;
import net.sourceforge.jwbf.mediawiki.ApiRequestBuilder;
import net.sourceforge.jwbf.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;

/**
 * Basic action to receive {@link Version}.
 *
 * @author Thomas Stock
 */
public class GetVersion extends MWAction {

  private static final Logger log = LoggerFactory.getLogger(GetVersion.class);

  private final Get msg;
  private String generator = "";
  private String sitename = "";
  private String base = "";
  private String theCase = "";
  private String mainpage = "";

  public static final Set<String> GENERATOR_EXT = Sets.newHashSet();

  static {
    GENERATOR_EXT.add("alpha");
    GENERATOR_EXT.add("wmf");
  }

  /** Create the request. */
  public GetVersion() {
    msg =
        new ApiRequestBuilder() //
            .action("query") //
            .formatXml() //
            .param("meta", "siteinfo") //
            .buildGet();
  }

  private void parse(final String xml) {
    XmlElement rootElement = XmlConverter.getRootElementWithError(xml);
    // XXX ignore errors here => fallback to unknown version
    findContent(rootElement);
  }

  /** {@inheritDoc} */
  @Override
  public final String processAllReturningText(final String s) {
    parse(s);
    return "";
  }

  /** @return the, like "Wikipedia" */
  public String getSitename() {
    return sitename;
  }

  /** @return the, like "http://de.wikipedia.org/wiki/Wikipedia:Hauptseite" */
  public String getBase() {
    return base;
  }

  /** @return the, like "first-letter" */
  public String getCase() {
    return theCase;
  }

  public Version getVersion() {
    for (String generatorFragment : GENERATOR_EXT) {
      if (getGenerator().contains(generatorFragment)) {
        return Version.DEVELOPMENT;
      }
    }

    for (Version version : Version.values()) {
      if (getGenerator().contains(version.getNumber())) {
        return version;
      }
    }
    if (log.isDebugEnabled()) {
      log.debug(
          "\nVersion is UNKNOWN for JWBF ("
              + JWBF.getVersion(getClass())
              + ") : \n\t"
              + getGenerator()
              + "\n\t"
              + "supported versions: "
              + Joiner.on(" ").join(Version.values())
              + "\n\t"
              + "\n\tUsing settings for actual Wikipedia development version");
    }
    return Version.UNKNOWN;
  }

  /** @return the MediaWiki Generator, like "MediaWiki 1.16alpha" */
  public String getGenerator() {
    return generator;
  }

  /** @return the, like "Main Page" */
  public String getMainpage() {
    return mainpage;
  }

  protected void findContent(final XmlElement root) {

    for (XmlElement xmlElement : root.getChildren()) {
      if (xmlElement.getQualifiedName().equalsIgnoreCase("general")) {

        mainpage = xmlElement.getAttributeValue("mainpage");
        base = xmlElement.getAttributeValue("base");
        sitename = xmlElement.getAttributeValue("sitename");
        generator = xmlElement.getAttributeValue("generator");
        theCase = xmlElement.getAttributeValue("case");
      } else {
        // FIXME recursion is bad
        findContent(xmlElement);
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public HttpAction getNextMessage() {
    return msg;
  }
}
