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

import java.util.Map;

/**
 * The {@code FormatWriter} class is responsible for processing the serialization of
 * Java object into text format.
 * 
 * @param <T> The Java type of object to create.
 * @param <F> The type of the text {@link FormatWriter}.
 * 
 * @author Yannick Ebongue
 */
public interface FormatWriter<T, F extends FormatWriter<T, F>> extends FormatProcessor<T, F> {

	/**
	 * Set additional text field property values.
	 * 
	 * @param properties The additional text field property values to set
	 * @return This {@code FormatWriter}.
	 */
	F setProperties(final Map<String, Object> properties);

	/**
	 * Set additional text field property values.
	 * 
	 * @param properties The additional text field property values to set
	 * @return This {@code FormatWriter}.
	 */
	default F withProperties(final Map<String, Object> properties) {
		return setProperties(properties);
	}

	/**
	 * Serialize the specified {@code obj} into a formatted text data.
	 * 
	 * @param obj The Java object to serialize.
	 * @return The formatted text data.
	 * @throws FormatProcessingException if an error occurs during the write process.
	 */
	String write(final T obj) throws FormatProcessingException;

}
