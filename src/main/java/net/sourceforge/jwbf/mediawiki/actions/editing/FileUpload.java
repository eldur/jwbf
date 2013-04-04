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

import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_15;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_16;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_17;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_18;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_19;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_20;

import java.io.FileNotFoundException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.core.actions.Post;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.core.actions.util.ProcessException;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.actions.util.SupportedBy;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import net.sourceforge.jwbf.mediawiki.contentRep.SimpleFile;

/**
 * <p>
 * To allow your bot to upload media in your MediaWiki. Add at least the following line to your
 * MediaWiki's LocalSettings.php:<br>
 * 
 * <pre>
 * $wgEnableUploads = true;
 * </pre>
 * 
 * For more details see also <a
 * href="http://www.mediawiki.org/wiki/Help:Configuration_settings#Uploads" >Upload Config</a>
 * 
 * @author Justus Bisser
 * @author Thomas Stock
 * 
 */
@Slf4j
@SupportedBy({ MW1_15, MW1_16, MW1_17, MW1_18, MW1_19, MW1_20 })
public class FileUpload extends MWAction {

  private final Get g;
  private boolean first = true;
  private boolean second = true;
  private final SimpleFile a;
  private Post msg;

  public FileUpload(final SimpleFile a, MediaWikiBot bot) {
    super(bot.getVersion());
    if (!a.getFile().isFile() || !a.getFile().canRead()) {
      throw new ActionException("no such file " + a.getFile());
    }

    if (!bot.isLoggedIn()) {
      throw new ActionException("Please login first");
    }

    this.a = a;
    String uS = MediaWiki.URL_INDEX + "?title=" + MediaWiki.encode(a.getTitle())
        + "&action=edit&dontcountme=s";

    g = new Get(uS);

  }

  public FileUpload(MediaWikiBot bot, String filename) {
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
    uS = MediaWiki.URL_INDEX + "?title=Special:Upload";
    // uS = "/index.php?title=" + URLEncoder.encode("Spezial:Hochladen",
    // MediaWikiBot.CHARSET);
    // + "&action=submit";
    // } catch (UnsupportedEncodingException e) {
    // e.printStackTrace();
    // }

    try {

      log.info("WRITE: " + a.getTitle());
      Post post = new Post(uS);

      if (a.getText().length() == 0) {
        post.addParam("wpDestFile", a.getTitle());

        post.addParam("wpIgnoreWarning", "true");
        post.addParam("wpSourceType", "file");
        post.addParam("wpUpload", "Upload file");
        // post.addParam("wpUploadDescription", "false");
        // post.addParam("wpWatchthis", "false");

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
      throw new RuntimeException(e);
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
  public String processAllReturningText(String s) {

    if (s.contains("error")) {
      Pattern errFinder = Pattern.compile("<p>(.*?)</p>", Pattern.DOTALL | Pattern.MULTILINE);
      Matcher m = errFinder.matcher(s);
      String lastP = "";
      while (m.find()) {
        lastP = MediaWiki.decode(m.group(1));
        log.error("Upload failed: " + lastP);
      }

      throw new ProcessException("Upload failed - " + lastP);
    }
    return "";
  }

}
