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
package format.bind.runtime.impl.converter;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import format.bind.FormatFieldDescriptor;
import format.bind.annotation.FormatFactory;
import format.bind.annotation.FormatValue;
import format.bind.converter.FieldConversionException;
import format.bind.converter.FieldConverter;

final class ValueConverter<T> implements FieldConverter<T> {

	private final AccessibleObject accessor;

	private final AccessibleObject factory;

	private ValueConverter(final Class<T> fieldType) {
		this.accessor = getAnnotatedMember(fieldType, FormatValue.class);
		this.factory = getAnnotatedMember(fieldType, FormatFactory.class);
	}

	public static <T> ValueConverter<T> of(final Class<T> fieldType) {
		return new ValueConverter<>(fieldType);
	}

	@Override
	public String format(FormatFieldDescriptor descriptor, T value) throws FieldConversionException {
		try {
			if (accessor instanceof Field) {
				Field field = (Field) accessor;
				return format(descriptor, FieldUtils.readField(field, value, true), field.getType());
			} else {
				Method getter = (Method) accessor;
				return format(descriptor, MethodUtils.invokeMethod(value, getter.getName()), getter.getReturnType());
			}
		} catch (Exception e) {
			return FieldConverters.throwFormatFieldConversionException(descriptor, value, e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public T parse(FormatFieldDescriptor descriptor, String source) throws FieldConversionException {
		try {
			if (factory instanceof Constructor) {
				Constructor<T> constructor = (Constructor<T>) factory;
				Object value = parse(descriptor, source, constructor.getParameterTypes()[0]);
				return ConstructorUtils.invokeConstructor(constructor.getDeclaringClass(), value);
			} else {
				Method method = (Method) factory;
				Object value = parse(descriptor, source, method.getParameterTypes()[0]);
				return (T) MethodUtils.invokeStaticMethod(method.getReturnType(), method.getName(), value);
			}
		} catch (Exception e) {
			return FieldConverters.throwParseFieldConversionException(descriptor, source, e);
		}
	}

	public static <T> boolean containsAccessor(Class<T> fieldType) {
		return getAnnotatedMember(fieldType, FormatValue.class) != null;
	}

	private static <T> AccessibleObject getAnnotatedMember(Class<T> fieldType, Class<? extends Annotation> annotationType) {
		AccessibleObject[] annotatedFields = FieldUtils.getFieldsWithAnnotation(fieldType, annotationType);
		AccessibleObject[] annotatedMethods = MethodUtils.getMethodsWithAnnotation(fieldType, annotationType);
		List<AccessibleObject> accessibleObjects = Stream.concat(Stream.of(annotatedFields), Stream.of(annotatedMethods))
				.collect(Collectors.toList());
		if (accessibleObjects.size() > 1) {
			throw new IllegalStateException(String.format("The field type %s must have at most one field or property annotated with %s", fieldType, annotationType));
		}
		return accessibleObjects.isEmpty() ? null : accessibleObjects.get(0);
	}

	@SuppressWarnings("unchecked")
	private static <V> FieldConverter<V> getConverter(Class<V> targetClass, FormatFieldDescriptor descriptor) {
		return Optional.ofNullable(FieldConverters.getConverter(targetClass))
				.orElseGet(() -> {
					return (FieldConverter<V>) FieldConverters.getConverter(descriptor.targetClass());
				});
	}

	@SuppressWarnings("unchecked")
	private static <V> String format(FormatFieldDescriptor descriptor, V value, Class<?> type) {
		return getConverter((Class<V>) value.getClass(), descriptor).format(descriptor, value);
	}

	private static <V> V parse(FormatFieldDescriptor descriptor, String source, Class<V> type) {
		return (V) getConverter(type, descriptor).parse(descriptor, source);
	}

}
