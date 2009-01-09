
/*
 * Copyright 2007 Justus Bisser.
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
 */
package net.sourceforge.jwbf.actions.mw.editing;


import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import net.sourceforge.jwbf.actions.FilePost;
import net.sourceforge.jwbf.actions.Get;
import net.sourceforge.jwbf.actions.Post;
import net.sourceforge.jwbf.actions.mw.HttpAction;
import net.sourceforge.jwbf.actions.mw.util.ActionException;
import net.sourceforge.jwbf.actions.mw.util.MWAction;
import net.sourceforge.jwbf.actions.mw.util.ProcessException;
import net.sourceforge.jwbf.actions.mw.util.VersionException;
import net.sourceforge.jwbf.bots.MediaWikiBot;
import net.sourceforge.jwbf.bots.MediaWikiBotImpl;
import net.sourceforge.jwbf.contentRep.mw.SimpleFile;

import org.apache.log4j.Logger;

/**
 * <p>
 * To allow your bot to upload media in your MediaWiki add at least the following line
 * to your MediaWiki's LocalSettings.php:<br>
 *
 * <pre>
 * $wgEnableUploads = true;
 * </pre>
 * 
 * For more details see also 
 * <a href="http://www.mediawiki.org/wiki/Help:Configuration_settings#Uploads">Upload Config</a>
 * 
 * @author Justus Bisser
 * @author Thomas Stock
 * @supportedBy MediaWiki 1.11, 1.12, 1.13
 * 
 */
public class FileUpload extends MWAction {

	
	private static final Logger LOG = Logger.getLogger(FileUpload.class);
	private final Get g;
	private boolean first = true;
	private boolean second = true;
	private final SimpleFile a;
	private Post msg;
	/**
	 * 
	 * @param a the
	 * @param bot a 
	 * @throws ActionException on problems with file
	 * @throws VersionException on wrong MediaWiki version
	 */
	public FileUpload(final SimpleFile a, MediaWikiBotImpl bot) throws ActionException, VersionException {

		if (!a.getFile().isFile() || !a.getFile().canRead()) {
			throw new ActionException("no such file " + a.getFile());
		}
		
		if (!bot.isLoggedIn()) {
			throw new ActionException("Please login first");
		}
		
		
		switch (bot.getVersion()) {
		case MW1_09:
		case MW1_10:
			throw new VersionException("Not supportet by this version of MW");
		}

		
		this.a = a;
		String uS = "";
		try {
			uS = "/index.php?title="
					+ URLEncoder.encode(a.getLabel(), MediaWikiBot.CHARSET)
					+ "&action=edit&dontcountme=s";
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		g = new Get(uS);
		
	}
	
	public HttpAction getNextMessage() {
		if (first) {
			first = false;
			return g;
		}
		String uS = "";
		// try {
		uS = "/Spezial:Hochladen";
		uS = "/index.php?title=Special:Upload";
		// uS = "/index.php?title=" + URLEncoder.encode("Spezial:Hochladen",
		// MediaWikiBot.CHARSET);
		// + "&action=submit";
		// } catch (UnsupportedEncodingException e) {
		// e.printStackTrace();
		// }

		try {

			LOG.info("WRITE: " + a.getLabel());
			FilePost post = new FilePost(uS);
	
			if (a.getText().length() == 0) {
				post.addPart("wpDestFile", a.getLabel());
			
				post.addPart("wpIgnoreWarning", "true");
				post.addPart("wpSourceType", "file");
				post.addPart("wpUpload", "Upload file");
//				 post.addPart("wpUploadDescription", "false");
//				 post.addPart("wpWatchthis", "false");

				post.addPart("wpUploadFile", a.getFile());
				// new FilePart( f.getName(), f)

			
			} else {
				post.addPart("wpDestFile", a.getLabel());
				
				post.addPart("wpIgnoreWarning", "true");
				post.addPart("wpSourceType", "file");
				post.addPart("wpUpload", "Upload file");
						// new StringPart("wpUploadDescription", "false"),
						// new StringPart("wpWatchthis", "false"),

				post.addPart("wpUploadFile", a.getFile());
						// new FilePart( f.getName(), f)
				post.addPart("wpUploadDescription", a.getText());
				

			}
			if (!a.getFile().exists()) {
				throw new FileNotFoundException();
			}
	

			msg = post;
			second = false;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return msg;
	}
	
	@Override
	public boolean hasMoreMessages() {
		return first || second;
	}
	
	@Override
	public String processAllReturningText(String s) throws ProcessException {
		
		if(s.contains("error")) {
//			System.out.println(s);
//			TODO nicer error handling 
			LOG.error("Upload failed");
			throw new ProcessException("Upload failed");
		}
		return "";
	}


}
