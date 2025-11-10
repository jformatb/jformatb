/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package com.example.formatter;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import com.example.datatype.BEBBAN;
import com.example.datatype.DEBBAN;
import com.example.datatype.FRBBAN;
import com.example.datatype.GBBBAN;
import com.example.datatype.IBAN;
import com.example.datatype.ITBBAN;
import com.example.datatype.BBAN.BBANBuilder;

import format.bind.Formatter;

class IBANFormatterTest {

	private static Map<String, BBANBuilder<?, ?>> builders = new HashMap<>();

	private Map<String, Object> resolvedValues = new LinkedHashMap<>();

	private Condition<IBAN> valid;

	@BeforeAll
	static void setup() {
		builders.put("BE", BEBBAN.builder());
		builders.put("DE", DEBBAN.builder());
		builders.put("FR", FRBBAN.builder());
		builders.put("GB", GBBBAN.builder());
		builders.put("IT", ITBBAN.builder());
	}

	@BeforeEach
	void init() {
		resolvedValues.clear();
		valid = new Condition<>(IBAN::isValid, "valid");
	}

	@ParameterizedTest(name = "format{0}IBAN")
	@CsvFileSource(resources = "/iban.csv", numLinesToSkip = 1)
	void formatIBAN(String countryCode, String checkDigits, String bankCode, String branchCode,
			String accountNumber, String nationalCheckDigits, String iban) {
		String expected = iban;
		String actual = Formatter.of(IBAN.class)
				.createWriter()
				.withListener((obj, values) -> resolvedValues.putAll(values))
				.write(buildIBAN(countryCode, checkDigits, bankCode, branchCode, accountNumber, nationalCheckDigits));
		assertThat(actual).isEqualTo(expected);
		assertThat(resolvedValues).containsOnlyKeys("countryCode", "checkDigits", "bban");
	}

	@ParameterizedTest(name = "parse{0}IBAN")
	@CsvFileSource(resources = "/iban.csv", numLinesToSkip = 1)
	void parseIBAN(String countryCode, String checkDigits, String bankCode, String branchCode,
			String accountNumber, String nationalCheckDigits, String iban) {
		IBAN actual = Formatter.of(IBAN.class)
				.createReader()
				.setListener((obj, values) -> resolvedValues.putAll(values))
				.read(iban);
		IBAN expected = buildIBAN(countryCode, checkDigits, bankCode, branchCode, accountNumber, nationalCheckDigits);
		assertThat(actual)
				.is(valid)
				.isEqualTo(expected)
				.hasToString(iban);
		assertThat(resolvedValues).containsEntry("bban", actual.getBban());
	}

	private static IBAN buildIBAN(String countryCode, String checkDigits, String bankCode, String branchCode,
			String accountNumber, String nationalCheckDigits) {
		return IBAN.builder()
				.countryCode(countryCode)
				.checkDigits(checkDigits)
				.bban(builders.get(countryCode)
						.bankCode(StringUtils.trimToNull(bankCode))
						.branchCode(StringUtils.trimToNull(branchCode))
						.accountNumber(StringUtils.trimToNull(accountNumber))
						.checkDigits(StringUtils.trimToNull(nationalCheckDigits))
						.build())
				.build();
	}

}
