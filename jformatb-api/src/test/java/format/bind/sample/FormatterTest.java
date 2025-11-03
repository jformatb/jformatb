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
package format.bind.sample;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import format.bind.FormatException;
import format.bind.FormatProcessingException;
import format.bind.Formatter;
import format.bind.converter.FieldConverter;
import format.bind.converter.spi.FieldConverterProvider;

class FormatterTest {

	@Test
	void formatText() {
		Throwable exception = catchThrowable(() -> Formatter.of(Object.class)
				.format(new Object()));

		assertThat(exception)
				.isInstanceOf(FormatProcessingException.class);
	}

	@Test
	void formatBytesWithDefaultCharset() {
		Formatter<Object> formatter = Formatter.of(Object.class);
		Object obj = new Object();
		assertThatExceptionOfType(FormatProcessingException.class)
				.isThrownBy(() -> formatter.formatBytes(obj))
				.withCauseInstanceOf(UnsupportedOperationException.class);
	}

	@Test
	void formatBytesWithCharset() {
		Formatter<Object> formatter = Formatter.of(Object.class);
		Object obj = new Object();
		assertThatExceptionOfType(FormatProcessingException.class)
				.isThrownBy(() -> formatter.formatBytes(obj, StandardCharsets.US_ASCII))
				.withCauseInstanceOf(UnsupportedOperationException.class);
	}

	@Test
	void parseText() {
		Throwable exception = catchThrowable(() -> Formatter.of(Object.class)
				.parse(new StringBuilder().toString()));

		assertThat(exception)
				.isInstanceOf(FormatProcessingException.class);
	}

	@Test
	void parseBytesWithDefaultCharset() {
		Formatter<Object> formatter = Formatter.of(Object.class);
		byte[] bytes = new byte[0];
		assertThatExceptionOfType(FormatProcessingException.class)
				.isThrownBy(() -> formatter.parseBytes(bytes))
				.withCauseInstanceOf(UnsupportedOperationException.class);
	}

	@Test
	void parseBytesWithCharset() {
		Formatter<Object> formatter = Formatter.of(Object.class);
		byte[] bytes = new byte[0];
		assertThatExceptionOfType(FormatProcessingException.class)
				.isThrownBy(() -> formatter.parseBytes(bytes, StandardCharsets.US_ASCII))
				.withCauseInstanceOf(UnsupportedOperationException.class);
	}

	@Test
	void getFieldConverterProvider() {
		Throwable exception = catchThrowable(FieldConverter::provider);

		assertThat(exception)
				.isInstanceOf(FormatException.class)
				.hasMessageContaining(FieldConverterProvider.class.getName());
	}

}
