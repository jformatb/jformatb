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

import org.junit.jupiter.api.Test;

import format.bind.FormatFieldDescriptor;

class CharacterConverterTest extends AbstractConverterTest<Character> {

	CharacterConverterTest() {
		super(Character.class);
	}

	@Test
	void formatCharacter() {
		FormatFieldDescriptor descriptor = fieldDescriptorBuilder().build();
		char c = 'p';
		String expected = String.valueOf(c);
		String actual = converter.format(descriptor, c);
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void parseCharacter() {
		FormatFieldDescriptor descriptor = fieldDescriptorBuilder().length(4).build();
		String source = "Z   ";
		char expected = source.charAt(0);
		char actual = converter.parse(descriptor, source);
		assertThat(actual).isEqualTo(expected);
	}

}
