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
package format.datatype.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.ByteBuffer;

import org.apache.commons.codec.DecoderException;
import org.junit.jupiter.api.Test;

import format.bind.Formatter;
import format.datatype.ISO8583;

class ISO8583Test {

	@Test
	void formatPrimaryBitmap() throws DecoderException {
		String actual = Formatter.of(ISO8583.class).format(ISO8583.builder()
				.messageTypeIndicator("0800")
				.primaryBitmap(ByteBuffer.allocate(8)
						.put((byte) 0x20)
						.put((byte) 0x38)
						.put((byte) 0x00)
						.put((byte) 0x00)
						.put((byte) 0x00)
						.put((byte) 0x20)
						.put((byte) 0x00)
						.put((byte) 0x02)
						.array())
				.build());
		String expected = "08002038000000200002";
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void parsePrimaryBitmap() throws DecoderException {
		ISO8583 actual = Formatter.of(ISO8583.class).parse("01107010001102C04804");
		ISO8583 expected = ISO8583.builder()
				.messageTypeIndicator("0110")
				.primaryBitmap(ByteBuffer.allocate(8)
						.put((byte) 0x70)
						.put((byte) 0x10)
						.put((byte) 0x00)
						.put((byte) 0x11)
						.put((byte) 0x02)
						.put((byte) 0xC0)
						.put((byte) 0x48)
						.put((byte) 0x04)
						.array())
				.build();
		assertThat(actual).isEqualTo(expected);
	}

}
