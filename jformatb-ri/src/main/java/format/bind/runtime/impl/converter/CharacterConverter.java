/*
 * Copyright 2024 jFormat-B
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
			return FieldConverterUtil.throwFormatFieldConversionException(descriptor, value, e);
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
			return FieldConverterUtil.throwParseFieldConversionException(descriptor, source, e);
		}
	}

}
