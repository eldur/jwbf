package net.sourceforge.jwbf.test;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import net.sourceforge.jwbf.mediawiki.actions.util.SupportedBy;

/**
 * 
 * @see SupportedBy
 */
@Target({ TYPE })
@Retention(RUNTIME)
@Documented
public @interface TestNamer {
  Class<?> value();
}
