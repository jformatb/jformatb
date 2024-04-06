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

import static format.bind.runtime.impl.FormatUtil.applyOverrides;
import static format.bind.runtime.impl.FormatUtil.getField;
import static format.bind.runtime.impl.FormatUtil.getFieldContainer;
import static format.bind.runtime.impl.FormatUtil.getFieldConverter;
import static format.bind.runtime.impl.FormatUtil.getFieldPropertyType;
import static format.bind.runtime.impl.FormatUtil.getFormatSubType;
import static format.bind.runtime.impl.FormatUtil.parseFieldValue;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.expression.Resolver;
import org.apache.commons.lang3.StringUtils;

import format.bind.FormatProcessingException;
import format.bind.FormatReader;
import format.bind.Formatter;
import format.bind.annotation.Format;
import format.bind.annotation.FormatField;
import format.bind.annotation.FormatTypeInfo;
import format.bind.converter.FieldConverter;
import format.bind.runtime.impl.converter.FieldConverters;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A runtime implementation of {@link FormatReader}.
 * 
 * @param <T> The Java type of the object to create.
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
final class FormatReaderImpl<T> extends FormatProcessorImpl<T, FormatReaderImpl<T>> implements FormatReader<T, FormatReaderImpl<T>> {

	/**
	 * Creates a new instance of {@link FormatReaderImpl}.
	 * @param type The class instance of the Java object to create.
	 * @param pattern The pattern of the text format to read.
	 */
	private FormatReaderImpl(Class<T> type, String pattern) {
		super(type, pattern);
	}

	/**
	 * Creates a new instance of {@code FormatReaderImpl}.
	 * @param <T> The type parameter of the base type.
	 * @param type The base type instance to process.
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

			String input = pattern != null ? pattern : Optional.ofNullable(resultType.getAnnotation(Format.class))
					.map(Format::pattern)
					.orElse(null);
			Matcher matcher = Pattern.compile(REGEX)
					.matcher(input);

			Map<String, Object> resolvedValues = new LinkedHashMap<>();

			propertyUtils.setResolver(new PropertyResolver());

			AtomicInteger lastIndex = new AtomicInteger(0);
			AtomicInteger matcherEnd = new AtomicInteger(0);

			FormatTypeInfo typeInfo = resultType.getAnnotation(FormatTypeInfo.class);

			while (matcher.find()) {
				String expression = matcher.group(PROPERTY_GROUP);
				String[] parts = expression.split(":");
				String name = parts[0];

				// Resolve bean property name
				String property = resolveProperty(resultType, name, text, null);

				if (typeInfo != null && typeInfo.fieldName().equals(name) && property == null) {
					int start = matcher.start() - matcherEnd.get() + lastIndex.get();
					matcherEnd.set(matcher.end());
					lastIndex.set(start + typeInfo.length());
					String value = StringUtils.substring(text, start, lastIndex.get());

					resolvedValues.put(name, value);

					continue;
				}

				Field accessor = resolvedProperties.get(property);
				Class<?> propertyType = getFieldPropertyType(accessor, text);
				FieldConverter<?> converter = getFieldConverter(accessor, propertyType);
				FormatFieldDescriptorImpl descriptor = FormatFieldDescriptorImpl.from(accessor.getAnnotation(FormatField.class));

				applyOverrides(descriptor, accessor, propertyType);

				if (parts.length > 1) {
					// Override annotation field length
					descriptor = descriptor.length(Integer.parseInt(parts[1]));
				}

				if (parts.length > 2) {
					// Override annotation field placeholder
					descriptor = descriptor.placeholder(parts[2]);
				}

				if (converter == null) {
					// Use formatter converter to parse this field
					converter = FieldConverters.getConverter(Formatter.of(propertyType));
				}

				Object value = parseFieldValue(text, descriptor, converter, matcher, matcherEnd, lastIndex);

				// Set field value if not null
				if (value != null) {
					resolvedValues.put(name, value);
					setValue(obj, property, value);
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

	private String resolveProperty(final Class<?> beanType, final String expression, final String text, final String parent) {
		try {
			Resolver resolver = propertyUtils.getResolver();

			if (resolver.hasNested(expression)) {
				String next = resolver.next(expression);
				String containerName = resolver.getProperty(expression);

				// Find the field with FormatFieldContainer annotation that match the field name.
				Field accessor = getFieldContainer(beanType, containerName);

				if (accessor != null) {
					String property = new StringBuilder(accessor.getName())
							.append(next.substring(containerName.length()))
							.toString();

					Class<?> propertyType = getFieldPropertyType(accessor, text);
					property = parent == null ? property : String.join(".", parent, property);

					return resolveProperty(propertyType, expression.substring(next.length() + 1), text, property);
				}
			} else {
				String fieldName = resolver.getProperty(expression);

				// Find the field with FormatField annotation that match the field name.
				Field accessor = getField(beanType, fieldName);

				// If the field was found then build the final property name.
				if (accessor != null) {
					String property = new StringBuilder(accessor.getName())
							.append(expression.substring(fieldName.length()))
							.toString();

					property = parent == null ? property : String.join(".", parent, property);
					resolvedProperties.put(property, accessor);

					return property;
				}
			}

			return null;
		} catch (Exception e) {
			throw new FormatProcessingException(String.format("Unable to resolve field expression ${%s} on object class [%s]", expression, beanType), e);
		}
	}

}
