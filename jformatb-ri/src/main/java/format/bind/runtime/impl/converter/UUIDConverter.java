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

import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import format.bind.FormatFieldDescriptor;
import format.bind.annotation.FormatField.Type;
import format.bind.converter.FieldConversionException;
import format.bind.converter.FieldConverter;

final class UUIDConverter implements FieldConverter<UUID> {

	@Override
	public byte[] formatBytes(FormatFieldDescriptor descriptor, UUID value) throws FieldConversionException {
		if (descriptor.type() == Type.BINARY) {
			return FieldConverters.getConverter(byte[].class).formatBytes(descriptor, toByteArray(value));
		} else {
			return FieldConverters.getConverter(String.class).formatBytes(descriptor, value.toString());
		}
	}

	@Override
	public UUID parseBytes(final FormatFieldDescriptor descriptor, final byte[] source) throws FieldConversionException {
		try {
			if (descriptor.type() == Type.BINARY) {
				return Optional.ofNullable(FieldConverters.getConverter(byte[].class).parseBytes(descriptor, source))
						.filter(ArrayUtils::isNotEmpty)
						.map(UUIDConverter::valueOf)
						.orElse(null);
			} else {
				return Optional.ofNullable(FieldConverters.getConverter(String.class).parseBytes(descriptor, source))
						.filter(StringUtils::isNotBlank)
						.map(UUID::fromString)
						.orElse(null);
			}
		} catch (Exception e) {
			throw FieldConverters.parseFieldConversionException(descriptor, source, e);
		}
	}

	private static byte[] toByteArray(final UUID uuid) {
		ByteBuffer buffer = ByteBuffer.allocate(16);
		buffer.putLong(uuid.getMostSignificantBits());
		buffer.putLong(uuid.getLeastSignificantBits());
		return buffer.array();
	}

	private static UUID valueOf(final byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		long mostSigBits = buffer.getLong();
		long leastSigBits = buffer.getLong();
		return new UUID(mostSigBits, leastSigBits);
	}

}
