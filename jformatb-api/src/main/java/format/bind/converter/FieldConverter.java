/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package format.bind.converter;

import format.bind.annotation.FormatField;
import format.bind.annotation.FormatFieldConverter;

/**
 * Converts a text format field to Java type or vice versa.
 * 
 * @param <T> The Java type to be converted.
 * 
 * @author Yannick Ebongue
 * 
 * @see FormatFieldConverter
 * 
 */
public interface FieldConverter<T> {

	/**
	 * Converts a bound type value to a text format value.
	 * 
	 * @param field The text format field specification.
	 * @param value The Java value to be converted. Can be null.
	 * @return The formatted text value.
	 * @throws IllegalArgumentException if there is an error during the conversion.
	 */
	String format(final FormatField field, final T value);

	/**
	 * Converts a text format value to a bound type value.
	 * 
	 * @param field The text format field specification.
	 * @param source The text format value to be converted. Cannot be empty, but
	 * 		can be blank.
	 * @return The bound type value.
	 * @throws IllegalArgumentException if there is an error during the conversion.
	 */
	T parse(final FormatField field, final String source);

}
