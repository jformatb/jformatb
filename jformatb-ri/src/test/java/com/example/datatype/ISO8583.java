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
package com.example.datatype;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

import format.bind.annotation.FormatFactory;
import format.bind.annotation.FormatField;
import format.bind.annotation.FormatField.Type;
import format.bind.annotation.FormatValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.Value;
import lombok.With;

@Data
@Builder
@AllArgsConstructor
public class ISO8583 implements Serializable {

	private static final long serialVersionUID = 2260586751727192971L;

	@FormatField(name = "MTI", length = 4, type = Type.NUMERIC)
	private MessageTypeIndicator messageTypeIndicator;

	@FormatField(name = "BITMAP")
	private byte[] bitmap;

	@FormatField(name = "DE")
	@Singular
	private List<DataElement> dataElements;

	public ISO8583() {
		dataElements = new ArrayList<>();
	}

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
				.messageTypeIndicator(MessageTypeIndicator.fromString(extractMessageTypeIndicator(message)))
				.bitmap(extractBitmap(message))
				.build();
	}

	public enum Version {
		ISO_8583_1987,
		ISO_8583_1993,
		ISO_8583_2003
	}

	public enum MessageClass {
		RESERVED,
		AUTHORIZATION,
		FINANCIAL,
		FILE,
		REVERSAL,
		RECONCILIATION,
		ADMINISTRATIVE,
		FEE,
		NETWORK
	}

	public enum MessageFunction {
		REQUEST,
		REQUEST_RESPONSE,
		ADVICE,
		ADVICE_RESPONSE,
		NOTIFICATION,
		NOTIFICATION_ACKNOWLEDGEMENT,
		INSTRUCTION,
		INSTRUCTION_ACKNOWLEDGEMENT
	}

	public enum MessageOrigin {
		ACQUIRER,
		ACQUIRER_REPEAT,
		ISSUER,
		ISSUER_REPEAT,
		OTHER
	}

	@Value
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class MessageTypeIndicator implements Serializable {

		private static final long serialVersionUID = 2150669968457852006L;

		private Version version;

		private MessageClass messageClass;

		private MessageFunction messageFunction;

		private MessageOrigin messageOrigin;

		@Override
		@FormatValue
		public String toString() {
			return new StringBuilder()
					.append(version.ordinal())
					.append(messageClass.ordinal())
					.append(messageFunction.ordinal())
					.append(messageOrigin.ordinal())
					.toString();
		}

		@FormatFactory
		public static MessageTypeIndicator fromString(String mti) {
			return new MessageTypeIndicator(
					Version.values()[Character.getNumericValue(mti.charAt(0))],
					MessageClass.values()[Character.getNumericValue(mti.charAt(1))],
					MessageFunction.values()[Character.getNumericValue(mti.charAt(2))],
					MessageOrigin.values()[Character.getNumericValue(mti.charAt(3))]);
		}

	}

	@Value(staticConstructor = "of")
	public static class DataElement implements Serializable {

		private static final long serialVersionUID = -4932375871728633474L;

		@With
		private int position;

		@FormatValue
		private Object value;

		private void writeObject(ObjectOutputStream out) throws IOException {
			out.writeInt(position);
			out.writeObject(value);
		}

		@FormatFactory
		public static DataElement of(Object value) {
			return new DataElement(0, value);
		}

	}

}
