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

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import format.bind.FormatFieldDescriptor;
import format.bind.converter.FieldConversionException;
import format.bind.converter.FieldConverter;

final class DateConverter implements FieldConverter<Date> {

	@Override
	public byte[] formatBytes(final FormatFieldDescriptor descriptor, final Date value) throws FieldConversionException {
		try {
			String str = Optional.ofNullable(value)
					.map(new SimpleDateFormat(descriptor.format(), locale(descriptor.locale()))::format)
					.orElseGet(descriptor::placeholder);
			return StringUtils.leftPad(str, descriptor.length(), "0").getBytes(descriptor.charset());
		} catch (Exception e) {
			throw FieldConverters.formatFieldConversionException(descriptor, value, e);
		}
	}

	@Override
	public Date parseBytes(final FormatFieldDescriptor descriptor, final byte[] source) throws FieldConversionException {
		try {
			if (Arrays.equals(source, descriptor.placeholder().getBytes(descriptor.charset()))) {
				return null;
			}

			return new SimpleDateFormat(descriptor.format(), locale(descriptor.locale())).parse(new String(source, descriptor.charset()));
		} catch (Exception e) {
			throw FieldConverters.parseFieldConversionException(descriptor, source, e);
		}
	}

}
