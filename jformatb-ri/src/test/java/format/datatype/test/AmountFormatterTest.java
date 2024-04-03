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
package format.datatype.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Locale;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import format.bind.Formatter;
import format.datatype.Amount;

public class AmountFormatterTest {

	@ParameterizedTest
	@CsvFileSource(resources = "/amounts.csv", numLinesToSkip = 1)
	void formatAmount(String languageTag, String source, String text) {
		String expected = text;
		String actual = Formatter.of(Amount.class)
				.withPattern("${currency:3}${value:12}")
				.format(Amount.of(Locale.forLanguageTag(languageTag), Long.parseLong(source)));
		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvFileSource(resources = "/amounts.csv", numLinesToSkip = 1)
	void parseAmount(String languageTag, String source, String text, String value) {
		Locale locale = Locale.forLanguageTag(languageTag);
		Amount expected = Amount.of(locale, Long.parseLong(source));
		Amount actual = Formatter.of(Amount.class)
				.withPattern("${currency:3}${value:12}")
				.parse(text);
		assertThat(actual)
				.isEqualTo(expected)
				.hasToString(value)
				.returns(new BigDecimal(value.substring(4).replaceAll(",", "")), Amount::toBigDecimal);
	}

}
