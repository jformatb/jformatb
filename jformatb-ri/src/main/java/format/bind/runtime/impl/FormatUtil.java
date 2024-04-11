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

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.stream.IntStream;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import format.bind.FormatFieldDescriptor;
import format.bind.Formatter;
import format.bind.annotation.Format;
import format.bind.annotation.FormatField;
import format.bind.annotation.FormatFieldContainer;
import format.bind.annotation.FormatFieldConverter;
import format.bind.annotation.FormatFieldOverride;
import format.bind.annotation.FormatSubTypes;
import format.bind.annotation.FormatTypeInfo;
import format.bind.annotation.FormatTypeValue;
import format.bind.converter.FieldConverter;
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

	private <A extends Annotation> Field getField(final Class<?> beanType, final String name, Class<A> annotationType, Function<A, String> annotationName) {
		return FieldUtils.getFieldsListWithAnnotation(beanType, annotationType).stream()
				.filter(field -> annotationName.apply(field.getAnnotation(annotationType)).equals(name) ||
						field.getName().equals(name))
				.findFirst()
				.orElseGet(() -> getFieldOverride(beanType, name));
	}

	Field getField(final Class<?> beanClass, final String name) {
		return getField(beanClass, name, FormatField.class, FormatField::name);
	}

	Field getFieldContainer(final Class<?> beanClass, final String name) {
		return getField(beanClass, name, FormatFieldContainer.class, FormatFieldContainer::name);
	}

	Field getFieldOverride(final Class<?> beanClass, final String name) {
		return Arrays.stream(beanClass.getDeclaredAnnotationsByType(FormatFieldOverride.class))
				.filter(override -> override.field().name().equals(name))
				.map(override -> FieldUtils.getField(beanClass, override.property(), true))
				.findFirst()
				.orElse(Optional.ofNullable(beanClass.getSuperclass())
						.map(superclass -> getFieldOverride(superclass, name))
						.orElse(null));
	}

	@SuppressWarnings("unchecked")
	<T> FieldConverter<T> getFieldConverter(final AccessibleObject accessor, final Class<T> type) {
		if (accessor.isAnnotationPresent(FormatFieldConverter.class)) {
			return FieldConverter.provider().getConverter(type, (Class<? extends FieldConverter<T>>) accessor.getAnnotation(FormatFieldConverter.class).value());
		} else if (accessor.isAnnotationPresent(Format.class)) {
			return FieldConverter.provider().getConverter(Formatter.of(type).withPattern(accessor.getAnnotation(Format.class).pattern()));
		} else {
			return FieldConverter.provider().getConverter(type);
		}
	}

	Class<? extends Object> getFieldPropertyType(final Field accessor, final Object value) {
		if (value != null) {
			return getFieldPropertyType(value.getClass(), accessor.getGenericType());
		} else {
			return getFieldPropertyType(accessor);
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
		ListIterator<Object> it = values.listIterator(values.size());
		while (it.hasPrevious()) {
			Object val = it.previous();
			if (val == null) {
				it.remove();
			} else {
				break;
			}
		}

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

	void applyOverrides(final FormatFieldDescriptorImpl descriptor, final Field accessor, final Class<?> propertyType) {
		List<Class<?>> superclasses = ClassUtils.getAllSuperclasses(propertyType);

		superclasses.add(0, propertyType);

		Collections.reverse(superclasses);

		superclasses.forEach(
				cls -> applyOverrides(descriptor, accessor, cls.getDeclaredAnnotationsByType(FormatFieldOverride.class)));

		applyOverrides(descriptor, accessor, accessor.getDeclaredAnnotationsByType(FormatFieldOverride.class));
	}

	void applyOverrides(final FormatFieldDescriptorImpl descriptor, final Field accessor, final FormatFieldOverride[] overrides) {
		Arrays.stream(overrides)
				.filter(annotation -> annotation.property().equals(accessor.getName()))
				.findFirst()
				.ifPresent(override -> applyOverride(descriptor, override));
	}

	void applyOverride(final FormatFieldDescriptorImpl descriptor, final FormatFieldOverride override) {
		try {
			FormatField field = override.field();
			Class<FormatField> type = FormatField.class;
			descriptor
					.name(!type.getDeclaredMethod("name").getDefaultValue().equals(field.name()) ? field.name() : descriptor.name())
					.type(!type.getDeclaredMethod("type").getDefaultValue().equals(field.type()) ? field.type() : descriptor.type())
					.length(!type.getDeclaredMethod("length").getDefaultValue().equals(field.length()) ? field.length() : descriptor.length())
					.scale(!type.getDeclaredMethod("scale").getDefaultValue().equals(field.scale()) ? field.scale() : descriptor.scale())
					.format(!type.getDeclaredMethod("format").getDefaultValue().equals(field.format()) ? field.format() : descriptor.format())
					.placeholder(!type.getDeclaredMethod("placeHolder").getDefaultValue().equals(field.placeholder()) ? field.placeholder() : descriptor.placeholder());
		} catch (NoSuchMethodException | SecurityException e) {
			throw new IllegalArgumentException(e);
		}
	}

}
