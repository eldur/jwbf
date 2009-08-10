package net.sourceforge.jwbf.core.contentRep;

import java.util.Date;

/**
 * 
 * @author Thomas Stock
 *
 */
public interface ArticleMeta extends ContentAccessable {
	/**
	 * TODO method is untested and MediaWiki special.
	 * 
	 * @return true if is
	 */
	boolean isRedirect();
	/**
	 * @return the
	 */
	Date getEditTimestamp();
	
	/**
	 * 
	 * @return the
	 */
	String getRevisionId();
}
