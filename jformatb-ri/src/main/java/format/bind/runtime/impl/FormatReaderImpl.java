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

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;

import org.apache.commons.lang3.StringUtils;

import format.bind.FormatFieldAccessor;
import format.bind.FormatFieldAccessor.Strategy;
import format.bind.FormatFieldDescriptor;
import format.bind.FormatProcessingException;
import format.bind.FormatReader;
import format.bind.annotation.FormatTypeInfo;
import format.bind.converter.FieldConverter;

/**
 * A runtime implementation of {@link FormatReader}.
 * 
 * @param <T> The Java type of the object to create.
 * 
 * @author Yannick Ebongue
 */
final class FormatReaderImpl<T> extends FormatProcessorImpl<T, FormatReaderImpl<T>>
		implements FormatReader<T, FormatReaderImpl<T>> {

	/**
	 * Creates a new instance of {@code FormatReaderImpl}.
	 * @param type The class instance of the Java object to create.
	 * @param pattern The pattern of the text format to read.
	 */
	private FormatReaderImpl(final Class<T> type, final String pattern) {
		super(type, pattern);
	}

	/**
	 * Obtain a new instance of {@code FormatReaderImpl}.
	 * @param <T> The base type of the object to create.
	 * @param type The base type instance of the object to process.
	 * @param pattern The pattern of the text format to read.
	 * @return A new instance of {@code FormatReaderImpl}.
	 */
	public static <T> FormatReaderImpl<T> of(final Class<T> type, final String pattern) {
		return new FormatReaderImpl<>(type, pattern);
	}

	@Override
	public T read(final String text) {
		try {
			T obj = createObject(text);
			Class<?> resultType = obj.getClass();
			Strategy strategy = getStrategy(resultType);

			String pattern = getPattern(resultType);
			Matcher matcher = compile(pattern);

			Map<String, Object> resolvedValues = new LinkedHashMap<>();

			int lastIndex = 0;
			int matcherEnd = 0;

			FormatTypeInfo typeInfo = resultType.getAnnotation(FormatTypeInfo.class);

			while (matcher.find()) {
				String expression = matcher.group(PROPERTY_GROUP);
				String[] parts = expression.split(":");
				String name = parts[0].replace("..", "::");

				// Resolve bean property name
				List<String> properties = resolveProperty(strategy, resultType, name, null);

				if (isTypeInfoFieldAbsent(typeInfo, name, properties)) {
					int start = matcher.start() - matcherEnd + lastIndex;
					matcherEnd = matcher.end();
					lastIndex = start + typeInfo.length();
					String value = StringUtils.substring(text, start, lastIndex);

					resolvedValues.put(name, value);

					continue;
				}

				ListIterator<String> iterator = properties.listIterator();
				int counter = 0;
				while (iterator.hasNext()) {
					String property = nextProperty(iterator, counter);

					FormatFieldAccessor accessor = resolvedProperties.get(property);
					Class<?> propertyType = getPropertyType(accessor, property, text);
					FormatFieldDescriptor descriptor = buildFieldDescriptor(accessor, property, propertyType, parts);
					FieldConverter<?> converter = getFieldConverter(accessor, property, propertyType);

					int start = matcher.start() - matcherEnd + lastIndex;

					if (start == text.length()) {
						break;
					}

					int length = Optional.of(descriptor.length())
							.filter(value -> value > 0)
							.orElseGet(text::length);

					lastIndex = Math.min((start + length), text.length());
					matcherEnd = matcher.end();

					String source = text.substring(start, lastIndex);

					Object value = parseFieldValue(source, descriptor, converter);

					resolvedValues.put(property, value);

					// Set field value if not null && not read only
					if (isValid(value, descriptor)) {
						setValue(obj, property, value, text);
					}

					if (++counter < properties.size()) {
						matcherEnd = matcher.start();
					}
				}

			}

			listener.get().postProcessing(obj, resolvedValues);

			return obj;
		} catch (Exception e) {
			throw handleException(text, e);
		}
	}

	private T createObject(final String text) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (type.isAnnotationPresent(FormatTypeInfo.class)) {
			FormatTypeInfo typeInfo = type.getAnnotation(FormatTypeInfo.class);
			String typeValue = StringUtils.substring(text, typeInfo.start(), typeInfo.start() + typeInfo.length());
			Class<? extends T> subType = getFormatSubType(type, typeValue);
			return subType.getConstructor().newInstance();
		}

		return type.getConstructor().newInstance();
	}

	private boolean isValid(final Object value, final FormatFieldDescriptor descriptor) {
		return value != null && !descriptor.readOnly();
	}

	private FormatProcessingException handleException(final String text, final Exception exception) {
		if (exception instanceof FormatProcessingException) {
			return (FormatProcessingException) exception;
		}

		return new FormatProcessingException(String.format("Unable to parse text [%s]", text), exception);
	}

}
