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

import java.time.ZoneId;
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
	default String format(final FormatFieldDescriptor descriptor, final T value) throws FieldConversionException {
		return new String(formatBytes(descriptor, value), descriptor.charset());
	}

	/**
	 * Converts a bound type value to a byte array format value.
	 * 
	 * @param descriptor The text format field descriptor.
	 * @param value The Java value to be converted. Can be null.
	 * @return The formatted byte array value.
	 * @throws FieldConversionException if there is an error during the conversion.
	 */
	byte[] formatBytes(final FormatFieldDescriptor descriptor, final T value) throws FieldConversionException;

	/**
	 * Converts a text format value to a bound type value.
	 * 
	 * @param descriptor The text format field descriptor.
	 * @param source The text format value to be converted. Cannot be empty, but
	 * 		can be blank.
	 * @return The bound type value.
	 * @throws FieldConversionException if there is an error during the conversion.
	 */
	default T parse(final FormatFieldDescriptor descriptor, final String source) throws FieldConversionException {
		return parseBytes(descriptor, source.getBytes(descriptor.charset()));
	}

	/**
	 * Converts a text format value to a bound type value.
	 * 
	 * @param descriptor The text format field descriptor.
	 * @param source The byte array format value to be converted. Cannot be empty.
	 * @return The bound type value.
	 * @throws FieldConversionException if there is an error during the conversion.
	 */
	T parseBytes(final FormatFieldDescriptor descriptor, final byte[] source) throws FieldConversionException;

	/**
	 * Returns the {@link Locale} of the given language tag if specified.
	 * 
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
	 * Returns the {@link ZoneId} of the given value if specified.
	 * 
	 * @param zoneId The ID of the {@link ZoneId}.
	 * @return The {@link ZoneId} instance.
	 */
	default ZoneId zone(final String zoneId) {
		return Optional.ofNullable(zoneId)
				.filter(value -> !value.isEmpty())
				.map(ZoneId::of)
				.orElseGet(this::defaultZone);
	}

	/**
	 * Returns the converter default {@link ZoneId}.
	 * 
	 * @return The default {@link ZoneId}.
	 */
	default ZoneId defaultZone() {
		return ZoneId.systemDefault();
	}

	/**
	 * Obtain the current {@link FieldConverter} service provider.
	 * 
	 * @return The {@link FieldConverter} service provider instance.
	 * @throws FormatException if no {@link FieldConverterProvider}
	 * 		implementation was found.
	 */
	static FieldConverterProvider provider() {
		return Providers.getInstance().getFieldConverterProvider();
	}

}
