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

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.Test;

import format.bind.FormatFieldDescriptor;
import format.bind.annotation.FormatField.Type;

class ByteArrayConverterTest extends AbstractConverterTest<byte[]> {

	public ByteArrayConverterTest() {
		super(byte[].class);
	}

	@Test
	void formatByteArray() throws DecoderException {
		FormatFieldDescriptor descriptor = fieldDescriptorBuilder()
				.length(16)
				.build();
		String data = "0412AC89ABCDEF67";
		String actual = converter.format(descriptor, Hex.decodeHex(data));
		assertThat(actual).isEqualTo(data);
	}

	@Test
	void parseByteArray() {
		FormatFieldDescriptor descriptor = fieldDescriptorBuilder()
				.length(32)
				.build();
		String source = "441234AAAAAAAAAA911B9B36BC7CE94E";
		byte[] actual = converter.parse(descriptor, source);
		assertThat(actual).asHexString().isEqualTo(source);
	}

	@Test
	void formatEmptyByteArray() {
		FormatFieldDescriptor descriptor = fieldDescriptorBuilder()
				.length(8)
				.build();
		String expected = "00000000";
		String actual = converter.format(descriptor, new byte[0]);
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void parseEmptyByteArray() {
		FormatFieldDescriptor descriptor = fieldDescriptorBuilder()
				.length(16)
				.build();
		String source = "0000000000000000";
		byte[] actual = converter.parse(descriptor, source);
		assertThat(actual).isEmpty();
	}

	@Test
	void formatBinaryByteArray() throws DecoderException {
		FormatFieldDescriptor descriptor = fieldDescriptorBuilder()
				.type(Type.BINARY)
				.length(8)
				.build();
		String data = "0412AC89ABCDEF67";
		byte[] actual = converter.formatBytes(descriptor, Hex.decodeHex(data));
		assertThat(actual).asHexString().isEqualTo(data);
	}

	@Test
	void parseBinaryByteArray() throws DecoderException {
		FormatFieldDescriptor descriptor = fieldDescriptorBuilder()
				.type(Type.BINARY)
				.length(16)
				.build();
		String source = "441234AAAAAAAAAA911B9B36BC7CE94E";
		byte[] actual = converter.parseBytes(descriptor, Hex.decodeHex(source));
		assertThat(actual).asHexString().isEqualTo(source);
	}

}
