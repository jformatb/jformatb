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
package format.bind.runtime.impl.converter;

import format.bind.FormatFieldDescriptor;
import format.bind.converter.FieldConversionException;
import lombok.experimental.UtilityClass;

@UtilityClass
class FieldConverterUtil {

	<T> String throwFormatFieldConversionException(
			final FormatFieldDescriptor descriptor, final T value, Exception cause) {
		if (cause instanceof FieldConversionException) {
			throw (FieldConversionException) cause;
		}

		throw new FieldConversionException(
				String.format("Unable to format value [%s] for field '%s'", value, descriptor.name()),
				cause);
	}

	<T> T throwParseFieldConversionException(
			final FormatFieldDescriptor descriptor, final String source, Exception cause) {
		if (cause instanceof FieldConversionException) {
			throw (FieldConversionException) cause;
		}

		throw new FieldConversionException(
				String.format("Unable to parse text [%s] for field '%s'", source, descriptor.name()),
				cause);
	}

}
