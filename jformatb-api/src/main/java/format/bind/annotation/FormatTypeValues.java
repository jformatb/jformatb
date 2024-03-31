/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package format.bind.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * A container for multiple {@link FormatTypeValue} annotations.
 * 
 * @author Yannick Ebongue
 * 
 * @see FormatTypeValue
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface FormatTypeValues {

	/**
	 * The {@link FormatTypeValue} list of the {@link Format} Java type.
	 * 
	 * @return An array of {@link FormatTypeValue} annotations.
	 */
	FormatTypeValue[] value();

}
