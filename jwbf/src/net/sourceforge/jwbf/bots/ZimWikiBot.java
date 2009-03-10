package net.sourceforge.jwbf.bots;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import net.sourceforge.jwbf.actions.util.ActionException;
import net.sourceforge.jwbf.actions.util.ProcessException;
import net.sourceforge.jwbf.contentRep.Article;
import net.sourceforge.jwbf.contentRep.ContentAccessable;
import net.sourceforge.jwbf.contentRep.SimpleArticle;

public class ZimWikiBot implements WikiBot {
	private static final String ZIMEXT = ".txt";
	private final File rootFolder;
	public ZimWikiBot(String zimRootFolder) {
		// specify the path to all zim files
		rootFolder = new File(zimRootFolder);
	}
	
	public void login(String user, String passwd) throws ActionException {
		throw new ActionException("login is not supported because this is a desktopwiki");
		
	}

	public void postDelete(String title) throws ActionException,
			ProcessException {
		// TODO Auto-generated method stub
		
	}

	public Article readContent(String title) throws ActionException,
			ProcessException {
		
		return readContent(title, 0); // FIXME add regular constants
	}

	public Article readContent(String title, int properties)
			throws ActionException, ProcessException {
		return new Article(this, readData(title, properties));
	}

	public SimpleArticle readData(String name, int properties)
			throws ActionException, ProcessException {
		File f = new File(getRootFolder(), name + ZIMEXT);
		SimpleArticle sa = new SimpleArticle();
		sa.setLabel(name);
		String text = "";
		// create a file reader
		try {
			BufferedReader myInput = new BufferedReader(new FileReader(f));

			String line = "";
			String cont = "";

			// if we are reading content, than
			while ((line = myInput.readLine()) != null) {

				// omit the headline
				if (line.startsWith("====== " + name + " ======")) {

					// store every line in 'text' and add a newline
					while ((cont = myInput.readLine()) != null) {
						text += cont + "\n";
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace(); // TODO transform to system exception
		}
		sa.setText(text);
		return sa;
	}

	public void writeContent(ContentAccessable sa) throws ActionException,
			ProcessException {
		// TODO Auto-generated method stub
		
	}
	
	public File getRootFolder() {
		return rootFolder;
	}

}
