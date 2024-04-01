/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package format.bind.runtime.impl.converter;

import java.util.Optional;

import format.bind.FormatFieldSpec;
import format.bind.converter.FieldConversionException;
import format.bind.converter.FieldConverter;
import format.bind.converter.spi.FieldConverterProvider;

class CharacterConverter implements FieldConverter<Character> {

	@Override
	public String format(FormatFieldSpec fieldSpec, Character value) throws FieldConversionException {
		try {
			return FieldConverterProvider.provider()
					.getConverter(String.class)
					.format(fieldSpec, String.valueOf(value.charValue()));
		} catch (Exception e) {
			return FieldConverterUtil.throwFieldConversionFormatException(fieldSpec, value, e);
		}
	}

	@Override
	public Character parse(FormatFieldSpec fieldSpec, String source) throws FieldConversionException {
		try {
			String value = FieldConverterProvider.provider()
					.getConverter(String.class)
					.parse(fieldSpec, source);
			return Optional.ofNullable(value)
					.map(str -> str.charAt(0))
					.orElse(null);
		} catch (Exception e) {
			return FieldConverterUtil.throwFieldConversionParseException(fieldSpec, source, e);
		}
	}

}
