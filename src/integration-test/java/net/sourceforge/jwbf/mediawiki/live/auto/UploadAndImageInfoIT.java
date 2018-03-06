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
package net.sourceforge.jwbf.mediawiki.live.auto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;

import javax.imageio.ImageIO;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.google.common.io.Resources;

import net.sourceforge.jwbf.GAssert;
import net.sourceforge.jwbf.JWBF;
import net.sourceforge.jwbf.TestHelper;
import net.sourceforge.jwbf.core.actions.util.ProcessException;
import net.sourceforge.jwbf.mediawiki.BotFactory;
import net.sourceforge.jwbf.mediawiki.LiveTestFather;
import net.sourceforge.jwbf.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.editing.FileUpload;
import net.sourceforge.jwbf.mediawiki.actions.queries.ImageInfo;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import net.sourceforge.jwbf.mediawiki.contentRep.SimpleFile;

/** @author Thomas Stock */
public class UploadAndImageInfoIT extends ParamHelper {

  private static final Logger log = LoggerFactory.getLogger(UploadAndImageInfoIT.class);

  @Parameters(name = "{0}")
  public static Collection<?> stableWikis() {
    return ParamHelper.prepare(Version.valuesStable());
  }

  public UploadAndImageInfoIT(Version v) {
    super(BotFactory.getMediaWikiBot(v, true));
  }

  @Test
  public final void upload() {
    generalUploadImageInfoTest(bot);
  }

  @Test
  public final void imageInfoFail() {
    String name = "UnknownImage.jpg";
    ImageInfo a = new ImageInfo(bot, name);
    try {
      a.getUrlAsString();
      fail();
    } catch (ProcessException e) {
      GAssert.assertStartsWith(
          String.format("no url for image with name \"%s\"", name), e.getMessage());
    }
  }

  /** Test to delete an image. */
  @Test
  public final void deleteImage() {
    generalUploadImageInfoTest(bot);
    String testFilename = LiveTestFather.getValue("filename");
    String urlAsString = new ImageInfo(bot, testFilename).getUrlAsString();
    assertTrue(urlAsString.endsWith("Test.gif"));
    bot.delete("File:" + testFilename);

    try {
      new ImageInfo(bot, testFilename).getUrlAsString();
      fail("file was found ");
    } catch (ProcessException e) {
      GAssert.assertStartsWith("no url for image with name \"Test.gif\"", e.getMessage());
    }
  }

  private void generalUploadImageInfoTest(MediaWikiBot bot) {
    String validFileName = LiveTestFather.getValue("validFile");
    File validFile = new File(validFileName);
    assertTrue("File (" + validFileName + ") not readable", validFile.canRead());
    String testFilename = LiveTestFather.getValue("filename");
    SimpleFile sf = new SimpleFile(testFilename, validFileName);
    sf.setText(TestHelper.getRandomAlph(10));
    String name = "File:" + testFilename;
    bot.delete(name);
    BufferedImage img = toImage(sf);
    int upWidth = img.getWidth();
    int upHeight = img.getHeight();
    FileUpload up = new FileUpload(sf, bot);

    bot.getPerformedAction(up);
    URL url = getUrl(bot, sf, ImmutableMap.<String, String>of());
    assertIdentical(url, validFile);
    assertImageDimension(url, upWidth, upHeight);

    // TODO bad values, try others
    int newWidth = 50;
    int newHeight = 50;
    ImmutableMap<String, String> params =
        ImmutableMap.of( //
            ImageInfo.HEIGHT, newHeight + "", //
            ImageInfo.WIDTH, newWidth + "" //
            );
    URL urlSizeVar = getUrl(bot, sf, params);
    assertImageDimension(urlSizeVar, newWidth, newHeight);
    String text = bot().getArticle(name).getText();
    assertEquals(sf.getText(), text);
  }

  private URL getUrl(MediaWikiBot bot, SimpleFile sf, ImmutableMap<String, String> params) {
    try {
      return new ImageInfo(bot, sf.getTitle(), params).getUrl();
    } catch (ProcessException e) {
      throw ProcessException.joinMsgs(e, "; \n is upload enabled? $wgEnableUploads = true;");
    }
  }

  private static BufferedImage toImage(SimpleFile sf) {
    try {
      return ImageIO.read(sf.getFile());
    } catch (IOException e) {
      throw new IllegalArgumentException(e);
    }
  }

  private static void assertIdentical(URL url, File file) {
    filesAreIdentical(download(url.toExternalForm()), file);
  }

  private static void assertImageDimension(URL url, int width, int height) {
    try {
      BufferedImage img = ImageIO.read(url);
      assertEquals(height, img.getHeight());
      assertEquals(width, img.getWidth());
    } catch (IOException e) {
      throw new IllegalArgumentException(e);
    }
  }

  private static File download(String url) {
    try {
      File tempFile = File.createTempFile("jwbf", "gif");
      tempFile.deleteOnExit();
      Files.write(Resources.toByteArray(JWBF.newURL(url)), tempFile);
      return tempFile;
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  private static void filesAreIdentical(File left, File right) {
    String leftHash = hashFile(left);
    String rightHash = hashFile(right);
    assertEquals(leftHash, rightHash);
  }

  private static String hashFile(File file) {
    try {
      return Hashing.sha1().hashBytes(Files.toByteArray(file)).toString();
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }
}
