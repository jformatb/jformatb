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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.beanutils.expression.DefaultResolver;

import format.bind.FormatFieldAccessor;
import format.bind.FormatFieldAccessor.Strategy;
import format.bind.FormatFieldDescriptor;
import format.bind.FormatProcessor;
import format.bind.Formatter;
import format.bind.annotation.Format;
import format.bind.annotation.FormatAccess;
import format.bind.annotation.FormatAccess.Type;
import format.bind.converter.FieldConverter;
import format.bind.converter.spi.FieldConverterProvider;
import format.bind.annotation.FormatField;
import format.bind.annotation.FormatFieldConverter;
import format.bind.annotation.FormatMapEntry;
import format.bind.annotation.FormatMapEntryField;
import format.bind.annotation.FormatTypeInfo;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

abstract class FormatProcessorImpl<T, F extends FormatProcessorImpl<T, F>> implements FormatProcessor<T, F> {

	static final String REGEX = "\\$\\{(?<property>[^\\}]+)\\}";

	static final String PROPERTY_GROUP = "property";

	static final String INDEXED_PROP_FORMAT = "%s[%s]";
	static final String MAPPED_PROP_FORMAT = "%s[\"%s\"]";

	/** The class instance representing the Java type of the object to process. */
	final Class<T> type;

	/** The pattern of the text format to process. */
	final String pattern;

	/** The post processing event callback {@link Listener} for this {@code FormatProcessorImpl}. */
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	final ThreadLocal<Listener<T>> listener = ThreadLocal.withInitial(() -> (target, fields) -> {});

	/** The bean property accessor utility of this {@code FormatProcessorImpl}. */
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	final PropertyUtilsBean propertyUtils;

	/** The property name expression resolver to set to the property utils bean. */
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	final PropertyResolver resolver = new PropertyResolver();

	/** The map of all resolved fields of this format processor. */
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	final Map<String, FormatFieldAccessor> resolvedProperties = new LinkedHashMap<>();

	/**
	 * Creates a new instance of {@code FormatProcessorImpl}.
	 * @param type The class instance of the Java object to be processed by this processor.
	 * @param pattern The pattern of the text format to process.
	 */
	protected FormatProcessorImpl(Class<T> type, String pattern) {
		this.type = type;
		this.pattern = pattern;

		propertyUtils = BeanUtilsBean.getInstance().getPropertyUtils();
		propertyUtils.setResolver(resolver);
	}

	@SuppressWarnings("unchecked")
	public F setListener(final Listener<T> listener) {
		if (listener == null) {
			this.listener.remove();
		} else {
			this.listener.set(listener);
		}
		return (F) this;
	}

	<U> Strategy getStrategy(Class<U> resultType) {
		return resultType.isAnnotationPresent(FormatAccess.class) && resultType.getAnnotation(FormatAccess.class).value() == Type.PROPERTY ? Strategy.PROPERTY : Strategy.FIELD;
	}

	boolean isTypeInfoFieldAbsent(final FormatTypeInfo typeInfo, final String name, final List<String> properties) {
		return typeInfo != null && typeInfo.fieldName().equals(name) && properties.isEmpty();
	}

	String nextProperty(final ListIterator<String> iterator, final int next) {
		String property = iterator.next();
		Matcher matcher = Pattern.compile("\\[(?<start>\\d+)\\+\\]").matcher(property);

		if (matcher.find()) {
			FormatFieldAccessor accessor = resolvedProperties.get(property);
			int index = Integer.parseInt(matcher.group("start")) + next;
			property = matcher.replaceAll(String.format("[%d]", index));
			iterator.previous();
			iterator.add(property);
			resolvedProperties.put(property, accessor);
		}

		return property;
	}

	String getPattern(Class<?> resultType) {
		return Optional.ofNullable(pattern)
				.orElseGet(() -> Optional.ofNullable(resultType.getAnnotation(Format.class))
						.map(Format::pattern)
						.orElse(null));
	}

