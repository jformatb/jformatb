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

import java.util.Currency;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import format.bind.FormatFieldDescriptor;
import format.bind.converter.FieldConversionException;
import format.bind.converter.FieldConverter;

final class CurrencyConverter implements FieldConverter<Currency> {

	@Override
	public String format(FormatFieldDescriptor descriptor, Currency value) throws FieldConversionException {
		try {
			return StringUtils.rightPad(Optional.ofNullable(value)
					.map(Currency::toString)
					.orElseGet(descriptor::placeholder), descriptor.length());
		} catch (Exception e) {
			throw FieldConverters.formatFieldConversionException(descriptor, value, e);
		}
	}

	@Override
	public Currency parse(FormatFieldDescriptor descriptor, String source) throws FieldConversionException {
		try {
			return Currency.getInstance(StringUtils.trimToEmpty(source));
		} catch (Exception e) {
			throw FieldConverters.parseFieldConversionException(descriptor, source, e);
		}
	}

}
