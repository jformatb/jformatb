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

import format.bind.FormatFieldSpec;
import format.bind.converter.FieldConversionException;
import format.bind.converter.FieldConverter;

abstract class TemporalAccessorConverter<T extends TemporalAccessor> implements FieldConverter<T> {

	@Override
	public String format(final FormatFieldSpec fieldSpec, final T value) throws FieldConversionException {
		try {
			String str = Optional.ofNullable(value)
					.map(DateTimeFormatter.ofPattern(fieldSpec.format())::format)
					.orElseGet(fieldSpec::placeholder);
			return StringUtils.leftPad(str, fieldSpec.length(), "0");
		} catch (Exception e) {
			return FieldConverterUtil.throwFieldConversionFormatException(fieldSpec, value, e);
		}
	}

	@Override
	public T parse(final FormatFieldSpec fieldSpec, final String source) throws FieldConversionException {
		try {
			if (source.equals(fieldSpec.placeholder())) {
				return null;
			}

			return DateTimeFormatter.ofPattern(fieldSpec.format()).parse(source, query());
		} catch (Exception e) {
			return FieldConverterUtil.throwFieldConversionParseException(fieldSpec, source, e);
		}
	}

	protected abstract TemporalQuery<T> query();

}
