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

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;

import com.example.datatype.ISO8583;
import com.example.datatype.ISO8583.DataElement;

import format.bind.Formatter;

class ISO8583Test {

	@Test
	void formatBitmap() {
		String expected = "01107010001102C04804";
		String actual = Formatter.of(ISO8583.class)
				.withPattern("${MTI:4}${BITMAP:16}")
				.format(ISO8583.fromString("01107010001102C04804"));
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void parseBitmap() {
		ISO8583 expected = ISO8583.fromString("08002038000000200002");
		ISO8583 actual = Formatter.of(ISO8583.class)
				.withPattern("${MTI:4}${BITMAP:16}")
				.parse("08002038000000200002");
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void formatDataElements() {
		Instant now = Instant.now();
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyMMddHHmm").withZone(ZoneId.of("UTC"));
		String expected = new StringBuilder()
				.append(dateTimeFormatter.format(now))
				.append("000001")
				.toString();
		String actual = Formatter.of(ISO8583.class)
				.withPattern(new StringBuilder()
						.append("${DE[0]:--type=NUMERIC --length=10 --format=yyMMddHHmm --targetClass=java.time.Instant}")
						.append("${DE[1]:--type=NUMERIC --length=6 --targetClass=java.lang.Integer}")
						.toString())
				.format(ISO8583.builder()
						.dataElement(DataElement.of(7, now))
						.dataElement(DataElement.of(11, 1))
						.build());
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void parseDataElements() {
		Instant now = Instant.now();
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyMMddHHmm").withZone(ZoneId.of("UTC"));
		String message = new StringBuilder()
				.append(dateTimeFormatter.format(now))
				.append("000001")
				.toString();
		ISO8583 expected = ISO8583.builder()
				.dataElement(DataElement.of(dateTimeFormatter.parse(message.substring(0, 10), Instant::from)))
				.dataElement(DataElement.of(1))
				.build();
		ISO8583 actual = Formatter.of(ISO8583.class)
				.withPattern(new StringBuilder()
						.append("${DE[0]:--type=NUMERIC --length=10 --format=yyMMddHHmm --targetClass=java.time.Instant}")
						.append("${DE[1]:--type=NUMERIC --length=6 --targetClass=java.lang.Integer}")
						.toString())
				.parse(message);
		assertThat(actual).isEqualTo(expected);
	}

}
