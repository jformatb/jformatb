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
package format.bind.sample.impl;

import java.math.BigInteger;

import format.bind.FormatProcessingException;
import format.bind.FormatReader;

public class FormatReaderImpl<T> extends FormatProcessorImpl<T, FormatReaderImpl<T>>
		implements FormatReader<T, FormatReaderImpl<T>> {

	public FormatReaderImpl(Class<T> type, String pattern) {
		super(type, pattern);
    }

	@Override
	public T read(String text) throws FormatProcessingException {
		throw new FormatProcessingException(String.format("Unable to parse text '%s'", text), new UnsupportedOperationException("Operation not supported"));
	}

	@Override
	public T readBytes(byte[] bytes) throws FormatProcessingException {
		throw new FormatProcessingException(String.format("Unable to parse byte array '%s", formatHex(bytes)), new UnsupportedOperationException("Operation not supported"));
	}

	private static String formatHex(final byte[] bytes) {
		if (bytes != null && bytes.length > 0) {
			String format = "%0" + (bytes.length << 1) + "X";
			return String.format(format, new BigInteger(1, bytes));
		} else {
			return "";
		}
	}

}
