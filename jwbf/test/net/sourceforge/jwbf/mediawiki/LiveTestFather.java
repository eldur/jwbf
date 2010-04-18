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
package net.sourceforge.jwbf.mediawiki;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.Vector;

import net.sourceforge.jwbf.TestHelper;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import org.junit.AfterClass;
import org.junit.Test;

/**
 * @author Thomas Stock
 */
public abstract class LiveTestFather extends TestHelper {

	private static final Properties data = new Properties();

	private static String filename = "";

	private final Collection<String> specialChars = new Vector<String>();

	private static boolean isVersionTestCase = false;

	protected static final Map<String, Version > USEDVERSIONS = new HashMap<String, Version >();
	protected static final Map<String, Version > TESTEDVERSIONS = new HashMap<String, Version >();
	protected static final Map<String, Version >  DOCUMENTEDVERSIONS = new HashMap<String, Version >();
	/**
	 * Inits.
	 */
	protected LiveTestFather() {
		specialChars.add("\"");
		specialChars.add("\'");
		specialChars.add("?");
		specialChars.add("%");
		specialChars.add("&");
		specialChars.add("[");
		specialChars.add("]");
	}


	static {

			// find jwftestfile
			Collection<String> filepos = new Vector<String>();
			filepos.add(System.getProperty("user.home") + "/.jwbf/test.xml");
			filepos.add(System.getProperty("user.home") + "/jwbftest.xml");
			filepos.add("test.xml");
			for (String fname : filepos) {
				if (new File(fname).canRead()) {
					filename = fname;
					System.out.println("use testfile: " + filename);



					break;
				}
			}
			if (filename.length() < 1) {
				System.err.println("no testfile found. Use: " + System.getProperty("user.home") + "/.jwbf/test.xml");
				filename = System.getProperty("user.home") + "/.jwbf/test.xml";
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e2) {
				e2.printStackTrace();
			}
			try {
				data.loadFromXML(new FileInputStream(filename));
			} catch (InvalidPropertiesFormatException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				File f = new File(filename);
				try {
					f.createNewFile();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

	}
	protected static void addInitSupporterVersions(Class < ? > mwc) {
		isVersionTestCase = true;
		Version [] vs = MWAction.findSupportedVersions(mwc);
		for (int j = 0; j < vs.length; j++) {
			DOCUMENTEDVERSIONS.put(mwc.getCanonicalName() + vs[j], vs[j]);
		}


	}
	/**
	 *
	 * @return the current UTC
	 */
	public Date getCurrentUTC() {
		long currentDate = System.currentTimeMillis();
		TimeZone tz = TimeZone.getDefault();
		Calendar localCal = Calendar.getInstance(tz);
		localCal.setTimeInMillis(currentDate - tz.getOffset(currentDate));

		return new Date(localCal.getTimeInMillis());

	}
	/**
	 * Use in a invalid testcase.
	 * @param clazz a
	 * @param v a
	 */
	protected static final void registerUnTestedVersion(Class < ? > clazz, Version v) {
		if (v != Version.DEVELOPMENT) {
			USEDVERSIONS.put(clazz.getCanonicalName() + v, v);
		}
	}

	/**
	 * Use in a valid testcase.
	 * @param clazz a
	 * @param v a
	 */
	protected static final void registerTestedVersion(Class < ? > clazz, Version v) {
		if (v != Version.DEVELOPMENT) {
			TESTEDVERSIONS.put(clazz.getCanonicalName() + v, v);
		}
		registerUnTestedVersion(clazz, v);
	}

	private static Map<String, Version > getUntestedButDocumentedVersions() {
		final Map<String, Version > data = new HashMap<String, Version>();
		data.putAll(DOCUMENTEDVERSIONS);

		final Set<String> testedKeys = TESTEDVERSIONS.keySet();
		for (String key : testedKeys) {
			data.remove(key);
		}


		return data;
	}

	private static Collection < Version > getUsedVersions() {
		final Vector<Version > data = new Vector<Version>();
		Version [] vas = Version.valuesStable();
		for (int i = 0; i < vas.length; i++) {
			data.add(vas[i]);
		}

		final Iterable<Version> testedKeys = USEDVERSIONS.values();
		for (Version key : testedKeys) {
			data.remove(key);
		}


		return data;
	}

	private static Map<String, Version> getTestedButUndocmentedVersions() {
		final Map<String, Version> data = new HashMap<String, Version>();
		data.putAll(TESTEDVERSIONS);

		final Set<String> documentedKeys = DOCUMENTEDVERSIONS.keySet();
		for (String key : documentedKeys) {
			data.remove(key);
		}
		return data;
	}

	@Test
	public void yTestVersionDocumentation() throws Exception {
		if (isVersionTestCase) {
			assertTrue("no versions are supported", !DOCUMENTEDVERSIONS.isEmpty());
			assertTrue("not all documented versions are tested \n{ "
					+ getUntestedButDocumentedVersions() + " }", getUntestedButDocumentedVersions().isEmpty());
			assertTrue("there are undocumented tests for versions \n{ "
					+ getTestedButUndocmentedVersions() + " }", getTestedButUndocmentedVersions().isEmpty());

			assertTrue("missing tests for versions \n{ "
					+ getUsedVersions() + " }", getUsedVersions().isEmpty());
		}
	}

	@AfterClass
	public static void restData() throws Exception {
		DOCUMENTEDVERSIONS.clear();
		TESTEDVERSIONS.clear();
		isVersionTestCase = false;
	}
	public static void main(String[] args) {

		System.out.println(System.getenv());
		System.out.println(System.getProperties());
		System.out.println(System.getProperty("user.name"));
		System.out.println(System.getProperty("user.home"));
		new LiveTestFather() {
		}.getCurrentUTC();
	}

	private static void addEmptyKey(String key) {
		data.put(key, " ");
		try {
			data.storeToXML(new FileOutputStream(filename), "");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	protected static String getValue(final String key) throws Exception {
		if (!data.containsKey(key) || data.getProperty(key).trim().length() <= 0) {
			addEmptyKey(key);
			throw new Exception("No or empty value for key: \"" + key + "\" in " + filename);
		}
		return data.getProperty(key);
	}

	protected static String getWikiUrl(Version v) throws Exception {

		return getValue("wiki" + v.name() + "_url");
	}
	protected static String getWikiUser(Version v) throws Exception {
		return getValue("wiki" + v.name() + "_user");
	}
	protected static String getWikiPass(Version v) throws Exception {
		return getValue("wiki" + v.name() + "_pass");
	}

	protected static MediaWikiBot getMediaWikiBot(Version v, final boolean login) throws Exception {
		MediaWikiBot bot = new MediaWikiBot(getWikiUrl(v));
		if (login) {
			bot.login(getWikiUser(v), getWikiPass(v));
		}
		return bot;
	}

	protected static URL getURL(final String key) throws Exception {
		URL u = new URL(getValue(key));
		String host = u.getHost();
		int port = u.getPort();
		if (port < 1) {
			port = 80;
		}
		String protocol = u.getProtocol();
		String file = u.getFile();
		return new URL(protocol, host, port, file);
	}

	protected static int getIntValue(final String key)  throws Exception {
		return Integer.parseInt(getValue(key));
	}
	protected Collection<String> getSpecialChars() {
		return specialChars;
	}
}
