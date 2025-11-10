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
package format.bind.runtime.impl;

import java.nio.charset.Charset;
import java.util.Objects;
import java.util.Optional;

import format.bind.FormatFieldDescriptor;
import format.bind.annotation.FormatField;
import format.bind.annotation.FormatField.Type;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
class FormatFieldDescriptorImpl implements FormatFieldDescriptor {

	private String name;

	private Type type;

	private Charset charset;

	private int length;

	private int scale;

	private String format;

	private String locale;

	private String zone;

	private String placeholder;

	private boolean readOnly;

	private Class<?> targetClass;

	public static final FormatFieldDescriptorImpl from(FormatField field, Charset charset) {
		Objects.requireNonNull(field);
		return new FormatFieldDescriptorImpl()
				.name(field.name())
				.type(field.type())
				.charset(Optional.ofNullable(field.charset())
						.filter(charsetName -> !charsetName.isEmpty())
						.map(Charset::forName)
						.orElse(charset))
				.length(field.length())
				.scale(field.scale())
				.format(field.format())
				.locale(field.locale())
				.zone(field.zone())
				.placeholder(field.placeholder())
				.readOnly(field.readOnly())
				.targetClass(field.targetClass());
	}

}
