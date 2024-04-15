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
package format.bind;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import org.junit.jupiter.api.Test;

import format.bind.converter.FieldConverter;
import format.bind.converter.spi.FieldConverterProvider;

class FormatterTest {

	@Test
	void formatObject() {
		Throwable exception = catchThrowable(() -> Formatter.of(Object.class)
				.format(new Object()));

		assertThat(exception)
				.isInstanceOf(FormatProcessingException.class);
	}

	@Test
	void parseObject() {
		Throwable exception = catchThrowable(() -> Formatter.of(Object.class)
				.parse(new StringBuilder().toString()));

		assertThat(exception)
				.isInstanceOf(FormatProcessingException.class);
	}

	@Test
	void getFieldConverterProvider() {
		Throwable exception = catchThrowable(FieldConverter::provider);

		assertThat(exception)
				.isInstanceOf(FormatException.class)
				.hasMessageContaining(FieldConverterProvider.class.getName());
	}

}
