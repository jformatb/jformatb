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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import format.bind.annotation.FormatAccess;
import format.bind.annotation.FormatAccess.Type;
import format.bind.annotation.FormatField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter(onMethod_ = @FormatField)
@FormatAccess(Type.PROPERTY)
public class Amount implements Serializable {

	private static final long serialVersionUID = 277731192675579750L;

	private Currency currency;

	private long value;

	public static Amount of(final String language, final String country, final long value) {
		return Amount.of(Locale.forLanguageTag(String.join("-", language, country)), value);
	}

	public static Amount of(final String currencyCode, final long value) {
		return Amount.of(Currency.getInstance(currencyCode), value);
	}

	public static Amount of(final Locale locale, final long value) {
		return Amount.of(Currency.getInstance(locale), value);
	}

	public static Amount of(final Currency currency, final long value) {
		return Amount.builder()
				.currency(currency)
				.value(value)
				.build();
	}

	public BigDecimal toBigDecimal() {
		return BigDecimal.valueOf(value, currency.getDefaultFractionDigits());
	}

	@Override
	public String toString() {
		return toFormattedString();
	}

	public String toFormattedString() {
		int fractionDigits = currency.getDefaultFractionDigits();
		String pattern = "¤¤ #,##0" + (fractionDigits > 0 ? ("." + StringUtils.rightPad("", fractionDigits, "0")) : "");
		DecimalFormat format = new DecimalFormat(pattern, DecimalFormatSymbols.getInstance(Locale.ROOT));
		format.setCurrency(currency);
		return format.format(toBigDecimal());
	}

	public String toLocaleString(Locale locale) {
		NumberFormat format = NumberFormat.getCurrencyInstance(locale);
		format.setCurrency(currency);
		return format.format(toBigDecimal());
	}

}
