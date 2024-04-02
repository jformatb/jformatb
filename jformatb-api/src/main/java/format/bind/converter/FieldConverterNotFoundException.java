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
 * Thrown by the {@link FieldConverterProvider} when there is no field converter for
 * a specified field Java type.
 * 
 * @author Yannick Ebongue
 */
public class FieldConverterNotFoundException extends FormatException {

	private static final long serialVersionUID = -7062317058328977232L;

	/**
	 * Creates a new {@code FieldConverterNotFoundException} with the specified
	 * detail message.
	 * 
	 * @param message The detail message.
	 */
	public FieldConverterNotFoundException(String message) {
		super(message);
	}

	/**
	 * Creates a new {@code FieldConverterNotFoundException} with the specified
	 * detail message and cause.
	 * 
	 * @param message The detail message.
	 * @param cause The cause.
	 */
	public FieldConverterNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
