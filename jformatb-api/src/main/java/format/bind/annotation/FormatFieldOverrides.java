/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package format.bind.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Used to override multiple text format fields mapping.
 * 
 * @author Yannick Ebongue
 * 
 * @see FormatFieldOverride
 */
@Documented
@Retention(RUNTIME)
@Target({ TYPE, FIELD, METHOD })
public @interface FormatFieldOverrides {

	/**
	 * (Required) One or more field overrides.
	 * 
	 * @return An array of field overrides.
	 */
	FormatFieldOverride[] value();

}
