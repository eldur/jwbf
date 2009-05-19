package net.sourceforge.jwbf.actions;

import net.sourceforge.jwbf.actions.util.HttpAction;
import net.sourceforge.jwbf.actions.util.ProcessException;

public interface ReturningText {

	/**
	 * 
	 * @param s the returning text
	 * @param hm a
	 * @return the retruning text or a modification of it
	 * @throws ProcessException on internal problems of implementing class
	 */
	String processReturningText(final String s, HttpAction hm) throws ProcessException;
}
