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

import format.bind.FormatFieldSpec;
import format.bind.converter.FieldConverter;

final class DateConverter implements FieldConverter<Date> {

	@Override
	public String format(final FormatFieldSpec fieldSpec, final Date value) {
		String str = Optional.ofNullable(value)
				.map(new SimpleDateFormat(fieldSpec.format())::format)
				.orElseGet(fieldSpec::placeholder);
		return StringUtils.leftPad(str, fieldSpec.length(), "0");
	}

	@Override
	public Date parse(final FormatFieldSpec fieldSpec, final String source) {
		if (source.equals(fieldSpec.placeholder())) {
			return null;
		}

		try {
			return new SimpleDateFormat(fieldSpec.format()).parse(source);
		} catch (ParseException e) {
			throw new IllegalArgumentException(e);
		}
	}

}
