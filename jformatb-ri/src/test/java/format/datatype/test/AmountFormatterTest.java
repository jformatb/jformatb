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

import org.junit.jupiter.api.Test;

import format.bind.Formatter;
import format.datatype.Amount;

public class AmountFormatterTest {

	@Test
	void formatAmount() {
		String expected = "USD000000100000";
		String actual = Formatter.of(Amount.class)
				.withPattern("${currency:3}${value:12}")
				.format(Amount.of(Locale.US, 100000));
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void parseAmount() {
		Amount expected = Amount.of(Locale.ITALY, 100000);
		Amount actual = Formatter.of(Amount.class)
				.withPattern("${currency:3}${value:12}")
				.setListener((amount, fields) -> amount.setLocale(Locale.ITALY))
				.parse("EUR000000100000");
		assertThat(actual)
				.isEqualTo(expected)
				.returns("1.000,00", Amount::toFormattedString)
				.returns(BigDecimal.valueOf(100000, 2), Amount::toBigDecimal);
	}

}
