
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
import java.util.Hashtable;

import net.sourceforge.jwbf.actions.mw.util.ActionException;
import net.sourceforge.jwbf.actions.mw.util.MWAction;
import net.sourceforge.jwbf.actions.mw.util.ProcessException;
import net.sourceforge.jwbf.bots.util.LoginData;
import net.sourceforge.jwbf.contentRep.mw.SimpleFile;

import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.log4j.Logger;

/**
 * TODO no api use.
 * @author Justus Bisser
 * 
 */
public class FileUpload extends MWAction {

	
	private static final Logger LOG = Logger.getLogger(FileUpload.class);
	/**
	 * 
	 * @param a the
	 * @param tab internal value set
	 * @param login a 
	 * @throws ActionException on problems with file
	 */
	public FileUpload(final SimpleFile a,
			final Hashtable<String, String> tab,
			LoginData login) throws ActionException {

		if (!a.getFile().isFile() || !a.getFile().canRead()) {
			throw new ActionException("no such file " + a.getFile());
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
			PostMethod post = new PostMethod(uS);
			Part[] parts;
			if (a.getText().isEmpty()) {
				parts = new Part[] {
						new StringPart("wpDestFile", a.getLabel()),
						new StringPart("wpIgnoreWarning", "true"),
						new StringPart("wpSourceType", "file"),
						new StringPart("wpUpload", "Upload file"),
						// new StringPart("wpUploadDescription", "false"),
						// new StringPart("wpWatchthis", "false"),

						new FilePart("wpUploadFile", a.getFile())
				// new FilePart( f.getName(), f)

				};
			} else {
				parts = new Part[] {
						new StringPart("wpDestFile", a.getLabel()),
						new StringPart("wpIgnoreWarning", "true"),
						new StringPart("wpSourceType", "file"),
						new StringPart("wpUpload", "Upload file"),
						// new StringPart("wpUploadDescription", "false"),
						// new StringPart("wpWatchthis", "false"),

						new FilePart("wpUploadFile", a.getFile()),
						// new FilePart( f.getName(), f)
						new StringPart("wpUploadDescription", a.getText()) };

			}
			post.setRequestEntity(new MultipartRequestEntity(parts, post
					.getParams()));

			// int statusCode = hc.executeMethod(post);
			// log(statusCode);

			// log(Arrays.asList(post.getResponseHeaders()));
			//
			// String res = post.getResponseBodyAsString();
			// LOG.debug(res);
			// post.releaseConnection();
			// pm.setRequestBody(new NameValuePair[] { action, wpStarttime,
			// wpEditToken, wpEdittime, wpTextbox, wpSummary, wpMinoredit });
			msgs.add(post);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
