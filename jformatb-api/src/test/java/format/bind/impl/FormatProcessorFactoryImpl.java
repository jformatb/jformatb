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

import format.bind.FormatReader;
import format.bind.FormatWriter;
import format.bind.spi.FormatProcessorFactory;

public class FormatProcessorFactoryImpl implements FormatProcessorFactory {

	@SuppressWarnings("unchecked")
	@Override
	public <T, F extends FormatReader<T, F>> FormatReader<T, F> createReader(Class<? extends T> type, String pattern) {
		return (FormatReader<T, F>) new FormatReaderImpl<>(type, pattern);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T, F extends FormatWriter<T, F>> FormatWriter<T, F> createWriter(Class<? extends T> type, String pattern) {
		return (FormatWriter<T, F>) new FormatWriterImpl<>(type, pattern);
	}

}
