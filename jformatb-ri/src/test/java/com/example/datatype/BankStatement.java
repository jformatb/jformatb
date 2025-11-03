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
package com.example.datatype;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;

import format.bind.FormatFieldDescriptor;
import format.bind.annotation.Format;
import format.bind.annotation.FormatField;
import format.bind.annotation.FormatFieldConverter;
import format.bind.converter.FieldConversionException;
import format.bind.converter.FieldConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Format(pattern = "${transactions[0..*]:45}")
public class BankStatement implements Serializable {

	private static final long serialVersionUID = 575289698350954076L;

	@FormatField
	@Singular
	private List<Transaction> transactions;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@Format(pattern = "${entryDate:6}${valueDate:6}${description:20}${amount:13}")
	public static class Transaction implements Serializable {

		private static final long serialVersionUID = -2157455392448467482L;

		@FormatField(format = "ddMMyy")
		@CsvBindByName(column = "entry_date", required = true)
		@CsvDate("dd/MM/yyyy")
		private LocalDate entryDate;

		@FormatField(format = "ddMMyy")
		@CsvBindByName(column = "value_date", required = true)
		@CsvDate("dd/MM/yyyy")
		private LocalDate valueDate;

		@FormatField
		@CsvBindByName(required = true)
		private String description;

		@FormatField(scale = 2)
		@FormatFieldConverter(AmountConverter.class)
		@CsvBindByName(required = true)
		private BigDecimal amount;

	}

	public static class AmountConverter implements FieldConverter<BigDecimal> {

		private final FieldConverter<BigDecimal> converter;

		public AmountConverter() {
			converter = FieldConverter.provider().getConverter(BigDecimal.class);
		}

		@Override
		public String format(FormatFieldDescriptor descriptor, BigDecimal value) throws FieldConversionException {
			return new StringBuilder()
					.append(converter.format(descriptor, value.abs()).substring(1))
					.append(formatSign(value))
					.toString();
		}

		@Override
		public BigDecimal parse(FormatFieldDescriptor descriptor, String source) throws FieldConversionException {
			int length = descriptor.length() - 1;
			String value = StringUtils.leftPad(source.substring(0, length), descriptor.length(), "0");
			return converter.parse(descriptor, value)
					.multiply(parseSign(source.substring(length)));
		}

		private String formatSign(final BigDecimal value) {
			switch (value.signum()) {
			case -1:
				return "-";
			case 1:
				return "+";
			default:
				return " ";
			}
		}

		private BigDecimal parseSign(final String source) {
			if ("-".equals(source)) {
				return BigDecimal.ONE.negate();
			} else if ("+".equals(source)) {
				return BigDecimal.ONE;
			} else {
				return BigDecimal.ZERO;
			}
		}

	}

}
