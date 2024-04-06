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
package format.bind.converter;

import format.bind.FormatException;
import format.bind.converter.spi.FieldConverterProvider;

/**
 * Thrown when the {@link FieldConverterProvider} was not found.
 * 
 * @author Yannick Ebongue
 */
public class FieldConverterProviderNotFoundException extends FormatException {

	private static final long serialVersionUID = 6308979352377968181L;

	/**
	 * Creates a new {@code FieldConverterProviderNotFoundException} with the
	 * specified detail message.
	 * 
	 * @param message The detail message.
	 */
	public FieldConverterProviderNotFoundException(String message) {
		super(message);
	}

	/**
	 * Creates a new {@code FieldConverterProviderNotFoundException} with the
	 * specified detail message and cause.
	 * 
	 * @param message The detail message.
	 * @param cause The cause.
	 */
	public FieldConverterProviderNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
