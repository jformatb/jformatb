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

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;
import java.util.Arrays;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import format.bind.FormatFieldDescriptor;
import format.bind.converter.FieldConversionException;
import format.bind.converter.FieldConverter;

abstract class TemporalAccessorConverter<T extends TemporalAccessor> implements FieldConverter<T> {

	protected abstract TemporalQuery<T> query();

	protected DateTimeFormatter getFormatter(FormatFieldDescriptor descriptor) {
		return DateTimeFormatter.ofPattern(descriptor.format())
				.withLocale(locale(descriptor.locale()))
				.withZone(zone(descriptor.zone()));
	}

	@Override
	public byte[] formatBytes(final FormatFieldDescriptor descriptor, final T value) throws FieldConversionException {
		try {
			String str = Optional.ofNullable(value)
					.map(getFormatter(descriptor)::format)
					.orElseGet(descriptor::placeholder);
			return StringUtils.leftPad(str, descriptor.length(), "0").getBytes(descriptor.charset());
		} catch (Exception e) {
			throw FieldConverters.formatFieldConversionException(descriptor, value, e);
		}
	}

	@Override
	public T parseBytes(final FormatFieldDescriptor descriptor, final byte[] source) throws FieldConversionException {
		try {
			if (Arrays.equals(source, descriptor.placeholder().getBytes(descriptor.charset()))) {
				return null;
			}

			return getFormatter(descriptor).parse(new String(source, descriptor.charset()), query());
		} catch (Exception e) {
			throw FieldConverters.parseFieldConversionException(descriptor, source, e);
		}
	}

}
