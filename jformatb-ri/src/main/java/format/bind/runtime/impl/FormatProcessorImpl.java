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

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.beanutils.expression.DefaultResolver;

import format.bind.FormatProcessor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
abstract class FormatProcessorImpl<T, F extends FormatProcessorImpl<T, F>> implements FormatProcessor<T, F> {

	static final String REGEX = "\\$\\{(?<property>[^\\}]+)\\}(?<array>\\[(?<size>[\\d\\.\\*]+)\\])?";

	static final String PROPERTY_GROUP = "property";
	static final String ARRAY_GROUP = "array";
	static final String SIZE_GROUP = "size";

	/** The class instance representing the Java type of the object to process. */
	final Class<T> type;

	/** The pattern of the text format to process. */
	final String pattern;

	/** The map of all resolved fields of this format processor. */
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	final Map<String, Field> resolvedProperties = new LinkedHashMap<>();

	/** The bean property accessor utility of this {@code FormatProcessorImpl}. */
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	final PropertyUtilsBean propertyUtils = BeanUtilsBean.getInstance().getPropertyUtils();

	/** The post processing event callback {@link Listener} for this {@code FormatProcessorImpl}. */
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	final ThreadLocal<Listener<T>> listener = ThreadLocal.withInitial(() -> (target, fields) -> {});

	@SuppressWarnings("unchecked")
	public F setListener(final Listener<T> listener) {
		if (listener == null) {
			this.listener.remove();
		} else {
			this.listener.set(listener);
		}
		return (F) this;
	}

	<U extends T> Object getValue(final U target, final String expression) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		try {
			return propertyUtils.getProperty(target, expression);
		} catch (IndexOutOfBoundsException | NestedNullException e) {
			return null;
		}
	}

	<U extends T> void setValue(final U target, final String expression, final Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
		// Create nested beans
		String[] names = expression.split("\\.");
		Object bean = target;

		for (int i = 0; i < names.length - 1; i++) {
			String name = names[i];
			Object val = propertyUtils.getProperty(bean, name);

			if (val == null) {
				PropertyDescriptor propertyDescriptor = propertyUtils.getPropertyDescriptor(bean, name);
				Class<?> propertyType = propertyDescriptor.getPropertyType();

				if (propertyType == List.class) {
					val = new ArrayList<Object>();
				} else if (propertyType == Map.class) {
					val = new HashMap<String, Object>();
				} else {
					val = propertyType.getConstructor().newInstance();
				}

				propertyUtils.setProperty(bean, name, val);
			}

			bean = val;
		}

		// Set property value
		propertyUtils.setProperty(target, expression, value);
	}

	@NoArgsConstructor
	static final class PropertyResolver extends DefaultResolver {

	    static final String INDEXED_REGEX = "\\[(\\d+)\\]";
	    static final String MAPPED_REGEX  = "\\[\\\"([^\\\"]+)\\\"\\]";
	    static final String NESTED_REGEX  = "\\.";

		@Override
		public int getIndex(final String expression) {
	        if (expression == null || expression.length() == 0) {
	            return -1;
	        }

	        String next = next(expression.split(NESTED_REGEX)[0], MAPPED_REGEX);

			Matcher matcher = Pattern.compile(INDEXED_REGEX)
					.matcher(next);
			if (matcher.find()) {
				return Integer.parseInt(matcher.group(1));
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

	}

}
