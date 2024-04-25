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
package format.datatype;

import java.io.Serializable;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

import format.bind.annotation.FormatField;
import format.bind.annotation.FormatField.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ISO8583 implements Serializable {

	private static final long serialVersionUID = 2260586751727192971L;

	@FormatField(name = "MTI", length = 4, type = Type.NUMERIC)
	private String messageTypeIndicator;

	@FormatField(name = "BITMAP")
	private byte[] bitmap;

	private static String extractMessageTypeIndicator(final String message) {
		return message.substring(0, 4);
	}

	private static byte[] extractBitmap(final String message) {
		String dataElements = message.substring(4);
		int start = 0;
		int length = 16;
		StringBuilder bitmap = new StringBuilder();

		do {
			String hex = dataElements.substring(start, start + length);
			String bin = toBinaryString(hex);
			bitmap.append(hex);

			if (bin.charAt(0) == '0') {
				break;
			}

			start += length;
		} while (true);

		try {
			return Hex.decodeHex(bitmap.toString());
		} catch (DecoderException e) {
			throw new IllegalArgumentException(e);
		}
	}

	private static String toBinaryString(final String hex) {
		return StringUtils.leftPad(Long.toBinaryString(Long.parseLong(hex, 16)), (hex.length() / 2) * 8, "0");
	}

	public static ISO8583 fromString(final String message) {
		return ISO8583.builder()
				.messageTypeIndicator(extractMessageTypeIndicator(message))
				.bitmap(extractBitmap(message))
				.build();
	}

}
