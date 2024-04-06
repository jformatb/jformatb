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

import format.bind.Formatter;
import format.bind.converter.FieldConverter;
import format.bind.converter.spi.FieldConverterProvider;

/**
 * The default {@link FieldConverterProvider}.
 * 
 * @author Yannick Ebongue
 */
public class FieldConverterProviderImpl implements FieldConverterProvider {

	@Override
	public <T> FieldConverter<T> getConverter(Class<T> fieldType, Class<? extends FieldConverter<T>> converterType) {
		return FieldConverters.getConverter(fieldType, converterType);
	}

	@Override
	public <T> FieldConverter<T> getConverter(Class<T> fieldType) {
		return FieldConverters.getConverter(fieldType);
	}

	@Override
	public <T> FieldConverter<T> getConverter(Formatter<T> formatter) {
		return FieldConverters.getConverter(formatter);
	}

}
