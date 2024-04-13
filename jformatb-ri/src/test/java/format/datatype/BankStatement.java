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
import java.time.LocalDate;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

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
		private LocalDate entryDate;

		@FormatField(format = "ddMMyy")
		private LocalDate valueDate;

		@FormatField
		private String description;

		@FormatField
		@FormatFieldConverter(AmountConverter.class)
		private Long amount;

	}

	public static class AmountConverter implements FieldConverter<Long> {

		private static final int LENGTH = 12;

		@Override
		public String format(FormatFieldDescriptor descriptor, Long value) throws FieldConversionException {
			return new StringBuilder()
					.append(StringUtils.leftPad(String.valueOf(Math.abs(value)), LENGTH, "0"))
					.append(value < 0 ? "-" : "+")
					.toString();
		}

		@Override
		public Long parse(FormatFieldDescriptor descriptor, String source) throws FieldConversionException {
			return new BigDecimal(source.substring(0, LENGTH))
					.multiply("-".equals(source.substring(LENGTH)) ? BigDecimal.ONE.negate() : BigDecimal.ONE)
					.longValueExact();
		}

	}

}
