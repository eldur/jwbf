package net.sourceforge.jwbf.core.bots.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;

@Slf4j
public class SimpleCache implements CacheHandler {

  private final File folder;
  private final String ext = ".txt";
  private final int maxSaveTimeMils;
  private final int objectLiveTimeMilis = 100;
  private final Map<String, CachArticle> dynStore = new HashMap<String, CachArticle>();


  public SimpleCache(File folder, int maxSaveTimeMils) {
    this.folder = folder;
    this.maxSaveTimeMils = maxSaveTimeMils;

  }

  /**
   * {@inheritDoc}
   */
  public boolean containsKey(String title) {
    maintain(title);
    File f = new File(folder, getChecksum(title) + ext);

    return f.exists();
  }

  private void maintain(String title) {
    File fx = new File(folder, getChecksum(title) + ext);
    if (fx.exists()) {
      CachArticle it = read(title);

      long dif = it.getSaveDate().getTime() - System.currentTimeMillis()
          + maxSaveTimeMils;
      System.out.println("maintain: timedif file " + dif); // TODO RM
      if (dif < 0) {

        log.debug("maintain: delete: " + fx.getAbsolutePath()); // TODO RM
        dynStore.remove(it.getTitle());
        fx.delete();

      }
    }
    if (dynStore.containsKey(title)) {
      CachArticle it = dynStore.get(title);
      long dif = it.getSaveDate().getTime() - System.currentTimeMillis()
          + objectLiveTimeMilis;
      System.out.println("maintain: timedif dyn  " + dif); // TODO RM
      if (dif < 0) {

        log.debug("maintain: remove: " + it.getTitle()); // TODO RM
        dynStore.remove(it.getTitle());

      }
    }

  }
  /**
   * {@inheritDoc}
   */
  public SimpleArticle get(String title) {
    if (containsKey(title))
      return read(title);
    return new SimpleArticle(title);
  }
  /**
   * {@inheritDoc}
   */
  public void put(SimpleArticle sa) {

    write2File(new CachArticle(sa));


  }

  protected void write2File(CachArticle ca) {
    OutputStream fos = null;

    try {
      File sf = new File(folder, getChecksum(ca.getTitle()) + ext);
      log.debug("write2File " + sf.getAbsolutePath()); // TODO RM
      fos = new FileOutputStream(sf);
      ObjectOutputStream o = new ObjectOutputStream(fos);
      o.writeObject(ca);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        fos.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  protected String getChecksum(String s) {
    byte[] bytes = s.getBytes();

    Checksum checksumEngine = new CRC32();
    checksumEngine.update(bytes, 0, bytes.length);
    long checksum = checksumEngine.getValue();
    return Long.toHexString(checksum);

  }

  private CachArticle read(String title) {
    if (dynStore.containsKey(title)) {
      log.debug("readFrom Map"); // TODO RM
      return dynStore.get(title);
    } else {
      CachArticle temp = readFromFile(title);
      dynStore.put(title, temp);
      return temp;
    }
  }

  protected CachArticle readFromFile(String title) {
    InputStream fis = null;

    try {
      File rf = new File(folder, getChecksum(title) + ext);
      fis = new FileInputStream(rf);
      log.debug("readFromFile: " + rf.getAbsolutePath()); // TODO RM
      ObjectInputStream o = new ObjectInputStream(fis);
      CachArticle sa = (CachArticle) o.readObject();

      return sa;

    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } finally {
      try {
        fis.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return new CachArticle();
  }



}
