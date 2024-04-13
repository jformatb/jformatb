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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

import format.bind.FormatProcessingException;
import format.bind.Formatter;
import format.datatype.BankStatement;
import format.datatype.BankStatement.Transaction;

class BankStatementFormatterTest {

	private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	private CSVReader reader;

	@BeforeEach
	void init() {
		InputStream input = Thread.currentThread()
				.getContextClassLoader().getResourceAsStream("transactions.csv");
		reader = new CSVReaderBuilder(new InputStreamReader(input))
				.withSkipLines(1)
				.build();
	}

	@Test
	void formatBankStatement() throws FormatProcessingException, IOException, CsvException {
		List<String[]> rows = reader.readAll();
		String actual = Formatter.of(BankStatement.class).format(buildBankStatement(rows));
		String expected = buildTextMessage(rows);
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void parseBankStatement() throws FormatProcessingException, IOException, CsvException {
		List<String[]> rows = reader.readAll();
		BankStatement actual = Formatter.of(BankStatement.class).parse(buildTextMessage(rows));
		BankStatement expected = buildBankStatement(rows);
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void formatLastTenTransactions() throws FormatProcessingException, IOException, CsvException {
		List<String[]> rows = reader.readAll();
		String actual = Formatter.of(BankStatement.class)
				.withPattern("${transactions[0..9]:45}")
				.format(buildBankStatement(rows));
		assertThat(actual).hasSize(450);
	}

	@Test
	void parseLastTenTransactions() throws FormatProcessingException, IOException, CsvException {
		List<String[]> rows = reader.readAll();
		BankStatement actual = Formatter.of(BankStatement.class)
				.withPattern("${transactions[0..9]:45}")
				.parse(buildTextMessage(rows));
		assertThat(actual.getTransactions()).hasSize(10);
	}

	void cleanup() throws IOException {
		reader.close();
	}

	private String buildTextMessage(List<String[]> rows) throws IOException, CsvException {
		StringBuilder sb = new StringBuilder();

		DateTimeFormatter textDateTimeFormatter = DateTimeFormatter.ofPattern("ddMMyy");

		rows.forEach(columns -> {
			LocalDate entryDate = dateTimeFormatter.parse(columns[0], LocalDate::from);
			LocalDate valueDate = dateTimeFormatter.parse(columns[1], LocalDate::from);
			String description = columns[2];
			char sign = columns[3].charAt(0);
			long amount = new BigDecimal(columns[3].substring(1)).abs().unscaledValue().longValueExact();
			sb.append(new StringBuilder()
					.append(textDateTimeFormatter.format(entryDate))
					.append(textDateTimeFormatter.format(valueDate))
					.append(StringUtils.rightPad(description, 20))
					.append(StringUtils.leftPad(String.valueOf(amount), 12, "0"))
					.append(sign)
					.toString());
		});

		return sb.toString();
	}

	private BankStatement buildBankStatement(List<String[]> rows) throws IOException, CsvException {
		List<Transaction> transactions= rows.stream()
				.map(columns -> Transaction.builder()
						.entryDate(dateTimeFormatter.parse(columns[0], LocalDate::from))
						.valueDate(dateTimeFormatter.parse(columns[1], LocalDate::from))
						.description(columns[2])
						.amount(new BigDecimal(columns[3]).unscaledValue().longValueExact())
						.build())
				.collect(Collectors.toList());

		return BankStatement.builder()
				.transactions(transactions)
				.build();
	}

}
