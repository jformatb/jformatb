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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.Comparator;
import java.util.Currency;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import format.bind.FormatFieldDescriptor;
import format.bind.annotation.FormatField.Type;
import format.bind.converter.FieldConversionException;

class CurrencyConverterTest extends AbstractConverterTest<Currency> {

	CurrencyConverterTest() {
		super(Currency.class);
	}

	@Test
	void formatCurrency() {
		FormatFieldDescriptor descriptor = fieldDescriptorBuilder()
				.length(3)
				.locale("en-US")
				.build();
		Currency currency = Currency.getInstance(Locale.forLanguageTag(descriptor.locale()));
		String actual = converter.format(descriptor, currency);
		assertThat(actual).isEqualTo("USD");
	}

	@Test
	void parseCurrency() {
		FormatFieldDescriptor descriptor = fieldDescriptorBuilder()
				.length(3)
				.build();
		Currency expected = Currency.getInstance(Locale.FRANCE);
		Currency actual = converter.parse(descriptor, StringUtils.rightPad(expected.getCurrencyCode(), descriptor.length()));
		assertThat(actual).usingComparator(Comparator.comparing(Currency::getCurrencyCode)).isEqualTo(expected);
	}

	@Test
	void formatCurrencyNumericCode() {
		FormatFieldDescriptor descriptor = fieldDescriptorBuilder()
				.length(3)
				.type(Type.NUMERIC)
				.locale("en-US")
				.build();
		Currency currency = Currency.getInstance(Locale.forLanguageTag(descriptor.locale()));
		String actual = converter.format(descriptor, currency);
		assertThat(actual).isEqualTo("840");
	}

	@Test
	void parseCurrencyNumericCode() {
		FormatFieldDescriptor descriptor = fieldDescriptorBuilder()
				.length(3)
				.type(Type.NUMERIC)
				.build();
		Currency expected = Currency.getInstance(Locale.UK);
		Currency actual = converter.parse(descriptor, StringUtils.leftPad(String.valueOf(expected.getNumericCode()), descriptor.length(), "0"));
		assertThat(actual).usingComparator(Comparator.comparing(Currency::getNumericCode)).isEqualTo(expected);
	}

	@Test
	void parseInvalidCurrencyCode() {
		FormatFieldDescriptor descriptor = fieldDescriptorBuilder()
				.length(3)
				.build();
		assertThatExceptionOfType(FieldConversionException.class)
				.isThrownBy(() -> converter.parse(descriptor, "AAA"))
				.withCauseInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void parseInvalidNumericCode() {
		FormatFieldDescriptor descriptor = fieldDescriptorBuilder()
				.length(3)
				.type(Type.NUMERIC)
				.build();
		assertThatExceptionOfType(FieldConversionException.class)
				.isThrownBy(() -> converter.parse(descriptor, "001"))
				.withCauseInstanceOf(IllegalArgumentException.class);
	}

}
