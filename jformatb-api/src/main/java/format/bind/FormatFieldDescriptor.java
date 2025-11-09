/*
 * Copyright (c) 2024 jFormat-B
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
package format.bind;

import java.nio.charset.Charset;
import java.util.Locale;

import format.bind.annotation.FormatField;
import format.bind.annotation.FormatField.Type;

/**
 * Represents the computed configuration of a text format field.
 * 
 * @author Yannick Ebongue
 */
public interface FormatFieldDescriptor {

	/**
	 * The name of the text format field.
	 * 
	 * <p>
	 * If the value is empty (the default value), the text format field name is derived
	 * from the JavaBean property name.
	 * </p>
	 * 
	 * @return The name of the text format field.
	 */
	String name();

	/**
	 * The type of the text format field.
	 * 
	 * @return The type of the text format field.
	 */
	Type type();

	/**
	 * The charset of the text format field.
	 * 
	 * @return The charset of the text format field.
	 */
	Charset charset();

	/**
	 * The length of the text format field.
	 * 
	 * @return The length of the text format field.
	 */
	int length();

	/**
	 * The scale for numeric text format field.
	 * 
	 * @return The scale of the numeric text format field.
	 */
	int scale();

	/**
	 * The format of the text format field.
	 * 
	 * @return The format of the text format field.
	 */
	String format();

	/**
	 * The {@link Locale} to be used with the {@code #format()} if specified.
	 * 
	 * @return The {@link Locale} of the text format field.
	 * 
	 * @see FormatField#locale()
	 */
	String locale();

	/**
	 * The placeholder of the text format field.
	 * 
	 * @return The placeholder of the text format field.
	 */
	String placeholder();

	/**
	 * Whether the text format field is read only.
	 * 
	 * @return {@code true} if text format field is read only; otherwise returns
	 * 		{@code false} (default value).
	 * 
	 * @see FormatField#readOnly()
	 */
	boolean readOnly();

	/**
	 * The target class of the text format field.
	 * 
	 * @return The target class of the text format field.
	 */
	Class<?> targetClass();

}
