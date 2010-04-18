package net.sourceforge.jwbf.core.bots.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Date;

import net.sourceforge.jwbf.TestHelper;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;

import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
/**
 *
 * @author Thomas Stock
 *
 */
@Ignore
public class SimpleCachTest extends TestHelper {

	public static final String CACHFOLDER = "build/data";

	private File f = new File(CACHFOLDER);
	private static final String label = "CachTest";

	@BeforeClass
	public static void setUp() throws Exception {
		PropertyConfigurator.configureAndWatch("test4log4j.properties",
				60 * 1000);
	}

	@Before
	public final void prepare()  throws Exception {

		f.mkdirs();
	}

	@After
	public final void afterTest() {

		File [] fs = f.listFiles();
		for (int i = 0; i < fs.length; i++) {
			fs[i].delete(); // TODO comment in
		}
		f.deleteOnExit();  // TODO comment in

	}

	@Test
	public void innerWriteRead() throws Exception {


		SimpleCache db = new SimpleCache(f, 10000);
		SimpleArticle sa = new SimpleArticle(label);
		CachArticle ca = new CachArticle(sa);

		db.write2File(ca);


		// reinit the cach
		db = new SimpleCache(f, 10000);
		// check if contains
		CachArticle rx = db.readFromFile(label);
		assertEquals(sa.getTitle(), rx.getTitle());


	}

	/**
	 * @deprecated TODO DELETE
	 * @throws Exception
	 */
	@Deprecated
  @Test
	public void zapWrite() throws Exception {

		OutputStream fos = null;

		try {
			fos = new FileOutputStream(new File(f, "bert"));
			ObjectOutputStream o = new ObjectOutputStream(fos);
			CachArticle p = new CachArticle();
			p.setSaveDate(456);
			p.setTitle(getRandomAlph(8));
			o.writeObject(p);

		} catch (IOException e) {
			System.err.println(e);
		} finally {
			try {
				fos.close();
			} catch (Exception e) {
			}
		}

		InputStream fis = null;

		try
		{
		  fis = new FileInputStream( new File(f, "bert") );
		  ObjectInputStream o = new ObjectInputStream( fis );
		  CachArticle string = (CachArticle) o.readObject();


		  System.out.println( string.getSaveDate() );
		  System.out.println( string.getTitle() );

		}
		catch ( IOException e ) { System.err.println( e ); }
		catch ( ClassNotFoundException e ) { System.err.println( e ); }
		finally { try { fis.close(); } catch ( Exception e ) { } }

	}


	@Test
	public void checksumTest() throws Exception {
		SimpleCache x = new SimpleCache(f, 21);
		String bert = getRandomAlph(8);
		String v1 = x.getChecksum(bert);
		String v2 = x.getChecksum(bert);
		assertEquals(v1, v2);
	}
	@Test
	public void basic1() throws Exception {

		String title = getRandomAlph(8);
		CacheHandler db = new SimpleCache(f, 10000);
		// create an article
		SimpleArticle sai = new SimpleArticle(title);
		sai.setTitle(title);
		sai.setText(getRandom(8));
		sai.setEditTimestamp(new Date());
		// write in DB
		db.put(sai);


		// reinit the cach
//		db = new SimpleCache(f, 10000);
		// check if contains
		assertTrue("should contains", db.containsKey(title));
		System.out.println("text: " + db.get(title).getText());
		assertTrue("should have a", db.get(title).getTitle().length() > 1);


	}

	@Test
	public void basic2() throws Exception {

		String title = getRandom(8);
		CacheHandler db = new SimpleCache(f, 10000);
		SimpleArticle sai = new SimpleArticle();
		sai.setTitle(title);
		sai.setText(getRandom(8));
		sai.setEditTimestamp(new Date());
		db.put(sai);
		SimpleArticle sa = new SimpleArticle();
		db = new SimpleCache(f, 10000);
		assertTrue("should contains", db.containsKey(title));
		assertTrue("should have a", db.get(title).getTitle().length() > 1);
		SimpleArticle sa2 = new SimpleArticle();
		sa2.setTitle(title);
		sa2.setText(getRandom(8));
		db.put(sa);
		sa.setTitle(title);
		db = new SimpleCache(f, 10000);
		assertTrue("should contains", db.containsKey(title));
		assertTrue("should have a", db.get(title).getTitle().length() > 1);

	}

	/**
	 * Test.
	 * @throws Exception a
	 */
	@Test
	public final void cacheTestAttributes() throws Exception {

		CacheHandler cache = new SimpleCache(f, 1000);

		SimpleArticle a = new SimpleArticle(label);
		a.setText(getRandom(16));
		a.setEditSummary(getRandom(16));
		a.setMinorEdit(true);
		a.setEditTimestamp(new Date());
		a.setEditor("Editor");


		cache.put(a);
		assertTrue("should contains the article", cache.containsKey(label));

		SimpleArticle b = cache.get(label);
		assertEquals(a.getText(), b.getText());
		assertEquals(a.getEditSummary(), b.getEditSummary());
		assertEquals(a.isMinorEdit(), b.isMinorEdit());
		assertEquals(a.getEditTimestamp(), b.getEditTimestamp());
		assertEquals(a.getEditor(), b.getEditor());

	}
}
