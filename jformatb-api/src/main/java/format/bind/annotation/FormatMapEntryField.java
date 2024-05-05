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

import format.bind.converter.FieldConverter;

/**
 * Specifies the mapping of a map entry to a text format field.
 * 
 * @author Yannick Ebongue
 * 
 */
@Repeatable(FormatMapEntryFields.class)
@Documented
@Retention(RUNTIME)
@Target({ FIELD, METHOD })
public @interface FormatMapEntryField {

	/**
	 * (Required) The keys of the map entry to map.
	 * 
	 * @return The keys of the map entry.
	 */
	String[] keys();

	/**
	 * (Required) The {@link FormatField} annotation with specification to apply.
	 * 
	 * @return The {@link FormatField} annotation with specification to apply.
	 */
	FormatField field();

	/**
	 * (Optional) The class that converts a text format field of the map entry
	 * 		value.
	 * 
	 * @return The class that converts a text format field value of the map entry
	 * 		value.
	 */
	@SuppressWarnings("rawtypes")
	Class<? extends FieldConverter> converter() default FormatFieldConverter.DEFAULT.class;

}
