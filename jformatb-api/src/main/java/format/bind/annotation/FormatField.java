/*
 * Copyright 2024 jFormat-B
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package format.bind.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import format.bind.FormatFieldDescriptor.Type;

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

}
