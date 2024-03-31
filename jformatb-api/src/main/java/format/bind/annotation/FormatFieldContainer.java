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
 * Indicates that the JavaBean property contains nested text format fields.
 * 
 * <p><b>Usage</b></p>
 * <p>
 * The {@code FormatFieldContainer} annotation can be used with the following program elements:
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
public @interface FormatFieldContainer {}
