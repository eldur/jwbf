package net.sourceforge.jwbf.actions.zim;

import java.io.File;
import java.util.Iterator;
import java.util.Vector;

import net.sourceforge.jwbf.bots.ZimWikiBot;

public class AllPageTitles implements Iterable<String>, Iterator<String> {

	
	
	private Vector<String> all = new Vector<String>();
	private Iterator<String> allIt = null;

	public AllPageTitles(ZimWikiBot bot) {
	

		
		File[] fileList = bot.getRootFolder().listFiles();
	
		
		// every file is going to be loaded
		for (File f : fileList) {

			// except those files, for whatever reason

			if (!f.getName().startsWith(".")
					&& !f.getName().startsWith("index")
					&& !f.getName().startsWith("Gentoo-Update")
					&& !f.getName().startsWith("p-Wert")) {

				// images shall not be loaded as well, yet
				if (f.getName().endsWith(".png")) {
					System.out.println("skipping image file!");

				} else {
					// cropping the ".txt" extension
					String fileName = f.getName().substring(0,
							f.getName().length() - 4);

					System.out.println("adding: " + fileName);

					// changing the root directory of zim to the page in wiki
					if (fileName.equals("Home"))
						fileName = "Benutzer:USERNAME";
					all.add(fileName);
					

				}
			}
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
