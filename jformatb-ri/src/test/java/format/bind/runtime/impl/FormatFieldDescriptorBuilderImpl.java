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

import format.bind.FormatFieldDescriptor;
import format.bind.annotation.FormatField.Type;

public class FormatFieldDescriptorBuilderImpl {

	private FormatFieldDescriptorImpl descriptor = new FormatFieldDescriptorImpl()
			.type(Type.DEFAULT)
			.charset(Charset.defaultCharset())
			.locale("")
			.placeholder("");

	public FormatFieldDescriptorBuilderImpl name(String name) {
		descriptor.name(name);
		return this;
	}

	public FormatFieldDescriptorBuilderImpl type(Type type) {
		descriptor.type(type);
		return this;
	}

	public FormatFieldDescriptorBuilderImpl charset(Charset charset) {
		descriptor.charset(charset);
		return this;
	}

	public FormatFieldDescriptorBuilderImpl length(int length) {
		descriptor.length(length);
		return this;
	}

	public FormatFieldDescriptorBuilderImpl scale(int scale) {
		descriptor.scale(scale);
		return this;
	}

	public FormatFieldDescriptorBuilderImpl format(String format) {
		descriptor.format(format);
		return this;
	}

	public FormatFieldDescriptorBuilderImpl locale(String locale) {
		descriptor.locale(locale);
		return this;
	}

	public FormatFieldDescriptorBuilderImpl zone(String zone) {
		descriptor.zone(zone);
		return this;
	}

	public FormatFieldDescriptorBuilderImpl placeholder(String placeholder) {
		descriptor.placeholder(placeholder);
		return this;
	}

	public FormatFieldDescriptorBuilderImpl readOnly(boolean readOnly) {
		descriptor.readOnly(readOnly);
		return this;
	}

	public FormatFieldDescriptorBuilderImpl targetClass(Class<?> targetClass) {
		descriptor.targetClass(targetClass);
		return this;
	}

	public FormatFieldDescriptor build() {
		return descriptor;
	}

}
