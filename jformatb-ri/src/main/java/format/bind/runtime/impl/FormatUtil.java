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

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.stream.IntStream;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;

import format.bind.FormatFieldDescriptor;
import format.bind.Formatter;
import format.bind.annotation.Format;
import format.bind.annotation.FormatFieldConverter;
import format.bind.annotation.FormatSubTypes;
import format.bind.annotation.FormatTypeInfo;
import format.bind.annotation.FormatTypeValue;
import format.bind.converter.FieldConverter;
import format.bind.converter.spi.FieldConverterProvider;
import lombok.experimental.UtilityClass;

@UtilityClass
class FormatUtil {

	private <T> List<Class<? extends T>> getFormatSubTypes(final Class<T> superclass) {
		List<Class<? extends T>> subclasses = new ArrayList<>();

		if (superclass.isAnnotationPresent(FormatSubTypes.class)) {
			Arrays.stream(superclass.getAnnotation(FormatSubTypes.class).value()).forEach(subclass -> {
				if (subclass.isAnnotationPresent(FormatTypeValue.class)) {
					subclasses.add(subclass.asSubclass(superclass));
				} else if (subclass.isAnnotationPresent(FormatSubTypes.class)) {
					subclasses.addAll(getFormatSubTypes(subclass.asSubclass(superclass)));
				}
			});
		}

		return subclasses;
	}

	<T> Class<? extends T> getFormatSubType(final Class<T> type, final String typeValue) {
		return getFormatSubTypes(type).stream()
				.filter(subclass -> Arrays.stream(subclass.getDeclaredAnnotationsByType(FormatTypeValue.class))
						.anyMatch(annotation -> annotation.value().equals(typeValue)))
				.findFirst()
				.orElse(type);
	}

	@SuppressWarnings("unchecked")
	<T> FieldConverter<T> getFieldConverter(final AccessibleObject access, final Class<T> type) {
		FieldConverterProvider provider = FieldConverter.provider();
		if (access.isAnnotationPresent(FormatFieldConverter.class)) {
			return provider.getConverter(type, (Class<? extends FieldConverter<T>>) access.getAnnotation(FormatFieldConverter.class).value());
		} else if (access.isAnnotationPresent(Format.class)) {
			return provider.getConverter(Formatter.of(type).withPattern(access.getAnnotation(Format.class).pattern()));
		} else {
			return provider.getConverter(type);
		}
	}

	Class<?> getFieldPropertyType(final Field accessor, final String text) {
		Class<?> propertyType = getFieldPropertyType(accessor);

		FormatTypeInfo typeInfo = Optional.ofNullable(accessor.getAnnotation(FormatTypeInfo.class))
				.orElse(propertyType.getAnnotation(FormatTypeInfo.class));

		if (typeInfo != null) {
			String typeValue = text.substring(typeInfo.start(), typeInfo.start() + typeInfo.length());
			propertyType = getFormatSubType(propertyType, typeValue);
		}

		return propertyType;
	}

	Class<?> getFieldPropertyType(final Field accessor) {
		return getFieldPropertyType(accessor.getType(), accessor.getGenericType());
	}

	Class<?> getFieldPropertyType(final Method accessor) {
		return getFieldPropertyType(accessor.getReturnType(), accessor.getGenericReturnType());
	}

	private Class<?> getFieldPropertyType(final Class<?> type, final Type genericType) {
		if (type.isPrimitive()) {
			return ClassUtils.primitiveToWrapper(type);
		}

		if (List.class.isAssignableFrom(type)) {
			return (Class<?>) ((ParameterizedType) genericType).getActualTypeArguments()[0];
		}

		if (Map.class.isAssignableFrom(type)) {
			return (Class<?>) ((ParameterizedType) genericType).getActualTypeArguments()[1];
		}

		return type;
	}

	private Object getFieldValue(final List<Object> values, final boolean array) {
		Object value = null;

		// Remove null values
		values.removeIf(Objects::isNull);

		if (!values.isEmpty()) {
			value = array ? values : values.get(0);
		}

		return value;
	}

	@SuppressWarnings("unchecked")
	<T, C extends FieldConverter<T>> String formatFieldValue(final Object value, final FormatFieldDescriptor descriptor, final FieldConverter<?> converter, final Matcher matcher, final boolean array) {
		StringBuilder output = new StringBuilder();
		int size = array ? Integer.parseInt(matcher.group(FormatProcessorImpl.SIZE_GROUP)) : 1;
		int index = 0;
		List<Object> values = new ArrayList<>(Collections.nCopies(size, null));

		if (value instanceof List) {
			List<?> list = (List<?>) value;
			IntStream.range(0, Math.min(size, list.size())).forEach(i -> values.set(i, list.get(i)));
		} else {
			values.set(0, value);
		}

		do {
			T val = (T) values.get(index);
			output.append(((C) converter).format(descriptor, val));
			index++;
		} while (index < size);

		return output.toString();
	}

	Object parseFieldValue(final String text, final FormatFieldDescriptor descriptor, final FieldConverter<?> converter, final Matcher matcher, final AtomicInteger matcherEnd, final AtomicInteger lastIndex) {
		List<Object> values = new ArrayList<>();
		boolean array = StringUtils.isNotBlank(matcher.group(FormatProcessorImpl.ARRAY_GROUP));
		int size = array ? Integer.parseInt(matcher.group(FormatProcessorImpl.SIZE_GROUP)) : 1;
		int length = descriptor.length();
		int index = 0;

		int start = matcher.start() - matcherEnd.get() + lastIndex.get();
		matcherEnd.set(matcher.end());
		length = length == 0 ? text.length() : length;

		if (start < text.length()) {
			do {
				int gap = index * length;
				int nextStart = start + gap;
				int nextEnd = Math.min((nextStart + length), text.length());
				lastIndex.set(nextEnd);

				String source = text.substring(nextStart, nextEnd);

				values.add(converter.parse(descriptor, source));

				index++;
			} while (index < size);
		}

		return getFieldValue(values, array);
	}

}
