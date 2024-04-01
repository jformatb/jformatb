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

/**
 * Thrown by the {@link Formatter} when an error occurs during formatting Java object
 * or parsing text format.
 * 
 * @author Yannick Ebongue
 */
public class FormatProcessingException extends FormatException {

	private static final long serialVersionUID = 7275383063852148739L;

	/**
	 * Creates a new {@code FormatProcessingException} with the specified
	 * detail message.
	 * 
	 * @param message The detail message.
	 */
	public FormatProcessingException(String message) {
		super(message);
	}

	/**
	 * Creates a new {@code FormatProcessingException} with the specified
	 * detail message and cause.
	 * 
	 * @param message The detail message.
	 * @param cause The cause.
	 */
	public FormatProcessingException(String message, Throwable cause) {
		super(message, cause);
	}

}
