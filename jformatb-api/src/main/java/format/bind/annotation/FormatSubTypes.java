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

import format.bind.Formatter;

/**
 * Instructs the {@link Formatter} to find the target {@link Format}.
 * 
 * @author Yannick Ebongue
 * 
 * @see Format
 * @see FormatTypeInfo
 * @see FormatTypeValue
 */
@Documented
@Retention(RUNTIME)
@Target({ TYPE, FIELD, METHOD })
public @interface FormatSubTypes {

	/**
	 * The list of {@link Format} subclasses to include.
	 * 
	 * @return An array of {@link Format} subclasses.
	 */
	Class<?>[] value();

}
