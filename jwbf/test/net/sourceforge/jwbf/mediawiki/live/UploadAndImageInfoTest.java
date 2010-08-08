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
package net.sourceforge.jwbf.mediawiki.live;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import net.sourceforge.jwbf.TestHelper;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.actions.util.ProcessException;
import net.sourceforge.jwbf.mediawiki.LiveTestFather;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.editing.FileUpload;
import net.sourceforge.jwbf.mediawiki.actions.queries.ImageInfo;
import net.sourceforge.jwbf.mediawiki.actions.util.VersionException;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import net.sourceforge.jwbf.mediawiki.contentRep.SimpleFile;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
/**
 * 
 * @author Thomas Stock
 *
 */
public class UploadAndImageInfoTest extends LiveTestFather {



  private MediaWikiBot bot = null;
  /**
   * Setup log4j.
   * @throws Exception a
   */
  @BeforeClass
  public static void setUp() throws Exception {
    TestHelper.prepareLogging();
    addInitSupporterVersions(FileUpload.class);
    addInitSupporterVersions(ImageInfo.class);
  }


  /**
   * Test upload.
   * @throws Exception a
   */
  @Test(expected = VersionException.class)
  public final void uploadMW1x09Fail() throws Exception {

    bot = getMediaWikiBot(Version.MW1_09, true);

    Assert.assertEquals(bot.getVersion(), Version.MW1_09);
    new FileUpload(bot, "README");
  }

  /**
   * Test category read. Test category must have more then 50 members.
   * @throws Exception a
   */
  @Test(expected = VersionException.class)
  public final void uploadMW1x10Fail() throws Exception {

    bot = getMediaWikiBot(Version.MW1_10, true);
    Assert.assertEquals(bot.getVersion(), Version.MW1_10);

    SimpleFile sf = new SimpleFile(getValue("filename"), getValue("validFile"));
    URL url = new URL(new ImageInfo(bot, sf.getFilename()).getUrlAsString());
    assertFile(url, sf.getFile());

  }

  /**
   * Test category read. Test category must have more then 50 members.
   * @throws Exception a
   */
  @Test
  public final void uploadMW1x11() throws Exception {

    bot = getMediaWikiBot(Version.MW1_11, true);
    generalUploadImageInfoTest(bot, Version.MW1_11);
  }

  /**
   * Test category read. Test category must have more then 50 members.
   * @throws Exception a
   */
  @Test
  public final void uploadMW1x12() throws Exception {

    bot = getMediaWikiBot(Version.MW1_12, true);
    generalUploadImageInfoTest(bot, Version.MW1_12);
  }
  /**
   * Test category read. Test category must have more then 50 members.
   * @throws Exception a
   */
  @Test
  public final void uploadMW1x13() throws Exception {

    bot = getMediaWikiBot(Version.MW1_13, true);
    generalUploadImageInfoTest(bot, Version.MW1_13);
  }

  /**
   * Test category read. Test category must have more then 50 members.
   * @throws Exception a
   */
  @Test
  public final void uploadMW1x14() throws Exception {

    bot = getMediaWikiBot(Version.MW1_14, true);
    generalUploadImageInfoTest(bot, Version.MW1_14);

  }
  /**
   * Test category read. Test category must have more then 50 members.
   * @throws Exception a
   */
  @Test
  public final void uploadMW1x15() throws Exception {

    bot = getMediaWikiBot(Version.MW1_15, true);
    generalUploadImageInfoTest(bot, Version.MW1_15);

  }

  /**
   * Test category read. Test category must have more then 50 members.
   * @throws Exception a
   */
  @Test
  public final void uploadMW1x16() throws Exception {

    bot = getMediaWikiBot(Version.MW1_16, true);
    generalUploadImageInfoTest(bot, Version.MW1_16);

  }



  /**
   * because image does not exists.
   * @throws Exception a
   */
  @Test(expected = ProcessException.class)
  public final void imageInfoFail() throws Exception {

    bot = getMediaWikiBot(Version.getLast(), true);
    ImageInfo a = new ImageInfo(bot, "UnknownImage.jpg");
    System.out.println(a.getUrlAsString());

  }

