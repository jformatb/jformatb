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

import java.util.Map;

import format.bind.FormatProcessingException;
import format.bind.FormatWriter;

public class FormatWriterImpl<T> extends FormatProcessorImpl<T, FormatWriterImpl<T>>
		implements FormatWriter<T, FormatWriterImpl<T>> {

	public FormatWriterImpl(Class<T> type, String pattern) {
		super(type, pattern);
	}

	@Override
	public FormatWriterImpl<T> setProperties(Map<String, Object> properties) {
		return this;
	}

	@Override
	public String write(T obj) throws FormatProcessingException {
		throw new FormatProcessingException(String.format("Unable to format object '%s'", obj), new UnsupportedOperationException("Operation not supported"));
	}

	@Override
	public byte[] writeBytes(T obj) throws FormatProcessingException {
		throw new FormatProcessingException(String.format("Unable to format object '%s'", obj), new UnsupportedOperationException("Operation not supported"));
	}

}
