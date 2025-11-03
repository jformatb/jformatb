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
package format.bind.runtime.impl;

import static format.bind.runtime.impl.FormatUtil.*;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import format.bind.FormatFieldAccessor;
import format.bind.FormatFieldAccessor.Strategy;
import format.bind.FormatFieldDescriptor;
import format.bind.FormatProcessingException;
import format.bind.FormatWriter;
import format.bind.annotation.FormatTypeInfo;
import format.bind.annotation.FormatTypeValue;
import format.bind.converter.FieldConverter;

/**
 * A runtime implementation of {@link FormatWriter}.
 * 
 * @param <T> The Java type of the object to format.
 * 
 * @author Yannick Ebongue
 */
final class FormatWriterImpl<T> extends FormatProcessorImpl<T, FormatWriterImpl<T>>
		implements FormatWriter<T, FormatWriterImpl<T>> {

	/** The map containing additional text field properties. */
	private Map<String, Object> additionalProperties = Collections.emptyMap();

	/**
	 * Creates a new instance of {@code FormatWriterImpl}.
	 * @param type The class instance of the Java object to format.
	 * @param pattern The pattern of the text format to write.
	 */
	private FormatWriterImpl(final Class<T> type, final String pattern) {
		super(type, pattern);
	}

	/**
	 * Obtain a new instance of {@code FormatWriterImpl}.
	 * @param <T> The base type of the object to format.
	 * @param type The base type instance of the object to process.
	 * @param pattern The pattern of the text format to write.
	 * @return A new instance of {@code FormatWriterImpl}.
	 */
	public static <T> FormatWriterImpl<T> of(final Class<T> type, final String pattern) {
		return new FormatWriterImpl<>(type, pattern);
	}

	@Override
	public FormatWriterImpl<T> setProperties(final Map<String, Object> properties) {
		additionalProperties = Optional.ofNullable(properties)
				.map(Collections::unmodifiableMap)
				.orElseGet(Collections::emptyMap);
		return this;
	}

	@Override
	public String write(final T obj) throws FormatProcessingException {
		try {
			return new String(writeBytes(obj), charset.get());
		} catch (FormatProcessingException e) {
			throw handleException(obj, e.getCause());
		} catch (Exception e) {
			throw handleException(obj, e);
		}
	}

	@Override
	public byte[] writeBytes(T obj) throws FormatProcessingException {
		try {
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			Class<?> resultType = obj.getClass();
			Strategy strategy = getStrategy(resultType);

			String pattern = getPattern(resultType);
			Matcher matcher = compile(pattern);

			Map<String, Object> resolvedValues = new LinkedHashMap<>();

			FormatTypeInfo typeInfo = resultType.getAnnotation(FormatTypeInfo.class);

			int lastIndex = 0;

			while (matcher.find()) {
				String expression = matcher.group(PROPERTY_GROUP);
				String[] parts = expression.split(":");
				String name = parts[0].replace("..", "::");

				// Resolve bean property name
				List<String> properties = resolveProperty(strategy, resultType, name, null);

				if (isTypeInfoFieldAbsent(typeInfo, name, properties)) {
					String value = resultType.getAnnotation(FormatTypeValue.class).value();
					output.write(pattern.substring(lastIndex, matcher.start()).getBytes(charset.get()));
					output.write(value.getBytes(charset.get()));
					lastIndex = matcher.end();

					resolvedValues.put(name, value);

					continue;
				}

				ListIterator<String> iterator = properties.listIterator();
				int counter = 0;
				while (iterator.hasNext()) {
					String property = nextProperty(iterator, counter);
					Object value = Optional.ofNullable(getValue(obj, property))
							.orElse(additionalProperties.get(property));

					if (value == null && Pattern.compile("\\[(\\d+::)?\\*\\]").matcher(name).find()) {
						lastIndex = matcher.end();
						break;
					}

					FormatFieldAccessor accessor = resolvedProperties.get(property);
					Class<?> propertyType = getPropertyType(accessor, property, value);
					FormatFieldDescriptor descriptor = buildFieldDescriptor(accessor, property, propertyType, parts);
					FieldConverter<?> converter = getFieldConverter(accessor, property, propertyType);

					output.write(pattern.substring(lastIndex, matcher.start()).getBytes(charset.get()));
					output.write(formatByteArrayFieldValue(value, descriptor, converter));
					resolvedValues.put(property, value);
					lastIndex = ++counter < properties.size() ? matcher.start() : matcher.end();
				}
			}

			if (lastIndex < pattern.length()) {
				output.write(pattern.substring(lastIndex, pattern.length()).getBytes(charset.get()));
			}

			listener.get().postProcessing(obj, resolvedValues);

			return output.toByteArray();
		} catch (Exception e) {
			throw handleException(obj, e);
		}
	}

	private static FormatProcessingException handleException(final Object obj, final Throwable exception) {
		return new FormatProcessingException(String.format("Unable to format object [%s]", obj), exception);
	}

}
