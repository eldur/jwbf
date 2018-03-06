package net.sourceforge.jwbf.core;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.io.CharStreams;

import net.sourceforge.jwbf.GAssert;
import net.sourceforge.jwbf.JWBFTest;

public class PostPackageIT {

  @Test
  public void testJarManifest() throws Exception {
    File mvnTargetDir = new File("target");

    File[] files =
        mvnTargetDir.listFiles(
            new FilenameFilter() {

              @Override
              public boolean accept(File dir, String name) {
                return name.endsWith(".jar")
                    && name.contains("jwbf")
                    && //
                    !name.contains("javadoc")
                    && !name.contains("sources");
              }
            });

    File jar = Iterables.getOnlyElement(ImmutableList.copyOf(files));
    Process p = Runtime.getRuntime().exec("java -jar " + jar.getAbsolutePath());

    String stdOutErr = //
        getString(p.getInputStream()).replaceAll("=>.*", "=>") + getString(p.getErrorStream());
    ImmutableList<String> lines = JWBFTest.splitVersionText(stdOutErr);
    GAssert.assertEquals(
        ImmutableList.of(
            "JWBF.", //
            "jwbf-core =>", //
            "jwbf-mediawiki =>" //
            ),
        lines);
  }

  private String getString(InputStream inputStream) throws IOException {
    return CharStreams.toString(new InputStreamReader(inputStream, Charsets.UTF_8));
  }
}
