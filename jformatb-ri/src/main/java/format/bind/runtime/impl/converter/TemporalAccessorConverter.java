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
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import format.bind.FormatFieldDescriptor;
import format.bind.converter.FieldConversionException;
import format.bind.converter.FieldConverter;

abstract class TemporalAccessorConverter<T extends TemporalAccessor> implements FieldConverter<T> {

	protected abstract TemporalQuery<T> query();

	protected DateTimeFormatter getFormatter(FormatFieldDescriptor descriptor) {
		return DateTimeFormatter.ofPattern(descriptor.format(), locale(descriptor.locale()));
	}

	@Override
	public String format(final FormatFieldDescriptor descriptor, final T value) throws FieldConversionException {
		try {
			String str = Optional.ofNullable(value)
					.map(getFormatter(descriptor)::format)
					.orElseGet(descriptor::placeholder);
			return StringUtils.leftPad(str, descriptor.length(), "0");
		} catch (Exception e) {
			return FieldConverters.throwFormatFieldConversionException(descriptor, value, e);
		}
	}

	@Override
	public T parse(final FormatFieldDescriptor descriptor, final String source) throws FieldConversionException {
		try {
			if (source.equals(descriptor.placeholder())) {
				return null;
			}

			return getFormatter(descriptor).parse(source, query());
		} catch (Exception e) {
			return FieldConverters.throwParseFieldConversionException(descriptor, source, e);
		}
	}

}
