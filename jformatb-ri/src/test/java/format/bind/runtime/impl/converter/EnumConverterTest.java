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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import format.bind.FormatFieldDescriptor;
import format.bind.annotation.FormatField.Type;
import format.bind.runtime.impl.FormatFieldDescriptorBuilderImpl;

class EnumConverterTest {

	@ParameterizedTest
	@EnumSource
	void formatEnumConstant(Currency constant) {
		FormatFieldDescriptor descriptor = new FormatFieldDescriptorBuilderImpl()
				.type(Type.ALPHANUMERIC)
				.length(3)
				.build();
		String actual = EnumConverter.of(Currency.class).format(descriptor, constant);
		assertThat(actual).isEqualTo(constant.name());
	}

	@ParameterizedTest
	@EnumSource
	void parseEnumConstant(Currency constant) {
		FormatFieldDescriptor descriptor = new FormatFieldDescriptorBuilderImpl()
				.type(Type.ALPHANUMERIC)
				.length(3)
				.build();
		Currency actual = EnumConverter.of(Currency.class).parse(descriptor, constant.name());
		assertThat(actual).isSameAs(constant);
	}

	@ParameterizedTest
	@EnumSource
	void formatEnumOrdinal(Answer constant) {
		FormatFieldDescriptor descriptor = new FormatFieldDescriptorBuilderImpl().build();
		String actual = EnumConverter.of(Answer.class).format(descriptor, constant);
		assertThat(actual).asInt().isEqualTo(constant.ordinal());
	}

	@ParameterizedTest
	@EnumSource
	void parseEnumOrdinal(Answer constant) {
		FormatFieldDescriptor descriptor = new FormatFieldDescriptorBuilderImpl().build();
		Answer actual = EnumConverter.of(Answer.class).parse(descriptor, String.valueOf(constant.ordinal()));
		assertThat(actual).isSameAs(constant);
	}

	enum Currency {

		AUD,
		CAD,
		CHF,
		EUR,
		GBP,
		USD

	}

	enum Answer {

		NO, YES

	}

}
