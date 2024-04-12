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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import org.apache.commons.lang3.StringUtils;

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
	private FormatReaderImpl(Class<T> type, String pattern) {
		super(type, pattern);
	}

	/**
	 * Obtain a new instance of {@code FormatReaderImpl}.
	 * @param <T> The base type of the object to create.
	 * @param type The base type instance of the object to process.
	 * @param pattern The pattern of the text format to read.
	 * @return A new instance of {@code FormatReaderImpl}.
	 */
	public static <T> FormatReaderImpl<T> of(Class<T> type, String pattern) {
		return new FormatReaderImpl<>(type, pattern);
	}

	@Override
	public T read(String text) {
		try {
			T obj = createObject(text);
			Class<?> resultType = obj.getClass();

			String pattern = getPattern(resultType);
			Matcher matcher = compile(pattern);

			Map<String, Object> resolvedValues = new LinkedHashMap<>();

			AtomicInteger lastIndex = new AtomicInteger(0);
			AtomicInteger matcherEnd = new AtomicInteger(0);

			FormatTypeInfo typeInfo = resultType.getAnnotation(FormatTypeInfo.class);

			while (matcher.find()) {
				String expression = matcher.group(PROPERTY_GROUP);
				String[] parts = expression.split(":");
				String name = parts[0].replace("..", "::");

				// Resolve bean property name
				List<String> properties = resolveProperty(resultType, name, null);
				int counter = 0;

				if (typeInfo != null && typeInfo.fieldName().equals(name) && properties.isEmpty()) {
					int start = matcher.start() - matcherEnd.get() + lastIndex.get();
					matcherEnd.set(matcher.end());
					lastIndex.set(start + typeInfo.length());
					String value = StringUtils.substring(text, start, lastIndex.get());

					resolvedValues.put(name, value);

					continue;
				}

				for (String property : properties) {
					Field accessor = resolvedProperties.get(property);
					Class<?> propertyType = getFieldPropertyType(accessor, text);
					FormatFieldDescriptorImpl descriptor = buildFieldDescriptor(accessor, propertyType, parts);
					FieldConverter<?> converter = getFieldConverter(accessor, propertyType);

					Object value = parseFieldValue(text, descriptor, converter, matcher, matcherEnd, lastIndex);

					// Set field value if not null
					if (value != null) {
						resolvedValues.put(name, value);
						setValue(obj, property, value, text);
					}

					if (++counter < properties.size()) {
						matcherEnd.set(matcher.start());
					}
				}

			}

			listener.get().postProcessing(obj, resolvedValues);

			return obj;
		} catch (FormatProcessingException e) {
			throw e;
		} catch (Exception e) {
			throw new FormatProcessingException(String.format("Unable to parse text [%s]", text), e);
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

}
