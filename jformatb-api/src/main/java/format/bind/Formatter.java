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

import java.nio.charset.Charset;
import java.util.Optional;
import java.util.function.BiFunction;

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
	private Class<? extends T> type;

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
		return reader();
	}

	/**
	 * Creates a new instance of {@link FormatWriter}.
	 * 
	 * @param <F> The instance type of the {@link FormatWriter}.
	 * @return The {@link FormatWriter} instance.
	 * @see FormatProcessorFactory#createWriter(Class, String)
	 */
	public final <F extends FormatWriter<T, F>> FormatWriter<T, F> createWriter() {
		return writer();
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
	 * Formats the given object to produce the corresponding formatted byte array.
	 * 
	 * @param obj The object instance to format.
	 * @return The formatted byte array.
	 * @throws FormatProcessingException if an error occurs during {@code obj} formatting.
	 * @see FormatWriter#writeBytes(Object)
	 */
	public final byte[] formatBytes(final T obj) throws FormatProcessingException {
		return createWriter().writeBytes(obj);
	}

	/**
	 * Formats the given object to produce the corresponding formatted byte array.
	 * 
	 * @param obj The object instance to format.
	 * @param charsetName The default charset name for encoding operation.
	 * @return The formatted byte array.
	 * @throws FormatProcessingException if an error occurs during {@code obj} formatting.
	 * @see FormatWriter#writeBytes(Object)
	 * @see FormatProcessor#withCharset(String)
	 */
	public final byte[] formatBytes(final T obj, final String charsetName) throws FormatProcessingException {
		return createWriter().withCharset(charsetName).writeBytes(obj);
	}

	/**
	 * Formats the given object to produce the corresponding formatted byte array.
	 * 
	 * @param obj The object instance to format.
	 * @param charset The default charset for encoding operation.
	 * @return The formatted byte array.
	 * @throws FormatProcessingException if an error occurs during {@code obj} formatting.
	 * @see FormatWriter#writeBytes(Object)
	 * @see FormatProcessor#withCharset(Charset)
	 */
	public final byte[] formatBytes(final T obj, final Charset charset) throws FormatProcessingException {
		return createWriter().withCharset(charset).writeBytes(obj);
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
	 * Parses the given formatted byte array from the beginning to produce an object.
	 * 
	 * <p>
	 * The default charset used for decoding operation is provided the JVM.
	 * </p>
	 * 
	 * @param bytes The formatted byte array to parse.
	 * @return The parsed object.
	 * @throws FormatProcessingException if an error occurs during {@code bytes} parsing.
	 * @see FormatReader#readBytes(byte[])
	 */
	public final T parseBytes(final byte[] bytes) throws FormatProcessingException {
		return createReader().readBytes(bytes);
	}

	/**
	 * Parses the given formatted byte array from the beginning to produce an object.
	 * 
	 * @param bytes The formatted byte array to parse.
	 * @param charsetName The default charset name for decoding operation.
	 * @return The parsed object.
	 * @throws FormatProcessingException if an error occurs during {@code bytes} parsing.
	 * @see FormatReader#readBytes(byte[])
	 * @see FormatProcessor#withCharset(String)
	 */
	public final T parseBytes(final byte[] bytes, final String charsetName) throws FormatProcessingException {
		return createReader().withCharset(charsetName).readBytes(bytes);
	}

	/**
	 * Parses the given formatted byte array from the beginning to produce an object.
	 * 
	 * @param bytes The formatted byte array to parse.
	 * @param charset The default charset for decoding operation.
	 * @return The parsed object.
	 * @throws FormatProcessingException if an error occurs during {@code bytes} parsing.
	 * @see FormatReader#readBytes(byte[])
	 * @see FormatProcessor#withCharset(Charset)
	 */
	public final T parseBytes(final byte[] bytes, final Charset charset) throws FormatProcessingException {
		return createReader().withCharset(charset).readBytes(bytes);
	}

	/**
	 * Provides the default reader of this formatter.
	 * 
	 * @param <F> The instance type of the {@link FormatReader}.
	 * @return The {@link FormatReader} instance.
	 * @see FormatProcessorFactory#createReader(Class, String)
	 */
	public final <F extends FormatReader<T, F>> FormatReader<T, F> reader() {
		return reader(processorFactory::createReader);
	}

	/**
	 * Provides a new reader created by the specified factory function.
	 * 
	 * @param <F> The instance type of the {@link FormatReader}.
	 * @param factory The factory function.
	 * @return The {@link FormatReader} instance.
	 */
	public final <F extends FormatReader<T, F>> FormatReader<T, F> reader(
			BiFunction<Class<? extends T>, String, FormatReader<T, F>> factory) {
		return factory.apply(type, pattern);
	}

	/**
	 * Provides the default writer of this formatter.
	 * 
	 * @param <F> The instance type of the {@link FormatWriter}.
	 * @return The {@link FormatWriter} instance.
	 * @see FormatProcessorFactory#createWriter(Class, String)
	 */
	public final <F extends FormatWriter<T, F>> FormatWriter<T, F> writer() {
		return writer(processorFactory::createWriter);
	}

	/**
	 * Provides a new writer created by the specified factory function.
	 * 
	 * @param <F> The instance type of the {@link FormatWriter}.
	 * @param factory The factory function.
	 * @return The {@link FormatWriter} instance.
	 */
	public final <F extends FormatWriter<T, F>> FormatWriter<T, F> writer(
			BiFunction<Class<? extends T>, String, FormatWriter<T, F>> factory) {
		return factory.apply(type, pattern);
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
		return Providers.getInstance().getProcessorFactory();
	}

}
