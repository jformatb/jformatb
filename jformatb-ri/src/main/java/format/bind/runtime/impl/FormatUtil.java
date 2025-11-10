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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.function.Failable;
import org.apache.commons.lang3.reflect.MethodUtils;

import format.bind.FormatFieldAccessor;
import format.bind.FormatFieldAccessor.Strategy;
import format.bind.FormatFieldDescriptor;
import format.bind.FormatProcessingException;
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
import lombok.Value;
import lombok.experimental.UtilityClass;

@UtilityClass
class FormatUtil {

	@Value(staticConstructor = "of")
	private static class FieldDescriptorSetter<T> {

		private BiConsumer<FormatFieldDescriptorImpl, T> setter;

		private Function<String, T> extractor;

		void accept(FormatFieldDescriptorImpl descriptor, String value) {
			setter.accept(descriptor, extractor.apply(value));
		}

	}

	private final String FORMAT_FIELD_OPTION_KEY = "key";
	private final String FORMAT_FIELD_OPTION_VALUE = "value";
	private final String FORMAT_FIELD_OPTION_FLAG = "flag";
	private final String FORMAT_FIELD_OPTION_REGEX = String.format("\\-\\-(?<%s>\\w+)=(?<%s>[\\w\\.\\-\\[\\]\\\\/,;:'_]+)|\\-\\-(?<%s>\\w+)",
			FORMAT_FIELD_OPTION_KEY, FORMAT_FIELD_OPTION_VALUE, FORMAT_FIELD_OPTION_FLAG);

	private Map<String, FieldDescriptorSetter<?>> fieldDescriptorSetters = new HashMap<>();

	static {
		registerFieldDescriptorSetter("name", FieldDescriptorSetter.of(FormatFieldDescriptorImpl::name, Function.identity()));
		registerFieldDescriptorSetter("type", FieldDescriptorSetter.of(FormatFieldDescriptorImpl::type, FormatField.Type::valueOf));
		registerFieldDescriptorSetter("charset", FieldDescriptorSetter.of(FormatFieldDescriptorImpl::charset, Charset::forName));
		registerFieldDescriptorSetter("length", FieldDescriptorSetter.of(FormatFieldDescriptorImpl::length, Integer::parseInt));
		registerFieldDescriptorSetter("scale", FieldDescriptorSetter.of(FormatFieldDescriptorImpl::scale, Integer::parseInt));
		registerFieldDescriptorSetter("format", FieldDescriptorSetter.of(FormatFieldDescriptorImpl::format, Function.identity()));
		registerFieldDescriptorSetter("locale", FieldDescriptorSetter.of(FormatFieldDescriptorImpl::locale, Function.identity()));
		registerFieldDescriptorSetter("zone", FieldDescriptorSetter.of(FormatFieldDescriptorImpl::zone, Function.identity()));
		registerFieldDescriptorSetter("placeholder", FieldDescriptorSetter.of(FormatFieldDescriptorImpl::placeholder, Function.identity()));
		registerFieldDescriptorSetter("readOnly", FieldDescriptorSetter.of(FormatFieldDescriptorImpl::readOnly, Boolean::parseBoolean));
		registerFieldDescriptorSetter("targetClass", FieldDescriptorSetter.of(FormatFieldDescriptorImpl::targetClass, Failable.asFunction(ClassUtils::getClass)));
	}

	private <T> void registerFieldDescriptorSetter(final String key, final FieldDescriptorSetter<T> setter) {
		fieldDescriptorSetters.put(key, setter);
	}

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

	Class<?> getFieldPropertyType(final FormatFieldAccessor accessor, final byte[] bytes, final Charset charset) {
		Class<?> propertyType = getFieldPropertyType(accessor);

		FormatTypeInfo typeInfo = Optional.ofNullable(accessor.getAnnotation(FormatTypeInfo.class))
				.orElse(propertyType.getAnnotation(FormatTypeInfo.class));

		if (typeInfo != null) {
			String typeValue = new String(bytes, typeInfo.start(), typeInfo.length(), charset);
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
	<T> String formatFieldValue(final T value, final FormatFieldDescriptor descriptor, final FieldConverter<?> converter) {
		return ((FieldConverter<T>) converter).format(descriptor, value);
	}

	@SuppressWarnings("unchecked")
	<T> byte[] formatByteArrayFieldValue(final T value, final FormatFieldDescriptor descriptor, final FieldConverter<?> converter) {
		return ((FieldConverter<T>) converter).formatBytes(descriptor, value);
	}

	Object parseFieldValue(final String source, final FormatFieldDescriptor descriptor, final FieldConverter<?> converter) {
		return converter.parse(descriptor, source);
	}

	Object parseByteArrayFieldValue(final byte[] source, final FormatFieldDescriptor descriptor, final FieldConverter<?> converter) {
		return converter.parseBytes(descriptor, source);
	}

	FormatFieldDescriptorImpl buildFieldDescriptor(final FormatFieldAccessor accessor, final Class<?> propertyType, final Charset charset, final String[] options) {
		FormatFieldDescriptorImpl descriptor = FormatFieldDescriptorImpl.from(accessor.getAnnotation(FormatField.class), charset);

		applyOverrides(descriptor, accessor, propertyType);

		updateFieldDescriptorOptions(descriptor, accessor.getName(), options);

		return descriptor;
	}

	void updateFieldDescriptorOptions(FormatFieldDescriptorImpl descriptor, String name, String[] options) {
		if (descriptor.name().isEmpty()) {
			// Use class field name
			descriptor.name(name);
		}

		if (options.length > 1) {
			if (options[1].matches("\\d+")) {
				// Override annotation field length
				descriptor.length(Integer.parseInt(options[1]));
			} else {
				// Override annotation options
				try {
					Matcher matcher = Pattern.compile(FORMAT_FIELD_OPTION_REGEX).matcher(options[1]);
					while (matcher.find()) {
						Optional.ofNullable(matcher.group(FORMAT_FIELD_OPTION_KEY)).ifPresent(
								key -> fieldDescriptorSetters.get(key).accept(descriptor, matcher.group(FORMAT_FIELD_OPTION_VALUE)));
						Optional.ofNullable(matcher.group(FORMAT_FIELD_OPTION_FLAG)).ifPresent(
								key -> fieldDescriptorSetters.get(key).accept(descriptor, "true"));
					}
				} catch (Exception e) {
					throw new FormatProcessingException("Unable to update field descriptor options", e);
				}
			}
		}

		if (options.length > 2) {
			// Override annotation field placeholder
			descriptor.placeholder(options[2]);
		}

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
		FormatField field = override.field();
		Class<FormatField> type = FormatField.class;
		fieldDescriptorSetters.keySet().forEach(Failable.asConsumer(name -> {
			Object defaultValue = type.getDeclaredMethod(name).getDefaultValue();
			Object fieldValue = MethodUtils.invokeExactMethod(field, name);
			Object descriptorValue = MethodUtils.invokeExactMethod(descriptor, name);
			Object value = Objects.equals(defaultValue, fieldValue) ? descriptorValue : fieldValue;
			MethodUtils.invokeExactMethod(descriptor, name, value);
		}));
	}

}
