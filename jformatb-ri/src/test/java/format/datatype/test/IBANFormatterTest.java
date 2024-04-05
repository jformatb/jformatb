/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package format.datatype.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import format.bind.Formatter;
import format.datatype.BBAN.BBANBuilder;
import format.datatype.BEBBAN;
import format.datatype.DEBBAN;
import format.datatype.FRBBAN;
import format.datatype.GBBBAN;
import format.datatype.IBAN;
import format.datatype.ITBBAN;

class IBANFormatterTest {

	private static Map<String, BBANBuilder<?, ?>> builders = new HashMap<>();

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
		valid = new Condition<>(IBAN::isValid, "valid");
	}

	@ParameterizedTest
	@CsvFileSource(resources = "/iban.csv", numLinesToSkip = 1)
	void formatIBAN(String countryCode, String checkDigits, String bankCode, String branchCode,
			String accountNumber, String nationalCheckDigits, String iban) {
		String expected = iban;
		String actual = Formatter.of(IBAN.class)
				.format(IBAN.builder()
						.countryCode(countryCode)
						.checkDigits(checkDigits)
						.BBAN(builders.get(countryCode)
								.bankCode(StringUtils.trimToNull(bankCode))
								.branchCode(StringUtils.trimToNull(branchCode))
								.accountNumber(StringUtils.trimToNull(accountNumber))
								.checkDigits(StringUtils.trimToNull(nationalCheckDigits))
								.build())
						.build());
		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvFileSource(resources = "/iban.csv", numLinesToSkip = 1)
	void parseIBAN(String countryCode, String checkDigits, String bankCode, String branchCode,
			String accountNumber, String nationalCheckDigits, String iban) {
		IBAN actual = Formatter.of(IBAN.class)
				.parse(iban);
		IBAN expected = IBAN.builder()
				.countryCode(countryCode)
				.checkDigits(checkDigits)
				.BBAN(builders.get(countryCode)
						.bankCode(StringUtils.trimToNull(bankCode))
						.branchCode(StringUtils.trimToNull(branchCode))
						.accountNumber(StringUtils.trimToNull(accountNumber))
						.checkDigits(StringUtils.trimToNull(nationalCheckDigits))
						.build())
				.build();
		assertThat(actual)
				.is(valid)
				.isEqualTo(expected)
				.hasToString(iban);
	}

}
