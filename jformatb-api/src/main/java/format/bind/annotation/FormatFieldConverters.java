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
 * A container for multiple {@link FormatFieldConverter} annotations.
 * 
 * @author Yannick Ebongue
 * 
 * @see FormatFieldConverter
 */
@Documented
@Retention(RUNTIME)
@Target({ TYPE, FIELD, METHOD })
public @interface FormatFieldConverters {

	/**
	 * (Required) One or more field converters.
	 * 
	 * @return An array of field converters.
	 */
	FormatFieldConverter[] value();

}
