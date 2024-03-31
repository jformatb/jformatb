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
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import format.bind.converter.FieldConverter;

/**
 * Specifies a converter that implements {@link FieldConverter} for custom conversion.
 * 
 * <p><b>Usage</b></p>
 * <p>
 * The {@code FormatFieldConverter} annotation can be used with the following program elements:
 * </p>
 * <ul>
 * <li>a JavaBean property</li>
 * <li>non static and non transient field</li>
 * <li>from within {@link FormatFieldConverters}</li>
 * </ul>
 * 
 * @author Yannick Ebongue
 * 
 */
@Repeatable(FormatFieldConverters.class)
@Documented
@Retention(RUNTIME)
@Target({ TYPE, FIELD, METHOD })
public @interface FormatFieldConverter {

	/**
	 * (Optional) The {@code property} must be specified if this annotation is used at
	 * class level. Otherwise the {@code property} must not be specified.
	 * 
	 * @return The property name of the text field converter.
	 */
	String property() default "";

	/**
	 * (Required) The class that converts a text format field value to a bound type
	 * or vice versa.
	 * 
	 * <p>
	 * See {@link FieldConverter} for more details.
	 * </p>
	 * 
	 * @return The class that converts a text format field.
	 */
	Class<? extends FieldConverter<?>> value();

}
