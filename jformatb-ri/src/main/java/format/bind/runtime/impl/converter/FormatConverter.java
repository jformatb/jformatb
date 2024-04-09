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

import java.util.Optional;

import format.bind.FormatFieldDescriptor;
import format.bind.Formatter;
import format.bind.converter.FieldConversionException;
import format.bind.converter.FieldConverter;

final class FormatConverter<T> implements FieldConverter<T> {

	private final Formatter<T> formatter;

	private FormatConverter(final Formatter<T> formatter) {
		this.formatter = formatter;
	}

	public static <T> FormatConverter<T> of(final Formatter<T> formatter) {
		return new FormatConverter<>(formatter);
	}

	@Override
	public String format(final FormatFieldDescriptor descriptor, final T value) throws FieldConversionException {
		return Optional.ofNullable(value)
				.map(formatter::format)
				.orElseGet(descriptor::placeholder);
	}

	@Override
	public T parse(final FormatFieldDescriptor descriptor, final String source) throws FieldConversionException {
		return source.equals(descriptor.placeholder()) ? null : formatter.parse(source);
	}

}
