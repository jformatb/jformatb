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
package format.bind.runtime.impl.converter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.junit.jupiter.api.Test;

import format.bind.FormatFieldDescriptor;

class DateConverterTest extends AbstractConverterTest<Date> {

	DateConverterTest() {
		super(Date.class);
	}

	@Test
	void formatDate() {
		String pattern = "yyyyMMddHHmmss";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.systemDefault());
		Date date = Date.from(Instant.now());
		FormatFieldDescriptor descriptor = fieldDescriptorBuilder()
				.length(12)
				.format(pattern)
				.locale("it-IT")
				.build();
		String expected = formatter.format(date.toInstant());
		String actual = converter.format(descriptor, date);
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void parseDate() {
		String pattern = "yyyyMMddHHmmss";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.systemDefault());
		FormatFieldDescriptor descriptor = fieldDescriptorBuilder()
				.length(12)
				.format(pattern)
				.locale("it-IT")
				.build();
		String source = formatter.format(Instant.now());
		Instant instant = formatter.parse(source, Instant::from);
		Date actual = converter.parse(descriptor, source);
		assertThat(actual).isEqualTo(instant);
	}

}
