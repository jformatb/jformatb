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

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import format.bind.FormatFieldDescriptor;
import format.bind.converter.FieldConversionException;
import format.bind.converter.FieldConverter;

final class BooleanConverter implements FieldConverter<Boolean> {

	@Override
	public byte[] formatBytes(final FormatFieldDescriptor descriptor, final Boolean value) throws FieldConversionException {
		try {
			return StringUtils.leftPad(String.valueOf(BooleanUtils.toInteger(value.booleanValue())), descriptor.length()).getBytes(descriptor.charset());
		} catch (Exception e) {
			throw FieldConverters.formatFieldConversionException(descriptor, value, e);
		}
	}

	@Override
	public Boolean parseBytes(final FormatFieldDescriptor descriptor, final byte[] source) throws FieldConversionException {
		try {
			return BooleanUtils.toBooleanObject(Integer.parseInt(StringUtils.trim(new String(source, descriptor.charset()))));
		} catch (Exception e) {
			throw FieldConverters.parseFieldConversionException(descriptor, source, e);
		}
	}

}
