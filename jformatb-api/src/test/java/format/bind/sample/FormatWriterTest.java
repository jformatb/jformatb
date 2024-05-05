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
package format.bind.sample;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import format.bind.FormatWriter;
import format.bind.Formatter;

class FormatWriterTest {

	@Test
	void createWriterWithListener() {
		FormatWriter<Object, ?> expected = Formatter.of(Object.class).createWriter();
		FormatWriter<Object, ?> actual = expected.withListener((obj, fields) -> {});
		assertThat(actual).isSameAs(expected);
	}

	@Test
	void createWriterWithProperties() {
		FormatWriter<Object, ?> expected = Formatter.of(Object.class).createWriter();
		FormatWriter<Object, ?> actual = expected.withProperties(Collections.singletonMap("property1", "value1"));
		assertThat(actual).isSameAs(expected);
	}

}
