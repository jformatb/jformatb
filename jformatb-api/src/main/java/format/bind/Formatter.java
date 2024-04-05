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

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;

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

	/**
	 * A functional interface to be registered with {@link Formatter} to listen
	 * for post parsing or formatting event.
	 * 
	 * @param <T> The type of the result object after parsing or formatting text.
	 */
	@FunctionalInterface
	public interface Listener<T> {

		/**
		 * Callback method invoked after reading to (parse) or writing from (format)
		 * the {@code target} parameter.
		 * @param target The processed target object.
		 * @param fields The map of the text format fields.
		 */
		void postProcessing(T target, Map<String, Object> fields);

	}

	/** The base type of this {@code Formatter}. */
	private Class<T> type;

	/**
	 * The text format pattern of this {@code Formatter}.
	 * 
	 * @param pattern The text format pattern to set.
	 */
	@With
	private String pattern;

	/** The post processing event callback {@link Listener} for this {@code Formatter}. */
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private ThreadLocal<Listener<T>> listener = ThreadLocal.withInitial(() -> (target, fields) -> {});

	/**
	 * Formats the given object to produce the corresponding text format.
	 * @param obj The object instance to format.
	 * @return The formatted text.
	 * @throws FormatProcessingException if an error occurs during {@code obj} formatting.
	 */
	public final String format(final T obj) throws FormatProcessingException {
		try {
			return getProcessorFactory().createWriter(type, pattern)
					.setListener(listener.get())
					.write(obj);
		} catch (FormatProcessingException e) {
			throw e;
		} catch (Exception e) {
			throw new FormatProcessingException(String.format("Unable to format object [%s]", obj), e);
		}
	}

	/**
	 * Parses the given text format from the beginning to produce an object.
	 * @param text The text format to parse.
	 * @return The parsed object.
	 * @throws FormatProcessingException if an error occurs during {@code text} parsing.
	 */
	public final T parse(final String text) throws FormatProcessingException {
		try {
			return getProcessorFactory().createReader(type, pattern)
					.setListener(listener.get())
					.read(text);
		} catch (FormatProcessingException e) {
			throw e;
		} catch (Exception e) {
			throw new FormatProcessingException(String.format("Unable to parse text [%s]", text), e);
		}
	}

	/**
	 * Register a post processing event callback {@link Listener} with this {@link Formatter}.
	 * 
	 * <p>
	 * There is only one {@code Listener} per {@code Formatter}. Setting a {@code Listener}
	 * replaces the previous registered. One can unregister the current listener by calling method
	 * {@link #removeListener()}
	 * 
	 * @param listener The post processing event callback for this {@link Formatter}.
	 * @return This {@code Formatter}.
	 * @throws NullPointerException if {@code listener} is null.
	 */
	public Formatter<T> setListener(Listener<T> listener) {
		Objects.requireNonNull(listener);
		this.listener.set(listener);
		return this;
	}

	/**
	 * Unregister the current post processing event callback {@link Listener} for this {@link Formatter}.
	 */
	public void removeListener() {
		this.listener.remove();
	}

	/**
	 * Create a new instance of {@code Formatter}.
	 * @param <T> The type parameter of the base type.
	 * @param type The base type instance to process.
	 * @return A new instance of {@code Formatter}.
	 */
	public static <T> Formatter<T> of(Class<T> type) {
		String pattern = Optional.ofNullable(type.getAnnotation(Format.class))
				.map(Format::pattern)
				.orElse(null);
		return new Formatter<>(type, pattern);
	}

	private static FormatProcessorFactory getProcessorFactory() {
		ServiceLoader<FormatProcessorFactory> loader = ServiceLoader.load(FormatProcessorFactory.class);
		Iterator<FormatProcessorFactory> it = loader.iterator();

		String className = System.getProperty(FormatProcessorFactory.class.getName(), "format.bind.runtime.impl.FormatProcessorFactoryImpl");

		while (it.hasNext()) {
			FormatProcessorFactory next = it.next();

			if (next.getClass().getName().equals(className)) {
				return next;
			}
		}

		throw new FormatProcessingException(String.format("No implementation %s found in classpath.", FormatProcessorFactory.class));
	}

}
