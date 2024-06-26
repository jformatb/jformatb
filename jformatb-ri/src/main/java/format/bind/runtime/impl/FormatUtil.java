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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.apache.commons.lang3.ClassUtils;

import format.bind.FormatFieldAccessor;
import format.bind.FormatFieldAccessor.Strategy;
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

	private <A extends Annotation> FormatFieldAccessor getField(final Strategy strategy, final Class<?> beanType, final String name, final Class<A> annotationType, final Function<A, String> annotationName) {
		return FormatFieldAccessorUtil.getFieldAccessors(strategy, beanType, annotationType).stream()
				.filter(field -> annotationName.apply(field.getAnnotation(annotationType)).equals(name) || field.getName().equals(name))
				.findFirst()
				.orElse(getFieldOverride(beanType, name));
	}

	FormatFieldAccessor getField(final Strategy strategy, final Class<?> beanClass, final String name) {
		return getField(strategy, beanClass, name, FormatField.class, FormatField::name);
	}

	FormatFieldAccessor getFieldContainer(final Strategy strategy, final Class<?> beanClass, final String name) {
		return getField(strategy, beanClass, name, FormatFieldContainer.class, FormatFieldContainer::name);
	}

	FormatFieldAccessor getFieldOverride(final Class<?> beanClass, final String name) {
		return Arrays.stream(beanClass.getDeclaredAnnotationsByType(FormatFieldOverride.class))
				.filter(override -> override.field().name().equals(name))
				.map(override -> FormatFieldAccessorUtil.getFieldAccessor(beanClass, override.property()))
				.findFirst()
				.orElse(Optional.ofNullable(beanClass.getSuperclass())
						.map(superclass -> getFieldOverride(superclass, name))
						.orElse(null));
	}

	@SuppressWarnings("unchecked")
	<T> FieldConverter<T> getFieldConverter(final FormatFieldAccessor accessor, final Class<T> type) {
		FieldConverterProvider provider = FieldConverter.provider();
		FieldConverter<T> converter = null;

		if (accessor.isAnnotationPresent(FormatFieldConverter.class)) {
			converter = provider.getConverter(type, (Class<? extends FieldConverter<T>>) accessor.getAnnotation(FormatFieldConverter.class).value());
		} else if (accessor.isAnnotationPresent(Format.class)) {
			converter = provider.getConverter(Formatter.of(type).withPattern(accessor.getAnnotation(Format.class).pattern()));
		} else {
			converter = provider.getConverter(type);
		}

		if (converter == null) {
			// Use formatter converter to parse this field
			converter = provider.getConverter(Formatter.of(type));
		}

		return converter;
	}

	Class<?> getFieldPropertyType(final FormatFieldAccessor accessor, final Object value) {
		Class<?> type = value != null ? value.getClass() : accessor.getType();
		return getFieldPropertyType(type, accessor.getGenericType());
	}

	Class<?> getFieldPropertyType(final FormatFieldAccessor accessor, final String text) {
		Class<?> propertyType = getFieldPropertyType(accessor);

		FormatTypeInfo typeInfo = Optional.ofNullable(accessor.getAnnotation(FormatTypeInfo.class))
				.orElse(propertyType.getAnnotation(FormatTypeInfo.class));

		if (typeInfo != null) {
			String typeValue = text.substring(typeInfo.start(), typeInfo.start() + typeInfo.length());
			propertyType = getFormatSubType(propertyType, typeValue);
		}

		return propertyType;
	}

	Class<?> getFieldPropertyType(final FormatFieldAccessor accessor) {
		return getFieldPropertyType(accessor.getType(), accessor.getGenericType());
	}

	Class<?> getFieldPropertyType(final Class<?> type, final Type genericType) {
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

	@SuppressWarnings("unchecked")
	<T, C extends FieldConverter<T>> String formatFieldValue(final Object value, final FormatFieldDescriptor descriptor, final FieldConverter<?> converter) {
		return ((C) converter).format(descriptor, (T) value);
	}

	Object parseFieldValue(final String source, final FormatFieldDescriptor descriptor, final FieldConverter<?> converter) {
		return converter.parse(descriptor, source);
	}

	FormatFieldDescriptorImpl buildFieldDescriptor(final FormatFieldAccessor accessor, final Class<?> propertyType, final String[] options) {
		FormatFieldDescriptorImpl descriptor = FormatFieldDescriptorImpl.from(accessor.getAnnotation(FormatField.class));

		applyOverrides(descriptor, accessor, propertyType);

		if (descriptor.name().isEmpty()) {
			// Use class field name
			descriptor.name(accessor.getName());
		}

		if (options.length > 1) {
			// Override annotation field length
			descriptor.length(Integer.parseInt(options[1]));
		}

		if (options.length > 2) {
			// Override annotation field placeholder
			descriptor.placeholder(options[2]);
		}

		return descriptor;
	}

	void applyOverrides(final FormatFieldDescriptorImpl descriptor, final FormatFieldAccessor accessor, final Class<?> propertyType) {
		List<Class<?>> superclasses = ClassUtils.getAllSuperclasses(propertyType);

		superclasses.add(0, propertyType);

		Collections.reverse(superclasses);

		superclasses.forEach(
				cls -> applyOverrides(descriptor, accessor, cls.getDeclaredAnnotationsByType(FormatFieldOverride.class)));

		applyOverrides(descriptor, accessor, accessor.getDeclaredAnnotationsByType(FormatFieldOverride.class));
	}

	void applyOverrides(final FormatFieldDescriptorImpl descriptor, final FormatFieldAccessor accessor, final FormatFieldOverride[] overrides) {
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
					.locale(!type.getDeclaredMethod("locale").getDefaultValue().equals(field.locale()) ? field.locale() : descriptor.locale())
					.placeholder(!type.getDeclaredMethod("placeHolder").getDefaultValue().equals(field.placeholder()) ? field.placeholder() : descriptor.placeholder());
		} catch (NoSuchMethodException | SecurityException e) {
			throw new IllegalArgumentException(e);
		}
	}

}
