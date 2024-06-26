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
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import format.bind.Formatter;

/**
 * Instructs the {@link Formatter} how to find the {@link FormatTypeValue} that
 * identifies the target {@link Format} Java type of the map entry value.
 * 
 * @author Yannick Ebongue
 * 
 * @see Format
 * @see FormatMapEntry
 * @see FormatSubTypes
 * @see FormatTypeValue
 * @see Formatter
 * 
 */
@Repeatable(FormatMapEntryTypeInfoList.class)
@Documented
@Retention(RUNTIME)
@Target({ FIELD, METHOD })
public @interface FormatMapEntryTypeInfo {

	/**
	 * (Required) The keys of the map entry to map.
	 * 
	 * @return The keys of the map entry.
	 */
	String[] keys();

	/**
	 * (Required) The format field name of the text format field that contains the type
	 * info value.
	 * 
	 * @return The format field name of the text format field.
	 */
	String fieldName();

	/**
	 * (Required) The length of the text format field containing the type info value.
	 * 
	 * @return The length of the text format field.
	 */
	int length();

	/**
	 * (Optional) The index of the starting point of the text format field in the whole
	 * formatted text.
	 * 
	 * @return The index of the starting point of the text format field.
	 */
	int start() default 0;

}
