/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package format.bind.runtime.converter;

import java.util.Optional;

import format.bind.FormatFieldSpec;
import format.bind.converter.FieldConverter;
import format.bind.converter.spi.FieldConverterProvider;

class CharacterConverter implements FieldConverter<Character> {

	@Override
	public String format(FormatFieldSpec fieldSpec, Character value) {
		return FieldConverterProvider.provider()
				.getConverter(String.class)
				.format(fieldSpec, String.valueOf(value.charValue()));
	}

	@Override
	public Character parse(FormatFieldSpec fieldSpec, String source) {
		String value = FieldConverterProvider.provider()
				.getConverter(String.class)
				.parse(fieldSpec, source);
		return Optional.ofNullable(value)
				.map(str -> str.charAt(0))
				.orElse(null);
	}

}
