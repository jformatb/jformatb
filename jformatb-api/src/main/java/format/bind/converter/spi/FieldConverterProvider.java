/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
/**
 * 
 */
package format.bind.converter.spi;

import java.util.Iterator;
import java.util.ServiceLoader;

import format.bind.Formatter;
import format.bind.converter.FieldConverter;
import format.bind.converter.FieldConverterNotFoundException;

/**
 * Service Provider Interface for {@link FieldConverter}.
 * 
 * @author Yannick Ebongue
 * 
 */
public interface FieldConverterProvider {

	/**
	 * Obtain the default {@link FieldConverter} service provider.
	 * 
	 * @return The {@link FieldConverter} service provider instance.
	 */
	static FieldConverterProvider provider() {
		return provider("format.bind.runtime.converter.DefaultFieldConverterProvider");
	}

	/**
	 * Obtain the {@link FieldConverter} service provider of the specified class name.
	 * 
	 * @param className The full qualified name of the class to lookup.
	 * @return The {@link FieldConverter} service provider instance.
	 */
	static FieldConverterProvider provider(String className) {
		ServiceLoader<FieldConverterProvider> loader = ServiceLoader.load(FieldConverterProvider.class);
		Iterator<FieldConverterProvider> it = loader.iterator();

		while (it.hasNext()) {
			FieldConverterProvider next = it.next();

			if (next.getClass().getName().equals(className)) {
				return next;
			}
		}

		return null;
	}

	/**
	 * Obtain the {@link FieldConverter} of the specified field type and converter type.
	 * @param <T> The Java type of the field.
	 * @param fieldType The class instance of the field Java type.
	 * @param converterType The class instance of the field converter to obtain.
	 * @return The field converter instance.
	 * @throws FieldConverterNotFoundException if no field converter found for the
	 * 		specified field Java type.
	 */
	<T> FieldConverter<T> getConverter(final Class<T> fieldType,
			Class<? extends FieldConverter<T>> converterType) throws FieldConverterNotFoundException;

	/**
	 * Obtain the {@link FieldConverter} of the specified field type.
	 * @param <T> The Java type of the field.
	 * @param fieldType The class instance of the field Java type.
	 * @return The field converter instance.
	 * @throws FieldConverterNotFoundException if no field converter found for the
	 * 		specified field Java type.
	 */
	<T> FieldConverter<T> getConverter(final Class<T> fieldType) throws FieldConverterNotFoundException;

	/**
	 * Obtain the {@link FieldConverter} of the specified {@code formatter}.
	 * @param <T> The type of the {@link Formatter} type.
	 * @param formatter The formatter
	 * @return The field converter instance.
	 * @throws FieldConverterNotFoundException if no field converter found for the
	 * 		specified {@code formatter}.
	 */
	<T> FieldConverter<T> getConverter(final Formatter<T> formatter) throws FieldConverterNotFoundException;

}
