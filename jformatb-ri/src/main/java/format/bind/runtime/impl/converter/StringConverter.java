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
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import format.bind.FormatFieldDescriptor;
import format.bind.FormatFieldDescriptor.Type;
import format.bind.converter.FieldConversionException;
import format.bind.converter.FieldConverter;

final class StringConverter implements FieldConverter<String> {

	@Override
	public String format(final FormatFieldDescriptor descriptor, final String value) throws FieldConversionException {
		try {
			if (descriptor.type() == Type.NUMERIC) {
				String str = Optional.ofNullable(value)
						.orElse(StringUtils.defaultIfBlank(descriptor.placeholder(), "0"));
				return StringUtils.leftPad(String.valueOf(Long.parseLong(str)), descriptor.length(), "0");
			} else {
				return StringUtils.rightPad(StringUtils.defaultString(value, descriptor.placeholder()), descriptor.length());
			}
		} catch (Exception e) {
			return FieldConverterUtil.throwFormatFieldConversionException(descriptor, value, e);
		}
	}

	@Override
	public String parse(final FormatFieldDescriptor fieldSpec, final String source) throws FieldConversionException {
		try {
			if (fieldSpec.type() == Type.NUMERIC) {
				long value = Long.parseLong(source);
				if (fieldSpec.placeholder().equals(String.valueOf(value))) {
					return null;
				} else {
					return new DecimalFormat(StringUtils.defaultIfBlank(fieldSpec.format(), "0")).format(value);
				}
			} else {
				return StringUtils.trimToNull((source.equals(fieldSpec.placeholder()) ? "" : source));
			}
		} catch (Exception e) {
			return FieldConverterUtil.throwParseFieldConversionException(fieldSpec, source, e);
		}
	}

}
