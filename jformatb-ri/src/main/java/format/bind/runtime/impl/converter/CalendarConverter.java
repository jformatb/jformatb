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

import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Optional;

import format.bind.FormatFieldDescriptor;
import format.bind.converter.FieldConversionException;
import format.bind.converter.FieldConverter;

final class CalendarConverter implements FieldConverter<Calendar> {

	private static FieldConverter<ZonedDateTime> converter = FieldConverters.getConverter(ZonedDateTime.class);

	@Override
	public byte[] formatBytes(FormatFieldDescriptor descriptor, Calendar value) throws FieldConversionException {
		return converter.formatBytes(descriptor, Optional.ofNullable(value)
				.map(calendar -> calendar.toInstant().atZone(calendar.getTimeZone().toZoneId()))
				.orElse(null));
	}

	@Override
	public Calendar parseBytes(FormatFieldDescriptor descriptor, byte[] source) throws FieldConversionException {
		return Optional.ofNullable(converter.parseBytes(descriptor, source))
				.map(GregorianCalendar::from)
				.orElse(null);
	}

}
