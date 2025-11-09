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

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import format.bind.FormatFieldDescriptor;
import format.bind.annotation.FormatField.Type;
import format.bind.converter.FieldConversionException;
import format.bind.converter.FieldConverter;

final class StringConverter implements FieldConverter<String> {

	@Override
	public byte[] formatBytes(final FormatFieldDescriptor descriptor, final String value) throws FieldConversionException {
		try {
			if (descriptor.type() == Type.NUMERIC) {
				String str = Optional.ofNullable(value)
						.orElse(StringUtils.defaultIfBlank(descriptor.placeholder(), "0"));
				return StringUtils.leftPad(String.valueOf(Long.parseLong(str)), descriptor.length(), "0").getBytes(descriptor.charset());
			} else {
				return StringUtils.rightPad(StringUtils.defaultString(value, descriptor.placeholder()), descriptor.length()).getBytes(descriptor.charset());
			}
		} catch (Exception e) {
			throw FieldConverters.formatFieldConversionException(descriptor, value, e);
		}
	}

	@Override
	public String parseBytes(final FormatFieldDescriptor descriptor, final byte[] source) throws FieldConversionException {
		try {
			if (descriptor.type() == Type.NUMERIC) {
				long value = Long.parseLong(new String(source, descriptor.charset()));
				if (StringUtils.defaultIfBlank(descriptor.placeholder(), "0").equals(String.valueOf(value))) {
					return null;
				} else {
					return new DecimalFormat(StringUtils.defaultIfBlank(descriptor.format(), StringUtils.leftPad("", descriptor.length(), "0"))).format(value);
				}
			} else {
				return StringUtils.trimToNull((Arrays.equals(source, descriptor.placeholder().getBytes(descriptor.charset()))) ? "" : new String(source, descriptor.charset()));
			}
		} catch (Exception e) {
			throw FieldConverters.parseFieldConversionException(descriptor, source, e);
		}
	}

}
