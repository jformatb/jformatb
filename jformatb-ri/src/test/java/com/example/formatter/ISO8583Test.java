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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

import com.example.datatype.ISO8583;
import com.example.datatype.ISO8583.DataElement;

import format.bind.Formatter;

class ISO8583Test {

	@Test
	void formatBitmap() {
		String message = "01107010001102C04804";
		String expected = message;
		byte[] actual = Formatter.of(ISO8583.class)
				.withPattern("${MTI:4}${BITMAP:16}")
				.formatBytes(ISO8583.fromString("01107010001102C04804"), "US-ASCII");
		assertThat(actual).asString(StandardCharsets.US_ASCII).isEqualTo(expected);
	}

	@Test
	void parseBitmap() {
		String message = "08002038000000200002";
		ISO8583 expected = ISO8583.fromString(message);
		ISO8583 actual = Formatter.of(ISO8583.class)
				.withPattern("${MTI:4}${BITMAP:16}")
				.parseBytes(message.getBytes(StandardCharsets.US_ASCII), "US-ASCII");
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void formatDataElements() {
		Instant now = Instant.now();
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyMMddHHmm").withZone(ZoneId.of("UTC"));
		Charset charset = StandardCharsets.US_ASCII;
		ISO8583 message = ISO8583.builder()
				.dataElement(DataElement.of(7, now))
				.dataElement(DataElement.of(11, 1))
				.build();
		String pattern = new StringBuilder()
				.append("${DE[0]:--type=NUMERIC --length=10 --format=yyMMddHHmm --targetClass=java.time.Instant}")
				.append("${DE[1]:--type=NUMERIC --length=6 --targetClass=java.lang.Integer}")
				.toString();
		byte[] expected = new StringBuilder()
				.append(dateTimeFormatter.format(now))
				.append("000001")
				.toString()
				.getBytes(charset);
		byte[] actual = Formatter.of(ISO8583.class)
				.withPattern(pattern)
				.formatBytes(message, charset);
		assertThat(actual).containsExactly(expected);
	}

	@Test
	void parseDataElements() {
		Instant now = Instant.now();
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyMMddHHmm").withZone(ZoneId.of("UTC"));
		Charset charset = StandardCharsets.US_ASCII;
		byte[] message = new StringBuilder()
				.append(dateTimeFormatter.format(now))
				.append("000001")
				.toString()
				.getBytes(charset);
		String pattern = new StringBuilder()
				.append("${DE[0]:--type=NUMERIC --length=10 --format=yyMMddHHmm --targetClass=java.time.Instant}")
				.append("${DE[1]:--type=NUMERIC --length=6 --targetClass=java.lang.Integer}")
				.toString();
		ISO8583 expected = ISO8583.builder()
				.dataElement(DataElement.of(dateTimeFormatter.parse(new String(Arrays.copyOfRange(message, 0, 10), charset), Instant::from)))
				.dataElement(DataElement.of(1))
				.build();
		ISO8583 actual = Formatter.of(ISO8583.class)
				.withPattern(pattern)
				.parseBytes(message, charset);
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void formatEmvRequestData() throws DecoderException {
		String data = "5F2A0209768407A00000000410109F360200039F03060000000000009C01005F3401019F10120110A0000F040000000000000000000000FF9F33030008C89A032204259F2608093A260A58500E949F2701809F020600000000010082021B809F34033F00029F1A0209769F37046F4D8104950500200000019F6E06005601023030";
		ISO8583 message = ISO8583.builder()
				.dataElement(DataElement.of(55, Hex.decodeHex(data)))
				.build();
		String pattern = new StringBuilder()
				.append("${DE[0]:--type=BINARY --charset=US-ASCII --length=129 --targetClass=[B}")
				.toString();
		byte[] actual = Formatter.of(ISO8583.class)
				.withPattern(pattern)
				.formatBytes(message);
		assertThat(actual).asHexString().isEqualTo(data);
	}

	@Test
	void parseEmvResponseData() throws DecoderException {
		String data = "9F36020015910AB58D60185BEF0247303072179F180430303031860E04DA9F580903B1BAEDFD1438BA48";
		String pattern = new StringBuilder()
				.append("${DE[0]:--type=BINARY --charset=US-ASCII --length=42 --targetClass=[B}")
				.toString();
		ISO8583 actual = Formatter.of(ISO8583.class)
				.withPattern(pattern)
				.parseBytes(Hex.decodeHex(data));
		assertThat(actual.getDataElements())
				.singleElement()
				.extracting(DataElement::getValue)
				.asInstanceOf(InstanceOfAssertFactories.BYTE_ARRAY)
				.asHexString()
				.isEqualTo(data);
	}

}
