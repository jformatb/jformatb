/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package it.bancomat.message.formatter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import format.bind.Formatter;
import it.bancomat.message.RequestMessage;

class MessageFormatterTest {

	@ParameterizedTest(name = "format{0}")
	@ArgumentsSource(MessageArgumentsProvider.class)
	void formatMessage(String name, String expected, RequestMessage message) {
		String actual = Formatter.of(RequestMessage.class)
				.format(message);
		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest(name = "parse{0}")
	@ArgumentsSource(MessageArgumentsProvider.class)
	void parseMessage(String name, String text, RequestMessage expected) {
		RequestMessage actual = Formatter.of(RequestMessage.class)
				.parse(text);
		assertThat(actual)
				.isExactlyInstanceOf(expected.getClass())
				.isEqualTo(expected);
	}

}
