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
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.nio.ByteBuffer;
import java.util.UUID;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.Test;

import format.bind.FormatFieldDescriptor;
import format.bind.annotation.FormatField.Type;
import format.bind.converter.FieldConversionException;

class UUIDConverterTest extends AbstractConverterTest<UUID> {

	UUIDConverterTest() {
		super(UUID.class);
	}

	@Test
	void formatUUID() {
		FormatFieldDescriptor descriptor = fieldDescriptorBuilder().build();
		UUID uuid = UUID.randomUUID();
		String expected = uuid.toString();
		String actual = converter.format(descriptor, uuid);
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void formatBinaryUUID() {
		FormatFieldDescriptor descriptor = fieldDescriptorBuilder()
				.type(Type.BINARY)
				.length(16)
				.build();
		UUID uuid = UUID.randomUUID();
		byte[] bytes = ByteBuffer.allocate(16)
				.putLong(uuid.getMostSignificantBits())
				.putLong(uuid.getLeastSignificantBits())
				.array();
		String expected = Hex.encodeHexString(bytes, false);
		byte[] actual = converter.formatBytes(descriptor, uuid);
		assertThat(actual).asHexString().isEqualTo(expected);
	}

	@Test
	void parseUUID() {
		FormatFieldDescriptor descriptor = fieldDescriptorBuilder().build();
		String source = UUID.randomUUID().toString();
		UUID expected = UUID.fromString(source);
		UUID actual = converter.parse(descriptor, source);
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void parseBinaryUUID() {
		FormatFieldDescriptor descriptor = fieldDescriptorBuilder()
				.type(Type.BINARY)
				.length(16)
				.build();
		UUID uuid = UUID.randomUUID();
		byte[] source = ByteBuffer.allocate(16)
				.putLong(uuid.getMostSignificantBits())
				.putLong(uuid.getLeastSignificantBits())
				.array();
		UUID expected = new UUID(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
		UUID actual = converter.parseBytes(descriptor, source);
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void parseInvalidUUID() {
		FormatFieldDescriptor descriptor = fieldDescriptorBuilder().build();
		assertThatExceptionOfType(FieldConversionException.class)
				.isThrownBy(() -> converter.parse(descriptor, "invalid-uuid"))
				.withCauseInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void parseInvalidBinaryUUID() throws DecoderException {
		FormatFieldDescriptor descriptor = fieldDescriptorBuilder()
				.type(Type.BINARY)
				.length(16)
				.build();
		byte[] source = Hex.decodeHex("0123456789ABCDEF");
		assertThatExceptionOfType(FieldConversionException.class)
				.isThrownBy(() -> converter.parseBytes(descriptor, source));
	}

}
