/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package format.bind.runtime.converter;

import java.text.DecimalFormat;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import format.bind.annotation.FormatField;
import format.bind.annotation.FormatField.Type;
import format.bind.converter.FieldConverter;

final class StringConverter implements FieldConverter<String> {

	@Override
	public String format(final FormatField field, final String value) {
		if (field.type() == Type.NUMERIC) {
			String str = Optional.ofNullable(value)
					.orElse(StringUtils.defaultIfBlank(field.placeholder(), "0"));
			return StringUtils.leftPad(String.valueOf(Long.parseLong(str)), field.length(), "0");
		} else {
			return StringUtils.rightPad(StringUtils.defaultString(value, field.placeholder()), field.length());
		}
	}

	@Override
	public String parse(final FormatField field, final String source) {
		if (field.type() == Type.NUMERIC) {
			long value = Long.parseLong(source);
			if (field.placeholder().equals(String.valueOf(value))) {
				return null;
			} else {
				return new DecimalFormat(StringUtils.defaultIfBlank(field.format(), "0")).format(value);
			}
		} else {
			return StringUtils.trimToNull((source.equals(field.placeholder()) ? "" : source));
		}
	}

}
