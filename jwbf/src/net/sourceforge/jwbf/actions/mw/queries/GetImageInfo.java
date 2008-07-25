package net.sourceforge.jwbf.actions.mw.queries;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import net.sourceforge.jwbf.actions.mw.util.MWAction;
import net.sourceforge.jwbf.actions.mw.util.ProcessException;
import net.sourceforge.jwbf.actions.mw.util.VersionException;
import net.sourceforge.jwbf.bots.MediaWikiBot;
import net.sourceforge.jwbf.contentRep.mw.Version;

import org.apache.commons.httpclient.methods.GetMethod;

public class GetImageInfo extends MWAction {

	public GetImageInfo(String name, Version v) throws VersionException {
		Requestor r = null;
		switch (v) {
		case MW1_09:
			r = new V1_09Requestor();
			break;
		case MW1_10:
			r = new V1_10Requestor();
			break;
		default:
			r = new Requestor();
			break;
		}
		r.buildRequest(name);
	}

	private class Requestor {

		void buildRequest(String name) throws VersionException {
			try {
				msgs.add(new GetMethod("/api.php?action=query&titles=Image:"
						+ URLEncoder.encode(name, MediaWikiBot.CHARSET)
						+ "&iiprop=url"
						+ "&format=xml"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private class V1_10Requestor extends Requestor {
		void buildRequest(String name) throws VersionException {
			throw new VersionException("Not supportet by this version of MW");
		}
	}

	private class V1_09Requestor extends Requestor {
		void buildRequest(String name) throws VersionException {
			throw new VersionException("Not supportet by this version of MW");
		}
	}

	public String getUrlAsString() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String processAllReturningText(String s) throws ProcessException {
		System.out.println(s);
		return "";
	}
}
