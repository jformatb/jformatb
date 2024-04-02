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
