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
package format.bind;

import format.bind.Formatter.Listener;

/**
 * The common interface of text format processors.
 * 
 * @param <T> The type of Java object to process.
 * @param <F> The type of text format processor.
 * 
 * @author Yannick Ebongue
 * 
 * @see FormatReader
 * @see FormatWriter
 */
public interface FormatProcessor<T, F extends FormatProcessor<T, F>> {

	/**
	 * Register a post processing event callback {@link Listener} with this {@link FormatProcessor}.
	 * 
	 * <p>
	 * There is only one {@code Listener} per {@code FormatProcessor}. Setting a {@code Listener}
	 * replaces the previous registered.
	 * 
	 * @param listener The post processing event callback for this {@link FormatProcessor}.
	 * @return This {@code FormatProcessor}.
	 */
	F setListener(Listener<T> listener);

}
