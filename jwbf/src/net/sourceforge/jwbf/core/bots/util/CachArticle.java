package net.sourceforge.jwbf.core.bots.util;

import java.io.Serializable;
import java.util.Date;

import net.sourceforge.jwbf.core.contentRep.ArticleMeta;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
/**
 * 
 * @author Thomas Stock
 *
 */
class CachArticle extends SimpleArticle implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8061809995421543211L;
	private Date d;

	
	
	CachArticle() {
		super();
		setSaveDate(1L);
	}


	CachArticle(ArticleMeta ca) {
		super(ca);
		
		setSaveDate(System.currentTimeMillis());
	}
	
	Date getSaveDate() {
		return d;
	}
	
	Date getInnerDate() {
		return d;
	}

	void setSaveDate(long milis) {
		d = new Date(milis);
	}

	
}