	Matcher compile(String input) {
		return Pattern.compile(REGEX).matcher(input);
	}

	FormatFieldDescriptor buildFieldDescriptor(final FormatFieldAccessor accessor, final String expression, final Class<?> propertyType, final String[] options) {
		String target = getTargetProperty(expression);

		if (resolver.isMapped(target)) {
			String key = resolver.getKey(target);
			FormatField field = Arrays.stream(accessor.getAnnotationsByType(FormatMapEntryField.class))
					.filter(annotation -> Arrays.asList(annotation.keys()).contains(key))
					.map(FormatMapEntryField::field)
					.findFirst()
					.orElse(accessor.getAnnotation(FormatField.class));
			FormatFieldDescriptorImpl descriptor = FormatFieldDescriptorImpl.from(field);

			updateFieldDescriptorOptions(descriptor, expression, options);

			return descriptor;
		}

		return FormatUtil.buildFieldDescriptor(accessor, propertyType, options);
	}

	<X> FieldConverter<X> getFieldConverter(final FormatFieldAccessor accessor, final String expression, final Class<X> propertyType) {
		String target = getTargetProperty(expression);

		if (resolver.isMapped(target)) {
			String key = resolver.getKey(target);
			FieldConverterProvider provider = FieldConverter.provider();
			@SuppressWarnings("unchecked")
			Class<? extends FieldConverter<X>> converterType = (Class<? extends FieldConverter<X>>) Arrays.stream(accessor.getAnnotationsByType(FormatMapEntryField.class))
					.filter(annotation -> Arrays.asList(annotation.keys()).contains(key))
					.map(FormatMapEntryField::converter)
					.findFirst()
					.filter(clazz -> !FormatFieldConverter.DEFAULT.class.isAssignableFrom(clazz))
					.orElse(null);

			if (converterType != null) {
				return provider.getConverter(propertyType, converterType);
			}

			Formatter<X> formatter = Arrays.stream(accessor.getAnnotationsByType(FormatMapEntry.class))
					.filter(annotation -> Arrays.asList(annotation.keys()).contains(key))
					.map(FormatMapEntry::pattern)
					.findFirst()
					.map(Formatter.of(propertyType)::withPattern)
					.orElse(null);

			if (formatter != null) {
				return provider.getConverter(formatter);
			}
		}

		return FormatUtil.getFieldConverter(accessor, propertyType);
	}

	Class<?> getPropertyType(final FormatFieldAccessor accessor, final String expression, final String text) {
		Class<?> propertyType = getMappedPropertyType(accessor, expression);
		return propertyType != null ? propertyType : getFieldPropertyType(accessor, text);
	}

	Class<?> getPropertyType(final FormatFieldAccessor accessor, final String expression, final Object value) {
		if (value == null) {
			Class<?> propertyType = getMappedPropertyType(accessor, expression);

			if (propertyType != null) {
				return propertyType;
			}
		}

		return getFieldPropertyType(accessor, value);
	}

