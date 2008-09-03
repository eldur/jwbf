package net.sourceforge.jwbf.contentRep.mw;

import java.util.Date;

public interface ArticleMeta extends ContentAccessable {

	boolean isRedirect();
	Date getEditTimestamp();
}
