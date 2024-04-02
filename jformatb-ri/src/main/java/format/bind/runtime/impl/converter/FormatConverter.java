/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package format.bind.runtime.impl.converter;

import java.util.Optional;

import format.bind.FormatFieldDescriptor;
import format.bind.Formatter;
import format.bind.converter.FieldConversionException;
import format.bind.converter.FieldConverter;
import lombok.Value;

@Value(staticConstructor = "of")
class FormatConverter<T> implements FieldConverter<T> {

	private Formatter<T> formatter;

	@Override
	public String format(FormatFieldDescriptor descriptor, T value) throws FieldConversionException {
		return Optional.ofNullable(value)
				.map(formatter::format)
				.orElseGet(descriptor::placeholder);
	}

	@Override
	public T parse(FormatFieldDescriptor descriptor, String source) throws FieldConversionException {
		return source.equals(descriptor.placeholder()) ? null : formatter.parse(source);
	}

}