	<U extends T> Object getValue(final U target, final String expression) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		try {
			return propertyUtils.getProperty(target, expression);
		} catch (IndexOutOfBoundsException | NestedNullException e) {
			return null;
		}
	}

	<U extends T> void setValue(final U target, final String expression, final Object value, final String text) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
		// Create nested beans
		Object bean = target;
		String expr = expression;

		do {
			String next = resolver.next(expr);
			Object val = propertyUtils.getProperty(bean, resolver.getProperty(next));

			prepareProperty(bean, next, val);

			if (resolver.hasNested(expr)) {
				// Get nested value
				val = propertyUtils.getProperty(bean, next);

				if (val == null) {
					// Initialize nested value
					val = createNested(bean, next, text);
				}

				bean = val;
			}

			expr = resolver.remove(expr);
		} while (expr != null);

		// Set property value
		propertyUtils.setProperty(target, expression, value);
	}

	List<String> resolveProperty(final Strategy strategy, final Class<?> beanType, final String expression, final String parent) {
		if (resolver.hasNested(expression)) {
			return resolveNestedProperty(strategy, beanType, expression, parent);
		} else {
			return resolveSimpleProperty(strategy, beanType, expression, parent);
		}
	}

	private List<String> resolveNestedProperty(final Strategy strategy, final Class<?> beanType, final String expression, final String parent) {
		String containerName = resolver.getProperty(expression);

		// Find the field with FormatFieldContainer annotation that match the field name.
		FormatFieldAccessor accessor = getFieldContainer(strategy, beanType, containerName);

		if (accessor != null) {
			final String property = new StringBuilder()
					.append(parent == null ? "" : parent + ".")
					.append(accessor.getName())
					.toString();

			Class<?> containerType = getFieldPropertyType(accessor);

			if (resolver.isIndexed(expression)) {
				return resolveIndexedNestedProperty(strategy, containerType, property, expression);
			} else if (resolver.isMapped(expression)) {
				return resolveMappedNestedProperty(strategy, containerType, property, expression);
			}

			return resolveProperty(strategy, containerType, resolver.remove(expression), property);
		}

		return Collections.emptyList();
	}

	private List<String> resolveIndexedNestedProperty(final Strategy strategy, final Class<?> containerType, final String property, final String expression) {
		int index = resolver.getIndex(expression);

		if (index == -1) {
			int[] boundaries = resolver.getBoundaries(expression);
			if (boundaries.length > 0) {
				int startInclusive = boundaries[0];
				int endInclusive = boundaries[1];

				if (endInclusive == -1) {
					return Stream.of(String.format(INDEXED_PROP_FORMAT, property, startInclusive + "+"))
							.flatMap(prop -> resolveProperty(strategy, containerType, resolver.remove(expression), prop).stream())
							.collect(Collectors.toList());
				} else {
					return IntStream.rangeClosed(startInclusive, Math.max(startInclusive, endInclusive))
							.mapToObj(i -> String.format(INDEXED_PROP_FORMAT, property, i))
							.flatMap(prop -> resolveProperty(strategy, containerType, resolver.remove(expression), prop).stream())
							.collect(Collectors.toList());
				}
			}
		} else {
			return resolveProperty(strategy, containerType, resolver.remove(expression), String.format(INDEXED_PROP_FORMAT, property, index));
		}

		return Collections.emptyList();
	}

	private List<String> resolveMappedNestedProperty(final Strategy strategy, final Class<?> containerType, final String property, final String expression) {
		return Optional.ofNullable(resolver.getKey(expression))
				.map(keys -> Arrays.stream(keys.split(","))
						.map(String::trim)
						.map(key -> String.format(MAPPED_PROP_FORMAT, property, key))
						.flatMap(prop -> resolveProperty(strategy, containerType, resolver.remove(expression), prop).stream())
						.collect(Collectors.toList()))
				.orElseGet(Collections::emptyList);
	}

	private List<String> resolveSimpleProperty(final Strategy strategy, final Class<?> beanType, final String expression, final String parent) {
		String fieldName = resolver.getProperty(expression);

		// Find the field with FormatField annotation that match the field name.
		FormatFieldAccessor accessor = getField(strategy, beanType, fieldName);

		// If the field was found then build the final property name.
		if (accessor != null) {
			final String property = new StringBuilder()
					.append(parent == null ? "" : parent + ".")
					.append(accessor.getName())
					.toString();

			if (resolver.isIndexed(expression)) {
				return resolveIndexedSimpleProperty(accessor, property, expression);
			} else if (resolver.isMapped(expression)) {
				return resolveMappedSimpleProperty(accessor, property, expression);
			}

			resolvedProperties.put(property, accessor);

			return Collections.singletonList(property);
		}

		// No property found for the given expression in the given bean type.
		// Maybe the expression designates the type info name on superclass.
		// This will be evaluated later by the processor.
		return Collections.emptyList();
	}

	private List<String> resolveIndexedSimpleProperty(final FormatFieldAccessor accessor, final String property, final String expression) {
		int index = resolver.getIndex(expression);

		if (index == -1) {
			int[] boundaries = resolver.getBoundaries(expression);

			if (boundaries.length > 0) {
				int startInclusive = boundaries[0];
				int endInclusive = boundaries[1];

				if (endInclusive == -1) {
					List<String> properties = Stream.of(String.format(INDEXED_PROP_FORMAT, property, startInclusive + "+"))
							.collect(Collectors.toList());

					properties.forEach(prop -> resolvedProperties.put(prop, accessor));

					return properties;
				} else {
					List<String> properties = IntStream.rangeClosed(startInclusive, Math.max(startInclusive, endInclusive))
							.mapToObj(i -> String.format(INDEXED_PROP_FORMAT, property, i))
							.collect(Collectors.toList());

					properties.forEach(prop -> resolvedProperties.put(prop, accessor));

					return Collections.unmodifiableList(properties);
				}
			}
		} else {
			String prop = String.format(INDEXED_PROP_FORMAT, property, index);
			resolvedProperties.put(prop, accessor);
			return Collections.singletonList(prop);
		}

		return Collections.emptyList();
	}

	private List<String> resolveMappedSimpleProperty(final FormatFieldAccessor accessor, final String property, final String expression) {
		return Optional.ofNullable(resolver.getKey(expression))
				.map(keys -> {
					List<String> properties = Arrays.stream(keys.split(","))
							.map(String::trim)
							.map(key -> String.format(MAPPED_PROP_FORMAT, property, key))
							.collect(Collectors.toList());

					properties.forEach(prop -> resolvedProperties.put(prop, accessor));

					return Collections.unmodifiableList(properties);
				})
				.orElseGet(Collections::emptyList);
	}

	@SuppressWarnings("unchecked")
	private void prepareProperty(final Object bean, final String expression, final Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		String name = resolver.getProperty(expression);

		boolean indexed = resolver.isIndexed(expression);
		boolean mapped = resolver.isMapped(expression);

		if (value == null) {
			if (indexed) {
				// Initialize list
				propertyUtils.setProperty(bean, name, new ArrayList<>());
			} else if (mapped) {
				// Initialize map
				propertyUtils.setProperty(bean, name, new LinkedHashMap<>());
			}
		}

		if (indexed) {
			List<Object> list = (List<Object>) propertyUtils.getProperty(bean, name);
			int index = resolver.getIndex(expression);
			if (index == list.size()) {
				list.add(index, null);
			}
		}
	}

	private Object createNested(final Object bean, final String expression, final String text) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalArgumentException, SecurityException {
		FormatFieldAccessor field = FormatFieldAccessorUtil.getFieldAccessor(bean.getClass(), resolver.getProperty(expression));
		Class<?> propertyType = getFieldPropertyType(field, text);
		Object value = propertyType.getConstructor().newInstance();
		propertyUtils.setProperty(bean, expression, value);
		return value;
	}

	private String getTargetProperty(final String expression) {
		return Arrays.stream(expression.split(PropertyResolver.NESTED_REGEX))
				.reduce((previous, current) -> current)
				.orElse(null);
	}

	private Class<?> getMappedPropertyType(final FormatFieldAccessor accessor, final String expression) {
		String target = getTargetProperty(expression);

		if (resolver.isMapped(target)) {
			String key = resolver.getKey(target);
			return Arrays.stream(accessor.getAnnotationsByType(FormatMapEntryField.class))
					.filter(annotation -> Arrays.asList(annotation.keys()).contains(key))
					.map(FormatMapEntryField::field)
					.map(FormatField::targetClass)
					.findFirst()
					.filter(targetClass -> !Void.class.isAssignableFrom(targetClass))
					.orElse(null);
		}

		return null;
	}

	@NoArgsConstructor
	private static final class PropertyResolver extends DefaultResolver {

	    static final String INDEXED_REGEX = "\\[(\\d+|\\d+::(\\d+|\\*)|\\*)\\]";
	    static final String MAPPED_REGEX  = "\\[\\\"([^\\\"]+)\\\"\\]";
	    static final String NESTED_REGEX  = "\\.";

	    public int[] getBoundaries(final String expression) {
	        if (expression == null || expression.length() == 0) {
	            return new int[0];
	        }

	        String next = next(expression.split(NESTED_REGEX)[0], MAPPED_REGEX);

			Matcher matcher = Pattern.compile(INDEXED_REGEX)
					.matcher(next);

			if (matcher.find()) {
				String[] boundaries = matcher.group(1).split("::");
				if (boundaries.length == 1) {
					return new int[] { 0, -1 };
				} else {
					return new int[] {
							Integer.parseInt(boundaries[0]),
							parseBoundary(boundaries[1])
					};
				}
			}

			return new int[0];
	    }

		@Override
		public int getIndex(final String expression) {
	        if (expression == null || expression.length() == 0) {
	            return -1;
	        }

	        String next = next(expression.split(NESTED_REGEX)[0], MAPPED_REGEX);

			Matcher matcher = Pattern.compile(INDEXED_REGEX)
					.matcher(next);
			if (matcher.find()) {
				try {
					return Integer.parseInt(matcher.group(1));
				} catch (NumberFormatException e) {
					return -1;
				}
			}

			return -1;
		}

		@Override
		public String getKey(final String expression) {
	        if (expression == null || expression.length() == 0) {
	            return null;
	        }

	        String next = next(expression.split(NESTED_REGEX)[0], INDEXED_REGEX);

			Matcher matcher = Pattern.compile(MAPPED_REGEX)
					.matcher(next);
			if (matcher.find()) {
	        	return matcher.group(1);
			}

	        return null;
		}

		@Override
		public String getProperty(final String expression) {
	        if (expression == null || expression.length() == 0) {
	            return expression;
	        }

	        String next = expression.split(NESTED_REGEX)[0];

			Matcher matcher = Pattern.compile(INDEXED_REGEX + "|" + MAPPED_REGEX)
					.matcher(next);
			if (matcher.find()) {
	        	next = next.substring(0, matcher.start());
			}

	        return next;
		}

		@Override
		public boolean isIndexed(final String expression) {
	        if (expression == null || expression.length() == 0) {
	            return false;
	        }

	        String next = next(expression.split(NESTED_REGEX)[0], MAPPED_REGEX);

	        return Pattern.compile(INDEXED_REGEX)
					.matcher(next)
					.find();
		}

		@Override
		public boolean isMapped(String expression) {
	        if (expression == null || expression.length() == 0) {
	            return false;
	        }

	        String next = next(expression.split(NESTED_REGEX)[0], INDEXED_REGEX);

			return Pattern.compile(MAPPED_REGEX)
					.matcher(next)
					.find();
		}

		@Override
		public String next(final String expression) {
	        if (expression == null || expression.length() == 0) {
	            return null;
	        }

	        return next(expression.split(NESTED_REGEX)[0], INDEXED_REGEX, MAPPED_REGEX);
		}

		private String next(final String expression, final String regex) {
			Objects.requireNonNull(expression);
			Objects.requireNonNull(regex);
			Matcher matcher = Pattern.compile(regex).matcher(expression);
			return matcher.find() ? expression.substring(0, matcher.end()) : expression;
		}

		private String next(final String expression, final String... regex) {
			Objects.requireNonNull(regex);
			String next = expression;
			for (int i = 0; i < regex.length; i++) {
				next = next(next, regex[i]);
			}
			return next;
		}

		private int parseBoundary(String bound) {
			return ("*".equals(bound) ? -1 : Integer.parseInt(bound));
		}

	}

}
