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

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;

import format.bind.FormatFieldDescriptor;

class TimestampConverterTest extends AbstractConverterTest<Timestamp> {

	TimestampConverterTest() {
		super(Timestamp.class);
	}

	@Test
	void formatTimestamp() {
		String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		Timestamp timestamp = Timestamp.from(Instant.now());
		FormatFieldDescriptor descriptor = fieldDescriptorBuilder()
				.length(23)
				.format(pattern)
				.build();
		String expected = formatter.format(timestamp.toLocalDateTime());
		String actual = converter.format(descriptor, timestamp);
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void parseTimestamp() {
		String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		FormatFieldDescriptor descriptor = fieldDescriptorBuilder()
				.length(23)
				.format(pattern)
				.build();
		String source = formatter.format(LocalDateTime.now());
		Timestamp expected = Timestamp.valueOf(formatter.parse(source, LocalDateTime::from));
		Timestamp actual = converter.parse(descriptor, source);
		assertThat(actual).isEqualTo(expected);
	}

}
