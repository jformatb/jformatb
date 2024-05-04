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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.datatype.BankStatement;
import com.example.datatype.BankStatement.Transaction;
import com.opencsv.bean.CsvToBeanBuilder;

import format.bind.FormatProcessingException;
import format.bind.Formatter;

class BankStatementFormatterTest {

	private InputStream input;

	@BeforeEach
	void init() {
		input = Thread.currentThread()
				.getContextClassLoader()
				.getResourceAsStream("transactions.csv");
	}

	@Test
	void formatBankStatement() throws FormatProcessingException {
		List<Transaction> rows = buildTransactionList();
		String actual = Formatter.of(BankStatement.class).format(buildBankStatement(rows));
		String expected = buildTextMessage(rows);
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void parseBankStatement() throws FormatProcessingException {
		List<Transaction> rows = buildTransactionList();
		BankStatement actual = Formatter.of(BankStatement.class).parse(buildTextMessage(rows));
		BankStatement expected = buildBankStatement(rows);
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void formatLastTenTransactions() throws FormatProcessingException {
		List<Transaction> rows = buildTransactionList();
		String actual = Formatter.of(BankStatement.class)
				.withPattern("${transactions[0..9]:45}")
				.format(buildBankStatement(rows));
		assertThat(actual).hasSize(450);
	}

	@Test
	void parseLastTenTransactions() throws FormatProcessingException {
		List<Transaction> rows = buildTransactionList();
		BankStatement actual = Formatter.of(BankStatement.class)
				.withPattern("${transactions[0..9]:45}")
				.parse(buildTextMessage(rows));
		assertThat(actual.getTransactions()).hasSize(10);
	}

	void cleanup() throws IOException {
		input.close();
	}

	private BankStatement buildBankStatement(List<Transaction> transactions) {
		return BankStatement.builder()
				.transactions(transactions)
				.build();
	}

	private String buildTextMessage(List<Transaction> transactions) {
		StringBuilder sb = new StringBuilder();

		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("ddMMyy");

		transactions.forEach(transaction -> sb.append(new StringBuilder()
				.append(dateTimeFormatter.format(transaction.getEntryDate()))
				.append(dateTimeFormatter.format(transaction.getValueDate()))
				.append(StringUtils.rightPad(transaction.getDescription(), 20))
				.append(StringUtils.leftPad(String.valueOf(transaction.getAmount().abs().unscaledValue().longValueExact()), 12, "0"))
				.append(transaction.getAmount().signum() == -1 ? "-" : "+")
				.toString()));

		return sb.toString();
	}

	private List<Transaction> buildTransactionList() {
		return new CsvToBeanBuilder<Transaction>(new InputStreamReader(input))
				.withType(Transaction.class)
				.build().parse();
	}

}
