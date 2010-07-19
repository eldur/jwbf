package net.sourceforge.jwbf.core;

import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.io.StringWriter;

import org.junit.Test;

public class PostPackageTest {


  @Test
  public void testJarManifest() throws Exception {
    File mvnTargetDir = new File("target");
    assumeTrue(mvnTargetDir.exists());
    File[] files = mvnTargetDir.listFiles(new FilenameFilter() {

      public boolean accept(File dir, String name) {
        return name.contains(".jar")
        && !name.contains("javadoc") && !name.contains("sources");
      }
    });

    for (File jar : files) {

      BufferedReader in = null;
      try {
        String text ="";
        StringWriter strOut = new StringWriter();
        Process p = Runtime.getRuntime().exec("java -jar " + jar.getAbsolutePath() + " JWBF.");
        in = new BufferedReader(new InputStreamReader(p.getInputStream()));
        while ((text = in.readLine()) != null) {
          strOut.write(text);
          strOut.flush();
        }
        String out = strOut.toString();
        assertTrue("should contain jwbf, but was: " + out + " <= " + jar.getAbsolutePath(), out.contains("jwbf"));

      } finally {
        if (in != null)
          in.close();
      }
    }
  }
}
