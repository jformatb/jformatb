/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package format.bind.runtime.converter;

import java.util.Optional;

import format.bind.Formatter;
import format.bind.annotation.FormatField;
import format.bind.converter.FieldConverter;
import lombok.Value;

@Value(staticConstructor = "of")
class FormatConverter<T> implements FieldConverter<T> {

	private Formatter<T> formatter;

	@Override
	public String format(FormatField field, T value) {
		return Optional.ofNullable(value)
				.map(formatter::format)
				.orElseGet(field::placeholder);
	}

	@Override
	public T parse(FormatField field, String source) {
		return source.equals(field.placeholder()) ? null : formatter.parse(source);
	}

}
