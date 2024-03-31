/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package format.datatype.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import format.bind.Formatter;
import format.datatype.BBAN;
import format.datatype.BEBBAN;
import format.datatype.DEBBAN;
import format.datatype.FRBBAN;
import format.datatype.GBBBAN;
import format.datatype.IBAN;
import format.datatype.ITBBAN;

class IBANAdapterTest {

	private Condition<IBAN> valid;

	@BeforeEach
	void setup() {
		valid = new Condition<>(IBAN::isValid, "valid");
	}

	@Test
	void formatBEIBAN() {
		String expected = "BE68539007547034";
		String actual = Formatter.of(IBAN.class)
				.format(IBAN.builder()
						.countryCode("BE")
						.checkDigits("68")
						.BBAN(BEBBAN.builder()
								.bankCode("539")
								.accountNumber("0075470")
								.checkDigits("34")
								.build())
						.build());
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void formatDEIBAN() {
		String expected = "DE89370400440532013000";
		String actual = Formatter.of(IBAN.class)
				.format(IBAN.builder()
						.countryCode("DE")
						.checkDigits("89")
						.BBAN(DEBBAN.builder()
								.bankCode("37040044")
								.accountNumber("0532013000")
								.build())
						.build());
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void formatFRIBAN() {
		String expected = "FR1420041010050500013M02606";
		String actual = Formatter.of(IBAN.class)
				.format(IBAN.builder()
						.countryCode("FR")
						.checkDigits("14")
						.BBAN(FRBBAN.builder()
								.bankCode("20041")
								.branchCode("01005")
								.accountNumber("0500013M026")
								.checkDigits("06")
								.build())
						.build());
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void formatGBIBAN() {
		String expected = "GB29NWBK60161331926819";
		String actual = Formatter.of(IBAN.class)
				.format(IBAN.builder()
						.countryCode("GB")
						.checkDigits("29")
						.BBAN(GBBBAN.builder()
								.bankCode("NWBK")
								.branchCode("601613")
								.accountNumber("31926819")
								.build())
						.build());
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void formatITIBAN() {
		String expected = "IT30X0306933000100000006524";
		String actual = Formatter.of(IBAN.class)
				.format(IBAN.builder()
						.countryCode("IT")
						.checkDigits("30")
						.BBAN(ITBBAN.builder()
								.checkDigits("X")
								.bankCode("03069")
								.branchCode("33000")
								.accountNumber("100000006524")
								.build())
						.build());
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void parseBEIBAN() {
		String iban = "BE68539007547034";
		IBAN actual = Formatter.of(IBAN.class)
				.parse(iban);
		IBAN expected = IBAN.builder()
				.countryCode("BE")
				.checkDigits("68")
				.BBAN(BEBBAN.builder()
						.bankCode("539")
						.accountNumber("0075470")
						.checkDigits("34")
						.build())
				.build();
		check(actual, expected, iban, BEBBAN.class);
	}

	@Test
	void parseDEIBAN() {
		String iban = "DE89370400440532013000";
		IBAN actual = Formatter.of(IBAN.class)
				.parse(iban);
		IBAN expected = IBAN.builder()
				.countryCode("DE")
				.checkDigits("89")
				.BBAN(DEBBAN.builder()
						.bankCode("37040044")
						.accountNumber("0532013000")
						.build())
				.build();
		check(actual, expected, iban, DEBBAN.class);
	}

	@Test
	void parseFRIBAN() {
		String iban = "FR1420041010050500013M02606";
		IBAN actual = Formatter.of(IBAN.class)
				.parse(iban);
		IBAN expected = IBAN.builder()
				.countryCode("FR")
				.checkDigits("14")
				.BBAN(FRBBAN.builder()
						.bankCode("20041")
						.branchCode("01005")
						.accountNumber("0500013M026")
						.checkDigits("06")
						.build())
				.build();
		check(actual, expected, iban, FRBBAN.class);
	}

	@Test
	void parseGBIBAN() {
		String iban = "GB29NWBK60161331926819";
		IBAN actual = Formatter.of(IBAN.class)
				.parse(iban);
		IBAN expected = IBAN.builder()
				.countryCode("GB")
				.checkDigits("29")
				.BBAN(GBBBAN.builder()
						.bankCode("NWBK")
						.branchCode("601613")
						.accountNumber("31926819")
						.build())
				.build();
		check(actual, expected, iban, GBBBAN.class);
	}

	@Test
	void parseITIBAN() {
		String iban = "IT30X0306933000100000006524";
		IBAN actual = Formatter.of(IBAN.class)
				.parse(iban);
		IBAN expected = IBAN.builder()
				.countryCode("IT")
				.checkDigits("30")
				.BBAN(ITBBAN.builder()
						.checkDigits("X")
						.bankCode("03069")
						.branchCode("33000")
						.accountNumber("100000006524")
						.build())
				.build();
		check(actual, expected, iban, ITBBAN.class);
	}

	private void check(IBAN actual, IBAN expected, String iban, Class<? extends BBAN> bbanType) {
		assertThat(actual)
				.is(valid)
				.isEqualTo(expected)
				.hasToString(iban)
				.extracting(IBAN::getBBAN).isExactlyInstanceOf(bbanType);
	}

}
