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

import format.bind.Formatter;

/**
 * Instructs the {@link Formatter} to find the target {@link Format}.
 * 
 * @author Yannick Ebongue
 * 
 * @see Format
 * @see FormatTypeInfo
 * @see FormatTypeValue
 */
@Documented
@Retention(RUNTIME)
@Target({ TYPE, FIELD, METHOD })
public @interface FormatSubTypes {

	/**
	 * The list of {@link Format} subclasses to include.
	 * 
	 * @return An array of {@link Format} subclasses.
	 */
	Class<?>[] value();

}
