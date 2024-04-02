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
package format.bind.converter.spi;

import format.bind.Formatter;
import format.bind.converter.FieldConverter;
import format.bind.converter.FieldConverterNotFoundException;

/**
 * Service Provider Interface for {@link FieldConverter}.
 * 
 * @author Yannick Ebongue
 * 
 */
public interface FieldConverterProvider {

	/**
	 * Obtain the {@link FieldConverter} of the specified field type and converter type.
	 * @param <T> The Java type of the field.
	 * @param fieldType The class instance of the field Java type.
	 * @param converterType The class instance of the field converter to obtain.
	 * @return The field converter instance.
	 * @throws FieldConverterNotFoundException if no field converter found for the
	 * 		specified field Java type.
	 */
	<T> FieldConverter<T> getConverter(final Class<T> fieldType,
			Class<? extends FieldConverter<T>> converterType) throws FieldConverterNotFoundException;

	/**
	 * Obtain the {@link FieldConverter} of the specified field type.
	 * @param <T> The Java type of the field.
	 * @param fieldType The class instance of the field Java type.
	 * @return The field converter instance.
	 * @throws FieldConverterNotFoundException if no field converter found for the
	 * 		specified field Java type.
	 */
	<T> FieldConverter<T> getConverter(final Class<T> fieldType) throws FieldConverterNotFoundException;

	/**
	 * Obtain the {@link FieldConverter} of the specified {@code formatter}.
	 * @param <T> The type of the {@link Formatter} type.
	 * @param formatter The formatter
	 * @return The field converter instance.
	 * @throws FieldConverterNotFoundException if no field converter found for the
	 * 		specified {@code formatter}.
	 */
	<T> FieldConverter<T> getConverter(final Formatter<T> formatter) throws FieldConverterNotFoundException;

}
