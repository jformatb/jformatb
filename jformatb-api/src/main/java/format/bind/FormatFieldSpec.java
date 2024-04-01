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

import format.bind.annotation.FormatField;

/**
 * Represents the computed configuration of a text format field.
 * 
 * @author Yannick Ebongue
 */
public interface FormatFieldSpec {

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
	 * The placeholder of the text format field.
	 * 
	 * @return The placeholder of the text format field.
	 */
	String placeholder();

	/**
	 * Used in {@link FormatField#type()} to specify the type of the
	 * text format field.
	 */
	public enum Type {

		/** Inferred from property signature. */
		DEFAULT,

		/** Alphanumeric text format field. */
		ALPHANUMERIC,

		/** Numeric text format field. */
		NUMERIC
	}

}
