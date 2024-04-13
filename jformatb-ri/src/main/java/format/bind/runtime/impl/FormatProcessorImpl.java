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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
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
import org.apache.commons.beanutils.expression.Resolver;
import org.apache.commons.lang3.reflect.FieldUtils;

import format.bind.FormatProcessor;
import format.bind.annotation.Format;
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

	/** The map of all resolved fields of this format processor. */
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	final Map<String, Field> resolvedProperties = new LinkedHashMap<>();

	/**
	 * Creates a new instance of {@code FormatProcessorImpl}.
	 * @param type The class instance of the Java object to be processed by this processor.
	 * @param pattern The pattern of the text format to process.
	 */
	protected FormatProcessorImpl(Class<T> type, String pattern) {
		this.type = type;
		this.pattern = pattern;

		propertyUtils = BeanUtilsBean.getInstance().getPropertyUtils();
		propertyUtils.setResolver(new PropertyResolver());
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

	boolean isTypeInfoFieldAbsent(final FormatTypeInfo typeInfo, final String name, final List<String> properties) {
		return typeInfo != null && typeInfo.fieldName().equals(name) && properties.isEmpty();
	}

	String nextProperty(final ListIterator<String> iterator, final int next) {
		String property = iterator.next();
		Matcher matcher = Pattern.compile("(?<start>\\d+) \\+ \\*").matcher(property);

		if (matcher.find()) {
			Field accessor = resolvedProperties.get(property);
			int index = Integer.parseInt(matcher.group("start")) + next;
			property = matcher.replaceAll(String.valueOf(index));
			iterator.previous();
			iterator.add(property);
			resolvedProperties.put(property, accessor);
		}

		return property;
	}

	String getPattern(Class<?> resultType) {
		return pattern != null ? pattern : Optional.ofNullable(resultType.getAnnotation(Format.class))
				.map(Format::pattern)
				.orElse(null);
	}

	Matcher compile(String input) {
		return Pattern.compile(REGEX)
				.matcher(input);
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
		Resolver resolver = getResolver();
		Object bean = target;
		String expr = expression;

		do {
			String next = resolver.next(expr);
			Object val = propertyUtils.getProperty(bean, resolver.getProperty(next));

			prepareProperty(bean, next, val);

			if (resolver.hasNested(expr)) {
				if (val == null) {
					// Initialize nested value
					val = setNested(bean, next, text);
				} else {
					// Get nested value
					val = propertyUtils.getProperty(bean, next);
				}

				bean = val;
			}

			expr = resolver.remove(expr);
		} while (expr != null);

		// Set property value
		propertyUtils.setProperty(target, expression, value);
	}

	List<String> resolveProperty(final Class<?> beanType, final String expression, final String parent) {
		if (getResolver().hasNested(expression)) {
			return resolveNestedProperty(beanType, expression, parent);
		} else {
			return resolveSimpleProperty(beanType, expression, parent);
		}
	}

	private List<String> resolveNestedProperty(final Class<?> beanType, final String expression, final String parent) {
		Resolver resolver = getResolver();

		String containerName = resolver.getProperty(expression);

		// Find the field with FormatFieldContainer annotation that match the field name.
		Field accessor = getFieldContainer(beanType, containerName);

		if (accessor != null) {
			final String property = new StringBuilder()
					.append(parent == null ? "" : parent + ".")
					.append(accessor.getName())
					.toString();

			Class<?> containerType = getFieldPropertyType(accessor);

			if (resolver.isIndexed(expression)) {
				return resolveIndexedNestedProperty(containerType, property, expression);
			} else if (resolver.isMapped(expression)) {
				return resolveMappedNestedProperty(containerType, property, expression);
			}

			return resolveProperty(containerType, resolver.remove(expression), property);
		}

		return Collections.emptyList();
	}

	private List<String> resolveIndexedNestedProperty(final Class<?> containerType, final String property, final String expression) {
		PropertyResolver resolver = getResolver();
		int index = resolver.getIndex(expression);

		if (index == -1) {
			int[] boundaries = resolver.getBoundaries(expression);
			if (boundaries.length > 0) {
				int startInclusive = boundaries[0];
				int endInclusive = boundaries[1];

				if (endInclusive == -1) {
					return Stream.of(String.format(INDEXED_PROP_FORMAT, property, startInclusive + " + *"))
							.flatMap(prop -> resolveProperty(containerType, resolver.remove(expression), prop).stream())
							.collect(Collectors.toList());
				} else {
					return IntStream.rangeClosed(startInclusive, Math.max(startInclusive, endInclusive))
							.mapToObj(i -> String.format(INDEXED_PROP_FORMAT, property, i))
							.flatMap(prop -> resolveProperty(containerType, resolver.remove(expression), prop).stream())
							.collect(Collectors.toList());
				}
			}
		} else {
			return resolveProperty(containerType, resolver.remove(expression), String.format(INDEXED_PROP_FORMAT, property, index));
		}

		return Collections.emptyList();
	}

	private List<String> resolveMappedNestedProperty(final Class<?> containerType, final String property, final String expression) {
		Resolver resolver = getResolver();
		return Arrays.stream(resolver.getKey(expression).split(","))
				.map(String::trim)
				.map(key -> String.format(MAPPED_PROP_FORMAT, property, key))
				.flatMap(prop -> resolveProperty(containerType, resolver.remove(expression), prop).stream())
				.collect(Collectors.toList());
	}

	private List<String> resolveSimpleProperty(final Class<?> beanType, final String expression, final String parent) {
		Resolver resolver = getResolver();

		String fieldName = resolver.getProperty(expression);

		// Find the field with FormatField annotation that match the field name.
		Field accessor = getField(beanType, fieldName);

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

	private List<String> resolveIndexedSimpleProperty(final Field accessor, final String property, final String expression) {
		PropertyResolver resolver = getResolver();
		int index = resolver.getIndex(expression);

		if (index == -1) {
			int[] boundaries = resolver.getBoundaries(expression);

			if (boundaries.length > 0) {
				int startInclusive = boundaries[0];
				int endInclusive = boundaries[1];

				if (endInclusive == -1) {
					List<String> properties = Stream.of(String.format(INDEXED_PROP_FORMAT, property, startInclusive + " + *"))
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

	private List<String> resolveMappedSimpleProperty(final Field accessor, final String property, final String expression) {
		Resolver resolver = getResolver();
		List<String> properties = Arrays.stream(resolver.getKey(expression).split(","))
				.map(String::trim)
				.map(key -> String.format(MAPPED_PROP_FORMAT, property, key))
				.collect(Collectors.toList());

		properties.forEach(prop -> resolvedProperties.put(prop, accessor));

		return Collections.unmodifiableList(properties);
	}

	@SuppressWarnings("unchecked")
	private void prepareProperty(final Object bean, final String expression, final Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Resolver resolver = getResolver();
		String name = resolver.getProperty(expression);

		boolean indexed = resolver.isIndexed(expression);
		boolean mapped = resolver.isMapped(expression);

		if (value == null) {
			if (indexed) {
				// Initialize list
				propertyUtils.setProperty(bean, name, new ArrayList<>());
			} else if (mapped) {
				// Initialize map
				propertyUtils.setProperty(bean, name, new HashMap<>());
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

	private Object setNested(final Object bean, final String expression, final String text) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalArgumentException, SecurityException {
		Resolver resolver = getResolver();
		Field field = FieldUtils.getField(bean.getClass(), resolver.getProperty(expression), true);
		Class<?> propertyType = getFieldPropertyType(field, text);
		Object value = propertyType.getConstructor().newInstance();
		propertyUtils.setProperty(bean, expression, value);
		return value;
	}

	private PropertyResolver getResolver() {
		return (PropertyResolver) propertyUtils.getResolver();
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
