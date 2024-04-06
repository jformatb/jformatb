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
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.expression.Resolver;
import org.apache.commons.lang3.StringUtils;
import format.bind.FormatException;
import format.bind.FormatProcessingException;
import format.bind.FormatWriter;
import format.bind.Formatter;
import format.bind.annotation.Format;
import format.bind.annotation.FormatField;
import format.bind.annotation.FormatTypeInfo;
import format.bind.annotation.FormatTypeValue;
import format.bind.converter.FieldConverter;
import format.bind.runtime.impl.converter.FieldConverters;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
final class FormatWriterImpl<T> extends FormatProcessorImpl<T, FormatWriterImpl<T>>
		implements FormatWriter<T, FormatWriterImpl<T>> {

	private FormatWriterImpl(Class<T> type, String pattern) {
		super(type, pattern);
	}

	public static <T> FormatWriterImpl<T> of(Class<T> type, String pattern) {
		return new FormatWriterImpl<>(type, pattern);
	}

	@Override
	public String write(T obj) {
		try {
			StringBuilder output = new StringBuilder();
			Class<?> resultType = obj.getClass();

			String input = pattern != null ? pattern : Optional.ofNullable(resultType.getAnnotation(Format.class))
					.map(Format::pattern)
					.orElse(null);
			Matcher matcher = Pattern.compile(REGEX)
					.matcher(input);

			Map<String, Object> resolvedValues = new LinkedHashMap<>();

			propertyUtils.setResolver(new PropertyResolver());

			FormatTypeInfo typeInfo = resultType.getAnnotation(FormatTypeInfo.class);

			int lastIndex = 0;

			while (matcher.find()) {
				String expression = matcher.group(PROPERTY_GROUP);
				String[] parts = expression.split(":");
				String name = parts[0];

				// Resolve bean property name
				String property = resolveProperty(obj, name, null);

				if (typeInfo != null && typeInfo.fieldName().equals(name) && property == null) {
					String value = resultType.getAnnotation(FormatTypeValue.class).value();
					output.append(input, lastIndex, matcher.start());
					output.append(value);
					lastIndex = matcher.end();

					resolvedValues.put(name, value);

					continue;
				}

				boolean array = StringUtils.isNotBlank(matcher.group(ARRAY_GROUP));
				Object value = getValue(obj, property);

				Field accessor = resolvedProperties.get(property);
				Class<?> propertyType = FormatUtil.getFieldPropertyType(accessor);
				FieldConverter<?> converter = FormatUtil.getFieldConverter(accessor, propertyType);
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
					converter = FieldConverters.getConverter(Formatter.of(value.getClass()));
				}

				output.append(input, lastIndex, matcher.start());
				output.append(FormatUtil.formatFieldValue(value, descriptor, converter, matcher, array));
				resolvedValues.put(name, value);
				lastIndex = matcher.end();
			}

			if (lastIndex < input.length()) {
				output.append(input, lastIndex, input.length());
			}

			listener.get().postProcessing(obj, resolvedValues);

			return output.toString();
		} catch (FormatException e) {
			throw e;
		} catch (Exception e) {
			throw new FormatProcessingException(String.format("Unable to format object [%s]", obj), e);
		}
	}

	private String resolveProperty(final Object bean, final String expression, final String parent) {
		try {
			Class<?> beanClass = bean.getClass();

			Resolver resolver = propertyUtils.getResolver();

			if (resolver.hasNested(expression)) {
				String next = resolver.next(expression);
				String containerName = resolver.getProperty(expression);

				// Find the field with FormatFieldContainer annotation that match the field name.
				Field accessor = getFieldContainer(beanClass, containerName);

				if (accessor != null) {
					String property = new StringBuilder(accessor.getName())
							.append(next.substring(containerName.length()))
							.toString();

					Object container = propertyUtils.getProperty(bean, property);
					property = parent == null ? property : String.join(".", parent, property);

					return resolveProperty(container, expression.substring(next.length()), property);
				}
			} else {
				String fieldName = resolver.getProperty(expression);

				// Find the field with FormatField annotation that match the field name.
				Field accessor = getField(beanClass, fieldName);

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
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | IllegalArgumentException | SecurityException e) {
			throw new FormatProcessingException(String.format("Unable to resolve field expression ${%s} on object class [%s]", expression, bean.getClass()), e);
		}
	}

}
