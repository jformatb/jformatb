/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package format.bind.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Maps a JavaBean property to a text format field.
 * 
 * <p><b>Usage</b></p>
 * <p>
 * The {@code FormatField} annotation can be used with the following program elements:
 * </p>
 * <ul>
 * <li>a JavaBean property</li>
 * <li>non static and non transient field</li>
 * </ul>
 * 
 * @author Yannick Ebongue
 * 
 */
@Documented
@Retention(RUNTIME)
@Target({ FIELD, METHOD })
public @interface FormatField {

	/**
	 * (Optional) The name of the text format field.
	 * 
	 * <p>
	 * If the value is empty (the default value), the text format field name is derived
	 * from the JavaBean property name.
	 * </p>
	 * 
	 * @return The name of the text format field.
	 */
	String name() default "";

	/**
	 * (Optional) The type of the text format field.
	 * 
	 * @return The type of the text format field.
	 */
	Type type() default Type.DEFAULT;

	/**
	 * (Optional) The length of the text format field.
	 * 
	 * @return The length of the text format field.
	 */
	int length() default 0;

	/**
	 * (Optional) The scale for numeric text format field.
	 * 
	 * @return The scale of the numeric text format field.
	 */
	int scale() default 0;

	/**
	 * (Optional) The format of the text format field.
	 * 
	 * @return The format of the text format field.
	 */
	String format() default "";

	/**
	 * (Optional) The placeholder of the text format field.
	 * 
	 * @return The placeholder of the text format field.
	 */
	String placeholder() default "";

	/**
	 * Used in {@link FormatField#type()} to specify the type of the
	 * text format field.
	 */
	public enum Type {

		/** Inferred from property signature. */
		DEFAULT,

		/** Alphanumeric text format field. */
		ALPHANUMERIC,

		/** Numeric text format field. */
		NUMERIC
	}

}
