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

import java.util.Comparator;
import java.util.Currency;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import format.bind.FormatFieldDescriptor;
import format.bind.annotation.FormatField.Type;
import format.bind.converter.FieldConversionException;
import format.bind.converter.FieldConverter;

final class CurrencyConverter implements FieldConverter<Currency> {

	private static Map<Integer, Currency> numericCodes = Currency.getAvailableCurrencies().stream()
			.sorted(Comparator.comparing(Currency::getNumericCode))
			.filter(currency -> currency.getNumericCode() > 0)
			.collect(Collectors.toMap(Currency::getNumericCode, Function.identity(), (u, v) -> u));

	private static Currency getInstance(int numericCode) {
		return Optional.ofNullable(numericCodes.get(numericCode))
				.orElseThrow(() -> new IllegalArgumentException(String.format("No ISO 4217 currency found with numeric code %d", numericCode)));
	}

	private static Currency getInstance(String currencyCode) {
		return Currency.getInstance(currencyCode);
	}

	@Override
	public byte[] formatBytes(FormatFieldDescriptor descriptor, Currency value) throws FieldConversionException {
		if (descriptor.type() == Type.NUMERIC) {
			return StringUtils.leftPad(Optional.ofNullable(value)
					.map(Currency::getNumericCode)
					.map(String::valueOf)
					.orElseGet(descriptor::placeholder), descriptor.length(), "0")
					.getBytes(descriptor.charset());
		} else {
			return StringUtils.rightPad(Optional.ofNullable(value)
					.map(Currency::getCurrencyCode)
					.orElseGet(descriptor::placeholder), descriptor.length())
					.getBytes(descriptor.charset());
		}
	}

	@Override
	public Currency parseBytes(FormatFieldDescriptor descriptor, byte[] source) throws FieldConversionException {
		try {
			String value = new String(source, descriptor.charset());
			if (descriptor.type() == Type.NUMERIC) {
				return getInstance(Integer.valueOf(value));
			} else {
				return getInstance(StringUtils.trimToEmpty(value));
			}
		} catch (Exception e) {
			throw FieldConverters.parseFieldConversionException(descriptor, source, e);
		}
	}

}
