/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package format.bind.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Provides the text value to identify a target {@link Format} Java type.
 * 
 * @author Yannick Ebongue
 * 
 * @see FormatTypeInfo
 */
@Documented
@Repeatable(FormatTypeValues.class)
@Retention(RUNTIME)
@Target(TYPE)
public @interface FormatTypeValue {

	/**
	 * (Required) The text value that identify the target {@link Format} Java type.
	 * 
	 * @return The text value that identify the target {@link Format} Java type.
	 */
	String value();

}
