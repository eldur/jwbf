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

import static net.sourceforge.jwbf.mediawiki.BotFactory.getMediaWikiBot;
import static net.sourceforge.jwbf.mediawiki.LiveTestFather.getValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;

import javax.imageio.ImageIO;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.actions.util.ProcessException;
import net.sourceforge.jwbf.mediawiki.VersionTestClassVerifier;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.editing.FileUpload;
import net.sourceforge.jwbf.mediawiki.actions.queries.ImageInfo;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import net.sourceforge.jwbf.mediawiki.contentRep.SimpleFile;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Verifier;
import org.junit.runners.Parameterized.Parameters;

/**
 * 
 * @author Thomas Stock
 * 
 */
@Slf4j
public class UploadAndImageInfoTest extends ParamHelper {

  @ClassRule
  public static VersionTestClassVerifier classVerifier = new VersionTestClassVerifier(
      FileUpload.class, ImageInfo.class);

  @Rule
  public Verifier successRegister = classVerifier.getSuccessRegister(this);

  @Parameters(name = "{0}")
  public static Collection<?> stableWikis() {
    return ParamHelper.prepare(Version.valuesStable());
  }

  public UploadAndImageInfoTest(Version v) {
    super(v);
  }

  @Test
  public final void upload() {
    if (bot.getVersion().greaterEqThen(Version.MW1_17)) {
      // TODO api upload
      Assume.assumeTrue("api upload is missing", false);
    }
    generalUploadImageInfoTest(bot);

  }

  /**
   * because image does not exists.
   * 
   * @throws Exception
   *           a
   */
  @Test(expected = ProcessException.class)
  public final void imageInfoFail() throws Exception {

    bot = getMediaWikiBot(Version.getLatest(), true);
    ImageInfo a = new ImageInfo(bot, "UnknownImage.jpg");
    log.info(a.getUrlAsString());

  }

  /**
   * because image does not exists.
   * 
   * @throws Exception
   *           a
   */
  @Test(expected = ActionException.class)
  public final void imageInfoPerformManual() throws Exception {

    ImageInfo a = new ImageInfo(bot, "UnknownImage.jpg");
    bot.performAction(a);

  }

  /**
   * Test to delete an image.
   * 
   * @throws Exception
   *           a
   */
  @Test(expected = ProcessException.class)
  public final void deleteImage() throws Exception {
    generalUploadImageInfoTest(bot);
    bot.delete("File:" + getValue("filename"));

    try {

      new URL(new ImageInfo(bot, getValue("filename")).getUrlAsString());
    } catch (ProcessException e) {
      throw new ProcessException(e.getLocalizedMessage() + "; \n is upload enabled ?");
    }
    fail("file was found ");
  }

  /**
   * 
   * @param bot
   *          a
   * @param v
   *          a
   * @throws Exception
   *           a
   */
  protected final void generalUploadImageInfoTest(MediaWikiBot bot) {
    assertTrue("File (" + getValue("validFile") + ") not readable",
        new File(getValue("validFile")).canRead());
    SimpleFile sf = new SimpleFile(getValue("filename"), getValue("validFile"));
    bot.delete("File:" + getValue("filename"));
    BufferedImage img = toImage(sf);
    int upWidth = img.getWidth();
    int upHeight = img.getHeight();
    FileUpload up = new FileUpload(sf, bot);

    bot.performAction(up);
    URL url = null;
    URL urlSizeVar = null;
    // TODO bad values, try others
    int newWidth = 50;
    int newHeight = 50;
    try {

      url = new ImageInfo(bot, sf.getTitle()).getUrl();
    } catch (ProcessException e) {
      throw new ProcessException(e.getLocalizedMessage()
          + "; \n is upload enabled? $wgEnableUploads = true;");
    }
    urlSizeVar = new ImageInfo(bot, sf.getTitle() //
        , new String[][] { //
        { ImageInfo.HEIGHT, newHeight + "" } //
            , { ImageInfo.WIDTH, newWidth + "" } //
        }).getUrl();
    Assert.assertTrue("file not found " + url, url.toExternalForm().length()
        - bot.getHostUrl().length() > 2);
    File file = new File(getValue("validFile"));
    assertFile(url, file);
    assertImageDimension(url, upWidth, upHeight);
    assertImageDimension(urlSizeVar, newWidth, newHeight);
  }

  protected BufferedImage toImage(SimpleFile sf) {
    try {
      return ImageIO.read(sf.getFile());
    } catch (IOException e) {
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * 
   * @param url
   *          a
   * @param file
   *          a
   * @throws Exception
   *           a
   */
  protected final void assertFile(URL url, File file) {
    File temp = new File("temp.file");

    download(url.toExternalForm(), temp);
    Assert.assertTrue("files are not ident", filesAreIdentical(temp, file));

    if (!temp.delete())
      throw new RuntimeException("unable to delete file");
  }

  protected void assertImageDimension(URL url, int width, int height) {
    try {
      BufferedImage img = ImageIO.read(url);
      assertEquals(height, img.getHeight());
      assertEquals(width, img.getWidth());
    } catch (IOException e) {
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * 
   * @param address
   *          a
   * @param localFileName
   *          a
   */
  protected static final void download(String address, File localFileName) {
    OutputStream out = null;
    URLConnection conn = null;
    InputStream in = null;
    try {
      URL url = new URL(address);
      out = new BufferedOutputStream(new FileOutputStream(localFileName));
      conn = url.openConnection();
      in = conn.getInputStream();
      byte[] buffer = new byte[1024];
      int numRead;
      long numWritten = 0;
      while ((numRead = in.read(buffer)) != -1) {
        out.write(buffer, 0, numRead);
        numWritten += numRead;
      }
      log.info(localFileName + "\t" + numWritten);
    } catch (Exception exception) {
      exception.printStackTrace();
    } finally {
      try {
        if (in != null) {
          in.close();
        }
        if (out != null) {
          out.close();
        }
      } catch (IOException ioe) {
        log.warn(ioe.getMessage());
      }
    }
  }

  /**
   * 
   * @param left
   *          a
   * @param right
   *          a
   * @return true if
   * @throws IOException
   *           a
   */
  protected static final boolean filesAreIdentical(File left, File right) {
    assert left != null;
    assert right != null;
    assert left.exists();
    assert right.exists();

    if (left.length() != right.length())
      return false;
    FileInputStream rin = null;
    FileInputStream lin = null;
    try {

      rin = new FileInputStream(right);
      lin = new FileInputStream(left);
      byte[] lbuffer = new byte[4096];
      byte[] rbuffer = new byte[lbuffer.length];
      int lcount = 0;
      lcount = lin.read(lbuffer);
      while (lcount > 0) {
        int bytesRead = 0;

        int rcount = 0;
        rcount = rin.read(rbuffer, bytesRead, lcount - bytesRead);
        while (rcount > 0) {
          bytesRead += rcount;
          rcount = rin.read(rbuffer, bytesRead, lcount - bytesRead);
        }

        for (int byteIndex = 0; byteIndex < lcount; byteIndex++) {
          if (lbuffer[byteIndex] != rbuffer[byteIndex])
            return false;
        }
        lcount = lin.read(lbuffer);
      }
    } catch (FileNotFoundException e) {
      throw new IllegalStateException(e);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    } finally {
      try {
        if (lin != null)
          lin.close();
        if (rin != null)
          rin.close();
      } catch (IOException e) {
        throw new IllegalStateException(e);
      }
    }
    return true;
  }
}
