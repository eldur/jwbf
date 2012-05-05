package net.sourceforge.jwbf.core.bots.util;

import java.io.Serializable;
import java.util.Date;

import net.sourceforge.jwbf.core.contentRep.ArticleMeta;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;

/**
 * 
 * @author Thomas Stock
 * @deprecated
 */
@Deprecated
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

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((d == null) ? 0 : d.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (!(obj instanceof CachArticle))
      return false;
    CachArticle other = (CachArticle) obj;
    if (d == null) {
      if (other.d != null)
        return false;
    } else if (!d.equals(other.d))
      return false;
    return true;
  }

}
