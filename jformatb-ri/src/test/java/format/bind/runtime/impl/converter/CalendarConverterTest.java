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
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import format.bind.FormatFieldDescriptor;

class CalendarConverterTest extends AbstractConverterTest<Calendar> {

	CalendarConverterTest() {
		super(Calendar.class);
	}

	@Test
	void formatCalendar() {
		String locale = "en-US";
		String pattern = "yyyyMMddHHmmss";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		Calendar calendar = Calendar.getInstance(Locale.forLanguageTag(locale));
		FormatFieldDescriptor descriptor = fieldDescriptorBuilder()
				.length(12)
				.format(pattern)
				.locale(locale)
				.build();
		String expected = formatter.format(calendar.toInstant().atZone(calendar.getTimeZone().toZoneId()));
		String actual = converter.format(descriptor, calendar);
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void parseCalendar() {
		String locale = "en-US";
		String pattern = "yyyyMMddHHmmss";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.systemDefault());
		FormatFieldDescriptor descriptor = fieldDescriptorBuilder()
				.length(12)
				.format(pattern)
				.locale(locale)
				.build();
		String source = formatter.format(Instant.now().atZone(ZoneId.systemDefault()));
		Calendar expected = GregorianCalendar.from(formatter.parse(source, ZonedDateTime::from));
		Calendar actual = converter.parse(descriptor, source);
		assertThat(actual).isEqualByComparingTo(expected);
	}

}
