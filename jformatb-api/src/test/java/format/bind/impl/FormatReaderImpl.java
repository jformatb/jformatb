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
package format.bind.impl;

import format.bind.FormatProcessingException;
import format.bind.FormatReader;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FormatReaderImpl<T> implements FormatReader<T, FormatReaderImpl<T>> {

	private final Class<T> type;

	private final String pattern;

	@Override
	public FormatReaderImpl<T> setListener(Listener<T> listener) {
		throw new UnsupportedOperationException();
	}

	@Override
	public T read(String text) throws FormatProcessingException {
		throw new FormatProcessingException(String.format("Unable to parse text '%s'", text));
	}

}
