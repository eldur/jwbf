package net.sourceforge.jwbf.mediawiki;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;

import org.junit.Test;

public class MediaWikiTest {

  @Test
  public void testEncode() throws UnsupportedEncodingException {
    String testStr = " a+&?=;.-";
    String mvEnc = MediaWiki.encode(testStr);
    String rawEncUft8 = URLEncoder.encode(testStr, "UTF-8");
    assertEquals(rawEncUft8, mvEnc);
    String rawEncAscii = URLEncoder.encode(testStr, "US-ASCII");
    assertEquals(rawEncUft8, rawEncAscii);
    //    String rawEncUtf16 = URLEncoder.encode(testStr, "UTF-16");
    //    assertEquals(rawEncUft8, rawEncUtf16);
  }

}
