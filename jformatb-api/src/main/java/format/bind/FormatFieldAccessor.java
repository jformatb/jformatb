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
package format.bind;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

/**
 * Provides access to text format field properties.
 * 
 * @author Yannick Ebongue
 * 
 */
public interface FormatFieldAccessor extends AnnotatedElement {

	/**
	 * Get the name of the text format field.
	 * 
	 * @return The name of the text format field.
	 */
	String getName();

	/**
	 * Get the type of the text format field.
	 * 
	 * @return The type of the text format field.
	 */
	Class<?> getType();

	/**
	 * Get the generic container type of the text format field.
	 * 
	 * <p>
	 * Only two generic types are supported:
	 * </p>
	 * <ul>
	 * <li>{@link java.util.List}</li>
	 * <li>{@link java.util.Map}
	 * </ul>
	 * 
	 * @return The generic container type of the text format field.
	 */
	Type getGenericType();

	/**
	 * Defines the text format field access strategies.
	 * 
	 * @author Yannick Ebongue
	 */
	enum Strategy {

		/**
		 * Used to access text format field by field (Default).
		 */
		FIELD,

		/**
		 * Used to access text format field by bean property.
		 */
		PROPERTY

	}

}
