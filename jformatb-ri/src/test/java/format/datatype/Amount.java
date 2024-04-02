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
package format.datatype;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import format.bind.annotation.FormatField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Amount implements Serializable {

	private static final long serialVersionUID = 277731192675579750L;

	@FormatField
	private Currency currency;

	@FormatField
	private long value;

	@Builder.Default
	private Locale locale = Locale.ROOT;

	public static Amount of(final Locale locale, final long value) {
		return Amount.builder()
				.currency(Currency.getInstance(locale))
				.value(value)
				.locale(locale)
				.build();
	}

	public static Amount of(final String currencyCode, final long value) {
		return Amount.builder()
				.currency(Currency.getInstance(currencyCode))
				.value(value)
				.build();
	}

	public BigDecimal toBigDecimal() {
		return BigDecimal.valueOf(value, currency.getDefaultFractionDigits());
	}

	@Override
	public String toString() {
		NumberFormat format = NumberFormat.getCurrencyInstance(locale);
		format.setCurrency(currency);
		return format.format(toBigDecimal());
	}

}
