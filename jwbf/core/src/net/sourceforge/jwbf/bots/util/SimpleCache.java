package net.sourceforge.jwbf.bots.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import net.sourceforge.jwbf.contentRep.ArticleMeta;
import net.sourceforge.jwbf.contentRep.SimpleArticle;

import org.apache.log4j.Logger;

public class SimpleCache implements CacheHandler {


	private Logger log = Logger.getLogger(SimpleCache.class);
	

	private final File folder;
	private final String ext = ".txt";
	private final int maxSaveTimeMils;
	
	
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
		CachSA it = readFromFile(title);
		
		long dif = it.getSaveDate().getTime() - System.currentTimeMillis() + maxSaveTimeMils;
		System.out.println(dif); // TODO RM
		if (dif < 0) {
			
		
			fx.delete();
			
		}
		}
	}
	/**
	 * {@inheritDoc}
	 */
	public SimpleArticle get(String title) {
		if (containsKey(title))
			return readFromFile(title);
		return new SimpleArticle(title);
	}
	/**
	 * {@inheritDoc}
	 */
	public void put(SimpleArticle sa) {
		log.debug("put file" + getChecksum(sa.getLabel())); // TODO RM
		write2File(new CachSA(sa));
		
		
	}
	
	private void write2File(CachSA sa) {
		OutputStream fos = null;

		try {
			fos = new FileOutputStream(new File(folder, getChecksum(sa.getLabel()) + ext));
			ObjectOutputStream o = new ObjectOutputStream(fos);
			o.writeObject(sa);
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
	
	private String getChecksum(String s) {
		byte[] bytes = s.getBytes();
	   
	    Checksum checksumEngine = new CRC32();
	    checksumEngine.update(bytes, 0, bytes.length);
	    long checksum = checksumEngine.getValue();
	    return Long.toHexString(checksum);

	}
	
	private CachSA readFromFile(String title) {
		InputStream fis = null;
		
		try {
			fis = new FileInputStream(new File(folder, getChecksum(title) + ext));
			log.debug("try to read from file: " + new File(folder, getChecksum(title) + ext).getAbsolutePath());
			ObjectInputStream o = new ObjectInputStream(fis);
			CachSA sa = (CachSA) o.readObject();

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
		return new CachSA();
	}

	private class CachSA extends SimpleArticle implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -3759968702455312374L;
		private Date d;
		
		public CachSA() {
			super();
			setSaveDate(1L);
		}
		
		public CachSA(ArticleMeta ca) {
			super(ca);
			setSaveDate(System.currentTimeMillis());
		}
		
		public Date getSaveDate() {
			return d;
		}
		
		public void setSaveDate(long milis) {
			d = new Date(milis);
		}
	}

}
