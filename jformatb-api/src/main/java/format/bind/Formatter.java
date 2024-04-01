/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package format.bind;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.beanutils.expression.DefaultResolver;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Value;
import lombok.With;
import lombok.experimental.Accessors;
import lombok.experimental.UtilityClass;

/**
 * The entry point to the format binding API. It provides functionality for 
 * reading (parse) and writing (format) text format, either to and from basic
 * POJOs (Plain Old Java Objects).
 * 
 * @param <T> The base type of object to convert to or from
 * 
 * @author Yannick Ebongue
 * 
 * @see Format
 * @see FormatField
 * @see FormatFieldContainer
 * @see FormatFieldOverride
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter(AccessLevel.NONE)
public class Formatter<T> {

	@Data
	@Accessors(fluent = true)
	private static final class FormatFieldSpecImpl implements FormatFieldSpec {

		private String name;

		private Type type;

		private int length;

		private int scale;

		private String format;

		private String placeholder;

		public static final FormatFieldSpecImpl from(FormatField field) {
			Objects.requireNonNull(field);
			return new FormatFieldSpecImpl()
					.name(field.name())
					.type(field.type())
					.length(field.length())
					.scale(field.scale())
					.format(field.format())
					.placeholder(field.placeholder());
		}

		public static final FormatFieldSpecImpl from(FormatField field, FormatFieldOverride override) {
			if (override == null) {
				return from(field);
			}

			try {
				FormatField field1 = field;
				FormatField field2 = override.field();
				Class<FormatField> type = FormatField.class;
				return new FormatFieldSpecImpl()
						.name(!type.getDeclaredMethod("name").getDefaultValue().equals(field2.name()) ? field2.name() : field1.name())
						.type(!type.getDeclaredMethod("type").getDefaultValue().equals(field2.type()) ? field2.type() : field1.type())
						.length(!type.getDeclaredMethod("length").getDefaultValue().equals(field2.length()) ? field2.length() : field1.length())
						.scale(!type.getDeclaredMethod("scale").getDefaultValue().equals(field2.scale()) ? field2.scale() : field1.scale())
						.format(!type.getDeclaredMethod("format").getDefaultValue().equals(field2.format()) ? field2.format() : field1.format())
						.placeholder(!type.getDeclaredMethod("placeHolder").getDefaultValue().equals(field2.placeholder()) ? field2.placeholder() : field1.placeholder());
			} catch (NoSuchMethodException | SecurityException e) {
				throw new IllegalArgumentException(e);
			}
		}

	}

	@Value(staticConstructor = "of")
	private static class FieldProperty {

		private String name;

		private Class<?> type;

		private FormatFieldSpec annotation;

		private FormatTypeInfo typeInfo;

		private FieldConverter<?> converter;

	}

	@NoArgsConstructor
	private static final class PropertyResolver extends DefaultResolver {

	    private static final String INDEXED_REGEX = "\\[(\\d+)\\]";
	    private static final String MAPPED_REGEX  = "\\[\\\"([^\\\"]+)\\\"\\]";
	    private static final String NESTED_REGEX  = "\\.";

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

	@UtilityClass
	private static class Util {
		
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

		public <T> Class<? extends T> getFormatSubType(final Class<T> type, final String typeValue) {
			return getFormatSubTypes(type).stream()
					.filter(subclass -> Arrays.stream(subclass.getDeclaredAnnotationsByType(FormatTypeValue.class))
							.anyMatch(annotation -> annotation.value().equals(typeValue)))
					.findFirst()
					.orElse(type);
		}

		public String getFieldPropertyName(final Field field, final String context) {
			Class<?> fieldType = field.getType();

			String fieldName = field.getName();
			String propertyName = context != null ? context + "." + fieldName : fieldName;

			if (List.class.isAssignableFrom(fieldType)) {
				return propertyName + INDEX_TEMPLATE;
			}

			if (Map.class.isAssignableFrom(fieldType)) {
				return propertyName + KEY_TEMPLATE;
			}

			return propertyName;
		}

		public Class<?> getFieldPropertyType(final Field field) {
			Class<?> fieldType = field.getType();

			if (List.class.isAssignableFrom(fieldType)) {
				Type valueType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
				return (Class<?>) valueType;
			}

			if (Map.class.isAssignableFrom(fieldType)) {
				Type valueType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[1];
				return (Class<?>) valueType;
			}

			return fieldType;
		}

		@SuppressWarnings("unchecked")
		public <T, C extends FieldConverter<T>> C getFieldConverter(final Field field, final Class<T> type) {
			if (field.isAnnotationPresent(FormatFieldConverter.class)) {
				return (C) converterProvider().getConverter(type, (Class<C>) field.getAnnotation(FormatFieldConverter.class).value());
			} else if (field.isAnnotationPresent(Format.class)) {
				return (C) converterProvider().getConverter(Formatter.of(type).withPattern(field.getAnnotation(Format.class).pattern()));
			} else {
				return (C) converterProvider().getConverter(type);
			}
		}

		public Object getFieldValue(final List<Object> values, final boolean array) {
			Object value = null;

			// Remove null values
			values.removeIf(Objects::isNull);

			if (!values.isEmpty()) {
				value = array ? values : values.get(0);
			}

			return value;
		}

		public void registerFieldProperties(final Map<String, FieldProperty> properties, final Map<String, FormatFieldOverride> overrides, final Class<?> type, final String context) {
			overrides.putAll(Arrays.stream(type.getDeclaredAnnotationsByType(FormatFieldOverride.class))
						.collect(Collectors.toMap(override -> context != null ? context + "." + override.property() : override.property(), Function.identity())));

			FieldUtils.getFieldsListWithAnnotation(type, FormatField.class).forEach(field -> {
				String propertyName = getFieldPropertyName(field, context);
				Class<?> fieldType = getFieldPropertyType(field);
				FormatFieldSpec annotation = FormatFieldSpecImpl.from(
						field.getAnnotation(FormatField.class),
						overrides.get(propertyName));
				FormatTypeInfo typeInfo = field.getAnnotation(FormatTypeInfo.class);
				FieldConverter<?> converter = getFieldConverter(field, fieldType);
				properties.put(
						StringUtils.defaultIfEmpty(annotation.name(), propertyName),
						FieldProperty.of(propertyName, fieldType, annotation, typeInfo, converter));
			});

			FieldUtils.getFieldsListWithAnnotation(type, FormatFieldContainer.class).forEach(field -> {
				String propertyName = getFieldPropertyName(field, context);
				Class<?> fieldType = getFieldPropertyType(field);
				overrides.putAll(Arrays.stream(field.getDeclaredAnnotationsByType(FormatFieldOverride.class))
						.collect(Collectors.toMap(override -> propertyName + "." + override.property(), Function.identity())));
				registerFieldProperties(properties, overrides, fieldType, propertyName);
			});
		}

		public Map<String, FieldProperty> buildFieldProperties(final Class<?> type) {
			Map<String, FieldProperty> properties = new HashMap<>();
			Map<String, FormatFieldOverride> overrides = new HashMap<>();

			registerFieldProperties(properties, overrides, type, null);

			return properties;
		}

		public String buildPropertyName(final String expression, final String name, final boolean array) {
			Matcher matcher = Pattern.compile(PropertyResolver.INDEXED_REGEX + "|" + PropertyResolver.MAPPED_REGEX)
					.matcher(expression);

			List<Integer> indexes = new ArrayList<>();
			List<String> keys = new ArrayList<>();

			boolean found = false;

			while (matcher.find()) {
				String index = matcher.group(1);
				String key = matcher.group(2);

				if (StringUtils.isNotBlank(index)) {
					indexes.add(Integer.parseInt(index));
				}

				if (StringUtils.isNotBlank(key)) {
					keys.add(key);
				}

				found = true;
			}

			String propertyName = name;

			if (found) {
				for (int index : indexes) {
					propertyName = propertyName.replaceFirst("\\[i\\]", "[" + index + "]");
				}

				for (String key : keys) {
					propertyName = propertyName.replaceFirst("\\[key\\]", "[\"" + key + "\"]");
				}
			}

			if (array) {
				return propertyName.substring(0, propertyName.lastIndexOf("["));
			}

			return propertyName;
		}

		@SuppressWarnings("unchecked")
		public <T, C extends FieldConverter<T>> String formatFieldValue(final Object value, final FormatFieldSpec annotation, final FieldConverter<?> converter, final Matcher matcher, final boolean array) {
			StringBuilder output = new StringBuilder();
			int size = array ? Integer.parseInt(matcher.group(SIZE_GROUP)) : 1;
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
				output.append(((C) converter).format(annotation, val));
				index++;
			} while (index < size);

			return output.toString();
		}

		public Object parseFieldValue(final String text, final FormatFieldSpec annotation, final FieldConverter<?> converter, final Matcher matcher, final AtomicInteger matcherEnd, final AtomicInteger lastIndex) {
			List<Object> values = new ArrayList<>();
			boolean array = StringUtils.isNotBlank(matcher.group(ARRAY_GROUP));
			int size = array ? Integer.parseInt(matcher.group(SIZE_GROUP)) : 1;
			int length = annotation.length();
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

					values.add(converter.parse(annotation, source));

					index++;
				} while (index < size);
			}

			return getFieldValue(values, array);
		}

	}

	/**
	 * A functional interface to be registered with {@link Formatter} to listen
	 * for post parsing or formatting event.
	 * 
	 * @param <T> The type of the result object after parsing or formatting text.
	 */
	@FunctionalInterface
	public interface Listener<T> {

		/**
		 * Callback method invoked after reading to (parse) or writing from (format)
		 * the {@code target} parameter.
		 * @param target The processed target object.
		 * @param fields The map of the text format fields.
		 */
		void postProcessing(T target, Map<String, Object> fields);

	}

	private static final String REGEX = "\\$\\{(?<property>[^\\}]+)\\}(?<array>\\[(?<size>[\\d\\.\\*]+)\\])?";

	private static final String PROPERTY_GROUP = "property";
	private static final String ARRAY_GROUP = "array";
	private static final String SIZE_GROUP = "size";

	private static final String INDEX_TEMPLATE = "[i]";
	private static final String KEY_TEMPLATE = "[key]";

	/** The current {@link FieldConverter} service converterProvider. */
	private static FieldConverterProvider converterProvider;

	/** The base type of this {@code Formatter}. */
	private Class<T> type;

	/**
	 * The text format pattern of this {@code Formatter}.
	 * 
	 * @param pattern The text format pattern to set.
	 */
	@With
	private String pattern;

	/** The bean property accessor utility of this {@code Formatter}. */
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private PropertyUtilsBean propertyUtils = BeanUtilsBean.getInstance().getPropertyUtils();

	/** The post processing event callback {@link Listener} for this {@code Formatter}. */
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private ThreadLocal<Listener<T>> listener = ThreadLocal.withInitial(() -> (target, fields) -> {});

	private T createObject(final String text) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (type.isAnnotationPresent(FormatTypeInfo.class)) {
			FormatTypeInfo typeInfo = type.getAnnotation(FormatTypeInfo.class);
			String typeValue = StringUtils.substring(text, typeInfo.start(), typeInfo.start() + typeInfo.length());
			Class<? extends T> subType = Util.getFormatSubType(type, typeValue);
			return subType.getConstructor().newInstance();
		}

		return type.getConstructor().newInstance();
	}

	private <U extends T> Object getValue(final U target, final String expression) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		try {
			return propertyUtils.getProperty(target, expression);
		} catch (IndexOutOfBoundsException | NestedNullException e) {
			return null;
		}
	}

	private <U extends T> void setValue(final U target, final String expression, final Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
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

	/**
	 * Formats the given object to produce the corresponding text format.
	 * @param obj The object instance to format.
	 * @return The formatted text.
	 */
	public String format(final T obj) {
		try {
			StringBuilder output = new StringBuilder();
			Class<?> resultType = obj.getClass();

			Map<String, FieldProperty> properties = Util.buildFieldProperties(resultType);

			String input = pattern != null ? pattern : Optional.ofNullable(resultType.getAnnotation(Format.class))
					.map(Format::pattern)
					.orElse(null);
			Matcher matcher = Pattern.compile(REGEX)
					.matcher(input);

			propertyUtils.setResolver(new PropertyResolver());

			Map<String, Object> resolvedFields = new LinkedHashMap<>();

			int lastIndex = 0;

			while (matcher.find()) {
				String expression = matcher.group(PROPERTY_GROUP);
				String[] parts = expression.split(":");
				String name = parts[0]
						.replaceAll(PropertyResolver.INDEXED_REGEX, INDEX_TEMPLATE)
						.replaceAll(PropertyResolver.MAPPED_REGEX, KEY_TEMPLATE);

				if (resultType.isAnnotationPresent(FormatTypeInfo.class)) {
					FormatTypeInfo typeInfo = resultType.getAnnotation(FormatTypeInfo.class);
					if (typeInfo.fieldName().equals(name) && !properties.containsKey(name)) {
						String value = resultType.getAnnotation(FormatTypeValue.class).value();
						output.append(input, lastIndex, matcher.start());
						output.append(value);
						lastIndex = matcher.end();

						resolvedFields.put(parts[0], value);

						continue;
					}
				}

				boolean array = StringUtils.isNotBlank(matcher.group(ARRAY_GROUP));
				FieldProperty fieldProperty = properties.get(name);
				String propertyName = Util.buildPropertyName(parts[0], fieldProperty.getName(), array);
				Object value = getValue(obj, propertyName);

				FieldConverter<?> converter = fieldProperty.getConverter();
				FormatFieldSpecImpl annotation = (FormatFieldSpecImpl) fieldProperty.getAnnotation();

				if (parts.length > 1) {
					// Override annotation field length
					annotation = annotation.length(Integer.parseInt(parts[1]));
				}

				if (converter == null) {
					converter = converterProvider().getConverter(Formatter.of(value.getClass()));
				}

				output.append(input, lastIndex, matcher.start());
				output.append(Util.formatFieldValue(value, annotation, converter, matcher, array));
				resolvedFields.put(propertyName, value);
				lastIndex = matcher.end();
			}

			if (lastIndex < input.length()) {
				output.append(input, lastIndex, input.length());
			}

			listener.get().postProcessing(obj, resolvedFields);

			return output.toString();
		} catch (FormatException e) {
			throw e;
		} catch (Exception e) {
			throw new FormatProcessingException(String.format("Unable to format object [%s]", obj), e);
		}
	}

	/**
	 * Parses the given text format from the beginning to produce an object.
	 * @param text The text format to parse.
	 * @return The parsed object.
	 */
	public T parse(final String text) {
		try {
			T obj = createObject(text);
			Class<?> resultType = obj.getClass();

			Map<String, FieldProperty> properties = Util.buildFieldProperties(resultType);

			String input = pattern != null ? pattern : Optional.ofNullable(resultType.getAnnotation(Format.class))
					.map(Format::pattern)
					.orElse(null);
			Matcher matcher = Pattern.compile(REGEX)
					.matcher(input);

			propertyUtils.setResolver(new PropertyResolver());

			Map<String, Object> resolvedFields = new LinkedHashMap<>();

			AtomicInteger lastIndex = new AtomicInteger(0);
			AtomicInteger matcherEnd = new AtomicInteger(0);

			while (matcher.find()) {
				String expression = matcher.group(PROPERTY_GROUP);
				String[] parts = expression.split(":");
				String name = parts[0]
						.replaceAll(PropertyResolver.INDEXED_REGEX, INDEX_TEMPLATE)
						.replaceAll(PropertyResolver.MAPPED_REGEX, KEY_TEMPLATE);

				if (resultType.isAnnotationPresent(FormatTypeInfo.class)) {
					FormatTypeInfo typeInfo = resultType.getAnnotation(FormatTypeInfo.class);
					if (typeInfo.fieldName().equals(name) && !properties.containsKey(name)) {
						// Skip type info property
						String value = resultType.getAnnotation(FormatTypeValue.class).value();
						int start = matcher.start() - matcherEnd.get() + lastIndex.get();
						matcherEnd.set(matcher.end());
						lastIndex.set(start + typeInfo.length());

						resolvedFields.put(parts[0], value);

						continue;
					}
				}

				FieldProperty fieldProperty = properties.get(name);
				FieldConverter<?> converter = fieldProperty.getConverter();
				FormatFieldSpecImpl annotation = (FormatFieldSpecImpl) fieldProperty.getAnnotation();

				if (parts.length > 1) {
					// Override annotation field length
					annotation = annotation.length(Integer.parseInt(parts[1]));
				}

				if (converter == null) {
					Class<?> type = fieldProperty.getType();
					FormatTypeInfo typeInfo = fieldProperty.getTypeInfo();
					String typeValue = StringUtils.substring(text, typeInfo.start(), typeInfo.start() + typeInfo.length());
					Class<?> subType = Util.getFormatSubType(type, typeValue);
					converter = converterProvider().getConverter(Formatter.of(subType));
				}

				Object value = Util.parseFieldValue(text, annotation, converter, matcher, matcherEnd, lastIndex);

				// Set field value if not null
				if (value != null) {
					String propertyName = Util.buildPropertyName(parts[0], fieldProperty.getName(), value instanceof List);
					resolvedFields.put(propertyName, value);
					setValue(obj, propertyName, value);
				}
			}

			listener.get().postProcessing(obj, resolvedFields);

			return obj;
		} catch (FormatException e) {
			throw e;
		} catch (Exception e) {
			throw new FormatProcessingException(String.format("Unable to parse text [%s]", text), e);
		}
	}

	/**
	 * Register a post processing event callback {@link Listener} with this {@link Formatter}.
	 * 
	 * <p>
	 * There is only one {@code Listener} per {@code Formatter}. Setting a {@code Listener}
	 * replaces the previous registered. One can unregister the current listener by calling method
	 * {@link #removeListener()}
	 * 
	 * @param listener The post processing event callback for this {@link Formatter}.
	 * @return This {@code Formatter}.
	 * @throws NullPointerException if {@code listener} is null.
	 */
	public Formatter<T> setListener(Listener<T> listener) {
		Objects.requireNonNull(listener);
		this.listener.set(listener);
		return this;
	}

	/**
	 * Unregister the current post processing event callback {@link Listener} for this {@link Formatter}.
	 */
	public void removeListener() {
		this.listener.remove();
	}

	/**
	 * Create a new instance of {@code Formatter}.
	 * @param <T> The type parameter of the base type.
	 * @param type The base type instance to process.
	 * @return A new instance of {@code Formatter}.
	 */
	public static <T> Formatter<T> of(Class<T> type) {
		String pattern = Optional.ofNullable(type.getAnnotation(Format.class))
				.map(Format::pattern)
				.orElse(null);
		return new Formatter<>(type, pattern);
	}

	/**
	 * Obtain the {@link FieldConverter} service converterProvider.
	 * 
	 * @return The field converter service converterProvider instance.
	 */
	private static synchronized FieldConverterProvider converterProvider() {
		if (converterProvider == null) {
			converterProvider = FieldConverterProvider.provider();
		}

		return converterProvider;
	}

}
