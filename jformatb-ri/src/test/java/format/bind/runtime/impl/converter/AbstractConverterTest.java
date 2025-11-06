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

import org.junit.jupiter.api.BeforeEach;

import format.bind.converter.FieldConverter;
import format.bind.runtime.impl.FormatFieldDescriptorBuilderImpl;

abstract class AbstractConverterTest<T> {

	private final Class<T> type;

	FieldConverter<T> converter;

	AbstractConverterTest(Class<T> type) {
		this.type = type;
	}

	@BeforeEach
	void init() {
		converter = FieldConverter.provider().getConverter(type);
	}

	static final FormatFieldDescriptorBuilderImpl fieldDescriptorBuilder() {
		return new FormatFieldDescriptorBuilderImpl();
	}

}
