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

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.Optional;

import format.bind.FormatException;
import format.bind.FormatFieldDescriptor;
import format.bind.Providers;
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
	 * Converts a bound type value to a byte array format value.
	 * 
	 * @param descriptor The text format field descriptor.
	 * @param value The Java value to be converted. Can be null.
	 * @return The formatted byte array value.
	 * @throws FieldConversionException if there is an error during the conversion.
	 * @throws UnsupportedEncodingException if the descriptor charset is not supported.
	 */
	default byte[] formatBytes(final FormatFieldDescriptor descriptor, final T value) throws FieldConversionException, UnsupportedEncodingException {
		return format(descriptor, value).getBytes(descriptor.charset());
	}

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
	 * Converts a text format value to a bound type value.
	 * 
	 * @param descriptor The text format field descriptor.
	 * @param source The byte array format value to be converted. Cannot be empty.
	 * @return The bound type value.
	 * @throws FieldConversionException if there is an error during the conversion.
	 * @throws UnsupportedEncodingException if the descriptor charset is not supported.
	 */
	default T parseBytes(final FormatFieldDescriptor descriptor, final byte[] source) throws FieldConversionException, UnsupportedEncodingException {
		return parse(descriptor, new String(source, descriptor.charset()));
	}

	/**
	 * Returns the {@link Locale} of the given language tag
	 * @param languageTag The language tag of the {@link Locale}.
	 * @return The {@link Locale} instance.
	 */
	default Locale locale(final String languageTag) {
		return Optional.ofNullable(languageTag.trim())
				.filter(tag -> !tag.isEmpty())
				.map(Locale::forLanguageTag)
				.orElseGet(Locale::getDefault);
	}

	/**
	 * Obtain the current {@link FieldConverter} service provider.
	 * @return The {@link FieldConverter} service provider instance.
	 * @throws FormatException if no {@link FieldConverterProvider}
	 * 		implementation was found.
	 */
	static FieldConverterProvider provider() {
		return Providers.getInstance().getFieldConverterProvider();
	}

}
