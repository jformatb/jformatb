/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package format.bind.runtime.impl.converter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import format.bind.FormatFieldSpec;
import format.bind.converter.FieldConversionException;
import format.bind.converter.FieldConverter;

final class DateConverter implements FieldConverter<Date> {

	@Override
	public String format(final FormatFieldSpec fieldSpec, final Date value) throws FieldConversionException {
		try {
			String str = Optional.ofNullable(value)
					.map(new SimpleDateFormat(fieldSpec.format())::format)
					.orElseGet(fieldSpec::placeholder);
			return StringUtils.leftPad(str, fieldSpec.length(), "0");
		} catch (Exception e) {
			return FieldConverterUtil.throwFieldConversionFormatException(fieldSpec, value, e);
		}
	}

	@Override
	public Date parse(final FormatFieldSpec fieldSpec, final String source) throws FieldConversionException {
		try {
			if (source.equals(fieldSpec.placeholder())) {
				return null;
			}

			return new SimpleDateFormat(fieldSpec.format()).parse(source);
		} catch (Exception e) {
			return FieldConverterUtil.throwFieldConversionParseException(fieldSpec, source, e);
		}
	}

}
