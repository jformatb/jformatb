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
package format.bind.spi;

import format.bind.FormatReader;
import format.bind.FormatWriter;
import format.bind.Formatter;

/**
 * Factory for creating {@link FormatReader} and {@link FormatWriter}.
 * 
 * @author Yannick Ebongue
 * 
 * @see Formatter
 * @see FormatReader
 * @see FormatWriter
 */
public interface FormatProcessorFactory {

	/**
	 * Creates a new instance of {@link FormatReader} for text format parsing.
	 * @param <T> The result Java object type.
	 * @param <F> The type of the {@link FormatReader} to create.
	 * @param type The class instance of the result Java object.
	 * @param pattern The pattern of the text format to read.
	 * @return The instance of {@link FormatReader}.
	 */
	<T, F extends FormatReader<T, F>> FormatReader<T, F> createReader(Class<? extends T> type, String pattern);

	/**
	 * Creates a new instance of {@link FormatWriter} for Java object formatting.
	 * @param <T> The source Java object type.
	 * @param <F> The type of the {@link FormatWriter} to create.
	 * @param type The class instance of the source Java object.
	 * @param pattern The pattern of the text format to write.
	 * @return The instance of {@link FormatWriter}.
	 */
	<T, F extends FormatWriter<T, F>> FormatWriter<T, F> createWriter(Class<? extends T> type, String pattern);

}
