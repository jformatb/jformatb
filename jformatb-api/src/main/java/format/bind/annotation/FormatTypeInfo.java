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
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import format.bind.Formatter;

/**
 * Instructs the {@link Formatter} how to find the {@link FormatTypeValue} that
 * identifies the target {@link Format} Java type.
 * 
 * @author Yannick Ebongue
 * 
 * @see Format
 * @see FormatSubTypes
 * @see FormatTypeValue
 * @see Formatter
 */
@Inherited
@Documented
@Retention(RUNTIME)
@Target({ TYPE, FIELD, METHOD })
public @interface FormatTypeInfo {

	/**
	 * The format field name of the text format field that contains the type
	 * info value.
	 * 
	 * @return The format field name of the text format field.
	 */
	String fieldName();

	/**
	 * The length of the text format field containing the type info value.
	 * 
	 * @return The length of the text format field.
	 */
	int length() default 1;

	/**
	 * The index of the starting point of the text format field in the whole
	 * formatted text.
	 * 
	 * @return The index of the starting point of the text format field.
	 */
	int start() default 0;

}
