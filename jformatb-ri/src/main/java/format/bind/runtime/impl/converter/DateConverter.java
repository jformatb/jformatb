/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package format.bind.runtime.impl.converter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import format.bind.FormatFieldDescriptor;
import format.bind.converter.FieldConversionException;
import format.bind.converter.FieldConverter;

final class DateConverter implements FieldConverter<Date> {

	@Override
	public String format(final FormatFieldDescriptor descriptor, final Date value) throws FieldConversionException {
		try {
			String str = Optional.ofNullable(value)
					.map(new SimpleDateFormat(descriptor.format())::format)
					.orElseGet(descriptor::placeholder);
			return StringUtils.leftPad(str, descriptor.length(), "0");
		} catch (Exception e) {
			return FieldConverterUtil.throwFieldConversionFormatException(descriptor, value, e);
		}
	}

	@Override
	public Date parse(final FormatFieldDescriptor fieldSpec, final String source) throws FieldConversionException {
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
