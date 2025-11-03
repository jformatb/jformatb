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

import java.nio.charset.Charset;

import format.bind.FormatProcessor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public abstract class FormatProcessorImpl<T, F extends FormatProcessorImpl<T, F>> implements FormatProcessor<T, F> {

	private final Class<T> type;

	private final String pattern;

	@SuppressWarnings("unchecked")
	@Override
	public F setCharset(Charset charset) {
		return (F) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public F setListener(Listener<T> listener) {
		return (F) this;
	}

}
