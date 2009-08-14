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
package net.sourceforge.jwbf;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * 
 * @author Thomas Stock
 *
 */
public final class JWBF {

	private static final Map<String, String> PARTS = new HashMap<String, String>();
	private static String version = "";
	private static String title = "";
	
	
	static {
		String packagename = JWBF.class.getPackage().getName().replace('.',
				File.separatorChar);
		URL url = JWBF.class.getClassLoader().getResource(packagename);
		final String jarFileIndex = "jar:file:";
		boolean isJar = url.toExternalForm().toLowerCase().contains(jarFileIndex);
		if (isJar) {
			try {
				int jarEnd = url.toExternalForm().indexOf("!/");
				String jarFileName = url.toExternalForm().substring(jarFileIndex.length(), jarEnd);
				JarFile jar = new JarFile(jarFileName);
				Enumeration<JarEntry> je =  jar.entries();
				while (je.hasMoreElements()) {
					JarEntry jarEntry = (JarEntry) je.nextElement();
					String slashCount =  jarEntry.getName().replaceAll("[a-zA-Z0-9]", "");
					if (jarEntry.isDirectory() && jarEntry.getName().contains(packagename) 
							&& slashCount.length() == 4 ) {
						
						registerModule(readMFProductTitle(jarFileName) + "-" + jarEntry.getName().split("/")[3],
								readMFVersion(jarFileName));
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				File root = new File(url.toURI());
				File[] dirs = root.listFiles();
				for (int i = 0; i < dirs.length; i++) {
					if (dirs[i].isDirectory()) {
						int lastIndex = dirs[i].toString().lastIndexOf(File.separatorChar) + 1;
						String partTitle = dirs[i].toString().substring(lastIndex, dirs[i].toString().length());
						registerModule(readMFProductTitle(root + "") + "-" + partTitle,
								readMFVersion(root + ""));
					}

				}
			} catch (URISyntaxException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		/*
		String[] cp = System.getProperty("java.class.path").split(":");
		for (int i = 0; i < cp.length; i++) {
			try {

				if (cp[i].endsWith(".jar") && cp[i].contains("jwbf")) {
					registerModule(readArtifactId("file:" + cp[i]),
							readVersion("file:" + cp[i]));

				} else if (cp[i].contains("jwbf")) {
					registerModule(readArtifactId("file:" + cp[i]),
							readVersion("file:" + cp[i]));
				}
			} catch (Exception e) {
				System.err.println(cp[i] + " seems to be no regular module");
			}

		}
		*/

	}

	/**
	 * 
	 *
	 */
	private JWBF() {
		// do nothing
	}

	/**
	 * 
	 * @param artifactId
	 *            a
	 * @param version
	 *            a
	 */
	private static void registerModule(String artifactId, String version) {
		PARTS.put(artifactId, version);

	}

	/**
	 * @param clazz
	 *            a class of the module
	 * @return the version
	 */
	public static String getVersion(Class<?> clazz) {
		try {			
			return getPartInfo(clazz)[1];
		} catch (Exception e) {
			return "Version Unknown";
		}
	}

	/**
	 * @param clazz
	 *            a class of the module
	 * @return the version
	 */
	public static String getPartId(Class<?> clazz) {
		try {
			
			return getPartInfo(clazz)[0];
		} catch (Exception e) {
			return "No Module for " + clazz.getName();
		}
	}

	private static String [] getPartInfo(Class <?> clazz) {
		String classContainer = clazz.getPackage().getName().split("\\.")[3];
		Iterable<String> keys = PARTS.keySet();
		for (String key : keys) {
			if (key.contains(classContainer)) {
				String [] result = {key, PARTS.get(key)};
				return result;
			}
		}
		return null;
	}
	
	/**
	 * Prints the JWBF Version.
	 */
	public static void printVersion() {
		System.out.println(PARTS);
	}

	public static void main(String[] args) {
		printVersion();
	}

	/**
	 * @return the JWBF Version.
	 */
	public static Map<String, String> getVersion() {
		return PARTS;
	}

	/**
	 * 
	 * @param path
	 *            a
	 * @return the version from manifest
	 * @throws IOException
	 *             if path invalid
	 */
	private static String readMFVersion(String path) throws IOException {
		if (version.length() < 1) {
			String implementationVersion = null; // =
			// clazz.getPackage().getImplementationVersion();

			implementationVersion = readFromManifest(path,
					"Implementation-Version");

			if (implementationVersion == null) {
				version = "DEVEL";
			} else {
				version = implementationVersion;
			}
		}
		return version;
	}

	

	/**
	 * 
	 * @param path
	 *            a
	 * @return the
	 * @throws IOException
	 *             if path invalid
	 */
	private static String readMFProductTitle(String path) throws IOException {
		if (title.length() < 1) {
			String implementationTitle = null; // =
			// clazz.getPackage().getImplementationTitle();
			implementationTitle = readFromManifest(path, "Implementation-Title");

			if (implementationTitle == null) {
				title = "jwbf-generic";
			} else {
				title = implementationTitle;
			}
		}
		return title;
	}

	/**
	 * 
	 * @param path
	 *            a
	 * @param key
	 *            a
	 * @return value
	 * @throws IOException
	 *             if path invalid
	 */
	private static String readFromManifest(String path, String key)
			throws IOException {

		URL manifestUrl = null;
		if (path.endsWith(".jar")) {

			manifestUrl = new URL("jar:file:" + path + "!/META-INF/MANIFEST.MF");
		} else {
			if (!path.endsWith("/"))
				path += "/";
			manifestUrl = searchMF(path);
		}
		Manifest manifest = new Manifest(manifestUrl.openStream());
		return manifest.getMainAttributes().getValue(key);
	}
	
	private static URL searchMF(String f) throws IOException {
		String foundE = "target" + File.separatorChar + "MANIFEST.MF";
		File fi = new File(f);
		if (new File(fi, foundE).exists()) {
			return new URL("file:" + fi + File.separatorChar + foundE);
		} else {
			return searchMF(fi.getParent());
		}
		
	}

}
