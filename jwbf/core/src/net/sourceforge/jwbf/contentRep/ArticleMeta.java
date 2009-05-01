package net.sourceforge.jwbf.contentRep;

import java.util.Date;


public interface ArticleMeta extends ContentAccessable {

	boolean isRedirect();
	Date getEditTimestamp();
}
