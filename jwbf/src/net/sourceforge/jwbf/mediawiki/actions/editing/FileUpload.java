
/*
 * Copyright 2007 Justus Bisser.
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
 * Thomas Stock
 */
package net.sourceforge.jwbf.mediawiki.actions.editing;


import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_11;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_12;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_13;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_14;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_15;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_16;

import java.io.FileNotFoundException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.core.actions.Post;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.core.actions.util.ProcessException;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.actions.util.SupportedBy;
import net.sourceforge.jwbf.mediawiki.actions.util.VersionException;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import net.sourceforge.jwbf.mediawiki.contentRep.SimpleFile;

import org.apache.log4j.Logger;

/**
 * <p>
 * To allow your bot to upload media in your MediaWiki. Add at least the following line
 * to your MediaWiki's LocalSettings.php:<br>
 *
 * <pre>
 * $wgEnableUploads = true;
 * </pre>
 * 
 * For more details see also
 * <a href="http://www.mediawiki.org/wiki/Help:Configuration_settings#Uploads">Upload Config</a>
 * 
 * @author Justus Bisser
 * @author Thomas Stock
 * 
 */
@SupportedBy({ MW1_11, MW1_12, MW1_13, MW1_14, MW1_15, MW1_16 })
public class FileUpload extends MWAction {


  private static final Logger LOG = Logger.getLogger(FileUpload.class);
  private final Get g;
  private boolean first = true;
  private boolean second = true;
  private final SimpleFile a;
  private Post msg;
  /**
   * 
   * @param a the
   * @param bot a
   * @throws ActionException on problems with file
   * @throws VersionException on wrong MediaWiki version
   */
  public FileUpload(final SimpleFile a, MediaWikiBot bot) throws ActionException, VersionException {
    super(bot.getVersion());
    if (!a.getFile().isFile() || !a.getFile().canRead()) {
      throw new ActionException("no such file " + a.getFile());
    }

    if (!bot.isLoggedIn()) {
      throw new ActionException("Please login first");
    }


    this.a = a;
    String uS = "/index.php?title="
      + MediaWiki.encode(a.getTitle())
      + "&action=edit&dontcountme=s";

    g = new Get(uS);

  }

  /**
   * 
   * @param filename to uplad
   * @param bot a
   * @throws ActionException on problems with file
   * @throws VersionException on wrong MediaWiki version
   */
  public FileUpload(MediaWikiBot bot, String filename) throws ActionException, VersionException {
    this(new SimpleFile(filename), bot);
  }
  /**
   * {@inheritDoc}
   */
  public HttpAction getNextMessage() {
    if (first) {
      first = false;
      return g;
    }
    String uS = "";
    // try {
    uS = "/Spezial:Hochladen";
    uS = "/index.php?title=Special:Upload";
    // uS = "/index.php?title=" + URLEncoder.encode("Spezial:Hochladen",
    // MediaWikiBot.CHARSET);
    // + "&action=submit";
    // } catch (UnsupportedEncodingException e) {
    // e.printStackTrace();
    // }

    try {

      LOG.info("WRITE: " + a.getTitle());
      Post post = new Post(uS);

      if (a.getText().length() == 0) {
        post.addParam("wpDestFile", a.getTitle());

        post.addParam("wpIgnoreWarning", "true");
        post.addParam("wpSourceType", "file");
        post.addParam("wpUpload", "Upload file");
        //				 post.addParam("wpUploadDescription", "false");
        //				 post.addParam("wpWatchthis", "false");

        post.addParam("wpUploadFile", a.getFile());
        // new FilePart( f.getName(), f)


      } else {
        post.addParam("wpDestFile", a.getTitle());

        post.addParam("wpIgnoreWarning", "true");
        post.addParam("wpSourceType", "file");
        post.addParam("wpUpload", "Upload file");
        // new StringPart("wpUploadDescription", "false"),
        // new StringPart("wpWatchthis", "false"),

        post.addParam("wpUploadFile", a.getFile());
        // new FilePart( f.getName(), f)
        post.addParam("wpUploadDescription", a.getText());


      }
      if (!a.getFile().exists()) {
        throw new FileNotFoundException();
      }


      msg = post;
      second = false;
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return msg;
  }
  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasMoreMessages() {
    return first || second;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String processAllReturningText(String s) throws ProcessException {

    if (s.contains("error")) {
      Pattern errFinder = Pattern
      .compile("<p>(.*?)</p>",
          Pattern.DOTALL | Pattern.MULTILINE);
      Matcher m = errFinder.matcher(s);
      String lastP = "";
      while (m.find()) {
        lastP = MediaWiki.decode(m.group(1));
        LOG.error("Upload failed: " + lastP);
      }

      throw new ProcessException("Upload failed - " + lastP);
    }
    return "";
  }


}
