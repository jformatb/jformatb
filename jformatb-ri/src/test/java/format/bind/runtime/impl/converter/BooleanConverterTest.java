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

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import format.bind.converter.FieldConversionException;

class BooleanConverterTest extends AbstractConverterTest<Boolean> {

	BooleanConverterTest() {
		super(Boolean.class);
	}

	@Test
	void formatBoolean() {
		String actual = converter.format(fieldDescriptorBuilder().build(), true);
		assertThat(actual).isEqualTo("1");
	}

	@Test
	void parseBoolean() {
		boolean actual = converter.parse(fieldDescriptorBuilder().build(), "0");
		assertThat(actual).isFalse();
	}

	@Test
	void failFormat() {
		Throwable exception = catchThrowable(() -> converter.format(fieldDescriptorBuilder().build(), null));
		assertThat(exception).isInstanceOf(FieldConversionException.class);
	}

	@Test
	void failParse() {
		Throwable exception = catchThrowable(() -> converter.parse(fieldDescriptorBuilder().build(), "true"));
		assertThat(exception).isInstanceOf(FieldConversionException.class);
	}

}