  /**
   * because image does not exists.
   * @throws Exception a
   */
  @Test(expected = ActionException.class)
  public final void imageInfoPerformManual() throws Exception {

    bot = getMediaWikiBot(Version.MW1_15, true);
    ImageInfo a = new ImageInfo(bot, "UnknownImage.jpg");
    bot.performAction(a);

  }
  /**
   * Test to delete an image.
   * @throws Exception a
   */
  @Test(expected=ProcessException.class)
  public final void deleteImage() throws Exception {
    bot = getMediaWikiBot(Version.MW1_15, true);
    generalUploadImageInfoTest(bot, Version.MW1_15);
    bot.postDelete("File:" + getValue("filename"));

    try {

      new URL(new ImageInfo(bot, getValue("filename")).getUrlAsString());
    } catch (ProcessException e) {
      throw new ProcessException(e.getLocalizedMessage() + "; \n is upload enabled ?");
    }
    fail("file was found ");
  }
  /**
   * 
   * @param bot a
   * @param v a
   * @throws Exception a
   */
  protected final void generalUploadImageInfoTest(MediaWikiBot bot, Version v) throws Exception {
    assertEquals(bot.getVersion(), v);
    assertTrue("File (" + getValue("validFile")
        + ") not readable", new File(getValue("validFile")).canRead());
    SimpleFile sf = new SimpleFile(getValue("filename"), getValue("validFile"));
    if (v.greaterEqThen(Version.MW1_12))
      try {
        bot.postDelete("File:" + getValue("filename"));
      } catch (Exception e) {
        // do nothing
      }
      FileUpload up = new FileUpload(sf, bot);

      bot.performAction(up);
      URL url = null;
      try {

        url = new URL(new ImageInfo(bot, sf.getTitle()).getUrlAsString());
      } catch (ProcessException e) {
        throw new ProcessException(e.getLocalizedMessage() + "; \n is upload enabled ?");
      }
      Assert.assertTrue("file not found "
          + url , url.toExternalForm().length() - bot.getHostUrl().length() > 2);
      File file = new File(getValue("validFile"));
      assertFile(url, file);
      registerTestedVersion(FileUpload.class, bot.getVersion());
      registerTestedVersion(ImageInfo.class, bot.getVersion());
  }
  /**
   * 
   * @param url a
   * @param file a
   * @throws Exception a
   */
  protected final void assertFile(URL url, File file) throws Exception {
    File temp = new File("temp.file");
    download(url.toExternalForm(), temp);
    Assert.assertTrue("files are not ident", filesAreIdentical(temp, file));

    temp.delete();
  }
  /**
   * 
   * @param address a
   * @param localFileName a
   */
  protected static final void download(String address, File localFileName) {
    OutputStream out = null;
    URLConnection conn = null;
    InputStream  in = null;
    try {
      URL url = new URL(address);
      out = new BufferedOutputStream(
          new FileOutputStream(localFileName));
      conn = url.openConnection();
      in = conn.getInputStream();
      byte[] buffer = new byte[1024];
      int numRead;
      long numWritten = 0;
      while ((numRead = in.read(buffer)) != -1) {
        out.write(buffer, 0, numRead);
        numWritten += numRead;
      }
      System.out.println(localFileName + "\t" + numWritten);
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
        System.err.println(ioe.getMessage());
      }
    }
  }

  /**
   * 
   * @param left a
   * @param right a
   * @return true if
   * @throws IOException a
   */
  protected static final boolean filesAreIdentical(File left, File right)
  throws IOException {
    assert left != null;
    assert right != null;
    assert left.exists();
    assert right.exists();

    if (left.length() != right.length())
      return false;

    FileInputStream lin = new FileInputStream(left);
    FileInputStream rin = new FileInputStream(right);
    try {
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
    } finally {
      lin.close();
      rin.close();
    }
    return true;
  }
}
