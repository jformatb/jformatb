/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package format.bind.runtime.converter;

import java.util.Optional;

import format.bind.FormatFieldSpec;
import format.bind.Formatter;
import format.bind.converter.FieldConversionException;
import format.bind.converter.FieldConverter;
import lombok.Value;

@Value(staticConstructor = "of")
class FormatConverter<T> implements FieldConverter<T> {

	private Formatter<T> formatter;

	@Override
	public String format(FormatFieldSpec fieldSpec, T value) throws FieldConversionException {
		return Optional.ofNullable(value)
				.map(formatter::format)
				.orElseGet(fieldSpec::placeholder);
	}

	@Override
	public T parse(FormatFieldSpec fieldSpec, String source) throws FieldConversionException {
		return source.equals(fieldSpec.placeholder()) ? null : formatter.parse(source);
	}

}
