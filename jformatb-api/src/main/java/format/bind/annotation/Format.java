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
 * Specifies that the annotated element is a text format.
 * 
 * <p><b>Usage</b></p>
 * <p>
 * The {@code Format} annotation can be used with the following program elements:
 * </p>
 * <ul>
 * <li>a top level class</li>
 * <li>a JavaBean property</li>
 * <li>non static and non transient field</li>
 * </ul>
 * 
 * @author Yannick Ebongue
 * 
 */
@Documented
@Retention(RUNTIME)
@Target({ FIELD, METHOD, TYPE })
public @interface Format {

	/**
	 * (Optional) The name of the text format which the class is mapped.
	 * 
	 * @return The name of the text format Java type.
	 */
	String name() default "";

	/**
	 * (Required) The pattern of the text format which the class is mapped.
	 * 
	 * @return The pattern used to convert the text format Java type.
	 */
	String pattern();

}
