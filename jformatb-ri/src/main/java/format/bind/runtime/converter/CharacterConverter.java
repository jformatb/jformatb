/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package format.bind.runtime.converter;

import java.util.Optional;

import format.bind.annotation.FormatField;
import format.bind.converter.FieldConverter;
import format.bind.converter.spi.FieldConverterProvider;

class CharacterConverter implements FieldConverter<Character> {

	@Override
	public String format(FormatField field, Character value) {
		return FieldConverterProvider.provider()
				.getConverter(String.class)
				.format(field, String.valueOf(value.charValue()));
	}

	@Override
	public Character parse(FormatField field, String source) {
		String value = FieldConverterProvider.provider()
				.getConverter(String.class)
				.parse(field, source);
		return Optional.ofNullable(value)
				.map(str -> str.charAt(0))
				.orElse(null);
	}

}
