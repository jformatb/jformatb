/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package format.bind.runtime.impl.converter;

import java.util.Optional;

import format.bind.FormatFieldDescriptor;
import format.bind.converter.FieldConversionException;
import format.bind.converter.FieldConverter;
import format.bind.converter.spi.FieldConverterProvider;

class CharacterConverter implements FieldConverter<Character> {

	@Override
	public String format(FormatFieldDescriptor descriptor, Character value) throws FieldConversionException {
		try {
			return FieldConverterProvider.provider()
					.getConverter(String.class)
					.format(descriptor, String.valueOf(value.charValue()));
		} catch (Exception e) {
			return FieldConverterUtil.throwFieldConversionFormatException(descriptor, value, e);
		}
	}

	@Override
	public Character parse(FormatFieldDescriptor descriptor, String source) throws FieldConversionException {
		try {
			String value = FieldConverterProvider.provider()
					.getConverter(String.class)
					.parse(descriptor, source);
			return Optional.ofNullable(value)
					.map(str -> str.charAt(0))
					.orElse(null);
		} catch (Exception e) {
			return FieldConverterUtil.throwFieldConversionParseException(descriptor, source, e);
		}
	}

}
