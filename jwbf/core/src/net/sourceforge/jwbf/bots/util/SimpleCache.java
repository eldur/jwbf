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


	public boolean containsKey(String title) {
		File f = new File(folder, title + ext);
		
		return f.exists();
	}
	
	private void maintain(String title) {
		CachSA it = readFromFile(title);
		long dif = it.getSaveDate().getTime() - System.currentTimeMillis() + maxSaveTimeMils;
		System.out.println(dif); // TODO RM
		if (dif < 0) {
			
			File f = new File(folder, title + ext);
			f.delete();
			
		}
	}

	public SimpleArticle get(String title) {
		if (containsKey(title))
			return readFromFile(title);
		return new SimpleArticle(title);
	}

	public void put(SimpleArticle sa) {
		
		write2File(new CachSA(sa));
		
		
	}
	
	private void write2File(CachSA sa) {
		OutputStream fos = null;

		try {
			fos = new FileOutputStream(new File(folder, sa.getLabel() + ext));
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
	
	private CachSA readFromFile(String title) {
		InputStream fis = null;
		
		try {
			fis = new FileInputStream(new File(folder, title + ext));
			log.debug("try to read from file: " + new File(folder, title + ext).getAbsolutePath());
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
		return null;
	}

	private class CachSA extends SimpleArticle implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -3759968702455312374L;
		private Date d;
		
		public CachSA(ArticleMeta ca) {
			super(ca);
			setSaveDate();
		}
		
		public Date getSaveDate() {
			return d;
		}
		
		public void setSaveDate() {
			d = new Date(System.currentTimeMillis());
		}
	}

}
