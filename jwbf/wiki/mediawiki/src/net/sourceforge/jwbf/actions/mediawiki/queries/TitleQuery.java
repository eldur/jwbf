package net.sourceforge.jwbf.actions.mediawiki.queries;

import java.util.Iterator;

import net.sourceforge.jwbf.actions.mediawiki.util.MWAction;
import net.sourceforge.jwbf.actions.mediawiki.util.VersionException;

import org.apache.log4j.Logger;

public abstract class TitleQuery extends MWAction implements Iterable<String>, Iterator<String> {

	protected Iterator<String> titleIterator;
	private Logger log = Logger.getLogger(getClass());
	protected TitleQuery() throws VersionException {
		super();
	}


	@SuppressWarnings("unchecked")
	public final Iterator<String> iterator() {
		try {
			return (Iterator<String>) this.clone();
		} catch (CloneNotSupportedException e) {
			log.error("cloning should be supported");
			e.printStackTrace();
			return null;
		}
	}
	
	

	

	public final boolean hasNext() {
		prepareCollection();
		return titleIterator.hasNext();
	}

	protected abstract void prepareCollection();
	


	public final String next() {
		prepareCollection();
		return titleIterator.next();
	}

	public final void remove() {
		titleIterator.remove();
	}


}
