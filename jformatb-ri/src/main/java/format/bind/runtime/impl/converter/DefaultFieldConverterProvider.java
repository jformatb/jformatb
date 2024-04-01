/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package format.bind.runtime.impl.converter;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.function.Supplier;

import format.bind.Formatter;
import format.bind.converter.FieldConverter;
import format.bind.converter.FieldConverterNotFoundException;
import format.bind.converter.spi.FieldConverterProvider;

/**
 * The default {@link FieldConverterProvider} implementation.
 * 
 * @author Yannick Ebongue
 * 
 */
public class DefaultFieldConverterProvider implements FieldConverterProvider {

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
		addConverter(Date.class, DateConverter::new);
		addConverter(LocalDate.class, LocalDateConverter::new);
		addConverter(LocalTime.class, LocalTimeConverter::new);
		addConverter(LocalDateTime.class, LocalDateTimeConverter::new);
		addConverter(OffsetTime.class, OffsetTimeConverter::new);
		addConverter(OffsetDateTime.class, OffsetDateTimeConverter::new);
		addConverter(ZonedDateTime.class, ZonedDateTimeConverter::new);
	}

	/**
	 * Creates a new instance of the given converter type.
	 * 
	 * @param <C> The type of the returned {@link FieldConverter}.
	 * @param converterType The Java type of the returned {@link FieldConverter}.
	 * @return An instance of a {@link FieldConverter}.
	 * @throws IllegalArgumentException if an error occurs during creation.
	 */
	private static <C extends FieldConverter<?>> C newInstance(final Class<C> converterType) {
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
	private static <T> FieldConverter<T> addConverter(final Class<T> type, final Supplier<FieldConverter<T>> converterSupplier) {
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
	private static <T> FieldConverter<T> addConverter(final Class<T> type, final Function<Class<?>, FieldConverter<?>> converterFunction) {
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
	private static <E extends Enum<E>> FieldConverter<E> getEnumConverter(final Class<?> enumType) {
		return EnumConverter.of((Class<E>) enumType);
	}

	@Override
	public <T> FieldConverter<T> getConverter(Class<T> fieldType, Class<? extends FieldConverter<T>> converterType)
			throws FieldConverterNotFoundException {
		return addConverter(fieldType, () -> newInstance(converterType));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> FieldConverter<T> getConverter(Class<T> fieldType) throws FieldConverterNotFoundException {
		if (fieldType.isEnum()) {
			return addConverter(fieldType, DefaultFieldConverterProvider::getEnumConverter);
		}

		return (FieldConverter<T>) converters.get(fieldType);
	}

	@Override
	public <T> FieldConverter<T> getConverter(Formatter<T> formatter) throws FieldConverterNotFoundException {
		return FormatConverter.of(formatter);
	}

}
