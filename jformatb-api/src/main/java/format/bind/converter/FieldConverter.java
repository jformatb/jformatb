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
package format.bind.converter;

import java.util.Iterator;
import java.util.ServiceLoader;

import format.bind.FormatFieldDescriptor;
import format.bind.annotation.FormatFieldConverter;
import format.bind.converter.spi.FieldConverterProvider;

/**
 * Converts a text format field to Java type or vice versa.
 * 
 * @param <T> The Java type to be converted.
 * 
 * @author Yannick Ebongue
 * 
 * @see FormatFieldConverter
 * 
 */
public interface FieldConverter<T> {

	/**
	 * Converts a bound type value to a text format value.
	 * 
	 * @param descriptor The text format field descriptor.
	 * @param value The Java value to be converted. Can be null.
	 * @return The formatted text value.
	 * @throws FieldConversionException if there is an error during the conversion.
	 */
	String format(final FormatFieldDescriptor descriptor, final T value) throws FieldConversionException;

	/**
	 * Converts a text format value to a bound type value.
	 * 
	 * @param descriptor The text format field descriptor.
	 * @param source The text format value to be converted. Cannot be empty, but
	 * 		can be blank.
	 * @return The bound type value.
	 * @throws FieldConversionException if there is an error during the conversion.
	 */
	T parse(final FormatFieldDescriptor descriptor, final String source) throws FieldConversionException;

	/**
	 * Obtain the default {@link FieldConverter} service provider.
	 * 
	 * @return The {@link FieldConverter} service provider instance.
	 */
	static FieldConverterProvider provider() {
		return provider("format.bind.runtime.impl.converter.DefaultFieldConverterProvider");
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

}
