/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package format.bind.runtime.converter;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import format.bind.annotation.FormatField;
import format.bind.converter.FieldConverter;

abstract class TemporalAccessorConverter<T extends TemporalAccessor> implements FieldConverter<T> {

	@Override
	public String format(final FormatField field, final T value) {
		String str = Optional.ofNullable(value)
				.map(DateTimeFormatter.ofPattern(field.format())::format)
				.orElseGet(field::placeholder);
		return StringUtils.leftPad(str, field.length(), "0");
	}

	@Override
	public T parse(final FormatField field, final String source) {
		if (source.equals(field.placeholder())) {
			return null;
		}

		return DateTimeFormatter.ofPattern(field.format()).parse(source, query());
	}

	protected abstract TemporalQuery<T> query();

}
