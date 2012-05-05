package net.sourceforge.jwbf.mediawiki.actions.util;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;

/**
 * Annotation which indicates MediaWiki support. It will be used to validate if 
 * the given version of MediaWiki supports the selected action. Works only 
 * with {@link MWAction}.
 * 
 * @author Thomas Stock
 *
 */
@Target({ CONSTRUCTOR, TYPE }) 
@Retention(RUNTIME)
@Documented
public @interface SupportedBy {
	/**
	 * 
	 * 
	 */
	MediaWiki.Version [] value();
}
