package net.sourceforge.jwbf.actions.mediawiki;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 
 * @author Thomas Stock
 *
 */
public final class MediaWiki {

	public static final int ARTICLE = 1 << 1;
	public static final int MEDIA = 1 << 2;
	public static final int SUBCATEGORY = 1 << 3;

	static final String CHARSET = "utf-8";

	public static final int NS_MAIN = 0;
	public static final int NS_MAIN_TALK = 1;
	public static final int NS_USER = 2;
	public static final int NS_USER_TALK = 3;
	public static final int NS_META = 4;
	public static final int NS_META_TALK = 5;
	public static final int NS_IMAGES = 6;
	public static final int NS_IMAGES_TALK = 7;
	public static final int NS_MEDIAWIKI = 8;
	public static final int NS_MEDIAWIKI_TALK = 9;
	public static final int NS_TEMPLATE = 10;
	public static final int NS_TEMPLATE_TALK = 11;
	public static final int NS_HELP = 12;
	public static final int NS_HELP_TALK = 13;
	public static final int NS_CATEGORY = 14;
	public static final int NS_CATEGORY_TALK = 15;
	
	public static final int [] NS_ALL = {NS_MAIN, NS_MAIN_TALK, NS_USER, NS_USER_TALK
		, NS_META, NS_META_TALK, NS_IMAGES, NS_IMAGES_TALK, NS_MEDIAWIKI, NS_MEDIAWIKI_TALK
		, NS_TEMPLATE, NS_TEMPLATE_TALK, NS_HELP, NS_HELP_TALK, NS_CATEGORY, NS_CATEGORY_TALK};
	
	/**
	 * Representaion of MediaWiki version.
	 * @author Thomas Stock
	 *
	 */
	public enum Version {
		MW1_09, MW1_10, MW1_11, MW1_12, MW1_13, MW1_14, MW1_15, UNKNOWN, DEVELOPMENT;
		
		/**
		 * 
		 * @return a, like 1.15
		 */
		public String getNumber() {
			return name().replace("MW", "").replace("_0", "_").replace("_", ".");
		}
		
		private int getIntValue() {
			try {
				return Integer.parseInt(getNumber().replace(".", ""));
			} catch (Exception e) {
				if (this == DEVELOPMENT)
					return Integer.MAX_VALUE;
				return -1;
			}
		}
		/**
		 * 
		 * @param v a
		 * @return true if
		 */
		public boolean greaterEqThen(Version v) {
			if (v.getIntValue() > getIntValue())
				return false;
			return true;
		}
	}
	
	private MediaWiki() {
//		do nothing
	}
	/**
	 * 
	 * @return the
	 */
	public static String getCharset() {
		return CHARSET;
	}
	/**
	 * 
	 * @param s a
	 * @return encoded s
	 */
	public static String encode(String s) {
		try {
			return URLEncoder.encode(s, MediaWiki.CHARSET);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return s;
	}
	/**
	 * 
	 * @param s a
	 * @return decoded s
	 */
	public static String decode(final String s) {
		String out = HTMLEntities.unhtmlentities(s);
		out = HTMLEntities.unhtmlQuotes(out);
		return out;
	}
}
