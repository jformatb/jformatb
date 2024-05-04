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
package com.example.formatter;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Locale;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import com.example.datatype.Amount;

import format.bind.Formatter;

class AmountFormatterTest {

	@ParameterizedTest(name = "formatAmountOf({0})")
	@CsvFileSource(resources = "/amounts.csv", numLinesToSkip = 1)
	void formatAmount(String languageTag, String value, String text) {
		String expected = text;
		String actual = Formatter.of(Amount.class)
				.withPattern("${currency:3}${value:12}")
				.format(Amount.of(Locale.forLanguageTag(languageTag), Long.parseLong(value)));
		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest(name = "parseAmountOf({0})")
	@CsvFileSource(resources = "/amounts.csv", numLinesToSkip = 1)
	void parseAmount(String languageTag, String value, String text, String str) {
		Locale locale = Locale.forLanguageTag(languageTag);
		Amount expected = Amount.of(locale, Long.parseLong(value));
		Amount actual = Formatter.of(Amount.class)
				.withPattern("${currency:3}${value:12}")
				.parse(text);
		assertThat(actual)
				.isEqualTo(expected)
				.hasToString(str)
				.returns(new BigDecimal(str.substring(4).replaceAll(",", "")), Amount::toBigDecimal);
	}

}
