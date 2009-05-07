package net.sourceforge.jwbf.actions.mediawiki.util;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import net.sourceforge.jwbf.actions.mediawiki.MediaWiki;

@Target( {CONSTRUCTOR, TYPE } ) 
@Retention( RUNTIME )
@Documented
public @interface SupportedBy {
	MediaWiki.Version [] value();
}
