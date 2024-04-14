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

import java.util.Optional;

import format.bind.annotation.Format;
import format.bind.annotation.FormatField;
import format.bind.annotation.FormatFieldContainer;
import format.bind.annotation.FormatFieldOverride;
import format.bind.spi.FormatProcessorFactory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.Value;
import lombok.With;

/**
 * The entry point to the format binding API. It provides functionality for 
 * reading (parse) and writing (format) text format, either to and from basic
 * POJOs (Plain Old Java Objects).
 * 
 * @param <T> The base type of object to convert to or from
 * 
 * @author Yannick Ebongue
 * 
 * @see Format
 * @see FormatField
 * @see FormatFieldContainer
 * @see FormatFieldOverride
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter(AccessLevel.NONE)
public class Formatter<T> {

	/** The base type of this {@code Formatter}. */
	private Class<T> type;

	/**
	 * The text format pattern of this {@code Formatter}.
	 * 
	 * @param pattern The text format pattern to set.
	 */
	@With
	private String pattern;

	/** The {@link FormatProcessorFactory} instance. */
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private FormatProcessorFactory processorFactory = getProcessorFactory();

	/**
	 * Creates a new instance of {@link FormatReader}.
	 * 
	 * @param <F> The instance type of the {@link FormatReader}.
	 * @return The {@link FormatReader} instance.
	 * @see FormatProcessorFactory#createReader(Class, String)
	 */
	public final <F extends FormatReader<T, F>> FormatReader<T, F> createReader() {
		return processorFactory.createReader(type, pattern);
	}

	/**
	 * Creates a new instance of {@link FormatWriter}.
	 * 
	 * @param <F> The instance type of the {@link FormatWriter}.
	 * @return The {@link FormatWriter} instance.
	 * @see FormatProcessorFactory#createWriter(Class, String)
	 */
	public final <F extends FormatWriter<T, F>> FormatWriter<T, F> createWriter() {
		return processorFactory.createWriter(type, pattern);
	}

	/**
	 * Formats the given object to produce the corresponding text format.
	 * 
	 * @param obj The object instance to format.
	 * @return The formatted text.
	 * @throws FormatProcessingException if an error occurs during {@code obj} formatting.
	 * @see FormatWriter#write(Object)
	 */
	public final String format(final T obj) throws FormatProcessingException {
		return createWriter().write(obj);
	}

	/**
	 * Parses the given text format from the beginning to produce an object.
	 * 
	 * @param text The text format to parse.
	 * @return The parsed object.
	 * @throws FormatProcessingException if an error occurs during {@code text} parsing.
	 * @see FormatReader#read(String)
	 */
	public final T parse(final String text) throws FormatProcessingException {
		return createReader().read(text);
	}

	/**
	 * Create a new instance of {@code Formatter}.
	 * 
	 * @param <T> The type parameter of the base type.
	 * @param type The base type instance to process.
	 * @return A new instance of {@code Formatter}.
	 */
	public static final <T> Formatter<T> of(Class<T> type) {
		String pattern = Optional.ofNullable(type.getAnnotation(Format.class))
				.map(Format::pattern)
				.orElse(null);
		return new Formatter<>(type, pattern);
	}

	/**
	 * Obtain the {@link FormatProcessorFactory} SPI implementation instance.
	 * 
	 * @return The {@link FormatProcessorFactory} instance.
	 * @throws FormatException if no {@link FormatProcessorFactory}
	 * 		implementation was found.
	 */
	private static FormatProcessorFactory getProcessorFactory() {
		return Providers.getProcessorFactory();
	}

}
