/*
 * Copyright 2009 Martin Koch.
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
 * Thomas Stock
 * 
 */

package net.sourceforge.jwbf.zim.actions;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Iterator;
import java.util.Vector;

import net.sourceforge.jwbf.zim.bots.ZimWikiBot;

/**
 * 
 * @author Martin Koch
 * @author Thomas Stock
 */
public class AllPageTitles implements Iterable<String>, Iterator<String> {

	
	private Vector<String> all = new Vector<String>();
	private Iterator<String> allIt = null;


	/**
	 * Constructor for an Iterator
	 * @param zim this is our zimBot on the local machine
	 * it will distinguish between test and image files
	 * and transform the local root dir of zim to the remote
	 * dir on the mediWiki server
	 * HINT: currently only P_ortable N_etwork G_raphics (.png)-files
	 * are supported! 
	 */

	public AllPageTitles(ZimWikiBot zim) {

		// specify the path to all zim files
		File dir = zim.getRootFolder();
		File[] fileList = dir.listFiles(new FilenameFilter() {
		
			public boolean accept(File dir, String name) {
				if (name.endsWith(".txt"))
					return true;
				return false;
			}
		});

		// every file is going to be loaded
		for (File f : fileList) {
				
//			// get all png image files // TODO not good ;-) rm
//			if (f.getName().endsWith(".png")) {
//				all.add(f.getName());
//			} 
				// get all text files
//				else if (f.getName().endsWith(".txt")) {

				// cropping the ".txt" extension
				String fileName = f.getName().substring(0,
						f.getName().length() - 4);

//				// changing the root directory of zim to the page in wiki
//				if (fileName.equals("Home"))
//					fileName = zim.getMWFolder();
				
				all.add(fileName);
//			}
		}
		allIt = all.iterator();
	}

	public Iterator<String> iterator() {
		return this;
	}

	public boolean hasNext() {
		return allIt.hasNext();
	}

	public String next() {
		return allIt.next();
	}

	public void remove() {
		allIt.remove();

	}


}
