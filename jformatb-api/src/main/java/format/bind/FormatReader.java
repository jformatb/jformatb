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

/**
 * The {@code FormatReader} class is responsible for processing the deserialization
 * of text format data into newly created Java type.
 * 
 * @param <T> The Java type of object to create.
 * @param <F> The type of the text {@link FormatReader}.
 * 
 * @author Yannick Ebongue
 */
public interface FormatReader<T, F extends FormatReader<T, F>> extends FormatProcessor<T, F> {

	/**
	 * Deserialize text format data into the resulting Java type.
	 * 
	 * @param text The text format to deserialize.
	 * @return The newly created Java object.
	 * @throws FormatProcessingException if an error occurs during the read process.
	 */
	T read(final String text) throws FormatProcessingException;

}
