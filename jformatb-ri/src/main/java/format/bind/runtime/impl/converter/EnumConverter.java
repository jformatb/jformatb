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

import org.apache.commons.lang3.StringUtils;

import format.bind.FormatFieldDescriptor;
import format.bind.annotation.FormatField.Type;
import format.bind.converter.FieldConversionException;
import format.bind.converter.FieldConverter;

final class EnumConverter<E extends Enum<E>> implements FieldConverter<E> {

	private final Class<E> enumType;

	private EnumConverter(final Class<E> enumType) {
		this.enumType = enumType;
	}

	public static <E extends Enum<E>> EnumConverter<E> of(final Class<E> enumType) {
		return new EnumConverter<>(enumType);
	}

	@Override
	public byte[] formatBytes(final FormatFieldDescriptor descriptor, final E value) throws FieldConversionException {
		if (descriptor.type() == Type.ALPHANUMERIC) {
			return StringUtils.rightPad(value.name(), descriptor.length()).getBytes(descriptor.charset());
		} else {
			return StringUtils.leftPad(String.valueOf(value.ordinal()), descriptor.length(), "0").getBytes(descriptor.charset());
		}
	}

	@Override
	public E parseBytes(final FormatFieldDescriptor descriptor, final byte[] source) throws FieldConversionException {
		if (descriptor.type() == Type.ALPHANUMERIC) {
			return Enum.valueOf(enumType, StringUtils.trim(new String(source, descriptor.charset())));
		} else {
			return enumType.getEnumConstants()[Integer.parseInt(StringUtils.trim(new String(source, descriptor.charset())))];
		}
	}

}
