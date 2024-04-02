/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
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
			return FieldConverterUtil.throwFieldConversionFormatException(descriptor, value, e);
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
			return FieldConverterUtil.throwFieldConversionParseException(fieldSpec, source, e);
		}
	}

}
