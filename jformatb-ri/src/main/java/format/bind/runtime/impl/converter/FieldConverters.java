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

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.function.Supplier;

import format.bind.FormatFieldDescriptor;
import format.bind.Formatter;
import format.bind.converter.FieldConversionException;
import format.bind.converter.FieldConverter;
import lombok.experimental.UtilityClass;

/**
 * The utility class used to obtain field converters.
 * 
 * @author Yannick Ebongue
 */
@UtilityClass
class FieldConverters {

	/** The collection of all resolved field converters. */
	private static final ConcurrentMap<Class<?>, FieldConverter<?>> converters = new ConcurrentHashMap<>();

	static {
		addConverter(Boolean.class, BooleanConverter::new);
		addConverter(Byte.class, ByteConverter::new);
		addConverter(Short.class, ShortConverter::new);
		addConverter(Integer.class, IntegerConverter::new);
		addConverter(Long.class, LongConverter::new);
		addConverter(Float.class, FloatConverter::new);
		addConverter(Double.class, DoubleConverter::new);
		addConverter(BigInteger.class, BigIntegerConverter::new);
		addConverter(BigDecimal.class, BigDecimalConverter::new);
		addConverter(Character.class, CharacterConverter::new);
		addConverter(String.class, StringConverter::new);
		addConverter(Instant.class, InstantConverter::new);
		addConverter(LocalDate.class, LocalDateConverter::new);
		addConverter(LocalTime.class, LocalTimeConverter::new);
		addConverter(LocalDateTime.class, LocalDateTimeConverter::new);
		addConverter(OffsetTime.class, OffsetTimeConverter::new);
		addConverter(OffsetDateTime.class, OffsetDateTimeConverter::new);
		addConverter(ZonedDateTime.class, ZonedDateTimeConverter::new);
		addConverter(Date.class, DateConverter::new);
		addConverter(Calendar.class, CalendarConverter::new);
		addConverter(Timestamp.class, TimestampConverter::new);
		addConverter(Currency.class, CurrencyConverter::new);
		addConverter(UUID.class, UUIDConverter::new);
		addConverter(byte[].class, ByteArrayConverter::new);
	}

	/**
	 * Creates a new instance of the given converter type.
	 * 
	 * @param <C> The type of the returned {@link FieldConverter}.
	 * @param converterType The Java type of the returned {@link FieldConverter}.
	 * @return An instance of a {@link FieldConverter}.
	 * @throws IllegalArgumentException if an error occurs during creation.
	 */
	private <C extends FieldConverter<?>> C newInstance(final Class<C> converterType) {
		try {
			return converterType.getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Add the supplied converter to the field converter collection if not present.
	 * 
	 * @param <T> The type of the field to convert.
	 * @param type The class instance of the field Java type.
	 * @param converterSupplier The supplier that return the field converter to add.
	 * @return The supplier field converter if not present in collection. Otherwise the existing
	 * 		converter for the field Java type.
	 */
	@SuppressWarnings("unchecked")
	private <T> FieldConverter<T> addConverter(final Class<T> type, final Supplier<FieldConverter<T>> converterSupplier) {
		return (FieldConverter<T>) converters.computeIfAbsent(type, cls -> converterSupplier.get());
	}

	/**
	 * Add the supplied converter to the field converter collection if not present.
	 * 
	 * @param <T> The type of the field to convert.
	 * @param type The class instance of the field Java type.
	 * @param converterFunction The function that return the field converter to add.
	 * @return The function field converter if not present in collection. Otherwise the existing
	 * 		converter for the field Java type.
	 */
	@SuppressWarnings("unchecked")
	private <T> FieldConverter<T> addConverter(final Class<T> type, final Function<Class<?>, FieldConverter<?>> converterFunction) {
		return (FieldConverter<T>) converters.computeIfAbsent(type, converterFunction);
	}

	/**
	 * Creates a field converter for the given enum class.
	 * 
	 * @param <E> The type of the enum class.
	 * @param enumType The class instance of the field enum type.
	 * @return The field converter instance.
	 */
	@SuppressWarnings("unchecked")
	private <E extends Enum<E>> FieldConverter<E> getEnumConverter(final Class<?> enumType) {
		return EnumConverter.of((Class<E>) enumType);
	}

	private <T> FieldConverter<T> getValueConverter(final Class<T> fieldType) {
		return ValueConverter.of(fieldType);
	}

	/**
	 * Obtain the {@link FieldConverter} of the specified field type and converter type.
	 * @param <T> The Java type of the field.
	 * @param fieldType The class instance of the field Java type.
	 * @param converterType The class instance of the field converter to obtain.
	 * @return The field converter instance.
	 */
	<T> FieldConverter<T> getConverter(Class<T> fieldType, Class<? extends FieldConverter<T>> converterType) {
		return newInstance(converterType);
	}

	/**
	 * Obtain the {@link FieldConverter} of the specified field type.
	 * @param <T> The Java type of the field.
	 * @param fieldType The class instance of the field Java type.
	 * @return The field converter instance.
	 */
	@SuppressWarnings("unchecked")
	<T> FieldConverter<T> getConverter(Class<T> fieldType) {
		FieldConverter<T> converter = (FieldConverter<T>) converters.get(fieldType);

		if (converter == null) {
			if (ValueConverter.containsAccessor(fieldType)) {
				converter = addConverter(fieldType, FieldConverters::getValueConverter);
			}

			if (fieldType.isEnum()) {
				converter = addConverter(fieldType, FieldConverters::getEnumConverter);
			}
		}

		return converter;
	}

	/**
	 * Obtain the {@link FieldConverter} of the specified {@code formatter}.
	 * @param <T> The type of the {@link Formatter} type.
	 * @param formatter The formatter
	 * @return The field converter instance.
	 */
	<T> FieldConverter<T> getConverter(Formatter<T> formatter) {
		return FormatConverter.of(formatter);
	}

	/**
	 * Throws a {@link FieldConversionException} during Java object formatting.
	 * @param <T> The type of the Java object to format.
	 * @param descriptor The {@link FormatFieldDescriptor}.
	 * @param value The Java object value to format.
	 * @param cause The cause of the exception to throw.
	 * @return The {@link FieldConversionException} to throw.
	 */
	<T> String throwFormatFieldConversionException(
			final FormatFieldDescriptor descriptor, final T value, Exception cause) {
		if (cause instanceof FieldConversionException) {
			throw (FieldConversionException) cause;
		}

		throw new FieldConversionException(
				String.format("Unable to format value [%s] for field '%s'", value, descriptor.name()),
				cause);
	}

	/**
	 * Throws a {@link FieldConversionException} during text format parsing.
	 * @param <T> The type of the Java object to create.
	 * @param descriptor The {@link FormatFieldDescriptor}.
	 * @param source The source text to parse.
	 * @param cause The cause of the exception to throw.
	 * @return The {@link FieldConversionException} to throw.
	 */
	<T> T throwParseFieldConversionException(
			final FormatFieldDescriptor descriptor, final String source, Exception cause) {
		if (cause instanceof FieldConversionException) {
			throw (FieldConversionException) cause;
		}

		throw new FieldConversionException(
				String.format("Unable to parse text [%s] for field '%s'", source, descriptor.name()),
				cause);
	}

}
