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
