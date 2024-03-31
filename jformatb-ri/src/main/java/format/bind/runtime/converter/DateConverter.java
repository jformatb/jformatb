/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package format.bind.runtime.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import format.bind.annotation.FormatField;
import format.bind.converter.FieldConverter;

final class DateConverter implements FieldConverter<Date> {

	@Override
	public String format(final FormatField field, final Date value) {
		String str = Optional.ofNullable(value)
				.map(new SimpleDateFormat(field.format())::format)
				.orElseGet(field::placeholder);
		return StringUtils.leftPad(str, field.length(), "0");
	}

	@Override
	public Date parse(final FormatField field, final String source) {
		if (source.equals(field.placeholder())) {
			return null;
		}

		try {
			return new SimpleDateFormat(field.format()).parse(source);
		} catch (ParseException e) {
			throw new IllegalArgumentException(e);
		}
	}

}
